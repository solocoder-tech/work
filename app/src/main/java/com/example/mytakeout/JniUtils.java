package com.example.mytakeout;

public class JniUtils {
    static {
        System.loadLibrary("native-ld");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    //    MapCut_ErodeContourBox(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);
    public native String erodeContourBox(byte[] mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);

    //    MapCut_Hough(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);
    public native String mapcutHough(byte[] mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);
}
