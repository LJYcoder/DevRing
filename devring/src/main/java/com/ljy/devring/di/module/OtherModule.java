package com.ljy.devring.di.module;


import android.support.v4.util.SimpleArrayMap;

import com.ljy.devring.base.activity.ActivityLife;
import com.ljy.devring.base.activity.IActivityLife;
import com.ljy.devring.base.fragment.FragmentLife;
import com.ljy.devring.base.fragment.IFragmentLife;
import com.ljy.devring.other.ActivityListManager;
import com.ljy.devring.other.CrashDiary;
import com.ljy.devring.other.PermissionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 其他模块（Activity栈管理，RxPermission，崩溃日志输出，activity/fragment基类）的供应Module
 */

@Module
public class OtherModule {

    @Singleton
    @Provides
    ActivityListManager activityListManager() {
        return new ActivityListManager();
    }

    @Singleton
    @Provides
    PermissionManager permissionManager() {
        return new PermissionManager();
    }

    @Singleton
    @Provides
    CrashDiary crashDiary() {
        return new CrashDiary();
    }

    @Singleton
    @Provides
    SimpleArrayMap<String, IActivityLife> iActivityLifes() {
        return new SimpleArrayMap<>();
    }

    @Provides
    IActivityLife iActivityLife() {
        return new ActivityLife();
    }

    @Singleton
    @Provides
    SimpleArrayMap<String, IFragmentLife> iFragmentLifes() {
        return new SimpleArrayMap<>();
    }

    @Provides
    IFragmentLife iFragmentLife() {
        return new FragmentLife();
    }


}

