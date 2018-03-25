package com.dev.base.mvp.view.widget.loadlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dev.base.R;


/**
 * author：    zp
 * date：      2015/10/6 & 10:51
 * version     1.0
 * description:
 * modify by   ljy
 */
public abstract class BaseLoadLayout extends FrameLayout implements State {

    /**
     * 加载动画类
     */
    private AnimationDrawable mAnimationDrawable;
    private View mSuccessView;
    protected View mLoadingView;
    protected View mFailedView;
    protected View mNoDataView;
    private int mState = State.SUCCESS;
    private OnLoadListener mLoadListener;

    protected abstract View createLoadingView();

    protected abstract View createLoadFailedView();

    protected abstract View createNoDataView();

    public BaseLoadLayout(Context context) {
        super(context);
        init();
    }

    public BaseLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseLoadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("只允许有一个子视图!");
        }

        if (getChildAt(0) != null) {
            mSuccessView = getChildAt(0);
        }
    }

    public void addSuccessView(View view) {
        if (getChildCount() >= 1) {
            throw new RuntimeException("只允许有一个子视图!");
        }
        mSuccessView = view;
        addView(view);
    }

    /**
     * 加载中
     */
    private void onLoading() {
        if (mSuccessView != null) {
            mSuccessView.setVisibility(View.GONE);
        }
        if (mFailedView != null) {
            mFailedView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            mLoadingView = createLoadingView();
            addView(mLoadingView);
            initAnim(mLoadingView);
        }
        if (mLoadListener != null) {
            mLoadListener.onLoad();
        }
        startAnim();
    }

    /**
     * 加载失败
     */
    private void onLoadFailed() {
        if (mSuccessView != null) {
            mSuccessView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mFailedView != null) {
            mFailedView.setVisibility(View.VISIBLE);
        } else {
            mFailedView = createLoadFailedView();
            addView(mFailedView);
        }
        stopAnim();
    }


    /**
     * 无数据处理
     */
    private void onLoadNoData() {
        if (mSuccessView != null) {
            mSuccessView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mFailedView != null) {
            mFailedView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mNoDataView = createNoDataView();
            addView(mNoDataView);
        }
        stopAnim();
    }

    /**
     * 加载成功
     */
    private void onLoadSuccess() {
        if (mSuccessView != null) {
            mSuccessView.setVisibility(View.VISIBLE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mFailedView != null) {
            mFailedView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
        stopAnim();
    }

    public void setLayoutState(final int state) {
        this.mState = state;

        switch (this.mState) {
            case LOADING:
                onLoading();
                break;
            case FAILED:
                onLoadFailed();
                break;
            case SUCCESS:
                onLoadSuccess();
                break;
            case NO_DATA:
                onLoadNoData();
                break;
            default:
                break;
        }
    }

    public int getLayoutState() {
        return mState;
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mLoadListener = listener;
    }

    private void initAnim(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_loading_kk);
        if (imageView.getBackground() != null) {
            mAnimationDrawable = (AnimationDrawable)imageView.getBackground();
        }
    }

    public void startAnim() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.start();
        }
    }

    public void stopAnim() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

}
