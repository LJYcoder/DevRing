package com.dev.base.di.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * author:  ljy
 * date:    2018/3/10
 * description:
 */

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DBScope {
}
