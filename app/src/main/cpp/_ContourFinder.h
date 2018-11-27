//从CleanPack2中移植
#pragma once

#include <inttypes.h>
#include <stdlib.h>
#include <vector>



struct _PointI32 {
	_PointI32() { x = y = 0; }
	_PointI32(int _x, int _y) { x = _x; y = _y; }
	int x, y;
	bool operator == (const _PointI32 & r) { return (x == r.x&&y == r.y); }
	_PointI32 operator - (const _PointI32 & r) { return _PointI32(x - r.x, y - r.y); }
};

struct _RectI32 {
	int x, y, w, h;
	_RectI32() { x = y = w = h = 0; }
	_RectI32(int x, int y, int w, int h) { this->x = x, this->y = y, this->w = w, this->h = h; }

	_PointI32 tl() { return _PointI32(x, y); }
	_PointI32 bl() { return _PointI32(x, y + h - 1); }
	_PointI32 tr() { return _PointI32(x + w - 1, y); }
	_PointI32 br() { return _PointI32(x + w - 1, y + h - 1); }
	bool contains(_PointI32  p) { return(x <= p.x&&y <= p.y && (x + w) > p.x && (y + h) > p.y); }
	float area() { return (w * h); };
};

//从CleanPack2中移植
class _ContourFinder {
public:
	_ContourFinder();
	~_ContourFinder();

	/*
			  v_prev
				 |
	  h_prev -- self -- h_next
				 |
			  v_next
	*/

	struct Contour {
		uint32_t flag;
		int index;
		int other_index[4];
		std::vector<_PointI32> points;
		_RectI32 rect;
		Contour *v_prev;
		Contour *v_next;
		Contour *h_prev;
		Contour *h_next;
	};

	int Find(const int8_t *img_data, int w, int h, int stride);
	const Contour * GetContour(int index);
	inline const Contour * GetFirstContour() { return GetContour(0); }
	inline int GetConturCount() const { return mContourArray.size(); }
	inline const std::vector<Contour *> * GetContours() const { return &mContourArray; }

private:

	struct ContourInfo {
		int flags;
		Contour *contour;
		struct ContourInfo *next;        /* next contour with the same mark value */
		struct ContourInfo *parent;      /* information about parent contour */
		_RectI32 rect;                /* bounding rectangle */
		_PointI32 origin;             /* origin point (where the contour was traced from) */
		int is_hole;                /* hole flag */

	};

	int8_t *mRawImg, *mImg;
	int mImgW, mImgH, mImgStride;
	_PointI32 mPoint, mLNBD, mOffset;
	int mNBD;
	int mImgBufferSize;

	ContourInfo *mLastContourInfo;    /* information about latest approx. contour */
	ContourInfo mFrameInfo;  /* information about frame */
	Contour mFrame;

	std::vector<ContourInfo *> mContourInfoArray;
	std::vector<Contour *> mContourArray;
	ContourInfo *mContourInfoTable[128];

	bool StartFind(const int8_t * img_data, int w, int h, int stride);
	Contour* FindNext();
	Contour* EndFind();
	void EndProcessContour();
	int TraceContour(int8_t *ptr, int step, int8_t *stop_ptr, int is_hole);
	void FetchContourEx(int8_t* ptr, int step, _PointI32 pt, Contour *contour, int nbd, _RectI32* _rect);
	void Release();
};
