//#include <jni.h>
//#include <string>
//#include <stdint.h>
//#include <stdlib.h>
//#include <android/log.h>
//#include "dewarpFishEyeImg.h"
//#include "map_cut.h"
//#include "dewarpFishEyeImg.h"
//
//
//#define TAG    "xxxx"
//#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
//
////
////extern "C"
////JNIEXPORT jstring JNICALL
////Java_com_example_ld_cmakedemo_JniUtils_stringFromJNI(
////        JNIEnv *env,
////        jobject /* this */) {
////    std::string hello = "cmake test++";
////    return env->NewStringUTF(hello.c_str());
////}
////
////
////extern "C"
////JNIEXPORT jstring JNICALL
////Java_com_example_mytakeout_JniUtils_erodeContourBox(JNIEnv *env, jobject instance,
////                                                    jbyteArray mapData_, jint OrgW,
////                                                    jint OrgH, jfloat xMin,
////                                                    jfloat yMin, jfloat resolution) {
////    jbyte *mapData = env->GetByteArrayElements(mapData_, NULL);
////
////    unsigned char *chars = NULL;
////    jbyte *bytes;
////    bytes = env->GetByteArrayElements(mapData_, 0);
////    int chars_len = env->GetArrayLength(mapData_);
////    chars = new unsigned char[chars_len + 1];
////    memcpy(chars, bytes, chars_len);
////    chars[chars_len] = 0;
////    memset(chars, 0, chars_len + 1);
////    env->ReleaseByteArrayElements(mapData_, bytes, 0);
////
////    // TODO
////    MapCut_ErodeContourBox(chars, OrgW, OrgH, xMin, yMin, resolution);
////
////    env->ReleaseByteArrayElements(mapData_, mapData, 0);
////
////    return env->NewStringUTF("1122");
////}
////
////
//////[{"name":"厨
//////房","id":0,"tag":"bedroom1","vertexs":[[0,10],[10,10],[10,0],
//////[0,0]],"active":"depth","mode":"default"},{"name":"客
//////厅","id":1,"tag":"diningroom1","vertexs":[[0,0],[10,0],[10,-10],
//////[0,-10]],"active":"normal","mode":"default"},{"name":"卫生
//////间","id":2,"tag":"diningroom2","vertexs":[[0,0],[0,10],[-10,10],
//////[-10,0]],"active":"forbid","mode":"default"}]
////
////static void BuildSingleVectorString(std::vector<_PointI32>, char *outBuf, int mask) {
////
////
////}
////
////static int GetVectorString(std::vector<std::vector<_PointI32> > vecs, char *outBuf, int maxSize) {
////    size_t vecSize = vecs.size();
////    if (vecSize > 15) { vecSize = 15; }
////    char *p = outBuf;
////    int len = 0;
////    len = sprintf(p, "[");
////    p += len;
////    for (int i = 0; i < vecSize; i++) {
////        std::vector<_PointI32> single = vecs[i];
////        if (i != 0) {
////            len = sprintf(p, ",");
////            p += len;
////        }
////        len = sprintf(p, "{\"name\":\"auto_%d\",\"id\":%d,\"vertexs\":[", i, 100 + i);
////        p += len;
////        for (int j = 0; j < single.size(); j++) {
////            if (j != 0) {
////                len = sprintf(p, ",");
////                p += len;
////            }
////            len = sprintf(p, "[%d,%d]", single[j].x, single[j].y);
////            p += len;
////        }
////        len = sprintf(p, "]}");
////        p += len;
////    }
////    len = sprintf(p, "]");
////    p += len;
////    LOGE("====== %d:%s", (p - outBuf), outBuf);
////    return len;
////}
////
/////**
//// * 把一个jstring转换成一个c语言的char* 类型.
//// */
//////char *_JString2CStr(JNIEnv *env, jstring jstr) {
//////    char *rtn = NULL;
//////    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
//////    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
//////    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
//////    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid,
//////                                                            strencode); // String .getByte("GB2312");
//////    jsize alen = (*env)->GetArrayLength(env, barr);
//////    jbyte *ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
//////    if (alen > 0) {
//////        rtn = (char *) malloc(alen + 1); //"\0"
//////        memcpy(rtn, ba, alen);
//////        rtn[alen] = 0;
//////    }
//////    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
//////    return rtn;
//////}
////
////static jstring charTojstring(JNIEnv *env, const char *pat) {
////    return (env)->NewStringUTF(pat);  //直接调用NewStringUTF方法
//////    //定义java String类 strClass
//////    jclass strClass = (env)->FindClass("Ljava/lang/String");
//////    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
//////    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
//////    //建立byte数组
//////    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
//////    //将char* 转换为byte数组
//////    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
//////    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
//////    jstring encoding = (env)->NewStringUTF("GB2312");
//////    //将byte数组转换为java String,并输出
//////    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
////}
////
////extern "C"
////JNIEXPORT jstring JNICALL
////Java_com_example_mytakeout_JniUtils_mapcutHough(JNIEnv *env, jobject instance,
////                                                jbyteArray mapData_, jint OrgW,
////                                                jint OrgH, jfloat xMin, jfloat yMin,
////                                                jfloat resolution) {
////    jbyte *mapData = env->GetByteArrayElements(mapData_, NULL);
////    int mapLen = env->GetArrayLength(mapData_);
////    // TODO
////    std::vector<std::vector<_PointI32> > rets = MapCut_Hough((uint8_t *) mapData, OrgW, OrgH, xMin,
////                                                             yMin, resolution);
////    char outBuf[2 * 1024];
////    int outLen = GetVectorString(rets, outBuf, sizeof(outBuf));
////    env->ReleaseByteArrayElements(mapData_, mapData, 0);
////    return charTojstring(env, outBuf);
////}
//
////extern "C"
////JNIEXPORT jstring JNICALL
////Java_com_example_mytakeout_JniUtils_handlerImage(JNIEnv *env) {
////
////    return env->NewStringUTF("1122");
////}
////cvremap(byte[] videoData,float[] tempData,int inHeight, int inWidth, int outHeight, int outWidth );
//extern "C"
//JNIEXPORT jbyteArray JNICALL
//Java_com_example_mytakeout_JniUtils_cvremap
//        (JNIEnv *env, jobject, jbyteArray videoData, jfloatArray tempData, jint inHeight,
//         jint inWidth,
//         jint outHeight, jint outWidth) {
//    //jbyteArray --> jbyte
//    LOGE("4444444444444");
//    jbyte *pSrcData = env->GetByteArrayElements(videoData, NULL);
//    if (pSrcData == NULL) {
//        LOGE("pSrcData为空");
//    }
//
////    jsize len = env->GetArrayLength(videoData);
////    jbyte *pSrcData = (jbyte *) malloc(len * sizeof(jbyte));
////    env->SetByteArrayRegion(videoData, 0, len, pSrcData);
//
//    jfloat *pMapData = env->GetFloatArrayElements(tempData, NULL);
//    if (pMapData == NULL) {
//        LOGE("pMapData为空");
//    }
//
////    jsize len1 = env->GetArrayLength(tempData);
////    jfloat *pMapData = (float *) malloc(len * sizeof(jbyte));
////    env->SetFloatArrayRegion(tempData, 0, len, pMapData);
//
//    jbyte *result = reinterpret_cast<jbyte *>(remap(reinterpret_cast<const uint8_t *>(pSrcData),
//                                                    pMapData, inHeight, inWidth, outHeight, outWidth));
//
//
//    LOGE("66666666666");
////    jbyteArray bytes = env->NewByteArray(sizeof(result));
////    env->SetByteArrayRegion(bytes, 0, sizeof(bytes), result);
//    int len = outHeight * inHeight * 3;
//    jbyteArray bytes = env->NewByteArray(len);
//    env->SetByteArrayRegion(bytes, 0, len, result);
//    jbyte *jbyte1 = env->GetByteArrayElements(bytes, NULL);
//
//    float a1 = *jbyte1;
//    float b1 = *(jbyte1 + 1);
//    float c1 = *(jbyte1 + 2);
//
//    LOGE("777777777777");
//
//    LOGE("88888888888");
//    env->ReleaseByteArrayElements(videoData, pSrcData, 0);
//    env->ReleaseFloatArrayElements(tempData, pMapData, 0);
//    return bytes;
//}
//
//
//extern "C"
//JNIEXPORT jfloatArray JNICALL
//Java_com_example_mytakeout_JniUtils_rectifyMap2
//        (JNIEnv *env, jobject, jint inWidth, jint inHeight, jfloat angleX, jfloat angleY,
//         jfloat angleZ,
//         jint outWidth, jint outHeight, jfloat radius) {
//    LOGE("hahahhaha1111");
//    float *temp = NULL;
//    temp = rectifyMap2(inWidth, inHeight, angleX, angleY, angleZ, outWidth, outHeight, radius);
//    float a = *temp;
//    float b = *(temp + 1);
//    float c = *(temp + 2);
//    //float* -->jfloatArray
//    LOGE("hahahhaha= %d", sizeof(temp));
//    int len = sizeof(float) * inWidth * inHeight * 2;
//    jfloatArray array = env->NewFloatArray(len);
//    env->SetFloatArrayRegion(array, 0, len / sizeof(float), temp);
//    jfloat *jfloat1 = env->GetFloatArrayElements(array, NULL);
//    if (array == NULL) {
//        LOGE("array是空数据");
//    }
//    return array;
//}