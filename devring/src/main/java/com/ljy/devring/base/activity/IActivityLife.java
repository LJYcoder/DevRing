package com.ljy.devring.base.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * author:  ljy
 * date:    2018/3/19
 * description:  要想通过本库的LifeCycleCallback实现相关的基类功能，那你的Activity需实现此接口
 */

public interface IActivityLife {

    void onCreate(Activity activity,Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();

}
