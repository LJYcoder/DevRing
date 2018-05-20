package com.ljy.devring.cache.support;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: SharedPreferences缓存管理
 */

public class SpCache {

    private SharedPreferences mSharedPreferences;

    public SpCache(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    public void put(String key, String value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, boolean value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public void put(String key, int value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, long value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void put(String key, Set<String> value) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().putStringSet(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences == null ? null : mSharedPreferences.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences == null ? defValue : mSharedPreferences.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences == null ? defValue : mSharedPreferences.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences == null ? defValue : mSharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences == null ? defValue : mSharedPreferences.getLong(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mSharedPreferences == null ? defValue : mSharedPreferences.getStringSet(key, defValue);
    }

    public void remove(String key) {
        if (mSharedPreferences == null) {
            return;
        }
        mSharedPreferences.edit().remove(key).apply();
    }

    public Map<String, ?> getAll() {
        return mSharedPreferences == null ? null : mSharedPreferences.getAll();
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
