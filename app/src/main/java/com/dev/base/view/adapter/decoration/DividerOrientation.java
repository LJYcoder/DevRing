package com.dev.base.view.adapter.decoration;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * author：    zp
 * date：      2015/10/27 & 17:39
 * version     1.0
 * description:
 * modify by
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({LinearDividerDecoration.HORIZONTAL_LIST, LinearDividerDecoration.VERTICAL_LIST})
@Target({METHOD,PARAMETER,FIELD,LOCAL_VARIABLE})
public @interface DividerOrientation {
}
