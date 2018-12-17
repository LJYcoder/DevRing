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

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/8
 * description: 主菜单页
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_http, R.id.btn_image, R.id.btn_db, R.id.btn_eventbus, R.id.btn_other, R.id.btn_util})
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_http:
                startActivity(new Intent(this, HttpActivity.class));
                break;

            case R.id.btn_image:
                startActivity(new Intent(this, ImageActivity.class));
                break;

            case R.id.btn_db:
                startActivity(new Intent(this, DBActivity.class));
                break;

            case R.id.btn_eventbus:
                startActivity(new Intent(this, BusActivityA.class));
                break;

            case R.id.btn_other:
                startActivity(new Intent(this, OtherActivity.class));
                break;

            case R.id.btn_util:
                startActivity(new Intent(this, UtilActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                DevRing.activityListManager().exitApp();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}
