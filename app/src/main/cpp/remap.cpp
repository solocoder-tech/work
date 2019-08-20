//#include "remapx1.h"
#include <stdint.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <stdio.h>

void undistort(/*float* A,*/ /*float* distPtr,*/int inheight,int inwidth, int outheight, int outwidth, float* remapxy, bool resizeflag/*,int usefulheight,int usefulwidth*/)
{
    //float cameramatrixpara[] = { 256.5165,0,657.4097  , 0,255.7891  ,488.3848  , 0,0,1 };
    //float dustrydistCoeffsPara[] = { -0.2138,0.0415  ,7.923015595125982e-04  ,  -3.703825615128305e-04,0 };
    
    double distPtr0[] = { -0.232056143874505,0.069651555271904  ,7.198227467877553e-04,-2.438627389025816e-04,0 };
    double A0[] = { 1.286571457014387e+02,0,3.294668990704366e+02  , 0,1.286160118505896e+02  ,2.445092249734441e+02  , 0,0,1 };
    double ir0[9] = {0.0097157444991653498,0,-3.20101610889888470,0,0.0097157444991653498,-2.3763489455685138,0,0,1 };
    double distPtr1[] = { -0.2138,0.0415  ,7.923015595125982e-04  ,  -3.703825615128305e-04,0 };
    double A1[] = { 256.5165,0,657.4097  , 0,255.7891  ,488.3848  , 0,0,1 };
    double ir1[9] = { 0.0048729806177523101,0,-3.2035448673578335,0,0.0048868386212069974,-2.3866576837998572,0,0,1 };
    
    double *distPtr = NULL;
    double *A = NULL;
    double *ir = NULL;
    if (inheight < 960 && inwidth < 1280){
        distPtr = distPtr0;
        A = A0;
        ir = ir0;
    }else{
        distPtr = distPtr1;
        A = A1;
        ir = ir1;
    }
    double k1 = distPtr[0];
    double k2 = distPtr[1];
    double p1 = distPtr[2];
    double p2 = distPtr[3];
    double k3 = 0;
    double k4 = 0;
    double k5 = 0;
    double k6 = 0;
    double u0 = A[2], v0 = A[5];
    double fx = A[0], fy = A[4];
    
    float intervalx = 1.0;
    float intervaly = 1.0;
    
    int dataheight= outheight;
    int datawidth= outwidth;
    if (resizeflag)
    {
        intervalx = 560.0 / (float)outwidth;
        intervalx = 600.0 / outheight;
//        printf("%f", intervalx);
        outwidth = 560;
        outheight = 600;
    }
    
    //float* totalmap = new float[1280*1920];
    if (!remapxy)
    {
        return;
    }
    
    int startx = (inwidth - outwidth) / 2 > 0 ? (inwidth - outwidth) / 2 : 0;
    int starty = (inheight - outheight) / 2 > 0 ? (inheight - outheight) / 2 : 0;
    int endx = startx + outwidth < inwidth ? startx + outwidth : inwidth;
    int endy = starty + outheight < inheight ? starty + outheight : inheight;
    
    float float_i = starty;
    float float_j = startx;
    for (int i = starty, iout = 0; i < endy; iout++, i++)
    {
        
        //利用逆矩阵iR将二维图像坐标(j,i)转换到摄像机坐标系(_x,_y,_w)
        double _x = i * ir[1] + ir[2] + startx * ir[0], _y = i * ir[4] + ir[5] + startx * ir[3], _w = i * ir[7] + ir[8] + startx * ir[6];
        
        if (resizeflag)
        {
            float_i += intervalx;
            i = (int)float_i;
        }
        for (int j = startx, jout = 0; j < endx; jout++, j++, _x += ir[0], _y += ir[3], _w += ir[6])
        {
            
            //摄像机坐标系归一化，令Z=1平面
            double w = 1. / _w, x = _x * w, y = _y * w;
            //这一部分请看OpenCV官方文档，畸变模型部分
            double x2 = x * x, y2 = y * y;
            double r2 = x2 + y2, _2xy = 2 * x*y;
            double kr = (1 + ((k3*r2 + k2)*r2 + k1)*r2) / (1 + ((k6*r2 + k5)*r2 + k4)*r2);
            double u = fx * (x*kr + p1 * _2xy + p2 * (r2 + 2 * x2)) + u0;
            double v = fy * (y*kr + p1 * (r2 + 2 * y2) + p2 * _2xy) + v0;
            remapxy[jout * 2 + iout * datawidth * 2] = (float)u;
            remapxy[jout * 2 + iout * datawidth * 2 + 1] = (float)v;
            
            if (resizeflag)
            {
                float_j += intervalx;
                j = (int)float_j;
            }
        }
    }
    
}

void resizemap(uint8_t *inputmap,uint8_t *outputmap,int startx,int starty,int width,int height)
{
    const int srcdatawidth=1280*3;
    const int datawidth=width*3;
    for (int i = 0; i < height; i++)
    {
        int srcy = i + starty;
        for (int j = 0; j < width; j++)
        {
            outputmap[j*3 + i * datawidth] = inputmap[j*3 + startx + (srcy) * srcdatawidth];
            outputmap[j*3 + i * datawidth+1] = inputmap[j*3 + startx + (srcy) * srcdatawidth+1];
            outputmap[j*3 + i * datawidth+2] = inputmap[j*3 + startx + (srcy) * srcdatawidth+2];

        }
        
    }
}

uint8_t *remap(const uint8_t *pSrcData, int inHeight, int inWidth, int outHeight, int outWidth)
{
    static float *pMapData=NULL;//imgremapxy;
	static uint8_t *pDstData = NULL;
	static int lastOutHeight = -1;
	static int lastOutWidth = -1;

	if (pDstData == NULL || lastOutHeight != outHeight || lastOutWidth != outWidth)
	{
		if (pDstData != NULL)
			free(pDstData);

//        for (int i = 0; i < 896000; i++){
//            imgremapxy[i] = 100;
//        }
		pDstData = (uint8_t *)malloc(outHeight * outWidth * 3);
		lastOutHeight = outHeight;
		lastOutWidth = outWidth;
//        for (int i = 0; i < 896000; i+=2) {
//            imgremapxy[i] -= 400;
//            imgremapxy[i+1] -= 240;
//        }
        pMapData = (float *)malloc(outHeight * outWidth * 2 * sizeof(float));
        undistort(inHeight, inWidth, outHeight, outWidth, pMapData, 0);
	}
#if 1
	for (int j = 0; j < outHeight; j++)
	{
		for (int i = 0; i < outWidth; i++)
		{
			int idx = j * outWidth * 2 + i * 2;
			float u = pMapData[idx + 0];
			float v = pMapData[idx + 1];

			int u0 = (int)(u);
			int v0 = (int)(v);
			float dx = u - u0;
			float dy = v - v0;
			float weight1 = (1 - dx) * (1 - dy);
			float weight2 = dx * (1 - dy);
			float weight3 = (1 - dx) * dy;
			float weight4 = dx * dy;

			if (u0 > 0 && v0 > 0 && (u0 + 1) < inWidth && (v0 + 1) < inHeight)
			{
				float b = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 0] + weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 0] +
					weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 0] + weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 0];

				float g = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 1] + weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 1] +
					weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 1] + weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 1];

				float r = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 2] + weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 2] +
					weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 2] + weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 2];

				int idxResult = j * outWidth * 3 + i * 3;
				pDstData[idxResult + 0] = (uint8_t)(b);
				pDstData[idxResult + 1] = (uint8_t)(g);
				pDstData[idxResult + 2] = (uint8_t)(r);
			}
		}
	}
#else
    int idx = 0;
    float u = 0.0;
    float v = 0.0;
    
    int u0 = 0;
    int v0 = 0;
    float dx = 0;
    float dy = 0;
    float weight1 = 0;
    float weight2 = 0;
    float weight3 = 0;
    float weight4 = 0;
    
    int vec00 = 0;
    uint8_t* src00 = NULL;
    uint8_t* src01 = NULL;
    uint8_t* src10 = NULL;
    uint8_t* src11 = NULL;
    float b,g,r;
    int idxResult = 0;
    uint8_t* pDst;
    
    for (int j = 0; j < outHeight; j++)
    {
        for (int i = 0; i < outWidth; i++)
        {
            idx = j * outWidth * 2 + i * 2;
            u = pMapData[idx + 0];
            v = pMapData[idx + 1];
            
            u0 = (int)(u);
            v0 = (int)(v);
            dx = u - u0;
            dy = v - v0;
            weight1 = (1 - dx) * (1 - dy);
            weight2 = dx * (1 - dy);
            weight3 = (1 - dx) * dy;
            weight4 = dx * dy;
            
            vec00 = v0 * inWidth * 3 + u0 * 3 + 0;
            src00 = (uint8_t*)(pSrcData + vec00);
            src01 = src00 + 3;
            src10 = src00 + inWidth * 3;
            src11 = src10 + 3;
            
            if (u0 > 0 && v0 > 0 && (u0 + 1) < inWidth && (v0 + 1) < inHeight)
            {
                b = weight1 * (*src00++) + weight2 * (*src01++) +
                weight3 * (*src10++) + weight4 * (*src11++);
                
                g = weight1 * (*src00++) + weight2 * (*src01++) +
                weight3 * (*src10++) + weight4 * (*src11++);
                
                r = weight1 * (*src00++) + weight2 * (*src01++) +
                weight3 * (*src10++) + weight4 * (*src11++);
                
                idxResult = j * outWidth * 3 + i * 3;
                pDst = pDstData + idxResult;
                *pDst++ = (uint8_t)(b);
                *pDst++ = (uint8_t)(g);
                *pDst++ = (uint8_t)(r);
            }
        }
    }
#endif

	return pDstData;
}
