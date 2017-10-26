package com.dev.base.view.widget.loadlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.dev.base.R;


/**
 * author：   zp
 * date：     2015/9/2 10:22
 * version    1.0
 * description 这个Layout用于加载,可以显示各种状态的布局, 如加载中，加载成功, 加载失败, 无数据
 * modify by  ljy
 */
public class LoadLayout extends BaseLoadLayout {

    private int mLoadingViewId = R.layout.layout_load_loading_view;
    private int mFailedViewId = R.layout.layout_load_failed_view;
    private int mNoDataViewId = R.layout.layout_load_null_data_view;
    private View mLoadingView;
    private View mFailedView;
    private View mNoDataView;

    public LoadLayout(Context context) {
        super(context);
    }

    public LoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LoadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected View createLoadingView() {
        mLoadingView = LayoutInflater.from(getContext()).inflate(mLoadingViewId, null);
        mLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return mLoadingView;
    }

    @Override
    protected View createLoadFailedView() {
        mFailedView = LayoutInflater.from(getContext()).inflate(mFailedViewId, null);
        mFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutState(State.LOADING);
            }
        });
        return mFailedView;
    }

    @Override
    protected View createNoDataView() {
        mNoDataView = LayoutInflater.from(getContext()).inflate(mNoDataViewId, null);
        mNoDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutState(State.LOADING);
            }
        });
        return mNoDataView;
    }

    public void setLoadingViewId(int loadingViewId) {
        this.mLoadingViewId = loadingViewId;
    }

    public void setFailedViewId(int failedViewId) {
        this.mFailedViewId = failedViewId;
    }

    public void setNoDataViewId(int noDataViewId) {
        this.mNoDataViewId = noDataViewId;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getFailedView() {
        return mFailedView;
    }

    public View getNoDataView() {
        return mNoDataView;
    }

    public void closeAnim() {
        if (getLayoutState() == State.LOADING && getAnim() != null) {
            getAnim().closeAnim();
        }
    }

    public void showAnim() {
        if (getLayoutState() == State.LOADING && getAnim() != null) {
            getAnim().closeAnim();
            getAnim().showAnim();
        }
    }

}
