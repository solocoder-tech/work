#include <jni.h>
#include <string>
#include <stdint.h>
#include <stdlib.h>
#include <android/log.h>
#include "remap1.h"


#define TAG    "xxxx"
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_ldvideo_JniUtils_remap
        (JNIEnv *env, jobject, jbyteArray videoData, jint inHeight,
         jint inWidth,
         jint outHeight, jint outWidth) {
    if (inWidth != 1280 || inHeight != 960) {
        LOGE("不是要求的分辨率(1280*960)，请调整");
        return NULL;
    }
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


JNIEXPORT void JNICALL Java_com_ldvideo_JniUtils_resizemap
        (JNIEnv *, jobject, jbyteArray, jbyteArray, jint, jint, jint, jint){

}

JNIEXPORT void JNICALL Java_com_ldvideo_JniUtils_undistort
        (JNIEnv *, jobject, jint, jint, jint, jint, jfloatArray, jboolean){

}
