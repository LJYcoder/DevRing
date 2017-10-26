package com.dev.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.dev.base.app.constant.BaseConstants;

import java.util.Map;
import java.util.Set;

/**
 * author：   zp
 * date：     2015/9/22 0022 10:52
 * version    1.0
 * description shared preference工具类
 * modify by
 */
public class PreferenceUtil {

    /**
     * 获取shared preference
     *
     * @param context context
     * @param name    name
     * @param mode    mode
     * @return
     */
    public static SharedPreferences getPreference(Context context, String name, int mode) {
        return context == null ? null : context.getSharedPreferences(name, mode);
    }

    /**
     * 获取默认shared preference
     *
     * @param context context
     * @param mode    mode
     * @return
     */
    public static SharedPreferences getDefaultPreference(Context context, int mode) {
        return getPreference(context, BaseConstants.APP_SHARE, mode);
    }

    /**
     * 获取默认shared preference以MODE_PRIVATE打开
     *
     * @param context
     * @return
     */
    public static SharedPreferences getDefaultPreference(Context context) {
        return getDefaultPreference(context, Context.MODE_PRIVATE);
    }

    /**
     * 获取 name shared preference 以MODE_PRIVATE打开
     *
     * @param context context
     * @param name    name
     * @return
     */
    public static SharedPreferences getPreference(Context context, String name) {
        return getPreference(context, name, Context.MODE_PRIVATE);
    }


    /*-----------------------------------------  根据传入的SharedPreferences 进行操作 ----------------------------------------*/
    public static void putString(SharedPreferences sp, String key, String value) {
        if (sp == null) {
            return;
        }
        sp.edit().putString(key, value).apply();
    }

    public static void putBoolean(SharedPreferences sp, String key, boolean value) {
        if (sp == null) {
            return;
        }
        sp.edit().putBoolean(key, value).apply();
    }

    public static void putFloat(SharedPreferences sp, String key, float value) {
        if (sp == null) {
            return;
        }
        sp.edit().putFloat(key, value).apply();
    }

    public static void putInt(SharedPreferences sp, String key, int value) {
        if (sp == null) {
            return;
        }
        sp.edit().putInt(key, value).apply();
    }

    public static void putLong(SharedPreferences sp, String key, long value) {
        if (sp == null) {
            return;
        }
        sp.edit().putLong(key, value).apply();
    }

    public static void putStringSet(SharedPreferences sp, String key, Set<String> value) {
        if (sp == null) {
            return;
        }
        sp.edit().putStringSet(key, value).apply();
    }

    public static String getString(SharedPreferences sp, String key, String defValue) {
        return sp == null ? null : sp.getString(key, defValue);
    }

    public static boolean getBoolean(SharedPreferences sp, String key, boolean defValue) {
        return sp == null ? defValue : sp.getBoolean(key, defValue);
    }

    public static float getFloat(SharedPreferences sp, String key, float defValue) {
        return sp == null ? defValue : sp.getFloat(key, defValue);
    }

    public static int getInt(SharedPreferences sp, String key, int defValue) {
        return sp == null ? defValue : sp.getInt(key, defValue);
    }

    public static long getLong(SharedPreferences sp, String key, long defValue) {
        return sp == null ? defValue : sp.getLong(key, defValue);
    }

    public static Set<String> getStringSet(SharedPreferences sp, String key, Set<String> defValue) {
        return sp == null ? defValue : sp.getStringSet(key, defValue);
    }

    public static void removeString(SharedPreferences sp, String key) {
        if (sp == null) {
            return;
        }
        sp.edit().remove(key).apply();
    }

    public static Map<String, ?> getAll(SharedPreferences sp) {
        return sp == null ? null : sp.getAll();
    }

    public static void clearAll(SharedPreferences sp) {
        sp.edit().clear().apply();
    }


    /*-----------------------------------------  根据默认的SharedPreferences 进行操作 ----------------------------------------*/
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getDefaultPreference(context);
        putString(sp, key, value);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getDefaultPreference(context);
        putBoolean(sp, key, value);
    }

    public static void putFloat(Context context, String key, float value) {
        SharedPreferences sp = getDefaultPreference(context);
        putFloat(sp, key, value);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getDefaultPreference(context);
        putInt(sp, key, value);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = getDefaultPreference(context);
        putLong(sp, key, value);
    }

    public static void putStringSet(Context context, String key, Set<String> value) {
        SharedPreferences sp = getDefaultPreference(context);
        putStringSet(sp, key, value);
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getString(sp, key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getBoolean(sp, key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getFloat(sp, key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getInt(sp, key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getLong(sp, key, defValue);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defValue) {
        SharedPreferences sp = getDefaultPreference(context);
        return getStringSet(sp, key, defValue);
    }

    public static void removeString(Context context, String key) {
        SharedPreferences sp = getDefaultPreference(context);
        removeString(sp, key);
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = getDefaultPreference(context);
        return getAll(sp);
    }

    public static void clearAll(Context context) {
        SharedPreferences sp = getDefaultPreference(context);
        sp.edit().clear().apply();
    }
}
