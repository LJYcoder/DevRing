package com.dev.base.view.activity.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.util.systemtype.StatusBarModeUtil;
import com.dev.base.view.widget.loadlayout.LoadLayout;

import org.zackratos.ultimatebar.UltimateBar;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * author:  ljy
 * date:    2017/9/13
 * description:
 * 含有ToolBar、加载布局（正文，加载中，加载失败，无数据）以及状态栏导航栏颜色调整的activity基类
 * 继承该类后，不需要再绑定ButterKnife
 * 实现setContentLayout来设置布局ID，
 * 实现initView来做视图相关的初始化，
 * 实现obtainData来做数据的初始化
 * 实现initEvent来做事件监听的初始化
 */
public abstract class ToolbarBaseActivity extends BaseActivity {

    LoadLayout mContentLayout;//加载布局，可以显示各种状态的布局, 如加载中，加载成功, 加载失败, 无数据

    @BindView(R.id.base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_right)
    TextView mTvToolbarRight;//toolbar右侧的文字控件

    //状态栏导航栏颜色工具类
    private UltimateBar ultimateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentView(int layoutResId) {
        super.setContentView(R.layout.activity_base_toolbar);

        addViewToContainer(layoutResId);
        init();
    }

    //该方法由子类DrawerBaseActivity调用，设置内容布局
    //由于视图初始化的顺序问题，所以init()改由DrawerBaseActivity执行
    public void setContentViewByDrawer(int layoutResId) {
        super.setContentView(R.layout.activity_base_toolbar);

        addViewToContainer(layoutResId);
    }

    //将布局加入到LoadLayout中
    public void addViewToContainer(int layoutResId) {
        mContentLayout = (LoadLayout) findViewById(R.id.base_content_layout);
        View view = getLayoutInflater().inflate(layoutResId, null);
        mContentLayout.removeAllViews();
        mContentLayout.addSuccessView(view);
    }

    public void init() {
        ButterKnife.bind(this);//butterknife绑定

        mToolbar.setTitle("");//必须再setSupportActionBar之前将标题置为空字符串，否则具体页面设置标题会无效
        this.setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnKeyClickListener != null) {//如果没有设置返回事件的监听，则默认finish页面。
                    mOnKeyClickListener.clickBack();
                } else {
                    finish();
                }
            }
        });

        //状态栏导航栏颜色工具类
        ultimateBar = new UltimateBar(this);
        ultimateBar.setColorBar(ContextCompat.getColor(this, R.color.colorPrimaryDark));//设置颜色，也可加入第二个参数控制不透明度（布局内容不占据状态栏空间）
//        ultimateBar.setTransparentBar(ContextCompat.getColor(this, R.color.lite_blue), 50);//设置颜色，第二个参数为不透明度（布局内容占据状态栏空间）
//        ultimateBar.setImmersionBar();//设置为全透明（布局内容占据状态栏空间）
//        ultimateBar.setHintBar();//隐藏状态栏导航栏
//        ultimateBar.setColorBarForDrawer(ContextCompat.getColor(this, R.color.lite_blue));//当有使用DrawerLayout时，用该句来设置颜色

    }

    public UltimateBar getUltimateBar() {
        return ultimateBar;
    }

    // 只有魅族（Flyme4+），小米（MIUI6+），android（6.0+）可以设置状态栏中的图标字体为黑白
    public boolean setStatusBarMode(boolean dark) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (StatusBarModeUtil.MIUISetStatusBarLightMode(window, dark)) {
                result = true;
            } else if (StatusBarModeUtil.FlymeSetStatusBarLightMode(window, dark)) {
                result = true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarModeUtil.Android6SetStatusBarLightMode(window, dark);
                result = true;
            }
        }
        return result;
    }

    //设置toolbar右侧文字控件的内容
    public void setToolbarRightTv(String text) {
        if (mTvToolbarRight != null) {
            mTvToolbarRight.setText(text);
        }
    }

    /**
     * 获取toolbar右侧的文字控件
     */
    public TextView getTvToolbarRight() {
        return mTvToolbarRight;
    }


    /**
     * 获取toolbar
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 内容布局
     *
     * @return 页面主要内容布局
     */
    public LoadLayout getLoadLayout() {
        return mContentLayout;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mContentLayout != null) {
            mContentLayout.showAnim();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mContentLayout != null) {
            mContentLayout.closeAnim();
        }
    }


}
