// **********************************************************************************
//
// BSD License.
// This file is part of a _Hough Transformation tutorial
// see: http://www.keymolen.com/2013/05/hough-transformation-c-implementation.html
//
// Copyright (c) 2013, Bruno Keymolen, email: bruno.keymolen@gmail.com
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// Redistributions in binary form must reproduce the above copyright notice, this
// list of conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// Neither the name of "Bruno Keymolen" nor the names of its contributors may be
// used to endorse or promote products derived from this software without specific
// prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
//
// **********************************************************************************

#pragma once

#include <vector>
#include <queue> 


namespace TH {

	class HoughLine {
	public:

		std::pair<int, int> PointA;
		std::pair<int, int> PointB;

		//存放霍夫变换中属于直线的点
		struct PointInLine {
			PointInLine(int xx, int yy, float dd) { x = xx, y = yy, dis = dd; }
			int x, y;
			float dis;//距离直线的距离
			float XinLine;//沿直线方向的距离
		};
		std::vector<HoughLine::PointInLine> VPoint;
		float DisVariance;



		int HoughR;
		int HoughT;
		int HoughM;

		HoughLine(int a, int b, int c) :HoughR(a), HoughT(b), HoughM(c) {
		}
		HoughLine() {
			HoughR = 0;
			HoughT = 0;
			HoughM = 0;
		}

	};


	class _Hough {
	public:
		_Hough();
		virtual ~_Hough();
	public:
		int Transform(unsigned char* img_data,int center_x,int center_y, int w, int h);
		int TransformJ45(short * idx_x, short * idx_y, int size, int w, int h);
		std::vector< std::pair< std::pair<int, int>, std::pair<int, int> > > GetAllLines(int threshold);
		std::vector<HoughLine> GetLines(int lineNum, int threshold);
		bool addPointToLine(std::vector<HoughLine>& lines, unsigned char* img_data, int w, int h, float dThre);
		bool addPointToLine_HoughP(std::vector<HoughLine>& lines, unsigned char* img_data, int w, int h, float dThre);
		std::vector<HoughLine> GetLinesJ45(int lineNum, int threshold);
		bool GetBestAngle(float & bestAngle);
		bool GetBestAngleAndMove(float& bestAngle, int& moveX, int& moveY);
		bool GetBestAngleAndMove(float& bestAngle, int& moveX, int& moveY,unsigned char* img_data, int center_x, int center_y, int w, int h);
		int NormalizeToPi(int angle);
		const unsigned int* GetAccu(int *w, int *h);
	private:
		unsigned int* _accu = nullptr;
		bool* _accu_pd = nullptr;

		int _accu_w = 0;
		int _accu_h = 0;
		int _img_w = 0;
		int _img_h = 0;

		int _center_x = 0;
		int _center_y = 0;
	};

}

