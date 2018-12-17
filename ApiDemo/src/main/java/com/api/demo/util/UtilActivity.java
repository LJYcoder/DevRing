package com.api.demo.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.api.demo.R;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.other.toast.RingToast;
import com.ljy.devring.other.toast.ToastBlackStyle;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/17
 * description: 工具类模块
 *
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 */

public class UtilActivity extends AppCompatActivity implements IBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util);
        ButterKnife.bind(this);
        setTitle("工具类模块");
    }

    @OnClick({R.id.btn_black_toast, R.id.btn_custom_toast})
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_black_toast:
                RingToast.initStyle(new ToastBlackStyle());
                RingToast.show("我是本库提供的黑色样式");
                break;

            case R.id.btn_custom_toast:
                RingToast.initStyle(new CustomToastStyle());
                RingToast.show("我是自定义样式");
                break;
        }
    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public boolean isUseFragment() {
        return false;
    }
}
