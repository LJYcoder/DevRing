package com.dev.base.view.widget.loadlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dev.base.R;
import com.dev.base.view.anim.FrameAnimation;


/**
 * author：    zp
 * date：      2015/10/6 & 10:51
 * version     1.0
 * description:
 * modify by   ljy
 */
public abstract class BaseLoadLayout extends FrameLayout implements State {

    private static final String TAG = BaseLoadLayout.class.getSimpleName();
    /**
     * 加载动画类
     */
    public FrameAnimation anim;
    private View mSuccessView;
    private View mLoadingView;
    private View mFailedView;
    private View mNullDataView;

    private int mState = State.SUCCESS;
    private OnLoadListener mLoadListener;

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
    }

    private void addViewInternal(@NonNull View child) {
        super.addView(child);
    }

    public void addSuccessView(View view) {
        if (getChildCount() >= 1) {
            throw new RuntimeException("只允许有一个子视图!");
        }
        mSuccessView = view;
        addViewInternal(view);
    }

    protected abstract View createLoadingView();

    protected abstract View createLoadFailedView();

    protected abstract View createNoDataView();

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
        if (mNullDataView != null) {
            mNullDataView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            mLoadingView = createLoadingView();
            addViewInternal(mLoadingView);
            anim = initAnim(mLoadingView);
        }
        if (mLoadListener != null) {
            mLoadListener.onLoad();
        }
        if (anim != null) {
            anim.showAnim();
        }
    }

    /**
     * 加载失败
     */
    private void onLoadFailed() {
        if (mSuccessView != null) {
            mSuccessView.setVisibility(View.GONE);
        }
        if (mNullDataView != null) {
            mNullDataView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mFailedView != null) {
            mFailedView.setVisibility(View.VISIBLE);
        } else {
            mFailedView = createLoadFailedView();
            addViewInternal(mFailedView);
        }
        if (anim != null) {
            anim.closeAnim();
        }
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
        if (mNullDataView != null) {
            mNullDataView.setVisibility(View.VISIBLE);
        } else {
            mNullDataView = createNoDataView();
            addViewInternal(mNullDataView);
        }
        if (anim != null) {
            anim.closeAnim();
        }
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
        if (mNullDataView != null) {
            mNullDataView.setVisibility(View.GONE);
        }
        if (anim != null) {
            anim.closeAnim();
        }
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

    private FrameAnimation initAnim(View inflated) {
        ImageView iv = (ImageView) inflated.findViewById(R.id.iv_loading_kk);
        FrameAnimation la = new FrameAnimation(iv);
        return la;
    }

    public FrameAnimation getAnim() {
        return anim;
    }
}
