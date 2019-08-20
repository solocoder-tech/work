package com.ldvideo;

public class JniUtils {
    static {
        System.loadLibrary("native-ld-video");
    }


    public native byte[] remap(byte[] pSrcData, int inHeight, int inWidth, int outHeight, int outWidth);


    public native void yuv420p2yuv420sp(byte[] yuv420p,byte[] yuv420sp,int width,int height);

    public static native void init(String mp4FilePath, int widht, int height);

    public static native int writeH264Data(byte[] data, int size);

    public static native void close();

}
