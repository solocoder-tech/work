#include"map_cut.h"

#include <vector>
#include <stdlib.h>
#include <cmath>
#include <algorithm>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include <android/log.h>

#ifdef  _WINDOWS
#include<opencv2/opencv.hpp>
#else
#endif //  _WINDOWS

#include"_ContourFinder.h"
#include"_hough.h"



#define round(d) (floor((d) + 0.5))

#define TAG    "xxxx"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
//移植的定义
#define INT_MIN     (-2147483647 - 1) // minimum (signed) int value
#define INT_MAX       2147483647    // maximum (signed) int value
#ifndef MIN
#  define MIN(a,b)  ((a) > (b) ? (b) : (a))
#endif
#ifndef MAX
#  define MAX(a,b)  ((a) < (b) ? (b) : (a))
#endif




//移植opencv
struct PolyEdge {
	PolyEdge() : y0(0), y1(0), x(0), dx(0), next(0) {}
	//PolyEdge(int _y0, int _y1, int _x, int _dx) : y0(_y0), y1(_y1), x(_x), dx(_dx) {}

	int y0, y1;
	int64_t x, dx;
	PolyEdge *next;
};

struct CmpEdges {
	bool operator ()(const PolyEdge& e1, const PolyEdge& e2) {
		return e1.y0 - e2.y0 ? e1.y0 < e2.y0 :
			e1.x - e2.x ? e1.x < e2.x : e1.dx < e2.dx;
	}
};

static inline void ICV_HLINE_X(uint8_t* ptr, int xl, int xr, const uint8_t* color, int pix_size) {
	uint8_t* hline_min_ptr = (uint8_t*)(ptr)+(xl)*(pix_size);
	uint8_t* hline_end_ptr = (uint8_t*)(ptr)+(xr + 1)*(pix_size);
	uint8_t* hline_ptr = hline_min_ptr;
	if (pix_size == 1)
		memset(hline_min_ptr, *color, hline_end_ptr - hline_min_ptr);
	else//if (pix_size != 1)
	{
		if (hline_min_ptr < hline_end_ptr) {
			memcpy(hline_ptr, color, pix_size);
			hline_ptr += pix_size;
		}//end if (hline_min_ptr < hline_end_ptr)
		size_t sizeToCopy = pix_size;
		while (hline_ptr < hline_end_ptr) {
			memcpy(hline_ptr, hline_min_ptr, sizeToCopy);
			hline_ptr += sizeToCopy;
			sizeToCopy = std::min(2 * sizeToCopy, static_cast<size_t>(hline_end_ptr - hline_ptr));
		}//end while(hline_ptr < hline_end_ptr)
	}//end if (pix_size != 1)
}
//end ICV_HLINE_X()
static inline void ICV_HLINE(uint8_t* ptr, int xl, int xr, const void* color, int pix_size) {
	ICV_HLINE_X(ptr, xl, xr, reinterpret_cast<const uint8_t*>(color), pix_size);
}
enum { XY_SHIFT = 16, XY_ONE = 1 << XY_SHIFT, DRAWING_STORAGE_BLOCK = (1 << 12) - 256 };

void  FillEdgeCollection(unsigned char *img, int w, int h, std::vector<PolyEdge>& edges, unsigned char color) {
	PolyEdge tmp;
	int i, y, total = (int)edges.size();
	PolyEdge* e;
	int y_max = INT_MIN, y_min = INT_MAX;
	int64_t x_max = 0xFFFFFFFFFFFFFFFF, x_min = 0x7FFFFFFFFFFFFFFF;
	//int pix_size = 1;//单通道

	if (total < 2)
		return;

	for (i = 0; i < total; i++) {
		PolyEdge& e1 = edges[i];
		assert(e1.y0 < e1.y1);
		// Determine x-coordinate of the end of the edge.
		// (This is not necessary x-coordinate of any vertex in the array.)
		int64_t x1 = e1.x + (e1.y1 - e1.y0) * e1.dx;
		y_min = std::min(y_min, e1.y0);
		y_max = std::max(y_max, e1.y1);
		x_min = std::min(x_min, e1.x);
		x_max = std::max(x_max, e1.x);
		x_min = std::min(x_min, x1);
		x_max = std::max(x_max, x1);
	}

	if (y_max < 0 || y_min >= h || x_max < 0 || x_min >= (w << XY_SHIFT))
		return;

	std::sort(edges.begin(), edges.end(), CmpEdges());

	// start drawing
	tmp.y0 = INT_MAX;
	edges.push_back(tmp); // after this point we do not add
						  // any elements to edges, thus we can use pointers
	i = 0;
	tmp.next = 0;
	e = &edges[i];
	y_max = MIN(y_max, h);

	//这个循环是绘制图像中的一行
	for (y = e->y0; y < y_max; y++) {
		PolyEdge *last, *prelast, *keep_prelast;
		int sort_flag = 0;
		int draw = 0;
		int clipline = y < 0;

		prelast = &tmp;
		last = tmp.next;

		//这个循环是绘制图像一行中的各个部分
		while (last || e->y0 == y) {
			if (last && last->y1 == y) {
				// exclude edge if y reaches its lower point
				prelast->next = last->next;
				last = last->next;
				continue;
			}
			keep_prelast = prelast;
			if (last && (e->y0 > y || last->x < e->x)) {
				// go to the next edge in active list
				prelast = last;
				last = last->next;
			}
			else if (i < total) {
				// insert new edge into active list if y reaches its upper point
				prelast->next = e;
				e->next = last;
				prelast = e;
				e = &edges[++i];
			}
			else
				break;

			if (draw) {
				if (!clipline) {
					// convert x's from fixed-point to image coordinates
					uint8_t *timg = img + y * w;
					int x1, x2;

					if (keep_prelast->x > prelast->x) {
						x1 = (int)((prelast->x + XY_ONE - 1) >> XY_SHIFT);
						x2 = (int)(keep_prelast->x >> XY_SHIFT);
					}
					else {
						x1 = (int)((keep_prelast->x + XY_ONE - 1) >> XY_SHIFT);
						x2 = (int)(prelast->x >> XY_SHIFT);
					}

					// clip and draw the line
					if (x1 < w && x2 >= 0) {
						if (x1 < 0)
							x1 = 0;
						if (x2 >= w)
							x2 = w - 1;
						//ICV_HLINE(timg, x1, x2, color, pix_size);//opencv org
						memset(timg + x1, color, (x2 - x1));
					}
				}
				keep_prelast->x += keep_prelast->dx;
				prelast->x += prelast->dx;
			}
			draw ^= 1;
		}

		// sort edges (using bubble sort)
		keep_prelast = 0;

		do {
			prelast = &tmp;
			last = tmp.next;

			while (last != keep_prelast && last->next != 0) {
				PolyEdge *te = last->next;

				// swap edges
				if (last->x > te->x) {
					prelast->next = te;
					last->next = te->next;
					te->next = last;
					prelast = te;
					sort_flag = 1;
				}
				else {
					prelast = last;
					last = te;
				}
			}
			keep_prelast = prelast;
		} while (sort_flag && keep_prelast != tmp.next && keep_prelast != &tmp);
	}
}

void  CollectPolyEdges(unsigned char *img, int w, int h, const std::vector<_PointI32>& v, std::vector<PolyEdge>& edges,
	const void* color = NULL, int line_type = 8, int shift = 0, _PointI32 offset = _PointI32(0, 0)) {
	int count = v.size();

	int i, delta = offset.y + ((1 << shift) >> 1);
	_PointI32 pt0 = v[count - 1], pt1;
	pt0.x = (pt0.x + offset.x) << (XY_SHIFT - shift);//pt0.x = pt0.x*65536
	pt0.y = (pt0.y + delta) >> shift;

	edges.reserve(edges.size() + count);

	for (i = 0; i < count; i++, pt0 = pt1) {
		_PointI32 t0, t1;
		PolyEdge edge;

		pt1 = v[i];
		pt1.x = (pt1.x + offset.x) << (XY_SHIFT - shift);
		pt1.y = (pt1.y + delta) >> shift;


		////TH 注释
		//if (line_type < CV_AA)
		//{
		//	t0.y = pt0.y; t1.y = pt1.y;
		//	t0.x = (pt0.x + (XY_ONE >> 1)) >> XY_SHIFT;
		//	t1.x = (pt1.x + (XY_ONE >> 1)) >> XY_SHIFT;
		//	Line(img, t0, t1, color, line_type);
		//}
		//else
		//{
		//	t0.x = pt0.x; t1.x = pt1.x;
		//	t0.y = pt0.y << XY_SHIFT;
		//	t1.y = pt1.y << XY_SHIFT;
		//	LineAA(img, t0, t1, color);
		//}

		if (pt0.y == pt1.y)
			continue;

		if (pt0.y < pt1.y) {
			edge.y0 = (int)(pt0.y);
			edge.y1 = (int)(pt1.y);
			edge.x = pt0.x;
		}
		else {
			edge.y0 = (int)(pt1.y);
			edge.y1 = (int)(pt0.y);
			edge.x = pt1.x;
		}
		edge.dx = (pt1.x - pt0.x) / (pt1.y - pt0.y);
		edges.push_back(edge);
	}
}

//优化方向 添加轮廓的话，可以加快腐蚀的速度，不需要所有的点都遍历
void erode(unsigned char *img, size_t w, size_t h) {
	//自操作选项
	unsigned char *dst = new unsigned char[w*h];
	memset(dst, 255, w*h);

	for (size_t y = 1; y < h - 1; y++) {
		unsigned char *p = img + y * w;
		for (size_t x = 1; x < w - 1; x++) {
			if (p[x] == 0) {
				for (int m = -1; m <= 1; m++) {
					unsigned char *sp = dst + (y + m) * w;
					for (int n = -1; n <= 1; n++) {
						sp[x + n] = 0;
					}
				}
			}
		}
	}

	memcpy(img, dst, w*h);
	delete[] dst;
}

void erode(unsigned char *img, unsigned char *dst, size_t w, size_t h) {

	for (size_t y = 1; y < h - 1; y++) {
		unsigned char *p = img + y * w;
		for (size_t x = 1; x < w - 1; x++) {
			if (p[x] == 0) {
				for (int m = -1; m <= 1; m++) {
					unsigned char *sp = dst + (y + m) * w;
					for (int n = -1; n <= 1; n++) {
						sp[x + n] = 0;
					}
				}
			}
		}
	}
}

void dilate(unsigned char *img, unsigned char *dst, size_t w, size_t h) {
	for (size_t y = 1; y < h - 1; y++) {
		unsigned char *p = img + y * w;
		for (size_t x = 1; x < w - 1; x++) {
			if (p[x] != 0) {
				for (int m = -1; m <= 1; m++) {
					unsigned char *sp = dst + (y + m) * w;
					for (int n = -1; n <= 1; n++) {
						sp[x + n] = p[x];
					}
				}
			}
		}
	}
}

void threshold(unsigned char *img, int size, unsigned char thre) {
	for (int i = 0; i < size; i++) {
		img[i] = img[i] > thre ? 255 : 0;
	}
}

void threshold(unsigned char *img, int size, unsigned char low, unsigned char up) {
	for (int i = 0; i < size; i++) {
		//if(low <img[i]&& img[i]<=up)
		if (img[i] == up)
			img[i] = 255;
		else {
			img[i] = 0;
		}
	}
}

double getContourArea(const std::vector<_PointI32> &vp) {
	double a00 = 0;
	_PointI32 prev = _PointI32(vp[vp.size() - 1].x, vp[vp.size() - 1].y);
	for (uint i = 0; i < vp.size(); i++) {
		_PointI32 p = _PointI32(vp[i].x, vp[i].y);
		a00 += (double)prev.x * p.y - (double)prev.y * p.x;
		prev = p;
	}
	a00 *= 0.5;
	a00 = fabs(a00);
	return a00;
}

double getContourArea(const _ContourFinder::Contour * contour) {
	const std::vector<_PointI32> &vp = (contour->points);
	return getContourArea(vp);
}

std::vector<_PointI32> ConvertScatterContourToContinueContour(const std::vector<_PointI32> &vp) {
	std::vector<_PointI32> vpc;

	if (vp.size() <= 1) {
		vpc = vp;
		return vpc;
	}

	size_t vpSize = vp.size();
	_PointI32 p1, p2;

	for (size_t i = 1; i < vpSize; i++) {
		p1 = vp[i - 1];
		p2 = vp[i];

		if (!(p1 == p2))//bug
		{
			if (abs(p1.x - p2.x) > abs(p1.y - p2.y)) {
				int delta = abs(p1.x - p2.x);
				int k = 0;
				if (p2.x > p1.x)k = 1;
				else k = -1;

				for (int j = 0; j < delta; j++) {
					_PointI32 p;
					p.x = p1.x + k * j;
					p.y = p1.y + round((float)(p2.y - p1.y) / (p2.x - p1.x)*k*j);//有bug 除0
					vpc.push_back(p);
				}
			}
			else {
				int delta = abs(p1.y - p2.y);
				int k = 0;
				if (p2.y > p1.y)k = 1;
				else k = -1;

				for (int j = 0; j < delta; j++) {
					_PointI32 p;
					p.y = p1.y + k * j;
					p.x = p1.x + round((float)(p2.x - p1.x) / (p2.y - p1.y)*k*j);
					vpc.push_back(p);
				}
			}
		}
	}


	{
		p1 = vp[vpSize - 1];
		p2 = vp[0];
		if (!(p1 == p2))//bug
		{
			if (abs(p1.x - p2.x) > abs(p1.y - p2.y)) {
				int delta = abs(p1.x - p2.x);
				int k = 0;
				if (p2.x > p1.x)k = 1;
				else k = -1;
				for (int j = 0; j <= delta; j++) {//=符号
					_PointI32 p;
					p.x = p1.x + k * j;
					p.y = p1.y + round((float)(p2.y - p1.y) / (p2.x - p1.x)*k*j);
					vpc.push_back(p);
				}
			}
			else {
				int delta = abs(p1.y - p2.y);
				int k = 0;
				if (p2.y > p1.y)k = 1;
				else k = -1;
				for (int j = 0; j <= delta; j++) {//=符号
					_PointI32 p;
					p.y = p1.y + k * j;
					p.x = p1.x + round((float)(p2.x - p1.x) / (p2.y - p1.y)*k*j);
					vpc.push_back(p);
				}
			}
		}
	}
	return vpc;
}

void DrawContour(const std::vector<_PointI32> &vp, unsigned char  *img_data, unsigned char val, int w, int h) {
	
	////将离散表示的连通域转换为连续表示的连通域
	//std::vector<_PointI32> vpc = ConvertScatterContourToContinueContour(vp);
	////if (w != mImgW + 1 || h != mImgH + 1)return;
	////注意的是vp可能不是连通域的所有点
	//for (uint i = 0; i < vpc.size(); i++) {
	//	img_data[vpc[i].y*w + vpc[i].x] = val;
	//}

	for (uint i = 0; i < vp.size(); i++) {
		int x = vp[i].x;
		int y = vp[i].y;
		if (x >= 0 && x < w&&y >= 0 && y < h)
			img_data[y*w + x] = val;
	}
}

void DrawContour_fill(const std::vector<_PointI32> &vp, unsigned char *img_data, unsigned char val, int w, int h) {
	//step zero 
	//如果不把轮廓画出来的话，最后的结果边缘会产生变化
	DrawContour(vp, img_data, val, w, h);

	//step one 
	std::vector<PolyEdge> edges;
	CollectPolyEdges(img_data, w, h, vp, edges);

	//step two
	FillEdgeCollection(img_data, w, h, edges, val);
}

//未完成
//不能控制线的宽度
void DrawLine(std::pair<int, int> point1, std::pair<int, int> point2, unsigned char *srcImage, unsigned char val, int w, int h) {
	//首先Pa Pb要合法 未完成


	//防止除0
	if (point1 == point2) {
		if(point1.first>=0&& point1.first<w&&point1.second>=0&& point1.second<h)
			srcImage[point1.second*w + point1.first] = val;
		return;
	}

	int MaxLen;
	if (abs(point1.first - point2.first) > (abs(point1.second - point2.second))) {
		MaxLen = abs(point1.first - point2.first);

		float vx = point1.first - point2.first;
		float vy = point1.second - point2.second;
		float dy = vy / vx;
		int K = 0;
		if (vx > 0) {
			K = 1;
		}
		else {
			K = -1;
		}
		std::pair<int, int> nextPoint;
		for (int j = 0; j <= MaxLen; j++) {
			nextPoint.first = point2.first + j * K;
			nextPoint.second = round(point2.second + dy * K*j);

			//这里判断并不高效，最好是修改AB点，使之符合要求
			if (nextPoint.first >= 0 && nextPoint.first < w&&nextPoint.second >= 0 && nextPoint.second < h)
				srcImage[nextPoint.second*w + nextPoint.first] = val;
		}
	}
	else {
		MaxLen = abs(point1.second - point2.second);

		float vx = point1.first - point2.first;
		float vy = point1.second - point2.second;
		float dx = vx / vy;
		int K = 0;
		if (vy > 0) {
			K = 1;
		}
		else {
			K = -1;
		}
		std::pair<int, int> nextPoint;
		for (int j = 0; j <= MaxLen; j++) {
			nextPoint.second = point2.second + j * K;
			nextPoint.first = round(point2.first + dx * K*j);

			//这里判断并不高效，最好是修改AB点，使之符合要求
			if (nextPoint.first >= 0 && nextPoint.first < w&&nextPoint.second >= 0 && nextPoint.second < h)
				srcImage[nextPoint.second*w + nextPoint.first] = val;
		}
	}
}

_RectI32 boundingRect(const std::vector<_PointI32> &vp) {
	_PointI32 LeftTop;
	_PointI32 RightButtom;
	if (vp.size() == 0) return _RectI32(0, 0, 0, 0);

	LeftTop.x = vp[0].x; LeftTop.y = vp[0].y;
	RightButtom.x = vp[0].x; RightButtom.y = vp[0].y;
	for (size_t i = 1; i < vp.size(); i++) {
		_PointI32 p = vp[i];
		if (LeftTop.x > p.x) LeftTop.x = p.x;
		if (LeftTop.y > p.y) LeftTop.y = p.y;
		if (RightButtom.x < p.x) RightButtom.x = p.x;
		if (RightButtom.y < p.y) RightButtom.y = p.y;
	}
	return _RectI32(LeftTop.x, LeftTop.y, RightButtom.x - LeftTop.x + 1, RightButtom.y - LeftTop.y + 1);
}

_RectI32 boundingRect(_ContourFinder::Contour *contour) {
	std::vector<_PointI32> &vp = contour->points;
	return boundingRect(vp);
}

bool PointInPic(_PointI32 p, int w, int h) {
	if (p.x < w &&p.x >= 0 && p.y < h && p.y >= 0)
		return true;
	else {
		return false;
	}
}





/*--------------------------------------------------convexHull--------------------------------------------------------*/
///https://github.com/tz28/ConvexHull/blob/master/convexhull.cpp

//注意一下这里不能被多线程调用
_PointI32 minP;

//计算叉积，小于0说明p1在p2的逆时针方向(右边)，即p0p1的极角大于p0p2的极角
float cross_product(_PointI32 p0, _PointI32 p1, _PointI32 p2) {
	return (p1.x - p0.x)*(p2.y - p0.y) - (p2.x - p0.x)*(p1.y - p0.y);
}

//计算距离
float dis(_PointI32 p1, _PointI32 p2) {
	return sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
}

bool com(const _PointI32 &p1, const _PointI32 &p2) {
	float temp = cross_product(minP, p1, p2);
	if (fabs(temp) < 1e-6) {//极角相等按照距离从小到大排序
		return dis(minP, p1) < dis(minP, p2);
	}
	else {
		return temp > 0;
	}
}

std::vector<_PointI32> convexHull_graham_scan(std::vector<_PointI32> &vp) {
	std::vector<_PointI32> ch;

	size_t n = vp.size();
	if (n < 3) {
		ch = vp;
		return ch;
	}

	int index = 0;
	for (size_t i = 1; i < n; ++i)//选出Y坐标最小的点，若Y坐标相等，选择X坐标小的点
	{
		if (vp[i].y < vp[index].y || (vp[i].y == vp[index].y && vp[i].x < vp[index].x)) {
			index = i;
		}
	}
	std::swap(vp[0], vp[index]);
	ch.push_back(vp[0]);


	//按极角排序
	minP = vp[0];
	std::sort(vp.begin(), vp.end(), com);
	ch.push_back(vp[1]);
	ch.push_back(vp[2]);


	int top = 2;
	for (size_t i = 3; i < n; ++i) {
		while (top > 0 && cross_product(ch[top - 1], vp[i], ch[top]) >= 0) {
			--top;
			ch.pop_back();
		}
		ch.push_back(vp[i]);
		++top;
	}
	return ch;
}
/*--------------------------------------------------convexHull--------------------------------------------------------*/


#define CHECK_AND_PUT_POINT_IN_LIST \
if (PointInPic(NextPoint, width,height) \
&& Map[NextPoint.y*width+ NextPoint.x] != 0 \
&& flag[NextPoint.y*width+ NextPoint.x] != 1 ){\
	flag[NextPoint.y*width+ NextPoint.x] = 1;\
	if (Mask[NextPoint.y*width+ NextPoint.x] == 0){\
		vpList.push_back(NextPoint);\
		RememberPList.push_back(NextPoint);\
	}\
	else{\
		color[Mask[NextPoint.y*width+ NextPoint.x]]++;\
	}\
}


inline float   idx2x(const unsigned int cx, float xMin, float resolution) { return xMin + (cx + 0.5f)*resolution; }
inline float   idx2y(const unsigned int cy, float yMin, float resolution) { return yMin + (cy + 0.5f)*resolution; }

//延长直线函数 P2->P1 向P1延长
void ExpendLine(std::pair<int, int>point2, std::pair<int, int>&point1, uint8_t *srcImage, int w, int h, int expendMin = 5, int MaxLen = 0) {

	//防止除0
	if (point1 == point2) return;

	if (abs(point1.first - point2.first) > (abs(point1.second - point2.second))) {

		if (MaxLen == 0)MaxLen = abs(point1.first - point2.first);

		float vx = point1.first - point2.first;
		float vy = point1.second - point2.second;
		float dy = vy / vx;
		int K = 0;
		if (vx > 0) {
			K = 1;
		}
		else {
			K = -1;
		}
		std::pair<int, int> nextPoint;
		for (int j = expendMin; j < MaxLen; j++) {
			nextPoint.first = point1.first + j * K;
			nextPoint.second = round(point1.second + dy * K*j);

			if (nextPoint.first < 0 || nextPoint.first >= w || nextPoint.second < 0 || nextPoint.second >= h)break;

			if (srcImage[nextPoint.second*w + nextPoint.first] < 128) {
				point1 = nextPoint;
				break;
			}
			else if (j == MaxLen - 1) {
				point1 = nextPoint;
				break;
			}
		}
	}
	else {
		if (MaxLen == 0)MaxLen = abs(point1.second - point2.second);

		float vx = point1.first - point2.first;
		float vy = point1.second - point2.second;
		float dx = vx / vy;
		int K = 0;
		if (vy > 0) {
			K = 1;
		}
		else {
			K = -1;
		}
		std::pair<int, int> nextPoint = point1;
		for (int j = expendMin; j < MaxLen; j++) {
			nextPoint.second = point1.second + j * K;
			nextPoint.first = round(point1.first + dx * K*j);

			if (nextPoint.first < 0 || nextPoint.first >= w || nextPoint.second < 0 || nextPoint.second >= h)break;


			if (srcImage[nextPoint.second*w + nextPoint.first] < 128) {
				point1 = nextPoint;
				break;
			}
			else if (j == MaxLen - 1) {
				point1 = nextPoint;
				break;
			}
		}
	}
}

#ifdef _WINDOWS
void ExpendLine(cv::Point point2, cv::Point &point1, cv::Mat & srcImage, int expendMin = 5, int MaxLen = 0) {

	std::pair<int, int> _point2(point2.x, point2.y);
	std::pair<int, int> _point1(point1.x, point1.y);
	uint8_t *_srcImage = srcImage.data;
	int w = srcImage.cols;
	int h = srcImage.rows;
	ExpendLine(_point2, _point1, _srcImage, w, h, expendMin, MaxLen);

	point1.x = _point1.first;
	point1.y = _point1.second;
}
#endif // _WINDOWS


//考虑对直线不准的情况
std::vector<std::vector<_PointI32>> MapCut_ErodeContourBox(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution) {

	//std::cout << "MapCut_ErodeContourBox in" << std::endl;

//	//测试
//#ifdef _WINDOWS
//	//cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\cleanpack3_all\\bin\\win_debug\\Debug\\savePic.bmp", 0);
//	cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\MapCut_ErodeContourBox.bmp", 0);
//	mapData = TestPic.data;
//	OrgW = TestPic.cols;
//	OrgH = TestPic.rows;
//#endif





	//保存图片用于查bug
#ifdef _WINDOWS
	//cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\cleanpack3_all\\bin\\win_debug\\Debug\\savePic.bmp", 0);
	cv::Mat imw = cv::Mat(OrgH, OrgW, CV_8U, mapData).clone();
	cv::imwrite("C:\\Users\\Tanhuan_work\\Desktop\\MapCut_ErodeContourBox.bmp", imw);
#endif


	int ROI_minX = OrgW, ROI_minY = OrgH, ROI_maxX = 0, ROI_maxY = 0;
	for (int y = 0; y < OrgH; y++) {
		unsigned char *p = mapData + y * OrgW;
		for (int x = 0; x < OrgW; x++) {
			if (abs(p[x] - 127) > 5) {
				//if (p[x] != 127) {
				if (x < ROI_minX) ROI_minX = x;
				if (y < ROI_minY) ROI_minY = y;
				if (x > ROI_maxX) ROI_maxX = x;
				if (y > ROI_maxY) ROI_maxY = y;
			}
		}
	}
	ROI_minX = std::max(0, ROI_minX - 5);
	ROI_minY = std::max(0, ROI_minY - 5);
	ROI_maxX = std::min(OrgW, ROI_maxX + 5);
	ROI_maxY = std::min(OrgH, ROI_maxY + 5);
	//传进来一个空地图的容错机制
	if (ROI_maxY < ROI_minY || ROI_maxX < ROI_minX) {
		std::vector<std::vector<_PointI32>> re;
		return re;
	}


	int width = ROI_maxX - ROI_minX + 1;
	int height = ROI_maxY - ROI_minY + 1;
	unsigned char *ORG = new unsigned char[width*height];
	for (int i = 0; i < height; i++) {
		memcpy(ORG + i * width, mapData + (i + ROI_minY)*OrgW + ROI_minX, width);
	}
#ifdef  _WINDOWS
	cv::Mat orgPicROI = cv::Mat(height, width, CV_8U, ORG);
#endif

	unsigned char *Map = new unsigned char[width*height];
	memcpy(Map, ORG, width*height);


	////_Hough
	//keymolen::Hough mHough;
	//mHough.Transform(Map, width/2, height/2, width, height);
	//std::vector<HoughLine> lines = mHough.GetLines(20,20);
	////删除斜向的直线
	//for (std::vector<HoughLine>::iterator it = lines.begin(); it != lines.end();) {
	//	cv::Point point1, point2;
	//	point1 = cv::Point((*it).PointA.first, (*it).PointA.second);
	//	point2 = cv::Point((*it).PointB.first, (*it).PointB.second);
	//	float k = atan2((point1.y - point2.y), (point1.x - point2.x));
	//	float d = CV_PI / 180 * 10;
	//	if (abs(k) < d
	//		|| abs(k - CV_PI / 2) < d
	//		|| abs(k + CV_PI / 2) < d
	//		|| abs(k - CV_PI) < d
	//		|| abs(k + CV_PI) < d
	//		) {
	//		it++;
	//	}
	//	else {
	//		it = lines.erase(it);
	//	}
	//}
	//mHough.addPointToLine(lines, Map, width, height, 3.0);
	//cv::Mat show = orgPicROI.clone();
	//cv::cvtColor(show, show, CV_GRAY2BGR);
	//for (size_t i = 0; i < lines.size(); i++) {
	//	cv::line(show,
	//		cv::Point(lines[i].PointA.first, lines[i].PointA.second),
	//		cv::Point(lines[i].PointB.first, lines[i].PointB.second),
	//		cv::Scalar(0, 0, 255));
	//	//float p1x = 0 + center_x;
	//	//float p1y = lines[i].HoughR / sin((float)lines[i].HoughT / 180 * CV_PI) + center_y;
	//	//float p2x = lines[i].HoughR / cos((float)lines[i].HoughT / 180 * CV_PI) + center_x;
	//	//float p2y = 0 + center_y;
	//	//cv::line(show,
	//	//	cv::Point(p1x, p1y),
	//	//	cv::Point(p2x, p2y),
	//	//	cv::Scalar(0, 255, 0));
	//	uint8_t b = rand() % 256, g = rand() % 256, r = rand() % 256;
	//	for (size_t j = 0; j < lines[i].VPoint.size(); j++) {
	//		show.at<cv::Vec3b>(lines[i].VPoint[j].y, lines[i].VPoint[j].x) = cv::Vec3b(b, g, r);
	//	}
	//}


	//threshold
	threshold(Map, width*height, 128);


	_ContourFinder mContourFinder;
	mContourFinder.Find((int8_t *)Map, width, height, width);
	int coutNum = mContourFinder.GetConturCount();//总连通域个数


	//get the max area contour
	_ContourFinder::Contour *MaxContour = NULL;
	_ContourFinder::Contour OrgMaxContour;
	double Area = -1;
	for (int i = 0; i < coutNum; i++) {
		double a = getContourArea((_ContourFinder::Contour *)mContourFinder.GetContour(i));
		if (a > Area) {
			Area = a;
			MaxContour = (_ContourFinder::Contour *)mContourFinder.GetContour(i);
		}
	}
	OrgMaxContour = *MaxContour;

	//draw the max contour
	memset(Map, 0, width*height);
	DrawContour_fill(MaxContour->points, Map, 255, width, height);


	//erode and get the rectangle	
	unsigned char *Map_2 = new unsigned char[width*height];//copy of image
	std::vector<_RectI32> VRect;
	int erosion_size = 1;
	int MaxContourIndex = -1;
	for (size_t i = 1; ; i++) {//i从0开始会漏掉最外面一圈

		if (MaxContourIndex == -1) {
			memset(Map_2, 255, width*height);
			erode(Map, Map_2, width, height);
		}
		else {//在已经找过一遍连通域的情况下，直接将轮廓涂黑会更快
			memcpy(Map_2, Map, width*height);
			size_t np = mContourFinder.GetContour(MaxContourIndex)->points.size();
			const std::vector<_PointI32> &lp = (mContourFinder.GetContour(MaxContourIndex)->points);
			for (size_t m = 0; m < np; m++) {
				Map_2[lp[m].y*width + lp[m].x] = 0;
			}
		}


#ifdef _WINDOWS
		cv::Mat dfad = cv::Mat(height, width, CV_8U, Map_2);
#endif

		mContourFinder.Find((int8_t *)Map_2, width, height, width);
		int coutNum = mContourFinder.GetConturCount();

		if (coutNum == 0) {
			break;
		}
		else if (coutNum == 1) {
			MaxContourIndex = 0;
			memcpy(Map, Map_2, width*height);

			//最大一个联通的域的面积小于一定值时，就把整个连通域划为一个矩形
			_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(0);
			double area = getContourArea(con);
			if (area < 500) {
				_RectI32 box = boundingRect(con);
				box.h += 2 * i * erosion_size;
				box.w += 2 * i * erosion_size;
				box.x -= 1 * i * erosion_size;
				box.y -= 1 * i * erosion_size;
				VRect.push_back(box);
				break;
			}
		}
		else {
			double MaxArea = -1;
			for (int j = 0; j < coutNum; j++) {
				_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
				double thisArea = getContourArea(con);
				if (thisArea > MaxArea) {
					MaxArea = thisArea;
					MaxContourIndex = j;
				}
			}


			for (int j = 0; j < coutNum; j++) {
				if (j != MaxContourIndex) {
					_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
					_RectI32 box = boundingRect(con);
					box.h += 2 * i * erosion_size;
					box.w += 2 * i * erosion_size;
					box.x -= 1 * i * erosion_size;
					box.y -= 1 * i * erosion_size;
					VRect.push_back(box);
				}
			}

			memset(Map, 0, width*height);
			DrawContour_fill(mContourFinder.GetContour(MaxContourIndex)->points, Map, 255, width, height);
		}

	}
	delete[]Map_2;
	memset(Map, 0, width*height);
	DrawContour_fill(OrgMaxContour.points, Map, 255, width, height);



#ifdef  _WINDOWS
	cv::Mat PIC1 = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(PIC1, PIC1, CV_GRAY2BGR);
	for (size_t i = 0; i < VRect.size(); i++) {
		cv::Rect temp(VRect[i].x, VRect[i].y, VRect[i].w, VRect[i].h);
		cv::rectangle(PIC1, temp, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));
	}
#endif


	////删除面积较小的矩形
	for (std::vector<_RectI32>::iterator it = VRect.begin(); it != VRect.end(); ) {
		if (it->w*it->h < 300)
			it = VRect.erase(it);
		else
			it++;
	}



	//解决矩形内包问题 
	for (std::vector<_RectI32>::iterator it1 = VRect.begin(); it1 < VRect.end();) {
		for (std::vector<_RectI32>::iterator it2 = VRect.begin(); it2 < VRect.end(); ) {
			if (it1 == it2) {
				it2++;
				continue;
			}


			if (it2->contains(it1->br()) && it2->contains(it1->tl())) {
				it1 = VRect.erase(it1);
				break;
			}
			else {
				it2++;
			}
		}
		it1++;
	}


	//解决矩形两个角在另一个矩形内部
	//如果一个矩形有两个点在另一个矩形内部，那么该矩形被分为两个部分，只保留外面的部分。
	for (std::vector<_RectI32>::iterator it1 = VRect.begin(); it1 < VRect.end();) {
		for (std::vector<_RectI32>::iterator it2 = VRect.begin(); it2 < VRect.end(); ) {
			if (it1 == it2) {
				it2++;
				continue;
			}

			int flag = 0;
			if (it1->contains(it2->tl())) {
				flag += 1;
			}
			if (it1->contains(it2->br())) {
				flag += 8;
			}
			if (it1->contains(it2->tr())) {
				flag += 2;
			}
			if (it1->contains(it2->bl())) {
				flag += 4;
			}

			//只处理flag=3 5 10 12的情况
			if (flag == 3) {
				it2->h = (it2->y + it2->h - 1) - (it1->y + it1->h - 1);
				it2->y = it1->y + it1->h + 1;
			}
			if (flag == 5) {
				it2->w = (it2->x + it2->w - 1) - (it1->x + it1->w - 1);
				it2->x = it1->x + it1->w - 1;
			}
			if (flag == 10) {
				it2->w = (it1->x - it2->x);
			}
			if (flag == 12) {
				it2->h = (it1->y - it2->y);
			}
			it2++;
		}
		it1++;
	}

	////删除面积较小的矩形
	for (std::vector<_RectI32>::iterator it = VRect.begin(); it != VRect.end(); ) {
		if (it->area() < 300)
			it = VRect.erase(it);
		else
			it++;
	}


#ifdef  _WINDOWS
	cv::Mat PIC2 = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(PIC2, PIC2, CV_GRAY2BGR);
	for (size_t i = 0; i < VRect.size(); i++) {
		cv::Rect temp(VRect[i].x, VRect[i].y, VRect[i].w, VRect[i].h);
		cv::rectangle(PIC2, temp, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));
	}
#endif


	//解决矩形只有一个角在另一个矩形内部
	//解决方式是，将公共区域一分为二，各占一边，变成五边形
	//有多少个连通域<连通域的上下左右四个角<每个角内的点>>>
	std::vector<std::vector<std::vector<_PointI32>>> RectContours(VRect.size());//逆时针存放点
	for (uint i = 0; i < VRect.size(); i++) {
		_RectI32 *it1 = &VRect[i];
		//存入原始的角点
		std::vector<_PointI32> vp(2);
		vp[0] = (it1->tl());
		vp[1] = (it1->tl());
		RectContours[i].push_back(vp);
		vp[0] = (it1->tr());
		vp[1] = (it1->tr());
		RectContours[i].push_back(vp);
		vp[0] = (it1->br());
		vp[1] = (it1->br());
		RectContours[i].push_back(vp);
		vp[0] = (it1->bl());
		vp[1] = (it1->bl());
		RectContours[i].push_back(vp);
	}
	for (uint i = 0; i < VRect.size(); i++) {
		_RectI32 *it1 = &VRect[i];
		for (uint j = i + 1; j < VRect.size(); j++) {
			_RectI32 *it2 = &VRect[j];

			int flag = 0;
			if (it1->contains(it2->tl())) {
				flag += 1;
			}
			if (it1->contains(it2->br())) {
				flag += 8;
			}
			if (it1->contains(it2->tr())) {
				flag += 2;
			}
			if (it1->contains(it2->bl())) {
				flag += 4;
			}

			if (flag == 0) //it1 it2不相交 并不意味着其他点不相交
			{

			}
			else if (flag == 1)//it2的左上角在it1内   
			{
				_PointI32 p1(it1->x + it1->w - 1, it2->y);
				_PointI32 p2(it2->x, it1->y + it1->h - 1);

				RectContours[i][2][0] = p1;
				RectContours[i][2][1] = p2;

				RectContours[j][0][0] = p2;
				RectContours[j][0][1] = p1;
			}
			else if (flag == 8) {
				_PointI32 p1(it2->x + it2->w - 1, it1->y);
				_PointI32 p2(it1->x, it2->y + it2->h - 1);

				RectContours[i][0][0] = p2;
				RectContours[i][0][1] = p1;

				RectContours[j][2][0] = p1;
				RectContours[j][2][1] = p2;
			}
			else if (flag == 2) {
				_PointI32 p1(it1->x, it2->y);
				_PointI32 p2(it2->x + it2->w - 1, it1->y + it1->h - 1);

				RectContours[i][3][0] = p2;
				RectContours[i][3][1] = p1;

				RectContours[j][1][0] = p1;
				RectContours[j][1][1] = p2;
			}
			else if (flag == 4) {
				_PointI32 p1(it2->x, it1->y);
				_PointI32 p2(it1->x + it1->w - 1, it2->y + it2->h - 1);

				RectContours[i][1][0] = p1;
				RectContours[i][1][1] = p2;

				RectContours[j][3][0] = p2;
				RectContours[j][3][1] = p1;
			}
		}
	}




	//变为标准的轮廓,用于绘制连通域
	std::vector<std::vector<_PointI32>> RectContoursResult;
	for (size_t i = 0; i < RectContours.size(); i++) {
		std::vector<_PointI32> vp;
		for (size_t j = 0; j < RectContours[i].size(); j++) {
			if (RectContours[i][j][0] == RectContours[i][j][1]) {
				vp.push_back(RectContours[i][j][0]);
			}
			else {
				vp.push_back(RectContours[i][j][0]);
				vp.push_back(RectContours[i][j][1]);
			}
		}
		RectContoursResult.push_back(vp);
	}


	//bug修复 
	//轮廓相交，在DrawContour_fill时会崩溃，应该将轮廓点再进行凸包运算
	for (size_t i = 0; i < RectContoursResult.size(); i++) {
		RectContoursResult[i] = convexHull_graham_scan(RectContoursResult[i]);
	}



	////删除面积较小的轮廓
	for (std::vector<std::vector<_PointI32>>::iterator it = RectContoursResult.begin(); it != RectContoursResult.end(); ) {
		if (getContourArea(*it) < 500)
			it = RectContoursResult.erase(it);
		else
			it++;
	}


#ifdef  _WINDOWS
	cv::Mat PIC3 = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(PIC3, PIC3, CV_GRAY2BGR);
	for (size_t i = 0; i < RectContoursResult.size(); i++) {
		cv::Scalar c = cv::Scalar(rand() % 256, rand() % 256, rand() % 256);
		for (size_t j = 0; j <= RectContoursResult[i].size(); j++) {
			cv::line(
				PIC3,
				cv::Point(RectContoursResult[i][j%RectContoursResult[i].size()].x, RectContoursResult[i][j%RectContoursResult[i].size()].y),
				cv::Point(RectContoursResult[i][(j + 1) % RectContoursResult[i].size()].x, RectContoursResult[i][(j + 1) % RectContoursResult[i].size()].y),
				c);
		}
	}
#endif


	////轮廓中的圈的白色区域不一定为一个连通域，需要筛选出来
	unsigned char *Mask = new unsigned char[width*height];
	memset(Mask, 0, width*height);
	for (size_t i = 0; i < RectContoursResult.size(); i++) {
		DrawContour_fill(RectContoursResult[i], Mask, i + 1, width, height);//这里有BUG，待查
	}
	for (int j = 0; j < width*height; j++) {
		if (Map[j] == 0) Mask[j] = 0;
	}


#ifdef  _WINDOWS
	cv::Mat PIC4 = cv::Mat(height, width, CV_8U, Mask).clone();
#endif

	//同一个Mask有可能划开了两个区域，要把小的区域置黑
	{
		unsigned char *temp = new unsigned char[width*height];
		for (size_t i = 0; i < RectContoursResult.size(); i++) {
			memcpy(temp, Mask, width*height);
			threshold(temp, width*height, i, i + 1);
			//cv::Mat aaa = cv::Mat(height, width, CV_8U, temp);

			mContourFinder.Find((int8_t *)temp, width, height, width);
			int coutNum = mContourFinder.GetConturCount();//总连通域个数
			if (coutNum > 1) {//这里有点问题，可能在矩形绘制的时候，绘制了一些独立的区域
				double MaxArea = -1;
				for (int j = 0; j < coutNum; j++) {
					_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
					double thisArea = getContourArea(con);
					if (thisArea > MaxArea) {
						MaxArea = thisArea;
						MaxContourIndex = j;
					}
				}

				for (int j = 0; j < coutNum; j++) {
					if (j == MaxContourIndex)continue;
					DrawContour_fill((mContourFinder.GetContour(j)->points), Mask, (unsigned char)0, (int)width, (int)height);
				}

			}
		}
		delete[] temp;
	}


#ifdef  _WINDOWS
	cv::Mat PIC5 = cv::Mat(height, width, CV_8U, Mask).clone();
#endif


	//将蒙版中不属于任意一个类的白点，归类到一个类中  广度优先搜索
	unsigned char *flag = new unsigned char[width*height];
	memset(flag, 0, width*height);
	std::vector<_PointI32> vpList;
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			if (Map[y*width + x] == 0) continue;
			if (Mask[y*width + x] != 0)continue;
			if (flag[y*width + x] == 1)continue;


			flag[y*width + x] = 1;
			vpList.push_back(_PointI32(x, y));

			std::vector<_PointI32> RememberPList;//存放该区域的点
			RememberPList.push_back(_PointI32(x, y));
			std::vector<int> color(RectContoursResult.size() + 1);//存放该区域与哪个区域的联通最多；

			while (vpList.size()) {
				_PointI32 LastPoint = vpList[vpList.size() - 1];
				vpList.pop_back();


				_PointI32 NextPoint;
				NextPoint = _PointI32(LastPoint.x + 1, LastPoint.y);
				CHECK_AND_PUT_POINT_IN_LIST;
				NextPoint = _PointI32(LastPoint.x - 1, LastPoint.y);
				CHECK_AND_PUT_POINT_IN_LIST;
				NextPoint = _PointI32(LastPoint.x, LastPoint.y + 1);
				CHECK_AND_PUT_POINT_IN_LIST;
				NextPoint = _PointI32(LastPoint.x, LastPoint.y - 1);
				CHECK_AND_PUT_POINT_IN_LIST;

				//NextPoint = Point(LastPoint.x + 1, LastPoint.y - 1);
				//CHECK_AND_PUT_POINT_IN_LIST;
				//NextPoint = Point(LastPoint.x + 1, LastPoint.y + 1);
				//CHECK_AND_PUT_POINT_IN_LIST;
				//NextPoint = Point(LastPoint.x - 1, LastPoint.y + 1);
				//CHECK_AND_PUT_POINT_IN_LIST;
				//NextPoint = Point(LastPoint.x - 1, LastPoint.y - 1);
				//CHECK_AND_PUT_POINT_IN_LIST;

			}
			//将该连通域内的点涂色
			int MaxColor = -1;
			int MaxColorIndex = -1;
			for (size_t i = 0; i < color.size(); i++) {
				if (color[i] > MaxColor) {
					MaxColor = color[i];
					MaxColorIndex = i;
				}
			}
			for (size_t i = 0; i < RememberPList.size(); i++) {
				Mask[RememberPList[i].y*width + RememberPList[i].x] = MaxColorIndex;
			}
		}
	}
	delete[]flag;


#ifdef  _WINDOWS
	cv::Mat PIC6 = cv::Mat(height, width, CV_8U, Mask).clone();
#endif


	//将Mask转轮廓vpList
	std::vector<std::vector<_PointI32>> Result;
	unsigned char *temp = new unsigned char[width*height];
	for (size_t i = 0; i < RectContoursResult.size(); i++) {
		memcpy(temp, Mask, width*height);
		threshold(temp, width*height, i, i + 1);

#ifdef _WINDOWS
		cv::Mat aaa = cv::Mat(height, width, CV_8U, temp);
#endif

		mContourFinder.Find((int8_t *)temp, width, height, width);
		int coutNum = mContourFinder.GetConturCount();//总连通域个数
		if (coutNum == 0) {
			continue;//这个地方就很奇怪了
		}
		else if (coutNum > 1) {//这里有点问题，可能在矩形绘制的时候，绘制了一些独立的区域(这个问题已经修复 coutNum只能为1)
			double MaxArea = -1;
			for (int j = 0; j < coutNum; j++) {
				_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
				double thisArea = getContourArea(con);
				if (thisArea > MaxArea) {
					MaxArea = thisArea;
					MaxContourIndex = j;
				}
			}
			Result.push_back(mContourFinder.GetContour(MaxContourIndex)->points);
		}
		else {
			Result.push_back(mContourFinder.GetFirstContour()->points);
		}

	}
	delete[] temp;



	////轮廓凸包
	////这样不能保证输出的点只有10个
	//std::vector<std::vector<_PointI32>> ConVList;
	//for (size_t i = 0; i < Result.size(); i++) {
	//	std::vector<_PointI32 > ConVList_t;
	//	convexHull(Result[i], ConVList_t);
	//	ConVList.push_back(ConVList_t);
	//}
	//Result = ConVList;



	////用最小的外接矩形，转换后的轮廓只有4个点
	std::vector<std::vector<_PointI32>> ConVList;
	for (size_t i = 0; i < Result.size(); i++) {
		std::vector<_PointI32 > ConVList_t;
		_RectI32 rt = boundingRect(Result[i]);
		ConVList_t.push_back(rt.tl());
		ConVList_t.push_back(rt.tr());
		ConVList_t.push_back(rt.br());
		ConVList_t.push_back(rt.bl());

		ConVList.push_back(ConVList_t);
	}
	Result = ConVList;


	////简化连通域数量
	//for (size_t i = 0; i < Result.size(); i++) {
	//	if (Result[i].size() < 10)continue;
	//	for (std::vector<_PointI32>::iterator it = Result[i].begin()+1; it != Result[i].end()-1;) {
	//		_PointI32 p1 = *(it - 1);
	//		_PointI32 p2 = *it;
	//		_PointI32 p3 = *(it + 1);
	//		int dx1 = p1.x - p2.x;
	//		int dy1 = p1.y - p2.y;
	//		int dx2 = p2.x - p3.x;
	//		int dy2 = p2.y - p3.y;
	//		if ((dx1 == 0 && dx2 == 0) || (dy1 == 0 && dy2 == 0)) {
	//			it = Result[i].erase(it);
	//		}
	//		else {
	//			it++;
	//		}
	//	}
	//	//首尾几个点就算了吧
	//	//再降采样一次吧；保证输出50个点的样子
	//	if (Result[i].size() < 50)continue;
	//	int skip = Result[i].size()/50;
	//	int n = 0;
	//	for (std::vector<_PointI32>::iterator it = Result[i].begin(); it < Result[i].end();) {
	//		n++;
	//		n = n % skip;
	//		if (n== 0) {
	//			it++;
	//		}
	//		else {
	//			it = Result[i].erase(it);
	//		}
	//	}
	//}


#ifdef  _WINDOWS
	cv::Mat PIC7 = cv::Mat(height, width, CV_8U, Mask).clone();
	cv::equalizeHist(PIC7, PIC7);
	cv::cvtColor(PIC7, PIC7, CV_GRAY2BGR);
	for (size_t i = 0; i < Result.size(); i++) {
		cv::Scalar c = cv::Scalar(rand() % 256, rand() % 256, rand() % 256);
		for (size_t j = 0; j <= Result[i].size(); j++) {
			cv::line(
				PIC7,
				cv::Point(Result[i][j%Result[i].size()].x, Result[i][j%Result[i].size()].y),
				cv::Point(Result[i][(j + 1) % Result[i].size()].x, Result[i][(j + 1) % Result[i].size()].y),
				c);
		}
	}
#endif

#ifdef _WINDOWS
	{
		cv::Mat show = cv::Mat(height, width, CV_8U, ORG).clone();
		cv::cvtColor(show, show, CV_GRAY2BGR);
		for (size_t i = 0; i < Result.size(); i++) {
			cv::Rect re = cv::Rect(cv::Point(Result[i][0].x, Result[i][0].y), cv::Point(Result[i][2].x, Result[i][2].y));
			cv::rectangle(show, re, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));
		}
		cv::imshow("MapCut_ErodeContourBox", show);
		cv::waitKey(10);
	}
#endif


	//转到ROI之前的坐标
	for (size_t i = 0; i < Result.size(); i++) {
		for (size_t j = 0; j < Result[i].size(); j++) {
			Result[i][j].x += ROI_minX;
			Result[i][j].y += ROI_minY;
		}
	}


	//从像素坐标转到世界坐标系 单位mm
	for (size_t i = 0; i < Result.size(); i++) {
		for (size_t j = 0; j < Result[i].size(); j++) {
			Result[i][j].x = idx2x(Result[i][j].x, xMin, resolution) * 1000;
			Result[i][j].y = idx2x(Result[i][j].y, yMin, resolution) * 1000;
		}
	}



	////按矩形面积大小排序
	//////Draw Rect;
	////cv::Mat re;
	////cv::cvtColor(src, re, CV_GRAY2BGR);
	////for (size_t i = 0; i < VRect.size(); i++)
	////{
	////	cv::rectangle(re, VRect[i], Scalar(rand() % 256, rand() % 256, rand() % 256));
	////}
	////cv::Mat Mask = src.clone();
	////cv::threshold(Mask, Mask, 128, 1, THRESH_BINARY);
	////for (size_t i = 0; i < VRect.size(); i++)
	////{
	////	int x0 = VRect[i].x;
	////	int y0 = VRect[i].y;
	////	int w = VRect[i].width;
	////	int h = VRect[i].height;
	////	for (int x = x0; x < x0 + w; x++)
	////	{
	////		for (int y = y0; y < y0 + h; y++)
	////		{
	////			if (Mask.at<uint8_t>(y, x) == 1)
	////				Mask.at<uint8_t>(y, x) = i + 1;
	////		}
	////	}
	////}


	delete[] ORG;
	delete[] Map;
	delete[] Mask;
	return Result;
}


//考虑对直线不准的情况,未完成
#ifdef _WINDOWS
std::vector<std::vector<_PointI32>> erode_contour_box_c(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution) {


	//测试
#ifdef _WINDOWS
	cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\cleanpack3_all\\bin\\win_debug\\Debug\\savePic.bmp", 0);
	mapData = TestPic.data;
	OrgW = TestPic.cols;
	OrgH = TestPic.rows;
#endif


	int ROI_minX = OrgW, ROI_minY = OrgH, ROI_maxX = 0, ROI_maxY = 0;
	for (int y = 0; y < OrgH; y++) {
		unsigned char *p = mapData + y * OrgW;
		for (int x = 0; x < OrgW; x++) {
			if (abs(p[x] - 127) > 5) {
				//if (p[x] != 127) {
				if (x < ROI_minX) ROI_minX = x;
				if (y < ROI_minY) ROI_minY = y;
				if (x > ROI_maxX) ROI_maxX = x;
				if (y > ROI_maxY) ROI_maxY = y;
			}
		}
	}
	ROI_minX = std::max(0, ROI_minX - 5);
	ROI_minY = std::max(0, ROI_minY - 5);
	ROI_maxX = std::min(OrgW, ROI_maxX + 5);
	ROI_maxY = std::min(OrgH, ROI_maxY + 5);
	//传进来一个空地图的容错机制
	if (ROI_maxY < ROI_minY || ROI_maxX < ROI_minX) {
		std::vector<std::vector<_PointI32>> re;
		return re;
	}


	int width = ROI_maxX - ROI_minX + 1;
	int height = ROI_maxY - ROI_minY + 1;
	unsigned char *Map = new unsigned char[width*height];
	for (int i = 0; i < height; i++) {
		memcpy(Map + i * width, mapData + (i + ROI_minY)*OrgW + ROI_minX, width);
	}
#ifdef  _WINDOWS
	cv::Mat orgPicROI = cv::Mat(height, width, CV_8U, Map);
#endif

	//threshold
	threshold(Map, width*height, 128);


	_ContourFinder mContourFinder;
	mContourFinder.Find((int8_t *)Map, width, height, width);
	int coutNum = mContourFinder.GetConturCount();//总连通域个数


	//get the max area contour
	_ContourFinder::Contour *MaxContour = NULL;
	_ContourFinder::Contour OrgMaxContour;
	double Area = -1;
	for (int i = 0; i < coutNum; i++) {
		double a = getContourArea((_ContourFinder::Contour *)mContourFinder.GetContour(i));
		if (a > Area) {
			Area = a;
			MaxContour = (_ContourFinder::Contour *)mContourFinder.GetContour(i);
		}
	}
	OrgMaxContour = *MaxContour;

	//draw the max contour
	memset(Map, 0, width*height);
	DrawContour_fill(MaxContour->points, Map, 255, width, height);


	//erode and get the rectangle	
	unsigned char *Map_2 = new unsigned char[width*height];//copy of image
	std::vector<std::vector<_PointI32>> VContour;
	int erosion_size = 1;
	int MaxContourIndex = -1;
	for (size_t i = 1; ; i++) {//i从0开始会漏掉最外面一圈

		if (MaxContourIndex == -1) {
			memset(Map_2, 255, width*height);
			erode(Map, Map_2, width, height);
		}
		else {//在已经找过一遍连通域的情况下，直接将轮廓涂黑会更快
			memcpy(Map_2, Map, width*height);
			size_t np = mContourFinder.GetContour(MaxContourIndex)->points.size();
			const std::vector<_PointI32> &lp = (mContourFinder.GetContour(MaxContourIndex)->points);
			for (size_t m = 0; m < np; m++) {
				Map_2[lp[m].y*width + lp[m].x] = 0;
			}
		}


#ifdef _WINDOWS
		cv::Mat dfad = cv::Mat(height, width, CV_8U, Map_2);
#endif

		mContourFinder.Find((int8_t *)Map_2, width, height, width);
		int coutNum = mContourFinder.GetConturCount();

		if (coutNum == 0) {
			break;
		}
		else if (coutNum == 1) {
			MaxContourIndex = 0;
			memcpy(Map, Map_2, width*height);

			//最大一个联通的域的面积小于一定值时，就把整个连通域划为一个矩形
			_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(0);
			double area = getContourArea(con);
			if (area < 500) {

				unsigned char *sMap = new unsigned char[width*height];//copy of image
				memset(sMap, 0, width*height);
				DrawContour_fill(con->points, sMap, 255, width, height);
				for (size_t k = 0; k < i; k++) {
					unsigned char *t = new unsigned char[width*height];//copy of image
					memset(t, 0, width*height);
					dilate(sMap, t, width, height);
					memcpy(sMap, t, width*height);
					delete[]t;
				}
				_ContourFinder sContourFinder;
				sContourFinder.Find((int8_t *)sMap, width, height, width);
				delete[]sMap;

				VContour.push_back(sContourFinder.GetFirstContour()->points);
				break;
			}
		}
		else {
			double MaxArea = -1;
			for (int j = 0; j < coutNum; j++) {
				_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
				double thisArea = getContourArea(con);
				if (thisArea > MaxArea) {
					MaxArea = thisArea;
					MaxContourIndex = j;
				}
			}


			for (int j = 0; j < coutNum; j++) {
				if (j != MaxContourIndex) {
					_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);

					unsigned char *sMap = new unsigned char[width*height];//copy of image
					memset(sMap, 0, width*height);
					DrawContour_fill(con->points, sMap, 255, width, height);

					cv::Mat ssss = cv::Mat(height, width, CV_8U, sMap);

					for (size_t k = 0; k < i; k++) {
						unsigned char *t = new unsigned char[width*height];//copy of image
						memset(t, 0, width*height);
						dilate(sMap, t, width, height);
						memcpy(sMap, t, width*height);
						delete[]t;
					}
					_ContourFinder sContourFinder;
					sContourFinder.Find((int8_t *)sMap, width, height, width);
					delete[]sMap;

					VContour.push_back(sContourFinder.GetFirstContour()->points);
				}
			}

			memset(Map, 0, width*height);
			DrawContour_fill(mContourFinder.GetContour(MaxContourIndex)->points, Map, 255, width, height);
		}

	}
	delete[]Map_2;
	memset(Map, 0, width*height);
	DrawContour_fill(OrgMaxContour.points, Map, 255, width, height);



#ifdef  _WINDOWS
	cv::Mat PIC1 = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(PIC1, PIC1, CV_GRAY2BGR);
	for (size_t i = 0; i < VContour.size(); i++) {
		cv::Scalar c = cv::Scalar(rand() % 256, rand() % 256, rand() % 256);
		for (size_t j = 0; j < VContour[i].size(); j++) {
			cv::line(PIC1,
				cv::Point(VContour[i][j].x, VContour[i][j].y),
				cv::Point(VContour[i][(j + 1) % VContour[i].size()].x, VContour[i][(j + 1) % VContour[i].size()].y),
				c);
		}
	}
#endif


	//	////删除面积较小的矩形
	//	for (std::vector<_RectI32>::iterator it = VRect.begin(); it != VRect.end(); ) {
	//		if (it->w*it->h < 300)
	//			it = VRect.erase(it);
	//		else
	//			it++;
	//	}
	//
	//
	//
	//	//解决矩形内包问题 
	//	for (std::vector<_RectI32>::iterator it1 = VRect.begin(); it1 < VRect.end();) {
	//		for (std::vector<_RectI32>::iterator it2 = VRect.begin(); it2 < VRect.end(); ) {
	//			if (it1 == it2) {
	//				it2++;
	//				continue;
	//			}
	//
	//
	//			if (it2->contains(it1->br()) && it2->contains(it1->tl())) {
	//				it1 = VRect.erase(it1);
	//				break;
	//			}
	//			else {
	//				it2++;
	//			}
	//		}
	//		it1++;
	//	}
	//
	//
	//	//解决矩形两个角在另一个矩形内部
	//	//如果一个矩形有两个点在另一个矩形内部，那么该矩形被分为两个部分，只保留外面的部分。
	//	for (std::vector<_RectI32>::iterator it1 = VRect.begin(); it1 < VRect.end();) {
	//		for (std::vector<_RectI32>::iterator it2 = VRect.begin(); it2 < VRect.end(); ) {
	//			if (it1 == it2) {
	//				it2++;
	//				continue;
	//			}
	//
	//			int flag = 0;
	//			if (it1->contains(it2->tl())) {
	//				flag += 1;
	//			}
	//			if (it1->contains(it2->br())) {
	//				flag += 8;
	//			}
	//			if (it1->contains(it2->tr())) {
	//				flag += 2;
	//			}
	//			if (it1->contains(it2->bl())) {
	//				flag += 4;
	//			}
	//
	//			//只处理flag=3 5 10 12的情况
	//			if (flag == 3) {
	//				it2->h = (it2->y + it2->h - 1) - (it1->y + it1->h - 1);
	//				it2->y = it1->y + it1->h + 1;
	//			}
	//			if (flag == 5) {
	//				it2->w = (it2->x + it2->w - 1) - (it1->x + it1->w - 1);
	//				it2->x = it1->x + it1->w - 1;
	//			}
	//			if (flag == 10) {
	//				it2->w = (it1->x - it2->x);
	//			}
	//			if (flag == 12) {
	//				it2->h = (it1->y - it2->y);
	//			}
	//			it2++;
	//		}
	//		it1++;
	//	}
	//
	//	////删除面积较小的矩形
	//	for (std::vector<_RectI32>::iterator it = VRect.begin(); it != VRect.end(); ) {
	//		if (it->area() < 300)
	//			it = VRect.erase(it);
	//		else
	//			it++;
	//	}
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC2 = cv::Mat(height, width, CV_8U, Map).clone();
	//	cv::cvtColor(PIC2, PIC2, CV_GRAY2BGR);
	//	for (size_t i = 0; i < VRect.size(); i++) {
	//		cv::Rect temp(VRect[i].x, VRect[i].y, VRect[i].w, VRect[i].h);
	//		cv::rectangle(PIC2, temp, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));
	//	}
	//#endif
	//
	//
	//	//解决矩形只有一个角在另一个矩形内部
	//	//解决方式是，将公共区域一分为二，各占一边，变成五边形
	//	//有多少个连通域<连通域的上下左右四个角<每个角内的点>>>
	//	std::vector<std::vector<std::vector<_PointI32>>> RectContours(VRect.size());//逆时针存放点
	//	for (uint i = 0; i < VRect.size(); i++) {
	//		_RectI32 *it1 = &VRect[i];
	//		//存入原始的角点
	//		std::vector<_PointI32> vp(2);
	//		vp[0] = (it1->tl());
	//		vp[1] = (it1->tl());
	//		RectContours[i].push_back(vp);
	//		vp[0] = (it1->tr());
	//		vp[1] = (it1->tr());
	//		RectContours[i].push_back(vp);
	//		vp[0] = (it1->br());
	//		vp[1] = (it1->br());
	//		RectContours[i].push_back(vp);
	//		vp[0] = (it1->bl());
	//		vp[1] = (it1->bl());
	//		RectContours[i].push_back(vp);
	//	}
	//	for (uint i = 0; i < VRect.size(); i++) {
	//		_RectI32 *it1 = &VRect[i];
	//		for (uint j = i + 1; j < VRect.size(); j++) {
	//			_RectI32 *it2 = &VRect[j];
	//
	//			int flag = 0;
	//			if (it1->contains(it2->tl())) {
	//				flag += 1;
	//			}
	//			if (it1->contains(it2->br())) {
	//				flag += 8;
	//			}
	//			if (it1->contains(it2->tr())) {
	//				flag += 2;
	//			}
	//			if (it1->contains(it2->bl())) {
	//				flag += 4;
	//			}
	//
	//			if (flag == 0) //it1 it2不相交 并不意味着其他点不相交
	//			{
	//
	//			}
	//			else if (flag == 1)//it2的左上角在it1内   
	//			{
	//				_PointI32 p1(it1->x + it1->w - 1, it2->y);
	//				_PointI32 p2(it2->x, it1->y + it1->h - 1);
	//
	//				RectContours[i][2][0] = p1;
	//				RectContours[i][2][1] = p2;
	//
	//				RectContours[j][0][0] = p2;
	//				RectContours[j][0][1] = p1;
	//			}
	//			else if (flag == 8) {
	//				_PointI32 p1(it2->x + it2->w - 1, it1->y);
	//				_PointI32 p2(it1->x, it2->y + it2->h - 1);
	//
	//				RectContours[i][0][0] = p2;
	//				RectContours[i][0][1] = p1;
	//
	//				RectContours[j][2][0] = p1;
	//				RectContours[j][2][1] = p2;
	//			}
	//			else if (flag == 2) {
	//				_PointI32 p1(it1->x, it2->y);
	//				_PointI32 p2(it2->x + it2->w - 1, it1->y + it1->h - 1);
	//
	//				RectContours[i][3][0] = p2;
	//				RectContours[i][3][1] = p1;
	//
	//				RectContours[j][1][0] = p1;
	//				RectContours[j][1][1] = p2;
	//			}
	//			else if (flag == 4) {
	//				_PointI32 p1(it2->x, it1->y);
	//				_PointI32 p2(it1->x + it1->w - 1, it2->y + it2->h - 1);
	//
	//				RectContours[i][1][0] = p1;
	//				RectContours[i][1][1] = p2;
	//
	//				RectContours[j][3][0] = p2;
	//				RectContours[j][3][1] = p1;
	//			}
	//		}
	//	}
	//
	//
	//
	//
	//	//变为标准的轮廓,用于绘制连通域
	//	std::vector<std::vector<_PointI32>> RectContoursResult;
	//	for (size_t i = 0; i < RectContours.size(); i++) {
	//		std::vector<_PointI32> vp;
	//		for (size_t j = 0; j < RectContours[i].size(); j++) {
	//			if (RectContours[i][j][0] == RectContours[i][j][1]) {
	//				vp.push_back(RectContours[i][j][0]);
	//			}
	//			else {
	//				vp.push_back(RectContours[i][j][0]);
	//				vp.push_back(RectContours[i][j][1]);
	//			}
	//		}
	//		RectContoursResult.push_back(vp);
	//	}
	//
	//
	//	//bug修复 
	//	//轮廓相交，在DrawContour_fill时会崩溃，应该将轮廓点再进行凸包运算
	//	for (size_t i = 0; i < RectContoursResult.size(); i++) {
	//		RectContoursResult[i] = convexHull_graham_scan(RectContoursResult[i]);
	//	}
	//
	//
	//
	//	////删除面积较小的轮廓
	//	for (std::vector<std::vector<_PointI32>>::iterator it = RectContoursResult.begin(); it != RectContoursResult.end(); ) {
	//		if (getContourArea(*it) < 500)
	//			it = RectContoursResult.erase(it);
	//		else
	//			it++;
	//	}
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC3 = cv::Mat(height, width, CV_8U, Map).clone();
	//	cv::cvtColor(PIC3, PIC3, CV_GRAY2BGR);
	//	for (size_t i = 0; i < RectContoursResult.size(); i++) {
	//		cv::Scalar c = cv::Scalar(rand() % 256, rand() % 256, rand() % 256);
	//		for (size_t j = 0; j <= RectContoursResult[i].size(); j++) {
	//			cv::line(
	//				PIC3,
	//				cv::Point(RectContoursResult[i][j%RectContoursResult[i].size()].x, RectContoursResult[i][j%RectContoursResult[i].size()].y),
	//				cv::Point(RectContoursResult[i][(j + 1) % RectContoursResult[i].size()].x, RectContoursResult[i][(j + 1) % RectContoursResult[i].size()].y),
	//				c);
	//		}
	//	}
	//#endif
	//
	//
	//	////轮廓中的圈的白色区域不一定为一个连通域，需要筛选出来
	//	unsigned char *Mask = new unsigned char[width*height];
	//	memset(Mask, 0, width*height);
	//	for (size_t i = 0; i < RectContoursResult.size(); i++) {
	//		DrawContour_fill(RectContoursResult[i], Mask, i + 1, width, height);//这里有BUG，待查
	//	}
	//	for (int j = 0; j < width*height; j++) {
	//		if (Map[j] == 0) Mask[j] = 0;
	//	}
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC4 = cv::Mat(height, width, CV_8U, Mask).clone();
	//#endif
	//
	//	//同一个Mask有可能划开了两个区域，要把小的区域置黑
	//	{
	//		unsigned char *temp = new unsigned char[width*height];
	//		for (size_t i = 0; i < RectContoursResult.size(); i++) {
	//			memcpy(temp, Mask, width*height);
	//			threshold(temp, width*height, i, i + 1);
	//			//cv::Mat aaa = cv::Mat(height, width, CV_8U, temp);
	//
	//			mContourFinder.Find((int8_t *)temp, width, height, width);
	//			int coutNum = mContourFinder.GetConturCount();//总连通域个数
	//			if (coutNum > 1) {//这里有点问题，可能在矩形绘制的时候，绘制了一些独立的区域
	//				double MaxArea = -1;
	//				for (int j = 0; j < coutNum; j++) {
	//					_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
	//					double thisArea = getContourArea(con);
	//					if (thisArea > MaxArea) {
	//						MaxArea = thisArea;
	//						MaxContourIndex = j;
	//					}
	//				}
	//
	//				for (int j = 0; j < coutNum; j++) {
	//					if (j == MaxContourIndex)continue;
	//					DrawContour_fill((mContourFinder.GetContour(j)->points), Mask, (unsigned char)0, (int)width, (int)height);
	//				}
	//
	//			}
	//		}
	//		delete[] temp;
	//	}
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC5 = cv::Mat(height, width, CV_8U, Mask).clone();
	//#endif
	//
	//
	//	//将蒙版中不属于任意一个类的白点，归类到一个类中  广度优先搜索
	//	unsigned char *flag = new unsigned char[width*height];
	//	memset(flag, 0, width*height);
	//	std::vector<_PointI32> vpList;
	//	for (int y = 0; y < height; y++) {
	//		for (int x = 0; x < width; x++) {
	//			if (Map[y*width + x] == 0) continue;
	//			if (Mask[y*width + x] != 0)continue;
	//			if (flag[y*width + x] == 1)continue;
	//
	//
	//			flag[y*width + x] = 1;
	//			vpList.push_back(_PointI32(x, y));
	//
	//			std::vector<_PointI32> RememberPList;//存放该区域的点
	//			RememberPList.push_back(_PointI32(x, y));
	//			std::vector<int> color(RectContoursResult.size() + 1);//存放该区域与哪个区域的联通最多；
	//
	//			while (vpList.size()) {
	//				_PointI32 LastPoint = vpList[vpList.size() - 1];
	//				vpList.pop_back();
	//
	//
	//				_PointI32 NextPoint;
	//				NextPoint = _PointI32(LastPoint.x + 1, LastPoint.y);
	//				CHECK_AND_PUT_POINT_IN_LIST;
	//				NextPoint = _PointI32(LastPoint.x - 1, LastPoint.y);
	//				CHECK_AND_PUT_POINT_IN_LIST;
	//				NextPoint = _PointI32(LastPoint.x, LastPoint.y + 1);
	//				CHECK_AND_PUT_POINT_IN_LIST;
	//				NextPoint = _PointI32(LastPoint.x, LastPoint.y - 1);
	//				CHECK_AND_PUT_POINT_IN_LIST;
	//
	//				//NextPoint = Point(LastPoint.x + 1, LastPoint.y - 1);
	//				//CHECK_AND_PUT_POINT_IN_LIST;
	//				//NextPoint = Point(LastPoint.x + 1, LastPoint.y + 1);
	//				//CHECK_AND_PUT_POINT_IN_LIST;
	//				//NextPoint = Point(LastPoint.x - 1, LastPoint.y + 1);
	//				//CHECK_AND_PUT_POINT_IN_LIST;
	//				//NextPoint = Point(LastPoint.x - 1, LastPoint.y - 1);
	//				//CHECK_AND_PUT_POINT_IN_LIST;
	//
	//			}
	//			//将该连通域内的点涂色
	//			int MaxColor = -1;
	//			int MaxColorIndex = -1;
	//			for (size_t i = 0; i < color.size(); i++) {
	//				if (color[i] > MaxColor) {
	//					MaxColor = color[i];
	//					MaxColorIndex = i;
	//				}
	//			}
	//			for (size_t i = 0; i < RememberPList.size(); i++) {
	//				Mask[RememberPList[i].y*width + RememberPList[i].x] = MaxColorIndex;
	//			}
	//		}
	//	}
	//	delete[]flag;
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC6 = cv::Mat(height, width, CV_8U, Mask).clone();
	//#endif
	//
	//
	//	//将Mask转轮廓vpList
	//	std::vector<std::vector<_PointI32>> Result;
	//	unsigned char *temp = new unsigned char[width*height];
	//	for (size_t i = 0; i < RectContoursResult.size(); i++) {
	//		memcpy(temp, Mask, width*height);
	//		threshold(temp, width*height, i, i + 1);
	//
	//#ifdef _WINDOWS
	//		cv::Mat aaa = cv::Mat(height, width, CV_8U, temp);
	//#endif
	//
	//		mContourFinder.Find((int8_t *)temp, width, height, width);
	//		int coutNum = mContourFinder.GetConturCount();//总连通域个数
	//		if (coutNum == 0) {
	//			continue;//这个地方就很奇怪了
	//		}
	//		else if (coutNum > 1) {//这里有点问题，可能在矩形绘制的时候，绘制了一些独立的区域(这个问题已经修复 coutNum只能为1)
	//			double MaxArea = -1;
	//			for (int j = 0; j < coutNum; j++) {
	//				_ContourFinder::Contour * con = (_ContourFinder::Contour *)mContourFinder.GetContour(j);
	//				double thisArea = getContourArea(con);
	//				if (thisArea > MaxArea) {
	//					MaxArea = thisArea;
	//					MaxContourIndex = j;
	//				}
	//			}
	//			Result.push_back(mContourFinder.GetContour(MaxContourIndex)->points);
	//		}
	//		else {
	//			Result.push_back(mContourFinder.GetFirstContour()->points);
	//		}
	//
	//	}
	//	delete[] temp;
	//
	//
	//
	//	////轮廓凸包
	//	////这样不能保证输出的点只有10个
	//	//std::vector<std::vector<_PointI32>> ConVList;
	//	//for (size_t i = 0; i < Result.size(); i++) {
	//	//	std::vector<_PointI32 > ConVList_t;
	//	//	convexHull(Result[i], ConVList_t);
	//	//	ConVList.push_back(ConVList_t);
	//	//}
	//	//Result = ConVList;
	//
	//
	//
	//	////用最小的外接矩形，转换后的轮廓只有4个点
	//	std::vector<std::vector<_PointI32>> ConVList;
	//	for (size_t i = 0; i < Result.size(); i++) {
	//		std::vector<_PointI32 > ConVList_t;
	//		_RectI32 rt = boundingRect(Result[i]);
	//		ConVList_t.push_back(rt.tl());
	//		ConVList_t.push_back(rt.tr());
	//		ConVList_t.push_back(rt.br());
	//		ConVList_t.push_back(rt.bl());
	//
	//		ConVList.push_back(ConVList_t);
	//	}
	//	Result = ConVList;
	//
	//
	//	////简化连通域数量
	//	//for (size_t i = 0; i < Result.size(); i++) {
	//	//	if (Result[i].size() < 10)continue;
	//	//	for (std::vector<_PointI32>::iterator it = Result[i].begin()+1; it != Result[i].end()-1;) {
	//	//		_PointI32 p1 = *(it - 1);
	//	//		_PointI32 p2 = *it;
	//	//		_PointI32 p3 = *(it + 1);
	//	//		int dx1 = p1.x - p2.x;
	//	//		int dy1 = p1.y - p2.y;
	//	//		int dx2 = p2.x - p3.x;
	//	//		int dy2 = p2.y - p3.y;
	//	//		if ((dx1 == 0 && dx2 == 0) || (dy1 == 0 && dy2 == 0)) {
	//	//			it = Result[i].erase(it);
	//	//		}
	//	//		else {
	//	//			it++;
	//	//		}
	//	//	}
	//	//	//首尾几个点就算了吧
	//	//	//再降采样一次吧；保证输出50个点的样子
	//	//	if (Result[i].size() < 50)continue;
	//	//	int skip = Result[i].size()/50;
	//	//	int n = 0;
	//	//	for (std::vector<_PointI32>::iterator it = Result[i].begin(); it < Result[i].end();) {
	//	//		n++;
	//	//		n = n % skip;
	//	//		if (n== 0) {
	//	//			it++;
	//	//		}
	//	//		else {
	//	//			it = Result[i].erase(it);
	//	//		}
	//	//	}
	//	//}
	//
	//
	//#ifdef  _WINDOWS
	//	cv::Mat PIC7 = cv::Mat(height, width, CV_8U, Mask).clone();
	//	cv::equalizeHist(PIC7, PIC7);
	//	cv::cvtColor(PIC7, PIC7, CV_GRAY2BGR);
	//	for (size_t i = 0; i < Result.size(); i++) {
	//		cv::Scalar c = cv::Scalar(rand() % 256, rand() % 256, rand() % 256);
	//		for (size_t j = 0; j <= Result[i].size(); j++) {
	//			cv::line(
	//				PIC7,
	//				cv::Point(Result[i][j%Result[i].size()].x, Result[i][j%Result[i].size()].y),
	//				cv::Point(Result[i][(j + 1) % Result[i].size()].x, Result[i][(j + 1) % Result[i].size()].y),
	//				c);
	//		}
	//	}
	//#endif
	//
	//
	//
	//
	//	//转到ROI之前的坐标
	//	for (size_t i = 0; i < Result.size(); i++) {
	//		for (size_t j = 0; j < Result[i].size(); j++) {
	//			Result[i][j].x += ROI_minX;
	//			Result[i][j].y += ROI_minY;
	//		}
	//	}
	//
	//
	//	//从像素坐标转到世界坐标系 单位mm
	//	for (size_t i = 0; i < Result.size(); i++) {
	//		for (size_t j = 0; j < Result[i].size(); j++) {
	//			Result[i][j].x = idx2x(Result[i][j].x, xMin, resolution) * 1000;
	//			Result[i][j].y = idx2x(Result[i][j].y, yMin, resolution) * 1000;
	//		}
	//	}
	//
	//
	//
	//	////按矩形面积大小排序
	//	//////Draw Rect;
	//	////cv::Mat re;
	//	////cv::cvtColor(src, re, CV_GRAY2BGR);
	//	////for (size_t i = 0; i < VRect.size(); i++)
	//	////{
	//	////	cv::rectangle(re, VRect[i], Scalar(rand() % 256, rand() % 256, rand() % 256));
	//	////}
	//	////cv::Mat Mask = src.clone();
	//	////cv::threshold(Mask, Mask, 128, 1, THRESH_BINARY);
	//	////for (size_t i = 0; i < VRect.size(); i++)
	//	////{
	//	////	int x0 = VRect[i].x;
	//	////	int y0 = VRect[i].y;
	//	////	int w = VRect[i].width;
	//	////	int h = VRect[i].height;
	//	////	for (int x = x0; x < x0 + w; x++)
	//	////	{
	//	////		for (int y = y0; y < y0 + h; y++)
	//	////		{
	//	////			if (Mask.at<uint8_t>(y, x) == 1)
	//	////				Mask.at<uint8_t>(y, x) = i + 1;
	//	////		}
	//	////	}
	//	////}
	//
	//
	//
	//	delete[] Map;
	//	delete[] Mask;


	std::vector<std::vector<_PointI32>>Result;
	return Result;
}
#endif // _WINDOWS


std::vector<std::vector<_PointI32>> MapCut_Hough(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution) {

	//使用Opencv修改
#ifdef _WINDOWS
    LOGE("MapCut_Hough====== ===01");
	if (0) {
	     LOGE("MapCut_Hough====== ===1");
		cv::Mat ORG = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\MapCutTestPic\\685.bmp", 0);
		int height = ORG.rows;
		int width = ORG.cols;

		cv::Mat BinPic;
		cv::threshold(ORG, BinPic, 130, 255, cv::THRESH_BINARY);


		int g_CannyThred = 150, g_CannyP = 0, g_CannySize = 0, g_HoughThred = 20, g_HoughThick = 0;
		int g_nWay = 0;
		int minLineLength = 5;
		int maxLineGap = 2;
		std::vector<cv::Vec4i> lines;

		cv::Mat cannyImage;
		Canny(BinPic, cannyImage, (double)g_CannyThred, (double)((g_CannyThred + 1) * (2 + g_CannyP / 100.0)), 3);
		HoughLinesP(cannyImage, lines, 1, CV_PI / 180, g_HoughThred + 1, minLineLength, maxLineGap);


		//删除斜向的直线
		for (std::vector<cv::Vec4i>::iterator it = lines.begin(); it != lines.end();) {
			cv::Point point1, point2;
			point1 = cv::Point((*it)[0], (*it)[1]);
			point2 = cv::Point((*it)[2], (*it)[3]);
			float k = atan2((point1.y - point2.y), (point1.x - point2.x));

			float d = CV_PI / 180 * 10;
			if (abs(k) < d
				|| abs(k - CV_PI / 2) < d
				|| abs(k + CV_PI / 2) < d
				|| abs(k - CV_PI) < d
				|| abs(k + CV_PI) < d
				) {
				it++;
			}
			else {
				it = lines.erase(it);
			}
		}


		//HoughLinesP检测出来的直线延长
		for (size_t i = 0; i < lines.size(); i++) {
			cv::Point point1, point2;
			point1 = cv::Point(lines[i][0], lines[i][1]);
			point2 = cv::Point(lines[i][2], lines[i][3]);
			ExpendLine(point1, point2, cannyImage, 0, 10000);
			lines[i][2] = point2.x;
			lines[i][3] = point2.y;

			point2 = cv::Point(lines[i][0], lines[i][1]);
			point1 = cv::Point(lines[i][2], lines[i][3]);
			ExpendLine(point1, point2, cannyImage, 0, 10000);
			lines[i][0] = point2.x;
			lines[i][1] = point2.y;

		}

		//显示线段
		cv::Mat dstImage0 = ORG.clone();
		cv::cvtColor(dstImage0, dstImage0, CV_GRAY2BGR);
		for (size_t i = 0; i < lines.size(); i++) {
			cv::Point point1, point2;
			point1 = cv::Point(lines[i][0], lines[i][1]);
			point2 = cv::Point(lines[i][2], lines[i][3]);
			line(dstImage0, point1, point2, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));
		}

		//延长直线
		for (size_t i = 0; i < lines.size(); i++) {
			cv::Point point1, point2;
			point1 = cv::Point(lines[i][0], lines[i][1]);
			point2 = cv::Point(lines[i][2], lines[i][3]);
			ExpendLine(point1, point2, BinPic);
			lines[i][2] = point2.x;
			lines[i][3] = point2.y;

			point2 = cv::Point(lines[i][0], lines[i][1]);
			point1 = cv::Point(lines[i][2], lines[i][3]);
			ExpendLine(point1, point2, BinPic);
			lines[i][0] = point2.x;
			lines[i][1] = point2.y;

		}


		//显示线段
		cv::Mat dstImage = ORG.clone();
		cv::cvtColor(dstImage, dstImage, CV_GRAY2BGR);
		for (size_t i = 0; i < lines.size(); i++) {
			cv::Point point1, point2;
			point1 = cv::Point(lines[i][0], lines[i][1]);
			point2 = cv::Point(lines[i][2], lines[i][3]);
			line(dstImage, point1, point2, cv::Scalar(rand() % 256, rand() % 256, rand() % 256));

			//控制绘制线的宽度就可以合并不同的直线
			line(ORG, point1, point2, cv::Scalar(0), 1);
		}
	}
#endif // _WINDOWS


//	//测试
//#ifdef _WINDOWS
//	//cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\cleanpack3_all\\bin\\win_debug\\Debug\\savePic.bmp", 0);
//	cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\MapCut_Hough.bmp", 0);
//	mapData = TestPic.data;
//	OrgW = TestPic.cols;
//	OrgH = TestPic.rows;
//#endif



	//保存图片用于查bug
#ifdef _WINDOWS
	//cv::Mat TestPic = cv::imread("C:\\Users\\Tanhuan_work\\Desktop\\cleanpack3_all\\bin\\win_debug\\Debug\\savePic.bmp", 0);
	cv::Mat imw = cv::Mat(OrgH, OrgW, CV_8U, mapData).clone();
	cv::imwrite("C:\\Users\\Tanhuan_work\\Desktop\\MapCut_Hough.bmp", imw);
#endif


	//ROI
	int ROI_minX = OrgW, ROI_minY = OrgH, ROI_maxX = 0, ROI_maxY = 0;
	for (int y = 0; y < OrgH; y++) {
		unsigned char *p = mapData + y * OrgW;
		for (int x = 0; x < OrgW; x++) {
			if (abs(p[x] - 127) > 5) {
				//if (p[x] != 127) {
				if (x < ROI_minX) ROI_minX = x;
				if (y < ROI_minY) ROI_minY = y;
				if (x > ROI_maxX) ROI_maxX = x;
				if (y > ROI_maxY) ROI_maxY = y;
			}
		}
	}
	ROI_minX = std::max(0, ROI_minX - 5);
	ROI_minY = std::max(0, ROI_minY - 5);
	ROI_maxX = std::min(OrgW, ROI_maxX + 5);
	ROI_maxY = std::min(OrgH, ROI_maxY + 5);
	//传进来一个空地图的容错机制
	if (ROI_maxY < ROI_minY || ROI_maxX < ROI_minX) {
		std::vector<std::vector<_PointI32>> re;
		return re;
	}


	int width = ROI_maxX - ROI_minX + 1;
	int height = ROI_maxY - ROI_minY + 1;
	int w = width;
	int h = height;
	unsigned char *ORG = new unsigned char[width*height];
	for (int i = 0; i < height; i++) {
		memcpy(ORG + i * width, mapData + (i + ROI_minY)*OrgW + ROI_minX, width);
	}
#ifdef  _WINDOWS
	cv::Mat orgPicROI = cv::Mat(height, width, CV_8U, ORG);
#endif

	unsigned char *Map = new unsigned char[width*height];
	memcpy(Map, ORG, width*height);


#ifdef _WINDOWS
	cv::Mat MapMat = cv::Mat(height, width, CV_8U, Map);
#endif // _WINDOWS


	//threshold
	threshold(Map, width*height, 128);


	_ContourFinder mContourFinder;
	mContourFinder.Find((int8_t *)Map, width, height, width);
	int coutNum = mContourFinder.GetConturCount();//总连通域个数
	if (coutNum == 0) {
		std::vector<std::vector<_PointI32>> re;
		return re;
	}

	//get the max area contour
	_ContourFinder::Contour *MaxContour = NULL;
	_ContourFinder::Contour OrgMaxContour;
	double Area = -1;
	for (int i = 0; i < coutNum; i++) {
		double a = getContourArea((_ContourFinder::Contour *)mContourFinder.GetContour(i));
		if (a > Area) {
			Area = a;
			MaxContour = (_ContourFinder::Contour *)mContourFinder.GetContour(i);
		}
	}
	OrgMaxContour = *MaxContour;


	//draw the max contour
	memset(Map, 0, width*height);
	DrawContour(MaxContour->points, Map, 255, width, height);


	TH::_Hough mHough;
	mHough.Transform(Map, width / 2, height / 2, width, height);
	std::vector<TH::HoughLine> lines = mHough.GetLines(20, 5);
	//mHough.addPointToLine(lines, mapData, OrgW, OrgH, 1.5);
	//删除倾斜的直线
#ifdef _WINDOWS
	cv::Mat show111 = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(show111, show111, CV_GRAY2BGR);
	for (size_t i = 0; i < lines.size(); i++) {
		cv::line(show111,
			cv::Point(lines[i].PointA.first, lines[i].PointA.second),
			cv::Point(lines[i].PointB.first, lines[i].PointB.second),
			cv::Scalar(0, 0, 255));
	}
#endif // _WINDOWS
	for (std::vector<TH::HoughLine>::iterator it = lines.begin(); it != lines.end();) {
		if (abs(it->HoughT) < 5 || abs(it->HoughT - 90) < 5 || abs(it->HoughT - 180) < 5) {
			it++;
		}
		else {
			it = lines.erase(it);
		}
	}
	mHough.addPointToLine_HoughP(lines, Map, width, height, 1.5);


#ifdef _WINDOWS
	cv::Mat show = cv::Mat(height, width, CV_8U, Map).clone();
	cv::cvtColor(show, show, CV_GRAY2BGR);
	for (size_t i = 0; i < lines.size(); i++) {
		cv::line(show,
			cv::Point(lines[i].PointA.first, lines[i].PointA.second),
			cv::Point(lines[i].PointB.first, lines[i].PointB.second),
			cv::Scalar(0, 0, 255));
		//float p1x = 0 + center_x;
		//float p1y = lines[i].HoughR / sin((float)lines[i].HoughT / 180 * CV_PI) + center_y;
		//float p2x = lines[i].HoughR / cos((float)lines[i].HoughT / 180 * CV_PI) + center_x;
		//float p2y = 0 + center_y;
		//cv::line(show,
		//	cv::Point(p1x, p1y),
		//	cv::Point(p2x, p2y),
		//	cv::Scalar(0, 255, 0));
		uint8_t b = rand() % 256, g = rand() % 256, r = rand() % 256;
		for (size_t j = 0; j < lines[i].VPoint.size(); j++) {
			show.at<cv::Vec3b>(lines[i].VPoint[j].y, lines[i].VPoint[j].x) = cv::Vec3b(b, g, r);
		}
	}
#endif // _WINDOWS


	//延长直线
	DrawContour_fill(MaxContour->points, Map, 255, width, height);
	for (size_t i = 0; i < lines.size(); i++) {
		ExpendLine(lines[i].PointA, lines[i].PointB, Map, w, h);
		ExpendLine(lines[i].PointB, lines[i].PointA, Map, w, h);
	}


	for (size_t i = 0; i < lines.size(); i++) {
		DrawLine(lines[i].PointA, lines[i].PointB, Map, 0, w, h);

		//加粗直线
		DrawLine(
			std::pair<int, int>(lines[i].PointA.first + 1, lines[i].PointA.second),
			std::pair<int, int>(lines[i].PointB.first + 1, lines[i].PointB.second),
			Map, 0, w, h);
		DrawLine(
			std::pair<int, int>(lines[i].PointA.first, lines[i].PointA.second + 1),
			std::pair<int, int>(lines[i].PointB.first, lines[i].PointB.second + 1),
			Map, 0, w, h);
	}

	//因为DrawLine暂时没有线宽参数，所用用腐蚀使连通域分开
	//采用腐蚀的策略会导致后面将点并入连通域有问题
	//erode(MaskMap.data, OrgW, OrgH);


	//_ContourFinder mContourFinder;
	mContourFinder.Find((int8_t *)Map, w, h, w);
	coutNum = mContourFinder.GetConturCount();//总连通域个数
#ifdef _WINDOWS
	cv::Mat MaskForShow = cv::Mat::zeros(h, w, CV_8U);
	for (int i = 0; i < coutNum; i++) {
		//只绘制面积一定大小的连通域
		const std::vector<_PointI32> &vp = mContourFinder.GetContour(i)->points;
		if (getContourArea(vp) > 20)
			DrawContour_fill(mContourFinder.GetContour(i)->points, MaskForShow.data, i + 1, w, h);
	}
#endif // _WINDOWS




	//已经可以直接输出了

	std::vector<std::vector<_PointI32>> Result;
	for (int i = 0; i < coutNum; i++) {
		//只绘制面积一定大小的连通域
		const std::vector<_PointI32> &vp = mContourFinder.GetContour(i)->points;
		if (getContourArea(vp) > 20) {
			std::vector<_PointI32>ConVList_t;
			_RectI32 rt = boundingRect(vp);

			if (rt.area() > 200) {
				ConVList_t.push_back(rt.tl());
				ConVList_t.push_back(rt.tr());
				ConVList_t.push_back(rt.br());
				ConVList_t.push_back(rt.bl());
				Result.push_back(ConVList_t);
			}
		}
	}


#ifdef _WINDOWS
	{
		cv::Mat show = cv::Mat(height, width, CV_8U, ORG).clone();
		cv::cvtColor(show, show, CV_GRAY2BGR);
		for (size_t i = 0; i < Result.size(); i++) {
			cv::Rect re = cv::Rect(cv::Point(Result[i][0].x, Result[i][0].y), cv::Point(Result[i][2].x, Result[i][2].y));
			cv::rectangle(show,re,cv::Scalar(rand()%256, rand() % 256, rand() % 256));
		}
		cv::imshow("MapCut_Hough", show);
		cv::waitKey(10);
	}
#endif



	//将点的坐标转换到ROI之前
	//转到ROI之前的坐标
	for (size_t i = 0; i < Result.size(); i++) {
		for (size_t j = 0; j < Result[i].size(); j++) {
			Result[i][j].x += ROI_minX;
			Result[i][j].y += ROI_minY;
		}
	}


	//从像素坐标转到世界坐标系 单位mm
	for (size_t i = 0; i < Result.size(); i++) {
		for (size_t j = 0; j < Result[i].size(); j++) {
			Result[i][j].x = idx2x(Result[i][j].x, xMin, resolution) * 1000;
			Result[i][j].y = idx2x(Result[i][j].y, yMin, resolution) * 1000;
		}
	}

	delete[] ORG;
	delete[] Map;
	return Result;


	////这里归不同于连通域归并，因为绘制的直线过长，导致每个点归类的Mask出问题
	////将不属于任何区域的白色点归并到区域中
	////将蒙版中不属于任意一个类的白点，归类到一个类中  广度优先搜索
	//unsigned char *Map = MaxContourBinMap.data;
	//unsigned char * Mask = mContourFinderPic.data;
	//int width = OrgW;
	//int height = OrgH;
	//unsigned char *flag = new unsigned char[width*height];
	//memset(flag, 0, width*height);
	//std::vector<_PointI32> vpList;
	//for (int y = 0; y < height; y++) {
	//	for (int x = 0; x < width; x++) {
	//		if (Map[y*width + x] == 0) continue;
	//		if (Mask[y*width + x] != 0)continue;
	//		if (flag[y*width + x] == 1)continue;
	//		flag[y*width + x] = 1;
	//		vpList.push_back(_PointI32(x, y));
	//		std::vector<_PointI32> RememberPList;//存放该区域的点
	//		RememberPList.push_back(_PointI32(x, y));
	//		std::vector<int> color(coutNum + 1);//存放该区域与哪个区域的联通最多；
	//		while (vpList.size()) {
	//			_PointI32 LastPoint = vpList[vpList.size() - 1];
	//			vpList.pop_back();
	//			_PointI32 NextPoint;
	//			NextPoint = _PointI32(LastPoint.x + 1, LastPoint.y);
	//			CHECK_AND_PUT_POINT_IN_LIST;
	//			NextPoint = _PointI32(LastPoint.x - 1, LastPoint.y);
	//			CHECK_AND_PUT_POINT_IN_LIST;
	//			NextPoint = _PointI32(LastPoint.x, LastPoint.y + 1);
	//			CHECK_AND_PUT_POINT_IN_LIST;
	//			NextPoint = _PointI32(LastPoint.x, LastPoint.y - 1);
	//			CHECK_AND_PUT_POINT_IN_LIST;
	//			//NextPoint = Point(LastPoint.x + 1, LastPoint.y - 1);
	//			//CHECK_AND_PUT_POINT_IN_LIST;
	//			//NextPoint = Point(LastPoint.x + 1, LastPoint.y + 1);
	//			//CHECK_AND_PUT_POINT_IN_LIST;
	//			//NextPoint = Point(LastPoint.x - 1, LastPoint.y + 1);
	//			//CHECK_AND_PUT_POINT_IN_LIST;
	//			//NextPoint = Point(LastPoint.x - 1, LastPoint.y - 1);
	//			//CHECK_AND_PUT_POINT_IN_LIST;
	//		}
	//		//将该连通域内的点涂色
	//		int MaxColor = -1;
	//		int MaxColorIndex = -1;
	//		for (size_t i = 0; i < color.size(); i++) {
	//			if (color[i] > MaxColor) {
	//				MaxColor = color[i];
	//				MaxColorIndex = i;
	//			}
	//		}
	//		for (size_t i = 0; i < RememberPList.size(); i++) {
	//			Mask[RememberPList[i].y*width + RememberPList[i].x] = MaxColorIndex;
	//		}
	//	}
	//}
	//delete[]flag;


}
