package com.example.mytakeout.anim;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 创建时间：2018/12/16  12:53
 * 作者：5#
 * 描述：TODO
 */
public class CustomAnimal extends Animation {
    private int mWidth;
    private int mHeight;

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        Matrix matrix = t.getMatrix();
        matrix.preScale(interpolatedTime, interpolatedTime);//缩放
        matrix.preRotate(interpolatedTime * 360);//旋转
        //下面的Translate组合是为了将缩放和旋转的基点移动到整个View的中心，不然系统默认是以View的左上角作为基点
        matrix.preTranslate(-mWidth / 2, -mHeight / 2);
        matrix.postTranslate(mWidth / 2, mHeight / 2);
    }
}
