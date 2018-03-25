package com.dev.base.di.component.other;

import com.dev.base.di.module.other.GreenDBModule;
import com.dev.base.di.module.other.NativeDBModule;
import com.dev.base.di.scope.DBScope;
import com.dev.base.mvp.model.db.greendao.GreenDBManager;
import com.dev.base.mvp.model.db.nativedao.NativeDBManager;
import com.ljy.devring.di.component.RingComponent;

import dagger.Component;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: GreenDao和原生数据库的Component
 */

@DBScope
@Component(modules = {GreenDBModule.class, NativeDBModule.class}, dependencies = RingComponent.class)
public interface DBComponent {

    void inject(GreenDBManager greenDbManager);

    void inject(NativeDBManager nativeDBManager);
}
