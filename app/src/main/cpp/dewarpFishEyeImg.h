#pragma once


#include <cstdint>

uint8_t *remap(const uint8_t *pSrcData, float *pMapData, int inHeight, int inWidth, int outHeight, int outWidth);

float *rectifyMap2(
		const int inWidth, const int inHeight,
		const float angleX, const float angleY, const float angleZ,
		const int outWidth, const int outHeight,
		const float radius);