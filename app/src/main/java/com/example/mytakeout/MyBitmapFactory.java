package com.example.mytakeout;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;

import java.nio.ByteBuffer;

/**
 * 创建时间：2019/3/10  18:26
 * 作者：5#
 * 描述：TODO
 */
public class MyBitmapFactory {

    /*
     * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
     */
    public static Bitmap createMyBitmap(byte[] data, int width, int height) {
        int[] colors = convertByteToColor(data);
        if (colors == null) {
            return null;
        }

        Bitmap bmp = null;

        try {
            bmp = Bitmap.createBitmap(colors, 0, width, width, height,
                    Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            return null;
        }

        return bmp;
    }


    /*
     * 将RGB数组转化为像素数组
     */
    public static int[] convertByteToColor(byte[] data) {
        int size = data.length;
        if (size == 0) {
            return null;
        }


        // 理论上data的长度应该是3的倍数，这里做个兼容
        int arg = 0;
        if (size % 3 != 0) {
            arg = 1;
        }

        int[] color = new int[size / 3 + arg];
        int red, green, blue;


        if (arg == 0) {                                    //  正好是3的倍数
            for (int i = 0; i < color.length; ++i) {

                color[i] = (data[i * 3] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00) |
                        (data[i * 3 + 2] & 0x000000FF) |
                        0xFF000000;
            }
        } else {                                        // 不是3的倍数
            for (int i = 0; i < color.length - 1; ++i) {
                color[i] = (data[i * 3] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00) |
                        (data[i * 3 + 2] & 0x000000FF) |
                        0xFF000000;
            }

            color[color.length - 1] = 0xFF000000;                    // 最后一个像素用黑色填充
        }

        return color;
    }


    public static byte[] bitmap2RGB(Bitmap bitmap) {

        if (bitmap == null) {

            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        byte screenByte[] = new byte[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            shortToByteArray1((short) pixels[i], screenByte, i * 2);
        }

        return screenByte;
    }

    public static int shortToByteArray1(short i, byte[] data, int offset) {
        data[offset + 1] = (byte) (i >> 8 & 255);
        data[offset] = (byte) (i & 255);
        return offset + 2;
    }

    /**
     * @方法描述 Bitmap转RGB
     */
    public static byte[] bitmap2RGB2(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B

        }

        return pixels;
    }


    /**
     * @方法描述 Bitmap转RGB
     */
    public static byte[] getRGBFromBMP(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        byte[] pixels = new byte[w * h * 3]; // Allocate for RGB

        int k = 0;

        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                int color = bmp.getPixel(y, x);
                pixels[k * 3] = (byte) Color.red(color);
                pixels[k * 3 + 1] = (byte) Color.green(color);
                pixels[k * 3 + 2] = (byte) Color.blue(color);
                k++;
            }
        }

        return pixels;
    }

    public static Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

}