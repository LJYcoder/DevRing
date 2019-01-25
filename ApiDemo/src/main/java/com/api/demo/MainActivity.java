package com.api.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.api.demo.bus.BusActivityA;
import com.api.demo.db.DBActivity;
import com.api.demo.http.HttpActivity;
import com.api.demo.image.ImageActivity;
import com.api.demo.other.OtherActivity;
import com.api.demo.util.UtilActivity;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/8
 * description: 主菜单页
 */
public class MainActivity extends AppCompatActivity implements IBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_http, R.id.btn_image, R.id.btn_db, R.id.btn_eventbus, R.id.btn_other, R.id.btn_util})
    protected void onClick(View view) {
        switch (view.getId()) {
            //网络模块
            case R.id.btn_http:
                startActivity(new Intent(this, HttpActivity.class));
                break;

            //图片模块
            case R.id.btn_image:
                startActivity(new Intent(this, ImageActivity.class));
                break;

            //数据库模块
            case R.id.btn_db:
                startActivity(new Intent(this, DBActivity.class));
                break;

            //事件总线模块
            case R.id.btn_eventbus:
                startActivity(new Intent(this, BusActivityA.class));
                break;

            //其他模块
            case R.id.btn_other:
                startActivity(new Intent(this, OtherActivity.class));
                break;

            //工具类
            case R.id.btn_util:
                startActivity(new Intent(this, UtilActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //使用activity管理者将所有存在的页面finish掉并杀死进程
                DevRing.activityListManager().exitApp();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
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
