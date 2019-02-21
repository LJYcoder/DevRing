package com.ljy.devring.util;

import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.ActivityLife;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.base.fragment.FragmentLife;
import com.ljy.devring.base.fragment.IBaseFragment;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 获取用于管理生命周期的Transformer
 */

public class RxLifecycleUtil {

    /**
     * 获取与Activity生命周期绑定的LifecycleTransformer
     * @param lifecycleEmitter 要求为实现了IBaseActivity的Object
     * @param event Activity的生命周期阶段，将在该阶段终止请求
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Object lifecycleEmitter, ActivityEvent event) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseActivity) {
            return RxBindUntilEvent(getActivityLifeSubject(lifecycleEmitter.toString()), event);
        } else {
            throw new IllegalArgumentException("lifecycleEmitter需为实现IBaseActivity的Object");
        }
    }

    /**
     * 获取与Fragment生命周期绑定的LifecycleTransformer
     * @param lifecycleEmitter 要求为实现了IBaseFragment的Object
     * @param event Fragment的生命周期阶段，将在该阶段终止请求
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Object lifecycleEmitter, FragmentEvent event) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseFragment) {
            return RxBindUntilEvent(getFragmentLifeSubject(lifecycleEmitter.toString()), event);
        } else {
            throw new IllegalArgumentException("lifecycleEmitter需为实现IBaseFragment的Object");
        }
    }

    /**
     * 获取当页面Destroy时就终止请求的LifecycleTransformer
     * @param lifecycleEmitter 如果请求在Activity中发起，则lifecycleEmitter需为实现IBaseActivity的Object；
     *                         如果在Fragment中发起，则lifecycleEmitter需为实现IBaseFragment的Object
     */
    public static <T> LifecycleTransformer<T> bindUntilDestroy(@NonNull Object lifecycleEmitter) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseActivity) {
            return RxBindUntilEvent(getActivityLifeSubject(lifecycleEmitter.toString()), ActivityEvent.DESTROY);
        } else if (lifecycleEmitter instanceof IBaseFragment) {
            return RxBindUntilEvent(getFragmentLifeSubject(lifecycleEmitter.toString()), FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("如果请求在Activity中发起，则lifecycleEmitter需为实现IBaseActivity的Object，如果在Fragment中发起，则lifecycleEmitter需为实现IBaseFragment的Object");
        }
    }

    public static <T, R> LifecycleTransformer<T> RxBindUntilEvent(@NonNull Observable<R> lifecycleEmitter, R event) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter == null");
        return RxLifecycle.bindUntilEvent(lifecycleEmitter, event);
    }

    /**
     * 获取该Activity用于控制网络请求生命周期的PublishSubject
     * @param key  Activity的内存地址，通过activity.toString()获取即可
     * @return PublishSubject
     */
    private static PublishSubject<ActivityEvent> getActivityLifeSubject(String key) {
        ActivityLife activityLife = DevRing.ringComponent().activityLifeCallback().getActivityLife(key);
        if (activityLife == null) {
            throw new IllegalArgumentException("请确保该Activity实现了IBaseActivity接口");
        }else{
            return activityLife.getLifecycleSubject();
        }
    }

    /**
     * 获取该Fragment用于控制网络请求生命周期的PublishSubject
     * @param key  Fragment的内存地址，通过fragment.toString()获取即可
     * @return PublishSubject
     */
    private static PublishSubject<FragmentEvent> getFragmentLifeSubject(String key) {
        FragmentLife fragmentLife = DevRing.ringComponent().fragmentLifeCallback().getFragmentLife(key);
        if (fragmentLife == null) {
            throw new IllegalArgumentException("请确保Fragment所在的Activity的isUseFragment()方法返回为true");
        }
        return fragmentLife.getLifecycleSubject();
    }
}
