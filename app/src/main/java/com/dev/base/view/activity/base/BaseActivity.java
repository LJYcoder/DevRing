package com.dev.base.view.activity.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.dev.base.R;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.util.ActivityStackManager;
import com.dev.base.util.CommonUtil;
import com.dev.base.util.FrescoUtil;
import com.dev.base.util.SystemTypeUtil;
import com.dev.base.util.log.LogUtil;
import com.dev.base.view.widget.ProgressDialog;

import org.zackratos.ultimatebar.UltimateBar;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * date：      2017/9/13
 * version     1.0
 * description: Activity的基类，包含Activity栈管理，状态栏/导航栏颜色设置，销毁时取消网络请求等
 * 子类需要进行ButterKnife绑定
 * <p>
 * http://www.jianshu.com/p/3d9ee98a9570
 */

public abstract class BaseActivity extends AbstractActivity implements IBaseActivity {

    //"加载中"的弹窗
    private ProgressDialog mProgressDialog;
    //页面的堆栈管理
    private ActivityStackManager mStackManager;
    //状态栏导航栏颜色工具类
    private UltimateBar ultimateBar;

    //用于控制retrofit的生命周期，以便在destroy或其他状态时终止网络请求
    public PublishSubject<LifeCycleEvent> lifecycleSubject = PublishSubject.create();

    //该方法用于提供lifecycleSubject（相当于实现了IBaseView中的getLifeSubject抽象方法）。
    //方便Presenter中直接通过IBaseView获取lifecycleSubject，而不用每次都作为参数传递过去
    public PublishSubject<LifeCycleEvent> getLifeSubject() {
        return lifecycleSubject;
    }

    //一般的rxjava使用场景下，控制Observable的生命周期
    public <T> ObservableTransformer<T, T> controlLife(final LifeCycleEvent event) {
        return  new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<LifeCycleEvent> lifecycleObservable = lifecycleSubject.filter(new Predicate<LifeCycleEvent>() {
                    @Override
                    public boolean test(LifeCycleEvent lifeCycleEvent) throws Exception {
                        //当生命周期为event状态时，发射事件
                        return lifeCycleEvent.equals(event);
                    }
                }).take(1);

                return upstream.takeUntil(lifecycleObservable);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        init();

        setContentLayout();//由具体的activity实现，设置内容布局ID
        initBarColor();//初始化状态栏/导航栏颜色，需在设置了布局后再调用
        initView();//由具体的activity实现，做视图相关的初始化
        obtainData();//由具体的activity实现，做数据的初始化
        initEvent();//由具体的activity实现，做事件监听的初始化

    }

    private void init() {
        mStackManager = ActivityStackManager.getInstance();
        mStackManager.pushOneActivity(this);

    }

    private void initBarColor() {
        int color = getResourceColor(R.color.colorPrimary);
        setBarColor(color, 0, color, 0);
    }


    public UltimateBar getUltimateBar() {
        if (ultimateBar == null) {
            ultimateBar = new UltimateBar(this);
        }
        return ultimateBar;
    }

    //设置状态栏、导航栏颜色，第二个参数控制透明度，布局内容不占据状态栏空间
    public void setBarColor(int statusColor, int statusAlpha, int navColor, int navAlpha) {
        getUltimateBar().setColorBar(statusColor, statusAlpha, navColor, navAlpha);
    }

    //单独设置状态栏的颜色，第二个参数控制透明度，布局内容不占据状态栏空间
    public void setStatusBarColor(int color, int alpha) {
        getUltimateBar().setColorStatusBar(color, alpha);
    }

    //设置状态栏、导航栏颜色（有DrawerLayout时可使用这种），第二个参数控制透明度，布局内容不占据状态栏空间
    public void setBarColorForDrawer(int statusColor, int statusAlpha, int navColor, int navAlpha) {
        getUltimateBar().setColorBarForDrawer(statusColor, statusAlpha, navColor, navAlpha);
    }

    //单独设置状态栏的颜色（有DrawerLayout时可使用这种），第二个参数控制透明度，布局内容不占据状态栏空间
    public void setStatusBarColorForDrawer(int color, int alpha) {
        getUltimateBar().setColorBarForDrawer(color, alpha);
    }

    //设置半透明的状态栏、导航栏颜色，第二个参数控制透明度，布局内容占据状态栏空间
    public void setBarTranslucent(int statusColor, int statusAlpha, int navColor, int navAlpha) {
        getUltimateBar().setTransparentBar(statusColor, statusAlpha, navColor, navAlpha);
    }

    //单独设置半透明的状态栏颜色，第二个参数控制透明度，布局内容不占据状态栏空间
    public void setStatusBarTranslucent(int color, int alpha) {
        getUltimateBar().setColorBarForDrawer(color, alpha);
    }

    //设置全透明的状态栏、导航栏颜色，布局内容占据状态栏空间，参数为是否也应用到
    public void setTransparentBar(boolean applyNav) {
        getUltimateBar().setImmersionBar(applyNav);
    }

    //隐藏状态栏、导航栏，布局内容占据状态栏导航栏空间，参数为是否也应用到导航栏上
    public void hideBar(boolean applyNav) {
        getUltimateBar().setHideBar(applyNav);
    }


    // 只有魅族（Flyme4+），小米（MIUI6+），android（6.0+）可以设置状态栏中图标、字体的颜色模式（深色模式/亮色模式）
    public boolean setStatusBarMode(boolean isDark) {
        Window window = getWindow();
        return SystemTypeUtil.setStatusBarLightMode(window, isDark);
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
        super.onDestroy();
        lifecycleSubject.onNext(LifeCycleEvent.DESTROY);
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
     * 显示圆形进度对话框（不可关闭）
     */
    public void showNoCancelLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.showDialog();
        mProgressDialog.setCancelable(false);
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
     * 按键的监听，供页面设置自定义的按键行为
     */
    public interface OnKeyClickListener {
        /**
         * 点击了返回键
         */
        void clickBack();

        //可加入其它按键事件
    }

}
