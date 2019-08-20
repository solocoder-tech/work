#pragma once


#include <cstdint>
void undistort(/*float* A,*/ /*float* distPtr,*/int inheight,int inwidth, int outheight, int outwidth, float* remapxy, bool resizeflag/*,int usefulheight,int usefulwidth*/);
void resizemap(uint8_t *inputmap,uint8_t *outputmap,int startx,int starty,int width,int height);
uint8_t *remap(const uint8_t *pSrcData, int inHeight, int inWidth, int outHeight, int outWidth);

