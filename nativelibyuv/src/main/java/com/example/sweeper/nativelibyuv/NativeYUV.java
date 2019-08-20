package com.example.sweeper.nativelibyuv;

/**
 * 创建时间：2019/8/3  14:38
 * 作者：5#
 * 描述：TODO
 */
public class NativeYUV {
    static {
        System.loadLibrary("native-yuv");
    }


    public native byte[] yuv2rgb(byte[] yuvData, int width, int height);
}
