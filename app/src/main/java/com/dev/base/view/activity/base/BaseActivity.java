package com.dev.base.view.activity.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.View;

import com.dev.base.app.MyApplication;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.util.ActivityStackManager;
import com.dev.base.util.CommonUtil;
import com.dev.base.util.FrescoUtil;
import com.dev.base.util.log.LogUtil;
import com.dev.base.view.widget.ProgressDialog;

import rx.subjects.PublishSubject;

/**
 * date：      2017/9/13
 * version     1.0
 * description: Activity的基类，包含Activity栈管理，网络状态监听，取消网络请求等
 */

public abstract class BaseActivity extends AbstractActivity implements IBaseActivity {

    private static final String TAG = BaseActivity.class.getSimpleName ();
    /**
     * Android Application 实例
     */
    private MyApplication mApplication = null;
    /**
     * 加载中的弹窗
     */
    private ProgressDialog mProgressDialog;
    /**
     * 页面的堆栈管理
     */
    private ActivityStackManager mStackManager;
    /**
     * 用于控制retrofit的生命周期，以便在destroy或其他状态时终止网络请求
     */
    public final PublishSubject<LifeCycleEvent> lifecycleSubject = PublishSubject.create();
    /**
     * 用于提供lifecycleSubject到RetrofitUtil中,方便Presenter中直接通过IBaseView获取lifecycleSubject，而不用每次都作为参数传递过去
     */
    public PublishSubject<LifeCycleEvent> getLifeSubject() {
        return lifecycleSubject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        init();

        //由具体的activity实现
        setContentLayout();
        initView();
        obtainData();
        initEvent();

    }

    private void init() {
        this.mApplication = MyApplication.getInstance();
        mStackManager = mApplication.getActivityStackManager();
        mStackManager.pushOneActivity(this);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(LifeCycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(LifeCycleEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mStackManager.popOneActivity(this);
        lifecycleSubject.onNext(LifeCycleEvent.DESTROY);
        super.onDestroy();
    }

    /**
     * 显示圆形进度对话框
     */
    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.showDialog();
    }

    /**
     * 关闭进度对话框
     */
    public void dismissLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.dismissDialog();
    }

    /**
     * 显示圆形进度对话框
     */
    public void showNoCancelLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.showDialog();
        mProgressDialog.setCancelable(false);
    }

    /**
     * 隐藏输入法
     */
    public void hideInput() {
        View view = getWindow().peekDecorView();
        CommonUtil.hideSoftInput(getContext(), view);
    }

    /**
     * 获取页面的堆栈管理
     */
    public ActivityStackManager getActivityStackManager() {
        return mStackManager;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public int getResourceColor(@ColorRes int colorId) {
        return ResourcesCompat.getColor(getResources(), colorId, null);
    }

    @Override
    public String getResourceString(@StringRes int stringId) {
        return getResources().getString(stringId);
    }

    @Override
    public String getResourceString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }

    @Override
    public Drawable getResourceDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(getResources(), id, null);
    }

    @Override
    public void onLowMemory() {
        LogUtil.e("内存不足");
        //清空图片内存缓存（包括Bitmap缓存和未解码图片的缓存）
        FrescoUtil.clearMemoryCaches();
        super.onLowMemory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mOnKeyClickListener != null) {//如果没有设置返回事件的监听，则默认finish页面。
                    mOnKeyClickListener.clickBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    OnKeyClickListener mOnKeyClickListener;

    public void setOnKeyListener(OnKeyClickListener onKeyClickListener) {
        this.mOnKeyClickListener = onKeyClickListener;
    }

    /**
     * 返回键的监听，供页面设置自定义的返回键行为
     */
    public interface OnKeyClickListener {
        /**
         * 点击了返回键
         */
        void clickBack();

    }

}
