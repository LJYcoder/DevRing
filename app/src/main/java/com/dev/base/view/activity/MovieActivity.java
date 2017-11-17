package com.dev.base.view.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.dev.base.R;
import com.dev.base.model.entity.eventbus.MovieEvent;
import com.dev.base.util.EventBusUtil;
import com.dev.base.util.ToastUtil;
import com.dev.base.view.activity.base.ToolbarBaseActivity;
import com.dev.base.view.fragment.MovieFragment;
import com.dev.base.view.fragment.base.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * author:  ljy
 * date:    2017/9/27
 * descrition: 电影浏览页面（正在上映与即将上映）
 */

//可以把基类ToolbarBaseActivity换成DrawerBaseActivity
//DrawerBaseActivity是在ToolbarBaseActivity的基础上添加了侧滑抽屉
public class MovieActivity extends ToolbarBaseActivity {

    @BindView(R.id.tl_movie)
    TabLayout mTlMovie;//顶部选项卡

    private BaseFragment mCurrentFragment;//当前展示的Fragment
    private MovieFragment mPlayingMovieFragment;//“正在上映”的Fragment
    private MovieFragment mCommingMovieFragment;//“即将上映”的Fragment

    private long mExitTime;//用于控制"点击两次退出程序"

    @Override
    protected void setContentLayout() {
        setContentView(R.layout.activity_movie);
    }

    @Override
    protected void initView() {
//        getToolbar().setBackgroundColor(getResourceColor(R.color.black)); //设置toolbar颜色
        getToolbar().setTitle(getResourceString(R.string.douban_movie));//设置toolbar标题

        //初始化TabLayout
        mTlMovie.setTabMode(TabLayout.MODE_FIXED);//支持水平滑动，当屏幕空间不足
        mTlMovie.setTabTextColors(getResourceColor(R.color.black_4d), getResourceColor(R.color.colorPrimary));//设置文本在未选中和选中时候的颜色
        mTlMovie.setSelectedTabIndicatorColor(getResourceColor(R.color.colorPrimary));//设置选中长条的颜色
        mTlMovie.addTab(mTlMovie.newTab().setText("正在上映"));
        mTlMovie.addTab(mTlMovie.newTab().setText("即将上映"));

        //设置toolbar在列表向上移动时消失，向下移动时出现
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) getToolbar().getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

    }

    @Override
    protected void obtainData() {
        EventBusUtil.register(this);//订阅事件
        setDefaultFragment();//设置默认的Fragment

    }

    @Override
    protected void initEvent() {
        //设置TabLayout选项卡的点击事件
        mTlMovie.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        clickPlaying();
                        break;

                    case 1:
                        clickComming();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //设置Toolbar右侧文字的点击事件
        getTvToolbarRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CollectActivity.class));
            }
        });

        //设置返回键点击监听
        setOnKeyListener(new OnKeyClickListener() {
            @Override
            public void clickBack() {
                //两秒内点击两次则退出程序
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    ToastUtil.show(getResourceString(R.string.exit_confirm));
                    mExitTime = System.currentTimeMillis();
                } else {
                    getActivityStackManager().exitApplication();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });

    }

    //设置默认Fragment
    private void setDefaultFragment() {
        if (mPlayingMovieFragment == null) {
            mPlayingMovieFragment = MovieFragment.newInstance(MovieFragment.TYPE_PLAYING);
        }

        if (!mPlayingMovieFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_movie, mPlayingMovieFragment, "1").commit();
            mCurrentFragment = mPlayingMovieFragment;
            mCurrentFragment.setUserVisibleHint(true);
        }
    }

    //显示“正在上映”Fragment
    private void clickPlaying() {
        if (mPlayingMovieFragment == null) {
            mPlayingMovieFragment = MovieFragment.newInstance(MovieFragment.TYPE_PLAYING);
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mPlayingMovieFragment, "1");
    }

    //显示“即将上映”Fragment
    private void clickComming() {
        if (mCommingMovieFragment == null) {
            mCommingMovieFragment = MovieFragment.newInstance(MovieFragment.TYPE_COMMING);
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mCommingMovieFragment, "2");
    }

    //显示或隐藏Fragment，用于切换Fragment的展示
    private void addOrShowFragment(FragmentTransaction transaction, BaseFragment fragment, String tag) {
        if (mCurrentFragment == fragment) return;

        if (!fragment.isAdded()) {
            transaction.hide(mCurrentFragment).add(R.id.fl_movie, fragment, tag).commit();
        } else {
            transaction.hide(mCurrentFragment).show(fragment).commit();
        }

        //不与ViewPager嵌套的话，需要显式调用setUserVisibleHint
        mCurrentFragment.setUserVisibleHint(false);
        mCurrentFragment = fragment;
        mCurrentFragment.setUserVisibleHint(true);
    }

    //EventBus的事件接收，从事件中获取最新的收藏数量并更新界面展示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(MovieEvent event) {
        setToolbarRightTv(getResourceString(R.string.collect, event.getCount()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消订阅
        EventBusUtil.unregister(this);
    }
}
