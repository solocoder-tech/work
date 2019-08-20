#include <stdint.h>
#include <stdlib.h>
#include <math.h>
#include <android/log.h>
#include <string.h>

const int ImgWidth = 1280;
const int ImgHeight = 960;
const double PI = 3.1415926;
const float Cx = 1280 / 2;
const float Cy = 960 / 2;
const double HalfFov = 40;

#define TAG    "xxxx"
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

//返回的是需要的byte[]
uint8_t *remap(const uint8_t *pSrcData, float *pMapData, int inHeight, int inWidth, int outHeight,
               int outWidth) {
    static uint8_t *pDstData = NULL;
    static int lastOutHeight = -1;
    static int lastOutWidth = -1;

    if (pDstData == NULL || lastOutHeight != outHeight || lastOutWidth != outWidth) {
        if (pDstData != NULL)
            free(pDstData);

        pDstData = (uint8_t *) malloc(outHeight * outWidth * 3);
        if (pDstData == NULL) {
            LOGE("error");
        }
        lastOutHeight = outHeight;
        lastOutWidth = outWidth;
    }

    for (int j = 0; j < outHeight; j++) {
        for (int i = 0; i < outWidth; i++) {
            int idx = j * outWidth * 2 + i * 2;
            float u = pMapData[idx + 0];
            float v = pMapData[idx + 1];

            int u0 = (int) (u);
            int v0 = (int) (v);
            float dx = u - u0; //<1
            float dy = v - v0; //<1
            float weight1 = (1 - dx) * (1 - dy);
            float weight2 = dx * (1 - dy);
            float weight3 = (1 - dx) * dy;
            float weight4 = dx * dy;

            if (u0 > 0 && v0 > 0 && (u0 + 1) < inWidth && (v0 + 1) < inHeight) {
                float b = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 0] +
                          weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 0] +
                          weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 0] +
                          weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 0];

                float g = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 1] +
                          weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 1] +
                          weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 1] +
                          weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 1];

                float r = weight1 * pSrcData[v0 * inWidth * 3 + u0 * 3 + 2] +
                          weight2 * pSrcData[v0 * inWidth * 3 + (u0 + 1) * 3 + 2] +
                          weight3 * pSrcData[(v0 + 1) * inWidth * 3 + u0 * 3 + 2] +
                          weight4 * pSrcData[(v0 + 1) * inWidth * 3 + (u0 + 1) * 3 + 2];

                int idxResult = j * outWidth * 3 + i * 3;
                pDstData[idxResult + 0] = (uint8_t) (b);
                pDstData[idxResult + 1] = (uint8_t) (g);
                pDstData[idxResult + 2] = (uint8_t) (r);
            }
        }
    }

    return pDstData;
}

//该函数的结果交给上面函数处理
float *rectifyMap2(
        const int inWidth, const int inHeight,
        const float angleX, const float angleY, const float angleZ,
        const int outWidth, const int outHeight,
        const float radius) {


    static float *pMapData = (float *) malloc(sizeof(float) * ImgWidth * ImgHeight * 2);
    memset(pMapData, 0, sizeof(float) * ImgWidth * ImgHeight * 2);

    double radianAngle;

    //x轴旋转矩阵
    radianAngle = angleX * PI / 180.0;
    double rotX[9] = {1, 0, 0, 0, 1, 0, 0, 0, 1};
    rotX[4] = cos(radianAngle);
    rotX[5] = -sin(radianAngle);
    rotX[7] = sin(radianAngle);
    rotX[8] = cos(radianAngle);

    //y轴旋转矩阵
    radianAngle = angleY * PI / 180.0;
    double rotY[9] = {1, 0, 0, 0, 1, 0, 0, 0, 1};
    rotY[0] = cos(radianAngle);
    rotY[2] = sin(radianAngle);
    rotY[6] = -sin(radianAngle);
    rotY[8] = cos(radianAngle);

    //z轴旋转矩阵
    double rotZ[9] = {1, 0, 0, 0, 1, 0, 0, 0, 1};
    radianAngle = angleZ * PI / 180.0;
    rotZ[0] = cos(radianAngle);
    rotZ[1] = -sin(radianAngle);
    rotZ[3] = sin(radianAngle);
    rotZ[4] = cos(radianAngle);

    double lx = 2 * radius * tan(HalfFov * PI / 180.0);
    double ly = radius;

    for (int j = 0; j < outHeight; j++) {
        for (int i = 0; i < outWidth; i++) {
            double x = lx / outWidth * i - lx / 2;
            double y = ly / outHeight * j - ly / 2;
            double z = radius;

            double x1 = rotX[0] * x + rotX[1] * y + rotX[2] * z;
            double y1 = rotX[3] * x + rotX[4] * y + rotX[5] * z;
            double z1 = rotX[6] * x + rotX[7] * y + rotX[8] * z;

            double x2 = rotY[0] * x1 + rotY[1] * y1 + rotY[2] * z1;
            double y2 = rotY[3] * x1 + rotY[4] * y1 + rotY[5] * z1;
            double z2 = rotY[6] * x1 + rotY[7] * y1 + rotY[8] * z1;

            double x3 = rotZ[0] * x2 + rotZ[1] * y2 + rotZ[2] * z2;
            double y3 = rotZ[3] * x2 + rotZ[4] * y2 + rotZ[5] * z2;
            double z3 = rotZ[6] * x2 + rotZ[7] * y2 + rotZ[8] * z2;

            double u = radius * x3 / sqrt(x3 * x3 + y3 * y3 + z3 * z3) + Cx;
            double v = radius * y3 / sqrt(x3 * x3 + y3 * y3 + z3 * z3) + Cy;

            if (u >= 0 && u < inWidth - 1 && v >= 0 && v < inHeight - 1) {
                pMapData[j * outWidth * 2 + 2 * i + 0] = (float) u;
                pMapData[j * outWidth * 2 + 2 * i + 1] = (float) v;
            } else {
                pMapData[j * outWidth * 2 + 2 * i + 0] = 0;
                pMapData[j * outWidth * 2 + 2 * i + 1] = 0;
            }
        }
    }

    if (pMapData == NULL) {
        LOGE("数据为空");
    }
    for (int i = 0; i <= 100; i++) {
        LOGE("输出的之时，%f", *(pMapData + i));
    }
    float a = *pMapData;
    float b = *(pMapData + 1);
    float c = *(pMapData + 2);
    return pMapData;
}
