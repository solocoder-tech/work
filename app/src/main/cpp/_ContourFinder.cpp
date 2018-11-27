#include "_ContourFinder.h"

#include<string.h>

#define CV_SEQ_FLAG_HOLE 65536
#define  CV_INIT_3X3_DELTAS( deltas, step, nch )            \
    ((deltas)[0] =  (nch),  (deltas)[1] = -(step) + (nch),  \
     (deltas)[2] = -(step), (deltas)[3] = -(step) - (nch),  \
     (deltas)[4] = -(nch),  (deltas)[5] =  (step) - (nch),  \
     (deltas)[6] =  (step), (deltas)[7] =  (step) + (nch))
#define CV_IS_SEQ_HOLE( seq )       (((seq)->flag & CV_SEQ_FLAG_HOLE) != 0)

static const _PointI32 CodeDeltas[8] =
{ _PointI32(1, 0), _PointI32(1, -1), _PointI32(0, -1), _PointI32(-1, -1), _PointI32(-1, 0), _PointI32(-1, 1), _PointI32(0, 1), _PointI32(1, 1) };



_ContourFinder::_ContourFinder() {
	mRawImg = NULL;
	for (int i = 0; i < 128; i++)
		mContourInfoTable[i] = NULL;
	mImg = NULL;
	mLastContourInfo = NULL;
	memset(&mFrameInfo, 0, sizeof(mFrameInfo));
	memset(&mFrame, 0, sizeof(mFrame));
	mImgStride = mImgH = mImgW = 0;
	mImgBufferSize = 0;
}

_ContourFinder::~_ContourFinder() {
	Release();

	if (mRawImg)
		delete[] mRawImg;
	mRawImg = NULL;
}

int _ContourFinder::Find(const int8_t * img_data, int w, int h, int stride) {
	int count = -1;
	//Contour * find_ret = NULL;

	Release();

	StartFind(img_data, w, h, stride);

	while (FindNext());

	EndFind();

	count = mContourArray.size();
	for (int i = 0; i < count; i++)
		mContourArray[i]->index = i;

	for (int i = 0; i < count; i++) {
		Contour * c = mContourArray[i];
		c->other_index[0] = c->h_next ? ((Contour*)c->h_next)->index : -1;
		c->other_index[1] = c->h_prev ? ((Contour*)c->h_prev)->index : -1;
		c->other_index[2] = c->v_next ? ((Contour*)c->v_next)->index : -1;
		c->other_index[3] = c->v_prev ? ((Contour*)c->v_prev)->index : -1;
	}
	return count;
}

const _ContourFinder::Contour* _ContourFinder::GetContour(int index) {
	if (mContourArray.size() <= (uint32_t)index)
		return NULL;

	return mContourArray[index];
}

bool _ContourFinder::StartFind(const int8_t * img_data, int w, int h, int stride) {
	int size = stride * h;

	//if buffer is not big enough, realloc new buffer
	if (size != mImgBufferSize) {
		if (mRawImg) delete[] mRawImg;
		mRawImg = new int8_t[size];
		mImgBufferSize = size;
	}

	mImg = mRawImg + stride;
	mImgW = w - 1;
	mImgH = h - 1;
	mImgStride = stride;
	mPoint.x = mPoint.y = 1;
	mLNBD.x = 0;
	mLNBD.y = 1;
	mNBD = 2;
	mOffset.x = mOffset.y = 0;

	memset(&mFrame, 0, sizeof(mFrame));

	mFrameInfo.is_hole = 1;
	mFrameInfo.contour = &mFrame;
	mFrameInfo.next = 0;
	mFrameInfo.parent = 0;
	mFrameInfo.rect = _RectI32(0, 0, w, h);
	mLastContourInfo = 0;

	/* make zero borders */
	int8_t *ptr = mRawImg;
	memset(ptr, 0, w);
	memset(ptr + stride * (h - 1), 0, w);
	ptr += stride;
	for (int y = 1; y < h - 1; y++, ptr += stride) {
		ptr[0] = ptr[w - 1] = 0;
	}

	/* converts all pixels to 0 or 1 */
	ptr = mRawImg;
	//int8_t * v = mRawImg + w;
	//const int8_t *img_ptr = img_data;
	for (int y = 1; y < h - 1; y++)
		for (int x = 1; x < w - 1; x++)
			ptr[y * w + x] = img_data[y * w + x] == 0 ? 0 : 1;
	return true;
}

_ContourFinder::Contour * _ContourFinder::FindNext() {
	EndProcessContour();

	int8_t* img0 = mRawImg;
	int8_t* img = mImg;
	int step = mImgStride;
	//int step_i = step / sizeof(int);
	int x = mPoint.x;
	int y = mPoint.y;
	int width = mImgW;
	int height = mImgH;
	_PointI32 lnbd = this->mLNBD;
	int nbd = this->mNBD;
	int prev = img[x - 1];
	int new_mask = -2;
	Contour *seq = NULL;
	int lval;

	for (; y < height; y++, img += step) {
		int p = 0;

		for (; x < width; x++) {

			for (; x < width && (p = img[x]) == prev; x++);

			if (x >= width)
				break;

			{

				ContourInfo *par_info = NULL;
				ContourInfo *l_cinfo = NULL;
				int is_hole = 0;
				_PointI32 origin;


				/* if not external contour */
				if (!(prev == 0 && p == 1)) {
					/* check hole */
					if (p != 0 || prev < 1)
						goto resume_scan;

					if (prev & new_mask) {
						lnbd.x = x - 1;
					}
					is_hole = 1;
				}

				origin.y = y;
				origin.x = x - is_hole;

				/* find contour parent */
				if (!is_hole || lnbd.x <= 0) {
					par_info = &(mFrameInfo);
				}
				else {

					//HH_PRINT("findnext step:%d,lnbd.y:%d,lnbd.x:%d\n", step, lnbd.y, lnbd.x); fflush(stdout);
					int lval = ((int)img0[lnbd.y * step + lnbd.x]) & 0x7f;

					ContourInfo *cur = mContourInfoTable[lval];
					/* find the first bounding contour */
					//bug 清扫结束后进入该循环出不来
					//GK_PRINT("lval(%d) ContourInfo cur = %p", lval, cur);
					while (cur) {
						if ((unsigned)(lnbd.x - cur->rect.x) < (unsigned)cur->rect.w &&
							(unsigned)(lnbd.y - cur->rect.y) < (unsigned)cur->rect.h) {
							if (par_info && TraceContour(img0 + par_info->origin.y * step +
								par_info->origin.x, step, img + lnbd.x,
								par_info->is_hole) > 0) {
								break;
							}
							par_info = cur;
						}
						cur = cur->next;
						//	GK_PRINT("lval(%d) ContourInfo cur->next = %p", lval, cur);
					}

					/* if current contour is a hole and previous contour is a hole or
					current contour is external and previous contour is external then
					the parent of the contour is the parent of the previous contour else
					the parent is the previous contour itself. */
					if (par_info->is_hole == is_hole) {
						par_info = par_info->parent;
						/* every contour must have a parent
						(at least, the frame of the image) */
						if (!par_info)
							par_info = &(mFrameInfo);
					}

					/* hole flag of the parent must differ from the flag of the contour */
					if (par_info->contour == 0)        /* removed contour */
						goto resume_scan;
				}

				lnbd.x = x - is_hole;

				// create new seq
				seq = new Contour();
				mContourArray.push_back(seq);
				seq->flag = is_hole ? CV_SEQ_FLAG_HOLE : 0;

				// create new contour info
				l_cinfo = new ContourInfo();
				mContourInfoArray.push_back(l_cinfo);

				lval = nbd;
				// change nbd
				nbd = (nbd + 1) & 127;
				nbd += nbd == 0 ? 3 : 0;
				FetchContourEx(img + x - is_hole, step,
					_PointI32(origin.x + mOffset.x, origin.y + mOffset.y), seq, lval, &(l_cinfo->rect));

				l_cinfo->rect.x -= mOffset.x;
				l_cinfo->rect.y -= mOffset.y;

				l_cinfo->next = mContourInfoTable[lval];
				mContourInfoTable[lval] = l_cinfo;

				l_cinfo->is_hole = is_hole;
				l_cinfo->contour = seq;
				l_cinfo->origin = origin;
				l_cinfo->parent = par_info;
				l_cinfo->contour->v_prev = l_cinfo->parent->contour;

				if (par_info->contour == 0) {
					l_cinfo->contour = 0;
					p = img[x];

				}
				else {
					this->mLastContourInfo = l_cinfo;
					this->mPoint.x = x + 1;
					this->mPoint.y = y;
					this->mLNBD = lnbd;
					this->mImg = img;
					this->mNBD = nbd;
					return l_cinfo->contour;
				}

			resume_scan:

				prev = p;
				/* update lnbd */
				if (prev & -2) {
					lnbd.x = x;
				}
			}                   /* end of prev != p */
		}                       /* end of loop on x */

		lnbd.x = 0;
		lnbd.y = y + 1;
		x = 1;
		prev = 0;
	}                           /* end of loop on y */

	return NULL;
}

_ContourFinder::Contour * _ContourFinder::EndFind() {
	EndProcessContour();

	return mFrame.v_next;
}

void _ContourFinder::EndProcessContour() {
	if (mLastContourInfo) {
		if (mLastContourInfo->contour) {
			//cvInsertNodeIntoTree( l_cinfo->contour, l_cinfo->parent->contour, &(scanner->frame) );
			Contour *node = mLastContourInfo->contour;
			Contour *parent = mLastContourInfo->parent->contour;
			node->v_prev = parent != &mFrame ? parent : NULL;
			node->h_next = parent->v_next;
			if (parent->v_next)
				parent->v_next->h_prev = node;
			parent->v_next = node;
		}
		mLastContourInfo = 0;
	}
}

void _ContourFinder::FetchContourEx(int8_t* ptr, int step, _PointI32 pt,
	Contour *contour, int nbd, _RectI32* _rect) {
	int         deltas[16];
	int8_t        *i0 = ptr, *i1, *i3, *i4;
	_RectI32      rect;
	int         prev_s = -1, s, s_end;
	int         method = 0;
	/* initialize local state */
	CV_INIT_3X3_DELTAS(deltas, step, 1);
	memcpy(deltas + 8, deltas, 8 * sizeof(deltas[0]));

	rect.x = rect.w = pt.x;
	rect.y = rect.h = pt.y;

	s_end = s = CV_IS_SEQ_HOLE(contour) ? 0 : 4;

	do {
		s = (s - 1) & 7;
		i1 = i0 + deltas[s];
		if (*i1 != 0)
			break;
	} while (s != s_end);

	if (s == s_end)            /* single pixel domain */
	{
		*i0 = (int8_t)(nbd | 0x80);
		if (method >= 0) {
			contour->points.push_back(pt);
		}
	}
	else {
		i3 = i0;

		prev_s = s ^ 4;

		/* follow border */
		for (;; ) {
			s_end = s;

			for (;; ) {
				i4 = i3 + deltas[++s];
				if (*i4 != 0)
					break;
			}
			s &= 7;

			/* check "right" bound */
			if ((unsigned)(s - 1) < (unsigned)s_end) {
				*i3 = (int8_t)(nbd | 0x80);
			}
			else if (*i3 == 1) {
				*i3 = (int8_t)nbd;
			}

			contour->points.push_back(pt);

			if (s != prev_s) {
				/* update bounds */
				if (pt.x < rect.x)
					rect.x = pt.x;
				else if (pt.x > rect.w)
					rect.w = pt.x;

				if (pt.y < rect.y)
					rect.y = pt.y;
				else if (pt.y > rect.h)
					rect.h = pt.y;
			}

			prev_s = s;
			pt.x += CodeDeltas[s].x;
			pt.y += CodeDeltas[s].y;

			if (i4 == i0 && i3 == i1)  break;

			i3 = i4;
			s = (s + 4) & 7;
		}                       /* end of border following loop */
	}

	rect.w -= rect.x - 1;
	rect.h -= rect.y - 1;

	contour->rect = rect;

	if (_rect)  *_rect = rect;
}

void _ContourFinder::Release() {
	for (uint32_t i = 0; i < mContourInfoArray.size(); i++) {
		delete mContourInfoArray[i];
		mContourInfoArray[i] = NULL;
	}
	mContourInfoArray.clear();

	for (uint32_t i = 0; i < mContourArray.size(); i++) {
		delete mContourArray[i];
		mContourArray[i] = NULL;
	}
	mContourArray.clear();

	for (uint32_t i = 0; i < 128; i++) {
		mContourInfoTable[i] = NULL;
	}

}

int _ContourFinder::TraceContour(int8_t *ptr, int step, int8_t *stop_ptr, int is_hole) {
	//GK_PRINT("TraceContour %p %d %p %d \n", ptr, step, stop_ptr, is_hole);
	int deltas[16];
	int8_t *i0 = ptr, *i1, *i3, *i4;
	int s, s_end;

	/* initialize local state */
	CV_INIT_3X3_DELTAS(deltas, step, 1);
	memcpy(deltas + 8, deltas, 8 * sizeof(deltas[0]));

	s_end = s = is_hole ? 0 : 4;

	do {
		s = (s - 1) & 7;
		i1 = i0 + deltas[s];
		if (*i1 != 0)
			break;
	} while (s != s_end);

	i3 = i0;

	/* check single pixel domain */
	if (s != s_end) {
		/* follow border */
		for (;; ) {
			s_end = s;

			for (;; ) {
				i4 = i3 + deltas[++s];
				if (*i4 != 0)
					break;
			}

			if (i3 == stop_ptr || (i4 == i0 && i3 == i1))
				break;

			i3 = i4;
			s = (s + 4) & 7;
		}                       /* end of border following loop */
	}
	return i3 == stop_ptr;
}
