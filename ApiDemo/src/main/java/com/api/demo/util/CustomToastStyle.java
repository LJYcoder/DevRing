package com.api.demo.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.api.demo.R;
import com.ljy.devring.DevRing;
import com.ljy.devring.other.toast.IToastStyle;

/**
 * author:  ljy
 * date:    2018/10/16
 * description:
 */

public class CustomToastStyle implements IToastStyle{

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public int getCornerRadius() {
        return 0;
    }

    @Override
    public int getBackgroundColor() {
        return 0X00000000;
    }

    @Override
    public int getTextColor() {
        return 0XEEFFFFFF;
    }

    @Override
    public float getTextSize() {
        return 15;
    }

    @Override
    public int getMaxLines() {
        return 5;
    }

    @Override
    public int getPaddingLeft() {
        return 0;
    }

    @Override
    public int getPaddingTop() {
        return 0;
    }

    @Override
    public int getPaddingRight() {
        return 0;
    }

    @Override
    public int getPaddingBottom() {
        return 0;
    }

    @Override
    public View getCustomToastView() {
        View view = LayoutInflater.from(DevRing.application()).inflate(R.layout.layout_custom_toast, null);
        return view;
    }
}
