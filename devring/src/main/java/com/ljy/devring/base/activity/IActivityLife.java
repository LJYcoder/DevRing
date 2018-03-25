package com.ljy.devring.base.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * author:  ljy
 * date:    2018/3/19
 * description:
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
