package com.ljy.devring.other.toast;

import android.view.View;

/**
 * author : HJQ
 * github : https://github.com/getActivity/ToastUtils
 * time   : 2018/09/01
 * desc   : 默认样式接口
 * modify : ljy
 */
public interface IToastStyle {

    int getGravity();//吐司的重心

    int getXOffset();//X轴偏移

    int getYOffset();//Y轴偏移

    int getCornerRadius();//文本框圆角大小

    int getBackgroundColor();//文本框背景颜色

    int getTextColor();//文本框的文本颜色

    float getTextSize();//文本框的文本大小sp

    int getMaxLines();//文本框的最大行数

    int getPaddingLeft();//文本框的左边内边距

    int getPaddingTop();//文本框的顶部内边距

    int getPaddingRight();//文本框的右边内边距

    int getPaddingBottom();//文本框的底部内边距

    View getCustomToastView();//自定义的Toast布局
    //如果返回null的话将使用默认提供TextView作为布局并用作文本显示
    //如果不返回null的话将使用返回的View作为布局，并将View中的第一个TextView用作文本显示
    //具体可看RingToast中的init方法
}