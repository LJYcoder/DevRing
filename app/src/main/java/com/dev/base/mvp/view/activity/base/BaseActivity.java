package com.dev.base.mvp.view.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dev.base.R;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.ljy.devring.base.activity.ActivityLifeCallback;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.util.ColorBar;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * author:  ljy
 * date：    2018/3/19
 * description: Activity的基类
 *
 * 此基类进行了状态栏/导航栏颜色设置、ButterKnife绑定、Presenter销毁操作。
 *
 * 由于Java的单继承的限制，DevRing库就不提供基类了，所以把一些基类操作通过Application.ActivityLifecycleCallbacks来完成
 * 但是前提是需实现你的Activity需实现IBaseActivity接口。
 * ActivityLifecycleCallbacks进行的基类操作有：（具体请查看 {@link ActivityLifeCallback})
 * 1.Retrofit的生命周期控制
 * 2.EventBus的注册/注销
 * 3.Activity栈管理的入栈与出栈
 * 4.FragmentLifecycleCallbacks的注册
 *
 * 这种基类实现方式，是从JessYan大神那学来的 <a>https://www.jianshu.com/p/75a5c24174b2</a>
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IBaseActivity {

    @BindColor(R.color.colorPrimary)
    int mColor;
    @Inject
    @Nullable
    protected P mPresenter;

    protected abstract int getContentLayout();
    protected abstract void initView(Bundle savedInstanceState);
    protected abstract void initData(Bundle savedInstanceState);
    protected abstract void initEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
            ButterKnife.bind(this);
        }
        initBarColor();//初始化状态栏/导航栏颜色，需在设置了布局后再调用
        initView(savedInstanceState);//由具体的activity实现，做视图相关的初始化
        initData(savedInstanceState);//由具体的activity实现，做数据的初始化
        initEvent();//由具体的activity实现，做事件监听的初始化
    }

    private void initBarColor() {
        ColorBar.newColorBuilder()
                .applyNav(true)
                .navColor(mColor).navDepth(0)
                .statusColor(mColor)
                .statusDepth(0)
                .build(this)
                .apply();
    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public boolean isUseFragment() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }
}
