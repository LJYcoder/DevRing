package com.dev.base.view.activity.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.util.ToastUtil;
import com.dev.base.view.widget.loadlayout.LoadLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * author:  ljy
 * date:    2017/9/13
 * description:  添加了toolbar和侧滑抽屉的基类
 * 子类不需要再绑定ButterKnife
 * 实现setContentLayout来设置布局ID，
 * 实现initView来做视图相关的初始化，
 * 实现obtainData来做数据的初始化
 * 实现initEvent来做事件监听的初始化
 */

public abstract class DrawerBaseActivity extends BaseActivity {

    LoadLayout mLoadLayout;//加载布局，可以显示各种状态的布局, 如加载中，加载成功, 加载失败, 无数据

    @BindView(R.id.base_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv_menu)
    NavigationView mNavigationView;
    @BindView(R.id.base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_right)
    TextView mTvToolbarRight;//toolbar右侧的文字控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentView(int layoutResId) {
        super.setContentView(R.layout.activity_base_drawer);

        addViewToContainer(layoutResId);
        init();
    }

    //将布局加入到LoadLayout中
    public void addViewToContainer(int layoutResId) {
        mLoadLayout = (LoadLayout) findViewById(R.id.base_content_layout);
        View view = getLayoutInflater().inflate(layoutResId, null);
        mLoadLayout.removeAllViews();
        mLoadLayout.addSuccessView(view);
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

        setDrawer(true);

    }

    /**
     * 初始化侧滑抽屉
     * @param bindArrow 是否将侧滑抽屉与Toolbar左侧的按钮绑定在一起，绑定的话，在抽屉移动的时候，该按钮会有动画变化
     */
    private void setDrawer(boolean bindArrow) {

        if (bindArrow) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            //初始化NavIcon
            mDrawerToggle.syncState();
            //设置toolbar上NavIcon变化的监听器
            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }


        //设置抽屉移动时，内容页面跟随移动缩放的监听器
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                mContent.setTranslationX(drawerView.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        //设置侧滑栏的菜单项图标恢复本来的颜色
//        mNavigationView.setItemIconTintList(null);

        //自定义设置侧滑栏的菜单项字体颜色（含选中与未选中两种颜色）
//        Resources resource = getBaseContext().getResources();
//        ColorStateList csl = resource.getColorStateList(R.color.menu_text_selector); //在res下新建一个color文件夹，在文件夹中建一个selector文件设置选中颜色与默认颜色
//        mNavigationView.setItemTextColor(csl);

        //设置侧滑栏的菜单选中项
//        mNavigationView.setCheckedItem(R.id.nav_item_1);

        //设置侧滑栏的菜单项点击事件(具体可在子类页面中通过getNavigationView()来设置点击事件)
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_item_1:
                        ToastUtil.show(item.getTitle());
                        break;

                    default:
                        ToastUtil.show(item.getTitle());
                        break;
                }
                //收起侧滑栏
                closeDrawer();

                //return true则点击的菜单项会变成选中状态
                //return false则不会变成选中状态
                return false;
            }
        });
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
     * 获取加载布局，从而设置各种加载状态
     */
    public LoadLayout getLoadLayout() {
        return mLoadLayout;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLoadLayout != null) {
            mLoadLayout.closeAnim();
        }
    }


    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else if (mOnKeyClickListener != null) {//如果没有设置返回事件的监听，则默认下面效果。关闭侧滑菜单
                    mOnKeyClickListener.clickBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }


}
