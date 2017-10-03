package com.dev.base.view.anim;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 * @author ljy
 * @Description 帧动画控制类
 * @datetime 2017/9/7
 */
public class FrameAnimation {

    private ImageView mIvLoading;
    private AnimationDrawable animationDrawable;

    public FrameAnimation(ImageView ivLoading) {
        this.mIvLoading = ivLoading;
    }

    /**
     * 显示动画
     */
    public void showAnim() {
        if (mIvLoading != null && (animationDrawable = (AnimationDrawable) mIvLoading.getBackground()) != null) {
            animationDrawable.start();
        }
    }


    /**
     * 关闭动画
     */
    public void closeAnim() {
        if (mIvLoading != null && animationDrawable != null) animationDrawable.stop();
    }


}
