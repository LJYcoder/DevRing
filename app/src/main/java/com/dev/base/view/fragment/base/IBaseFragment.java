package com.dev.base.view.fragment.base;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * date：     2017/3/3
 * description
 */
public interface IBaseFragment {

    /**
     * 从Resource中获取颜色
     * @param colorId colorId
     * @param theme theme
     * @return 颜色值
     */
    int getResourceColor(@ColorRes int colorId, Resources.Theme theme);

    /**
     * 从Resource中获取字符串
     * @param stringId stringId
     * @return string
     */
    String getResourceString(@StringRes int stringId);

    /**
     * 从Resource中获取Drawable
     * @param id DrawableRes
     * @return Drawable
     */
    Drawable getResourceDrawable(@DrawableRes int id);


}
