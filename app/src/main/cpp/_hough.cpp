// **********************************************************************************
//
// BSD License.
// This file is part of a _Hough Transformation tutorial,
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

#include "_hough.h"

#include <cmath>
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <algorithm>

//#include "Common/AlgCom/MathTable.h"


#ifdef  _WINDOWS
#include"opencv2/opencv.hpp"
#endif //  _WINDOWS

#define Pow2(x) ((x)*(x))


#define DEG2RAD 0.017453293f

#define AngleResolution  0.5f
#define AngleWeightSize 180/AngleResolution
#define AngleEffectW  2
#define AngleStopProb  0.4

#define SerchSize 5
#define LineWidth 1


namespace TH {

	bool operator < (const HoughLine &t1, const HoughLine &t2) {
		return t1.HoughM < t2.HoughM;
	}

	_Hough::_Hough() /*:_accu(0), _accu_w(0), _accu_h(0), _accu_pd(0), _img_w(0), _img_h(0)*/
	{

	}

	_Hough::~_Hough() {
		if(_accu)
			free(_accu);

		if (_accu_pd)
			free(_accu_pd);
	}

	int _Hough::Transform(unsigned char* img_data, int center_x, int center_y, int w, int h)
	{
		_img_w = w;
		_img_h = h;

		_center_x = center_x;
		_center_y = center_y;

		//Create the accu
		double hough_h = ((sqrt(2.0) * (double)(h>w?h:w)) / 2.0);
		_accu_h = hough_h * 2.0; // -r -> +r
		_accu_w = 180;

		if (_accu)
			free(_accu);
		_accu = (unsigned int*)calloc(_accu_h * _accu_w, sizeof(unsigned int));

		//double center_x = w/2;
		//double center_y = h/2;


		for(int y=0;y<h;y++)
		{
			for(int x=0;x<w;x++)
			{
				if( img_data[ (y*w) + x] > 200 )
				{
					//std::cout << "img_data < 100" << std::endl;

					double dx = (double)x - center_x;
					double dy = (double)y - center_y;

					for(int t=0;t<180;t++)
					{
						int tx = t ;//+ 45;

						float coss, sinn;
#ifdef _MATH_TABLE
						m_GetSinCos_al(sinn, coss, tx);
#else
						sinn = sin(tx*DEG2RAD);
						coss = cos(tx*DEG2RAD);
#endif

						double r = (dx * coss) + (dy * sinn);

						//double r = ( ((double)x - center_x) * cos((double)t * DEG2RAD)) + (((double)y - center_y) * sin((double)t * DEG2RAD));
						_accu[(int)((round(r + hough_h) * 180.0)) + t] += 1;
					}
				}
			}
		}

		return 0;
	}

	int _Hough::TransformJ45(short* idx_x, short* idx_y,int size,int w,int h)
	{
		_img_w = w;
		_img_h = h;

		//Create the accu
		double hough_h = ((sqrt(2.0) * (double)(h>w ? h : w)) / 2.0);
		_accu_h = hough_h * 2.0; // -r -> +r
		_accu_w = 180;

		if (_accu)
			free(_accu);
		_accu = (unsigned int*)calloc(_accu_h * _accu_w, sizeof(unsigned int));

		//double center_x = w / 2;
		//double center_y = h / 2;

		int hough_h_int = hough_h;

		for (int t = 0; t < 180; t++)
		{
			//float coss, sinn;
			//m_GetSinCos_al(sinn, coss, t);

			int tx = t + 45;

			int coss, sinn;
#ifdef _MATH_TABLE
			m_GetSinCosX10000_al(sinn, coss, tx);
#else
			sinn = 10000 * sin(tx*DEG2RAD);
			coss = 10000 * cos(tx*DEG2RAD);
#endif


			for (int i = 0; i < size; i++)
			{
				//double dx = idx_x[i];
				//double dy = idx_y[i];

				//double r = (dx * coss) + (dy * sinn);
				//_accu[(int)((round(r + hough_h) * 180.0)) + t]++;

				//double r = ( ((double)x - center_x) * cos((double)t * DEG2RAD)) + (((double)y - center_y) * sin((double)t * DEG2RAD));
			


				int dx = idx_x[i];
				int dy = idx_y[i];

				int r = ((dx * coss) + (dy * sinn)) / 10000;
				_accu[(r + hough_h_int) * _accu_w + t] += 3;

				//for (int i = 1; i <= LineWidth; i++)
				//{
				//	_accu[(int)((round(r + i + hough_h) * _accu_w)) + t]++;
				//	_accu[(int)((round(r - i + hough_h) * _accu_w)) + t]++;
				//}

			}
		}
		return 0;
	}

	std::vector< std::pair< std::pair<int, int>, std::pair<int, int> > > _Hough::GetAllLines(int threshold)
	{
		std::vector< std::pair< std::pair<int, int>, std::pair<int, int> > > lines;

		if (_accu == 0)
			return lines;

		if (_accu_pd)
			free(_accu_pd);
		_accu_pd = (bool*)calloc(_accu_h * _accu_w, sizeof(bool));

		for (int r = 0; r<_accu_h; r++)
		{
			for (int t = 0; t<_accu_w; t++)
			{
				int idx = (r*_accu_w) + t;

				if (_accu_pd[idx]) continue;

				_accu_pd[idx] = 1;

				if ((int)_accu[idx] >= threshold)
				{
					//Is this point a local maxima (9x9)
					int max = _accu[idx];

					int tolt = t;
					int tolr = r;
					int toln = 1;

					for (int ly = -SerchSize; ly <= SerchSize; ly++)
					{
						for (int lx = -SerchSize; lx <= SerchSize; lx++)
						{
							if ((ly + r >= 0 && ly + r<_accu_h) && (lx + t >= 0 && lx + t<_accu_w))
							{
								int newInd = ((r + ly)*_accu_w) + (t + lx);
								int now = (int)_accu[newInd];
								if (now > max)
								{
									max = now;
									ly = lx = SerchSize + 1;
								}
								else if (now < max)
								{
									_accu_pd[newInd] = 1;
								}
								else
								{
									_accu_pd[newInd] = 1;
									tolt += (t + lx);
									tolr += (r + ly);
									toln++;
								}
							}
						}
					}

					if (max > (int)_accu[idx])
						continue;

					int tx = tolt / toln;
					//tx = tx + 45;

					int rx = tolr / toln;

					//if (tx >= 180)
					//{
					//	tx -= 180;
					//}


					int x1, y1, x2, y2;
					x1 = y1 = x2 = y2 = 0;



					if (tx >= 45 && tx <= 135)
					{
						//y = (r - x cos(t)) / sin(t)
						x1 = 0;
						y1 = ((double)(rx - (_accu_h / 2)) - ((x1 - (_center_x)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_center_y);
						x2 = _img_w - 0;
						y2 = ((double)(rx - (_accu_h / 2)) - ((x2 - (_center_x)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_center_y);
					}
					else
					{
						//x = (r - y sin(t)) / cos(t);
						y1 = 0;
						x1 = ((double)(rx - (_accu_h / 2)) - ((y1 - (_center_y)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_center_x);
						y2 = _img_h - 0;
						x2 = ((double)(rx - (_accu_h / 2)) - ((y2 - (_center_y)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_center_x);
					}
					std::cout << "r= " << rx << "   t= " << tx << std::endl;
					lines.push_back(std::pair< std::pair<int, int>, std::pair<int, int> >(std::pair<int, int>(x1, y1), std::pair<int, int>(x2, y2)));

				}
			}
		}

		
		//std::cout << "lines: " << lines.size() << " " << threshold << std::endl;
		return lines;
	}

	std::vector<HoughLine> _Hough::GetLines(int lineNum, int threshold)
	{
		std::priority_queue<HoughLine> lines;
		std::vector<HoughLine> ansLines;

		if (_accu == 0)
			return ansLines;

		if (_accu_pd)
			free(_accu_pd);
		_accu_pd = (bool*)calloc(_accu_h * _accu_w, sizeof(bool));

		for (int r = 0; r<_accu_h; r++)
		{
			for (int t = 0; t<_accu_w; t++)
			{
				int idx = (r*_accu_w) + t;

				if (_accu_pd[idx]) continue;

				_accu_pd[idx] = 1;

				if ((int)_accu[idx] >= threshold)
				{
					//Is this point a local maxima (9x9)
					int max = _accu[idx];

					int tolt = t;
					int tolr = r;
					int toln = 1;

					for (int ly = -SerchSize; ly <= SerchSize; ly++)
					{
						for (int lx = -SerchSize; lx <= SerchSize; lx++)
						{
							if ((ly + r >= 0 && ly + r<_accu_h) && (lx + t >= 0 && lx + t<_accu_w))
							{
								int now = (int)_accu[((r + ly)*_accu_w) + (t + lx)];
								if (now > max)
								{
									max = now;
									ly = lx = SerchSize + 1;
								}
								else if (now < max)
								{
									_accu_pd[((r + ly)*_accu_w) + (t + lx)] = 1;
								}
								else
								{
									tolt += (t + lx);
									tolr += (r + ly);
									toln++;
								}
							}
						}
					}
					if (max > (int)_accu[(r*_accu_w) + t])
						continue;

					int tx = tolt / toln;
					//tx = tx + 45;

					int rx = tolr / toln;

					//if (tx >= 180)
					//{
					//	tx -= 180;
					//}


					int x1, y1, x2, y2;
					x1 = y1 = x2 = y2 = 0;



					if (tx >= 45 && tx <= 135)
					{
						//y = (r - x cos(t)) / sin(t)
						x1 = 0;
						y1 = ((double)(rx - (_accu_h / 2)) - ((x1 - (_center_x)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_center_y);
						x2 = _img_w - 0;
						y2 = ((double)(rx - (_accu_h / 2)) - ((x2 - (_center_x)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_center_y);
					}
					else
					{
						//x = (r - y sin(t)) / cos(t);
						y1 = 0;
						x1 = ((double)(rx - (_accu_h / 2)) - ((y1 - (_center_y)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_center_x);
						y2 = _img_h - 0;
						x2 = ((double)(rx - (_accu_h / 2)) - ((y2 - (_center_y)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_center_x);
					}

					HoughLine ansLine(rx - (_accu_h / 2), tx, max);
					ansLine.PointA = std::pair<int, int>(x1, y1);
					ansLine.PointB = std::pair<int, int>(x2, y2);
					
					//std::cout << "r= " << r << "   t= " << t << std::endl;
					//lines.push_back(std::pair< std::pair<int, int>, std::pair<int, int> >(std::pair<int, int>(x1, y1), std::pair<int, int>(x2, y2)));

					lines.push(ansLine);
					//if (lines.size() >= lineNum)
					//{
					//	r = _accu_h;
					//	t = _accu_w;
					//}

				}
			}
		}

		int tolLineNum = 0;
		while ((tolLineNum < lineNum)&&(!lines.empty()))
		{
			ansLines.push_back(lines.top());
			lines.pop();
			tolLineNum++;
		}

		//std::cout << "lines: " << ansLines.size() << " " << threshold << std::endl;
		return ansLines;
	}

	std::vector<HoughLine> _Hough::GetLinesJ45(int lineNum, int threshold)
	{
		std::priority_queue<HoughLine> lines;
		std::vector<HoughLine> ansLines;

		if (_accu == 0)
			return ansLines;

		if (_accu_pd)
			free(_accu_pd);
		_accu_pd = (bool*)calloc(_accu_h * _accu_w, sizeof(bool));

		for (int r = 0; r<_accu_h; r++)
		{
			for (int t = 0; t<_accu_w; t++)
			{
				int idx = (r*_accu_w) + t;

				if (_accu_pd[idx]) continue;

				_accu_pd[idx] = 1;

				if ((int)_accu[idx] >= threshold)
				{
					//Is this point a local maxima (9x9)
					int max = _accu[idx];

					int tolt = t;
					int tolr = r;
					int toln = 1;

					for (int ly = -SerchSize; ly <= SerchSize; ly++)
					{
						for (int lx = -SerchSize; lx <= SerchSize; lx++)
						{
							if ((ly + r >= 0 && ly + r<_accu_h) && (lx + t >= 0 && lx + t<_accu_w))
							{
								int now = (int)_accu[((r + ly)*_accu_w) + (t + lx)];
								if (now > max)
								{
									max = now;
									ly = lx = SerchSize + 1;
								}
								else if (now < max)
								{
									_accu_pd[((r + ly)*_accu_w) + (t + lx)] = 1;
								}
								else
								{
									tolt += (t + lx);
									tolr += (r + ly);
									toln++;
								}
							}
						}
					}
					if (max > (int)_accu[(r*_accu_w) + t])
						continue;

					int tx = tolt / toln;
					tx = tx + 45;

					int rx = tolr / toln;

					//if (tx >= 180)
					//{
					//	tx -= 180;
					//}


					int x1, y1, x2, y2;
					x1 = y1 = x2 = y2 = 0;



					if (tx >= 45 && tx <= 135)
					{
						//y = (r - x cos(t)) / sin(t)
						x1 = 0;
						y1 = ((double)(rx - (_accu_h / 2)) - ((x1 - (_img_w / 2)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_img_h / 2);
						x2 = _img_w - 0;
						y2 = ((double)(rx - (_accu_h / 2)) - ((x2 - (_img_w / 2)) * cos(tx * DEG2RAD))) / sin(tx * DEG2RAD) + (_img_h / 2);
					}
					else
					{
						//x = (r - y sin(t)) / cos(t);
						y1 = 0;
						x1 = ((double)(rx - (_accu_h / 2)) - ((y1 - (_img_h / 2)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_img_w / 2);
						y2 = _img_h - 0;
						x2 = ((double)(rx - (_accu_h / 2)) - ((y2 - (_img_h / 2)) * sin(tx * DEG2RAD))) / cos(tx * DEG2RAD) + (_img_w / 2);
					}

					HoughLine ansLine(rx - (_accu_h / 2), tx, max);
					ansLine.PointA = std::pair<int, int>(x1, y1);
					ansLine.PointB = std::pair<int, int>(x2, y2);

					//std::cout << "r= " << r << "   t= " << t << std::endl;
					//lines.push_back(std::pair< std::pair<int, int>, std::pair<int, int> >(std::pair<int, int>(x1, y1), std::pair<int, int>(x2, y2)));

					lines.push(ansLine);
					//if (lines.size() >= lineNum)
					//{
					//	r = _accu_h;
					//	t = _accu_w;
					//}

				}
			}
		}

		int tolLineNum = 0;
		while ((tolLineNum < lineNum) && (!lines.empty()))
		{
			ansLines.push_back(lines.top());
			lines.pop();
			tolLineNum++;
		}

		//std::cout << "lines: " << ansLines.size() << " " << threshold << std::endl;
		return ansLines;
	}

	bool _Hough::GetBestAngle(float& bestAngle)
	{
		int threshold = 20;
		int LineNum = 5;

		std::vector<HoughLine>lines = GetLinesJ45(LineNum, threshold);

		if (lines.size() == 0)
		{
			//std::cout << "lines.size = 0" << std::endl;
			return 0;
		}

		int AngleWeight[int(AngleWeightSize)];

		memset(AngleWeight, 0, sizeof(AngleWeight));

		int NormalizeM = lines[0].HoughM;

		for (size_t i = 0; i < lines.size(); i++)
		{
			for (int j = 0; j <= AngleEffectW; j++)
			{
				int NormalizeWeight = lines[i].HoughM * 1000 * (1.0f - (1.0f - AngleStopProb) / AngleEffectW*(j)) / NormalizeM;

				//std::cout << NormalizeWeight << std::endl;

				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

				if (j == 0)
					continue;

				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

			}
		}

		bestAngle = 0;
		int MaxM = 0;
		for (int i = 0; i < (int)AngleWeightSize; i++)
		{
			if (AngleWeight[i]>MaxM)
			{
				bestAngle = AngleResolution*float(i);
				MaxM = AngleWeight[i];
			}
			//std::cout << AngleWeight[i] << std::endl;
		}

		//printf("BestAngle = %.2f\n", bestAngle);
		//std::cout << "BestAngle = " << (double)bestAngle << "   MaxM = " << MaxM << std::endl;

		return 1;

	}
	
	bool _Hough::GetBestAngleAndMove(float& bestAngle, int& moveX, int& moveY)
	{
		int threshold = 10;
		int LineNum = 10;

		std::vector<HoughLine>lines = GetLines(LineNum, threshold);





		if (lines.size() == 0)
		{
			//std::cout << "lines.size = 0" << std::endl;
			return 0;
		}

		int AngleWeight[int(AngleWeightSize)];

		memset(AngleWeight, 0, sizeof(AngleWeight));

		int NormalizeM = lines[0].HoughM;

		for (size_t i = 0; i < lines.size(); i++)
		{
			for (int j = 0; j <= AngleEffectW; j++)
			{
				int NormalizeWeight = lines[i].HoughM * 1000 * (1.0f-(1.0f - AngleStopProb) / AngleEffectW*(j)) / NormalizeM;
				
				//std::cout << NormalizeWeight << std::endl;
				
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

				if (j == 0)
					continue;
				
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

			}
		}

		bestAngle = 0;
		int MaxM = 0;
		for (int i = 0; i < (int)AngleWeightSize; i++)
		{
			if (AngleWeight[i]>MaxM)
			{
				bestAngle = AngleResolution*float(i);
				MaxM = AngleWeight[i];
			}
			//std::cout << AngleWeight[i] << std::endl;
		}

		int MaxXM = 0;
		int MaxYM = 0;
		moveX = 0;
		moveY = 0;
		for (size_t i = 0; i < lines.size(); i++)
		{
			if (abs(lines[i].HoughT - bestAngle) <= AngleEffectW*2*AngleResolution)
			{
				if (lines[i].HoughM > MaxXM)
				{
					MaxXM = lines[i].HoughM;
					moveX = lines[i].HoughR;
				}
			}

			if (abs(lines[i].HoughT - bestAngle - 90) <= AngleEffectW*2*AngleResolution)
			{
				if (lines[i].HoughM > MaxYM)
				{
					MaxYM = lines[i].HoughM;
					moveY = lines[i].HoughR;
				}
			}
		}
		//printf("BestAngle = %.2f\n", bestAngle);
		//std::cout << "BestAngle = " << (double)bestAngle << "   MaxM = " << MaxM << std::endl;
		//std::cout << "MoveX = " << moveX << "   MoveY = " << moveY << std::endl;


		//TH
		//设定MaxYM与MaxXM的阈值 
		//这个阈值包含在GetLines(LineNum, threshold)中
		if (MaxXM < 40 || MaxYM < 40) {
			return 0;
		}

		return 1;
	}

	bool VPointComp(HoughLine::PointInLine &a, HoughLine::PointInLine &b) {
		if (a.y > b.y) {
			return true;
		}
		else if (a.y < b.y) {
			return false;
		}
		else if (a.x > b.x) {
			return true;
		}
		else {
			return false;
		}
	}

	bool VPointComp_Xline(const HoughLine::PointInLine &a, const HoughLine::PointInLine &b) {
		return a.XinLine < b.XinLine;
	}

	bool _Hough::addPointToLine(std::vector<HoughLine>& lines, unsigned char* img_data,int w, int h,float dThre) {
		//将点归类到直线中去
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (img_data[(y*w) + x] >200) {
					for (size_t i = 0; i < lines.size(); i++) {
						//点到直线的距离
						float p1x = x;
						float p1y = y;
						float p2x = lines[i].PointA.first;
						float p2y = lines[i].PointA.second;
						float p3x = lines[i].PointB.first;
						float p3y = lines[i].PointB.second;

						//面积
						float a00 = 0;
						a00 += p1x * p2y - p1y * p2x;
						a00 += p2x * p3y - p2y * p3x;
						a00 += p3x * p1y - p3y * p1x;
						//a00 *= 0.5;//求三角形的面积需要除以2，但是计算矩形的面积就可以了
						a00 = fabs(a00);


						//距离
						float d = a00 / sqrt((p2x - p3x)*(p2x - p3x) + (p2y - p3y)*(p2y - p3y));
						if (d < dThre) {
							lines[i].VPoint.push_back(HoughLine::PointInLine(x, y, d));
						}
					}
				}
			}
		}


		////排序点
		//for (size_t i = 0; i < lines.size(); i++) {
		//	std::sort(lines[i].VPoint.begin(), lines[i].VPoint.end(), VPointComp);
		//}


		return true;
	}

	bool _Hough::addPointToLine_HoughP(std::vector<HoughLine>& lines, unsigned char* img_data, int w, int h, float dThre) {
		
		//统计所有的点
		std::vector<std::pair<int, int>> allP;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (img_data[(y*w) + x] > 200) {
					allP.push_back(std::pair<int, int>(x, y));
				}
			}
		}
		
		
		//将点归类到直线中去
		for (size_t i = 0; i < allP.size(); i++) {
			for (size_t j = 0; j < lines.size(); j++) {
				//点到直线的距离
				float p1x = allP[i].first;
				float p1y = allP[i].second;
				float p2x = lines[j].PointA.first;
				float p2y = lines[j].PointA.second;
				float p3x = lines[j].PointB.first;
				float p3y = lines[j].PointB.second;

				//面积
				float a00 = 0;
				a00 += p1x * p2y - p1y * p2x;
				a00 += p2x * p3y - p2y * p3x;
				a00 += p3x * p1y - p3y * p1x;
				//a00 *= 0.5;//求三角形的面积需要除以2，但是计算矩形的面积就可以了
				a00 = fabs(a00);


				//距离
				float d = a00 / sqrt((p2x - p3x)*(p2x - p3x) + (p2y - p3y)*(p2y - p3y));
				if (d < dThre) {
					lines[j].VPoint.push_back(HoughLine::PointInLine(p1x, p1y, d));
				}
			}
		}


		//排序点,按照直线方向排序这些点，而不是按照XY轴的大小比较！
		//for (size_t i = 0; i < lines.size(); i++) {
		//	std::sort(lines[i].VPoint.begin(), lines[i].VPoint.end(), VPointComp);
		//}
		for (size_t i = 0; i < lines.size(); i++) {
			int Ax = lines[i].PointA.first;
			int Ay = lines[i].PointA.second;
			int Bx = lines[i].PointB.first;
			int By = lines[i].PointB.second;

			float x2 = Bx - Ax;
			float y2 = By - Ay;

			float LenAB = sqrt((float)(Ax - Bx)*(Ax - Bx)+ (float)(Ay - By)*(Ay - By));
			for (size_t j= 0; j < lines[i].VPoint.size(); j++) {
				float x1 = lines[i].VPoint[j].x - Ax;
				float y1 = lines[i].VPoint[j].y - Ay;

				float cross = x1 * x2 + y1 * y2;
				float xx = cross / LenAB;
				lines[i].VPoint[j].XinLine = xx;
			}

			std::vector<HoughLine::PointInLine> &v = lines[i].VPoint;
			sort(v.begin(), v.end(), TH::VPointComp_Xline);
		}


		//然后，将归属与同一条直线的点，划分出不同的线段
		//这里就可以设定MaxGap阈值
		std::vector<HoughLine> OutLines;
		for (size_t i = 0; i < lines.size(); i++) {

			if (lines[i].VPoint.size() < 2)continue;

			HoughLine t;
			t.PointA = std::pair<int, int>(lines[i].VPoint[0].x, lines[i].VPoint[0].y);
			float LastXline = lines[i].VPoint[0].XinLine;
			float starXline = LastXline;
			for (size_t j = 1; j < lines[i].VPoint.size(); j++) {
				if (lines[i].VPoint[j].XinLine - LastXline > 2.5 || j == lines[i].VPoint.size() - 1) {
					t.PointB = std::pair<int, int>(lines[i].VPoint[j - 1].x, lines[i].VPoint[j - 1].y);
					
					if (lines[i].VPoint[j].XinLine - starXline > 10&&
						sqrt(Pow2(t.PointA.first - t.PointB.first)+ Pow2(t.PointA.second - t.PointB.second))>10
						) {
						OutLines.push_back(t);
					}

					t.PointA = std::pair<int, int>(lines[i].VPoint[j].x, lines[i].VPoint[j].y);
					LastXline = lines[i].VPoint[j].XinLine;
					starXline = LastXline;
				}
				else{
					LastXline = lines[i].VPoint[j].XinLine;
					continue;
				}
			}
		}


		lines = OutLines;

		return true;
	}

	bool _Hough::GetBestAngleAndMove(float& bestAngle, int& moveX, int& moveY,unsigned char* img_data, int center_x, int center_y, int w, int h) 
	{
		int threshold = 20;
		int LineNum = 10;

		std::vector<HoughLine>lines = GetLines(LineNum, threshold);

		if (lines.size() == 0) {
			//std::cout << "lines.size = 0" << std::endl;
			return 0;
		}

		addPointToLine(lines, img_data, w, h, 5);



#ifdef _WINDOWS
		cv::Mat show = cv::Mat(h, w, CV_8U, img_data).clone();
		cv::cvtColor(show, show, CV_GRAY2BGR);
		for (size_t i = 0; i < lines.size(); i++) {
			cv::line(show,
				cv::Point(lines[i].PointA.first, lines[i].PointA.second),
				cv::Point(lines[i].PointB.first, lines[i].PointB.second),
				cv::Scalar(0, 0, 255));

			float p1x = 0 + center_x;
			float p1y = lines[i].HoughR / sin((float)lines[i].HoughT / 180 * CV_PI) + center_y;
			float p2x = lines[i].HoughR / cos((float)lines[i].HoughT / 180 * CV_PI) + center_x;
			float p2y = 0 + center_y;
			cv::line(show,
				cv::Point(p1x, p1y),
				cv::Point(p2x, p2y),
				cv::Scalar(0, 255, 0));

			uint8_t b = rand()%256, g = rand() % 256, r = rand() % 256;
			for (size_t j = 0; j < lines[i].VPoint.size(); j++) {
				show.at<cv::Vec3b>(lines[i].VPoint[j].y, lines[i].VPoint[j].x) = cv::Vec3b(b,g,r);
			}
		}
#endif // _WINDOWS



		//统计直线上点分布离散度



		int AngleWeight[int(AngleWeightSize)];

		memset(AngleWeight, 0, sizeof(AngleWeight));

		int NormalizeM = lines[0].HoughM;

		for (size_t i = 0; i < lines.size(); i++) {
			for (int j = 0; j <= AngleEffectW; j++) {
				int NormalizeWeight = lines[i].HoughM * 1000 * (1.0f - (1.0f - AngleStopProb) / AngleEffectW * (j)) / NormalizeM;

				//std::cout << NormalizeWeight << std::endl;

				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) + j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

				if (j == 0)
					continue;

				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j;
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}
				{
					int ansAngle = lines[i].HoughT*(1 / AngleResolution) - j + (90 / AngleResolution);
					ansAngle = NormalizeToPi(ansAngle);
					AngleWeight[ansAngle] += NormalizeWeight;
				}

			}
		}

		bestAngle = 0;
		int MaxM = 0;
		for (int i = 0; i < (int)AngleWeightSize; i++) {
			if (AngleWeight[i] > MaxM) {
				bestAngle = AngleResolution * float(i);
				MaxM = AngleWeight[i];
			}
			//std::cout << AngleWeight[i] << std::endl;
		}

		int MaxXM = 0;
		int MaxYM = 0;
		moveX = 0;
		moveY = 0;
		for (size_t i = 0; i < lines.size(); i++) {
			if (abs(lines[i].HoughT - bestAngle) <= AngleEffectW * 2 * AngleResolution) {
				if (lines[i].HoughM > MaxXM) {
					MaxXM = lines[i].HoughM;
					moveX = lines[i].HoughR;
				}
			}

			if (abs(lines[i].HoughT - bestAngle - 90) <= AngleEffectW * 2 * AngleResolution) {
				if (lines[i].HoughM > MaxYM) {
					MaxYM = lines[i].HoughM;
					moveY = lines[i].HoughR;
				}
			}
		}
		//printf("BestAngle = %.2f\n", bestAngle);
		//std::cout << "BestAngle = " << (double)bestAngle << "   MaxM = " << MaxM << std::endl;
		//std::cout << "MoveX = " << moveX << "   MoveY = " << moveY << std::endl;
		return 1;
	}

	int _Hough::NormalizeToPi(int angle)
	{
		while (angle < 0)
		{
			angle += AngleWeightSize;
		}
		while (angle >=AngleWeightSize)
		{
			angle -= AngleWeightSize;
		}
		return angle;
	}

	const unsigned int* _Hough::GetAccu(int *w, int *h)
	{
		*w = _accu_w;
		*h = _accu_h;

		return _accu;
	}
}
