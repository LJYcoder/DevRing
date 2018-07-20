package com.ljy.devring.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * https://blog.csdn.net/ludandan1234/article/details/53330944
 * date:    2018/5/10
 * description: 修改全局字体样式的工具类
 */

public class FontTypeUtil {

    //------------------ 在Activity/Fragment的基类中调用即可 -------------------//
    /**
     * <p>Replace the font of specified view and it's children</p>
     * 通过递归批量替换某个View及其子View的字体改变Activity内部控件的字体(TextView,Button,EditText,CheckBox,RadioButton等)
     * @param context
     * @param fontPath  assert文件夹下的字体文件路径，如 "fonts/FZLanTYJW.ttf"
     */
    public static void replaceFont(@NonNull Activity context, String fontPath) {
        replaceFont(getRootView(context),fontPath);
    }


    /**
     * <p>Replace the font of specified view and it's children</p>
     * @param root The root view.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public static void replaceFont(@NonNull View root, String fontPath) {
        if (root == null || TextUtils.isEmpty(fontPath)) {
            return;
        }


        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView)root;
            int style = Typeface.NORMAL;
            if (textView.getTypeface() != null) {
                style = textView.getTypeface().getStyle();
            }
            textView.setTypeface(createTypeface(root.getContext(), fontPath), style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), fontPath);
            }
        }
    }

    /*
     * Create a Typeface instance with your font file
     */
    public static Typeface createTypeface(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    /**
     * 从Activity 获取 rootView 根节点
     * @param context
     * @return 当前activity布局的根节点
     */
    public static View getRootView(Activity context)
    {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
    }


    //------------------ 在Application中调用即可 -------------------//
    /**
     * 通过改变App的系统字体替换App内部所有控件的字体(TextView,Button,EditText,CheckBox,RadioButton等)
     * @param context
     * @param fontPath assert文件夹下的字体文件路径，如 "fonts/FZLanTYJW.ttf"
     */
    public static void replaceSystemDefaultFont(@NonNull Context context, @NonNull String fontPath) {
        replaceTypefaceField("MONOSPACE", createTypeface(context, fontPath));
    }

    /**
     * <p>Replace field in class Typeface with reflection.</p>
     */
    private static void replaceTypefaceField(String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}