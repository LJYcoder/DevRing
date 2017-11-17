package com.dev.base.view.activity.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dev.base.R;
import com.dev.base.util.ToastUtil;

import butterknife.BindView;


/**
 * author:  ljy
 * date:    2017/9/13
 * description:  继承ToolbarBaseActivity, 额外添加了侧滑抽屉的基类
 * 子类不需要再绑定ButterKnife
 * 实现setContentLayout来设置布局ID，
 * 实现initView来做视图相关的初始化，
 * 实现obtainData来做数据的初始化
 * 实现initEvent来做事件监听的初始化
 */

public abstract class DrawerBaseActivity extends ToolbarBaseActivity {

    LinearLayout mContentLayout;
    @BindView(R.id.base_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv_menu)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentView(int layoutResId) {
        setContentView(layoutResId, true);
    }

    public void setContentView(int layoutResId, boolean bindArrow) {
        super.setContentViewByDrawer(R.layout.activity_base_drawer);

        mContentLayout = (LinearLayout) findViewById(R.id.base_drawer_content_layout);
        View view = getLayoutInflater().inflate(layoutResId, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//有时该view的match_parent属性失效，重新设置一次
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);

        super.init();
        setDrawer(bindArrow);
    }

    /**
     * 初始化侧滑抽屉
     * @param bindArrow 是否将侧滑抽屉与Toolbar左侧的按钮绑定在一起，绑定的话，在抽屉移动的时候，该按钮会有动画变化
     */
    private void setDrawer(boolean bindArrow) {

        if (bindArrow) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolbar(), R.string.open, R.string.close) {
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
                mContent.setTranslationX(drawerView.getMeasuredWidth() * slideOffset * (0.875f));
                mContent.setScaleX((1 - slideOffset * 0.2f));
                mContent.setScaleY((1 - slideOffset * 0.2f));
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
