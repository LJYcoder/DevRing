package com.ljy.devring.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * author:  ljy
 * date:    2018/3/19
 * description: 负责Fragment各生命周期处理，相当于基类的功能
 */
@Singleton
public class FragmentLifeCallback extends FragmentManager.FragmentLifecycleCallbacks {

    @Inject
    SimpleArrayMap<String, IFragmentLife> mMapFragmentLife;
    @Inject
    Provider<IFragmentLife> mFragmentLifeProvider;

    @Inject
    public FragmentLifeCallback() {
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        if (f instanceof IBaseFragment) {
            IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
            if (iFragmentLife == null || !iFragmentLife.isAdded()) {
                iFragmentLife = mFragmentLifeProvider.get();
                mMapFragmentLife.put(f.toString(), iFragmentLife);
//                RingLog.e("onCreate activity:" + activity.toString());
//                RingLog.e("onCreate iActivityLife:" + iActivityLife.toString());
            }
            iFragmentLife.onAttach(f, context);
        }
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onActivityCreate(savedInstanceState);
        }
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onCreateView(v,savedInstanceState);
        }
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onStart();
        }
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onResume();
        }
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onPause();
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onStop();
        }
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onDestroyView();
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onDestroy();
        }

        mMapFragmentLife.remove(f.toString());
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        IFragmentLife iFragmentLife = mMapFragmentLife.get(f.toString());
        if (iFragmentLife != null) {
            iFragmentLife.onDetach();
        }
    }

    public FragmentLife getFragmentLife(String key) {
        return (FragmentLife) mMapFragmentLife.get(key);
    }
}
