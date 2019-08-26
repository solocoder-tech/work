package com.example.mytakeout.ui.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.mytakeout.utils.LogUtils;


/**
 * PageTransformer是ViewPager内部定义的接口，实现该接口并应用于ViewPager可以控制ViewPager中item view的滑动效果。
 * <p>
 * 我们需重写transformPage方法，这个方法有2个参数。第一个参数是指需要设置滑动效果的页面，包括即将滑出页面，
 * 即将滑入页面，已经滑出的页面，我们可以通过第二个参数判断第一个参数是指哪个页面。第二个参数不是页面的下标，它是一个浮点数，在用户滑动的过程中，它是动态变化的，通过判断它的值可得出第一个参数的具体指向。
 * <p>
 * 如果ViewPager没有设置PageMargin属性，当前页面的position为0，前一个为-1，后一个为1。在滑动的过程中，
 * position不断变化。以左滑为例，当ViewPager向左滑动时，当前页面的position从0逐渐变为-1，
 * 即将滑入的页面position从1逐渐变为0，滑入的后一个页面position变为1。当设置了PageMargin属性时，
 * 当前页面依旧为0，前后页面得加上或减去一个偏移量。（前减后加）。偏移量为  pageMargin / pageWidth 。
 * 我们可以在滑动的过程中通过setAlpha()等方法，改变page的属性达到动态效果。
 */
public class CustPagerTransformer implements ViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    public CustPagerTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 180);
    }

    /**
     * 基准参考点，是指ViewPager处于(最近一次处于)SCROLL_STATE_IDLE状态(此状态下cureent page完整显示，没有滑动偏移。
     * 备注：若处于滑动过程，则取最近一次处于SCROLL_STATE_IDLE状态)时cureent page的position值，为0。
     * transformPage方法中的position都是相对于这个基准参考点的相对值。以基准参考点为中心，建立一维坐标，左侧为负，
     * 右侧为正，来描述page的position值。
     *
     * @param view
     * @param position
     */
    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }

        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        LogUtils.e("transformPage===" + leftInScreen);
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
        }
    }

    /**
     * dp和像素转换
     */
    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}
