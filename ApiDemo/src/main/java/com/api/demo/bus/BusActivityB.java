package com.api.demo.bus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.api.demo.R;
import com.api.demo.bus.event.CommonEvent;
import com.api.demo.bus.event.StickyEvent;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/14
 * description: 演示事件总线模块API使用
 *
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 * EventBus博客介绍：<a>https://www.jianshu.com/p/6fb4d78db19b</a>
 */

public class BusActivityB extends AppCompatActivity implements IBaseActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_b);
        ButterKnife.bind(this);
        setTitle("事件总线模块B页面");
    }

    @OnClick({R.id.btn_post_common})
    protected void onClick(View view) {
        switch (view.getId()) {
            //发送普通事件
            case R.id.btn_post_common:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createTime = simpleDateFormat.format(new Date());
                CommonEvent event = new CommonEvent("我是来自B页面的普通事件~", createTime);
                DevRing.busManager().postEvent(event);
                break;
        }
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true) //如果使用默认的EventBus则使用此@Subscribe
    @com.api.demo.bus.rxbus.support.Subscribe //如果使用RxBus则使用此@Subscribe
    public void onGetEvent(StickyEvent event) {
        mTvResult.setText(event.getContent() + "  " + event.getCreateTime());
    }

    @Override
    public boolean isUseEventBus() {
        return true;
    }

    @Override
    public boolean isUseFragment() {
        return false;
    }

}
