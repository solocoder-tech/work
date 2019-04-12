package com.ldvideo;

public class JniUtils {
    static {
        System.loadLibrary("native-ld-video");
    }

    //uint8_t *remap(const uint8_t *pSrcData, float *pMapData, int inHeight, int inWidth, int outHeight, int outWidth)
    public native byte[] cvremap(byte[] videoData,float[] tempData,int inHeight, int inWidth, int outHeight, int outWidth );


//    (
//            const int inWidth, const int inHeight,
//	const float angleX, const float angleY, const float angleZ,
//	const int outWidth, const int outHeight,
//	const float radius)
    public native float[] rectifyMap2(int inWidth, int inHeight, float angleX, float angleY, float angleZ,
	 int outWidth, int outHeight, float radius);

}
