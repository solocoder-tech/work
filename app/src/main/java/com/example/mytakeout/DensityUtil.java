//package com.example.mytakeout;
//
//import android.content.Context;
//import android.graphics.Paint.FontMetrics;
//import android.text.TextPaint;
//import android.util.DisplayMetrics;
//import android.view.Display;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.inmotion.sweeprobot.xrobot.Module.Application.MyApplication;
//
//import java.lang.reflect.Method;
//
//public class DensityUtil {
////    private static final Context mContext = MyApplication.getInstance().getApplicationContext();
//
//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px(float dpValue) {
//        final float scale = mContext.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    /**
//     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
//     */
//    public static int px2dip(float pxValue) {
//        final float scale = mContext.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }
//
//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2pxF(float dpValue) {
//        final float scale = mContext.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//
//    /**
//     * 将sp值转换为px值，保证文字大小不变
//     *
//     * @param spValue
//     * @param fontScale （DisplayMetrics类中属性scaledDensity）
//     * @return
//     */
//    public static int sp2px(float spValue) {
//        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
//    }
//
//    /**
//     * 获取屏幕宽度
//     */
//    public static int getScreenWidth() {
//        return mContext.getResources().getDisplayMetrics().widthPixels;
//    }
//
//    /**
//     * 获取屏幕高度
//     */
//    public static int getScreenHeight() {
//        return mContext.getResources().getDisplayMetrics().heightPixels;
//    }
//
//    public static float getDensity(){
//        return mContext.getResources().getDisplayMetrics().density;
//    }
//
//    public static float getTextViewLength(TextView textView, String text) {
//        TextPaint paint = textView.getPaint();
//        // 得到使用该paint写上text的时候,像素为多少
//        float textLength = paint.measureText(text);
//        return textLength;
//    }
//
//
//    public static float getTextViewHeight(TextView textView) {
//        //TextPaint paint = textView.getPaint();
//        TextPaint paint = textView.getPaint();
//        FontMetrics fm = paint.getFontMetrics();
//
//        return (float) Math.ceil(fm.descent - fm.ascent);
//    }
//
//    /**
//     * 获取 虚拟按键的高度
//     *
//     * @param context
//     * @return
//     */
//    public static int getBottomStatusHeight(Context context) {
//        int totalHeight = getDpi(context);
//
//        int contentHeight = getScreenHeight(context);
//
//        return totalHeight - contentHeight;
//    }
//
//    public static int getDpi(Context context) {
//        int dpi = 0;
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        @SuppressWarnings("rawtypes")
//        Class c;
//        try {
//            c = Class.forName("android.view.Display");
//            @SuppressWarnings("unchecked")
//            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//            method.invoke(display, displayMetrics);
//            dpi = displayMetrics.heightPixels;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dpi;
//    }
//
//    public static int getScreenHeight(Context context) {
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        return outMetrics.heightPixels;
//    }
//
//    /**
//     * 获取状态栏的高度
//     * @param context
//     * @return
//     */
//    public static int getStatusBarHeight(Context context) {
//        int result = 0;
//        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = context.getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }
//
//
//
//}
