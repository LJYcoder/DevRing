package com.dev.base.view.widget.loadlayout;

/**
 * author：    zp
 * date：      2015/10/6 & 11:37
 * version     1.0
 * description:
 * modify by
 */
public interface State {
    /**
     * 加载中
     */
    int LOADING = 1;

    /**
     * 加载成功
     */
    int SUCCESS = 2;

    /**
     * 加载失败
     */
    int FAILED = 3;

    /**
     * 加载成功且返回无数据
     */
    int NO_DATA = 4;

}
