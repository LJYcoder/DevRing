package com.api.demo.bus;

import android.content.Intent;
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

public class BusActivityA extends AppCompatActivity implements IBaseActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_a);
        ButterKnife.bind(this);
        setTitle("事件总线模块A页面");
    }

    @OnClick({R.id.btn_post_sticky, R.id.btn_go_b})
    protected void onClick(View view) {
        switch (view.getId()) {
            //发送粘性事件，B页面在注册订阅后，将会收到
            case R.id.btn_post_sticky:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createTime = simpleDateFormat.format(new Date());
                StickyEvent event = new StickyEvent("我是来自A页面的粘性事件~", createTime);
                DevRing.busManager().postStickyEvent(event);
                break;

            //跳转到B页面
            case R.id.btn_go_b:
                startActivity(new Intent(this, BusActivityB.class));
                break;
        }
    }

    @org.greenrobot.eventbus.Subscribe //如果使用默认的EventBus则使用此@Subscribe
    @com.api.demo.bus.rxbus.support.Subscribe //如果使用RxBus则使用此@Subscribe
    public void onGetEvent(CommonEvent event) {
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
