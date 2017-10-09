package com.dev.base.view.adapter.base;

import android.content.Context;

import java.util.List;

/**
 * author：   zp
 * date：     2015/8/27 0027 16:46
 * version    1.0
 * description
 * modify by
 */
public interface IRecyclerAdapter<T> {

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    Context getContext();

    /**
     * 获取第 position 个数据
     *
     * @param position 位置
     * @return T
     */
    T getItem(int position);

    /**
     * 获取全部数据
     *
     * @return
     */
    List<T> getDataList();

    /**
     * 插入一系列数据
     *
     * @param list       数据集
     * @param startIndex 开始位置
     */
    void insertItems(List<T> list, int startIndex);

    /**
     * 追加一系列数据
     *
     * @param list 数据集
     */
    void insertItems(List<T> list);

    /**
     * 插入单个数据
     *
     * @param t        数据
     * @param position 开始位置
     */
    void insertItem(T t, int position);

    /**
     * 追加单个数据
     *
     * @param t 数据
     */
    void insertItem(T t);

    /**
     * 替换整个数据
     *
     * @param list 数据集
     */
    void replaceData(List<T> list);

    /**
     * 通知更新
     *
     * @param positionStart 开始位置
     * @param itemCount     更新的个数
     */
    void updateItems(int positionStart, int itemCount);

    /**
     * 通知更新
     */
    void updateAll();

    /**
     * 移除一个item
     *
     * @param position 位置
     */
    void removeItem(int position);

    /**
     * 移除全部
     */
    void removeAll();
}
