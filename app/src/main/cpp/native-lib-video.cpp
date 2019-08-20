#include <jni.h>
#include <string>
#include <stdint.h>
#include <stdlib.h>
#include <android/log.h>
#include "remap.h"


#define TAG    "xxxx"
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

extern "C"
JNIEXPORT jbyteArray JNICALLJava_com_ldvideo_JniUtils_remap
        (JNIEnv *env, jobject, jbyteArray videoData, jint inHeight,
         jint inWidth,
         jint outHeight, jint outWidth) {
//    if (inWidth != 1280 || inHeight != 960) {
//        LOGE("不是要求的分辨率(1280*960)，请调整");
//        return NULL;
//    }
    jbyte *pSrcData = env->GetByteArrayElements(videoData, NULL);
    if (pSrcData == NULL) {
        LOGE("pSrcData为空");
    }

    jbyte *result = reinterpret_cast<jbyte *>(remap(reinterpret_cast<const uint8_t *>(pSrcData),
                                                    inHeight, inWidth, outHeight,
                                                    outWidth));

    int len = outHeight * outWidth * 3;
    jbyteArray bytes = env->NewByteArray(len);
    env->SetByteArrayRegion(bytes, 0, len, result);
    env->ReleaseByteArrayElements(videoData, pSrcData, 0);
    return bytes;
}

extern "C"
JNIEXPORT void JNICALL Java_com_ldvideo_JniUtils_yuv420p2yuv420sp (JNIEnv *env, jobject jobject1, jbyteArray java_yuv420p, jbyteArray java_yuv420sp,
         jint width,jint height) {
    unsigned char *yuv420p = (unsigned char *) ((env)->GetByteArrayElements(java_yuv420p, NULL));
    if (yuv420p == NULL) {
        LOGE("yuv420p is null");
        return;
    }
    unsigned char *yuv420sp = (unsigned char *) ((env)->GetByteArrayElements(java_yuv420sp, NULL));
    if (yuv420sp == NULL) {
        LOGE("yuv420sp is null");
        return;
    }
    int i, j;
    int y_size = width * height;

    unsigned char *y = yuv420p;
    unsigned char *u = yuv420p + y_size;
    unsigned char *v = yuv420p + y_size * 5 / 4;

    unsigned char *y_tmp = yuv420sp;
    unsigned char *uv_tmp = yuv420sp + y_size;

    // y
    memcpy(y_tmp, y, y_size);

    // u
    for (j = 0, i = 0; j < y_size / 2; j += 2, i++) {
        uv_tmp[j] = v[i];
        uv_tmp[j + 1] = u[i];
    }

    //释放资源
    env->ReleaseByteArrayElements(java_yuv420p, reinterpret_cast<jbyte *>(yuv420p), 0);
    env->ReleaseByteArrayElements(java_yuv420sp, reinterpret_cast<jbyte *>(yuv420sp), 0);
}







