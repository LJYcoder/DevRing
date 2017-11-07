package com.dev.base.view.adapter.base;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.dev.base.util.CollectionUtil;
import com.dev.base.util.log.LogUtil;

import java.util.List;

/**
 * author：   zp
 * date：     2015/8/27 0027 16:04
 * version    1.0
 * description recycler adapter 的基类,封装了ViewHolder
 * modify by  ljy
 */
public abstract class RecyclerBaseAdapter<T> extends RecyclerView.Adapter<ViewHolder> implements IRecyclerAdapter<T> {
    private static final String TAG = RecyclerBaseAdapter.class.getSimpleName();

    private List<T> mDataList;
    private Context mContext;

    protected RecyclerBaseAdapter(@NonNull Context context, @NonNull List<T> mDataList) {
        if (context == null) {
            throw new NullPointerException("context is not allow null!");
        }
        this.mDataList = mDataList;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int p = holder.getLayoutPosition();

        bindDataForView(holder, (mDataList != null && !mDataList.isEmpty()) ? (mDataList.size() > p ? mDataList.get(p) : null) : null, p);

    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    protected abstract void bindDataForView(ViewHolder holder, T t, int position);

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public T getItem(@IntRange(from = 0) int position) {
        if (position <= -1) {
            return null;
        }
        return !CollectionUtil.isEmpty(mDataList) ? mDataList.get(position) : null;
    }

    @Override
    public List<T> getDataList() {
        return mDataList;
    }

    //从某个位置开始，插入一组数据
    @Override
    public void insertItems(@NonNull List<T> list, @IntRange(from = 0) int startIndex) {

        if (mDataList == null) {
            return;
        }

        if (list == null || list.isEmpty()) {
            LogUtil.e(TAG, "插入的数据集为空或长度小于等于零, 请检查你的数据集!");
            return;
        }

        if (this.mDataList.containsAll(list)) {
            return;
        }

        notifyItemRangeInserted(startIndex, list.size());
        mDataList.addAll(startIndex, list);
        notifyItemRangeChanged(startIndex, getItemCount() - startIndex);
    }

    //从最底下插入一组数据
    @Override
    public void insertItems(@NonNull List<T> list) {
        this.insertItems(list, mDataList.size());
    }

    //从某个位置开始，插入一个数据
    @Override
    public void insertItem(@NonNull T t, @IntRange(from = 0) int position) {

        if (mDataList == null) {
            return;
        }

        if (t == null) {
            LogUtil.e(TAG, "插入的数据为空, 请检查你的数据!");
            return;
        }

//        if (this.mDataList.contains(t)) {
//            return;
//        }

        notifyItemInserted(position);
        mDataList.add(position, t);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    //从最底下插入一个数据
    @Override
    public void insertItem(@NonNull T t) {
        this.insertItem(t, mDataList.size());
    }

    //替换所有数据
    @Override
    public void replaceData(@NonNull List<T> list) {
        if (mDataList == null) {
            return;
        }

        if (list == null || list.isEmpty()) {
            LogUtil.e(TAG, "插入的数据集为空或长度小于等于零, 请检查你的数据集!");
            return;
        }

        mDataList = list;
        notifyDataSetChanged();
    }

    //从某个位置开始，更新n个数据
    @Override
    public void updateItems(@IntRange(from = 0) int positionStart, @IntRange(from = 0) int itemCount) {
        notifyItemRangeChanged(positionStart, itemCount);
    }

    //更新所有数据
    @Override
    public void updateAll() {
        updateItems(0, mDataList.size());
    }

    //移除某个位置的数据
    @Override
    public void removeItem(@IntRange(from = 0) int position) {
        if (CollectionUtil.isEmpty(mDataList) || position <= -1) {
            return;
        }
        notifyItemRemoved(position);
        mDataList.remove(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    //移除所有数据
    @Override
    public void removeAll() {
        if (CollectionUtil.isEmpty(mDataList)) {
            return;
        }

        notifyItemRangeRemoved(0, getItemCount());
        mDataList.clear();
        notifyItemRangeChanged(0, getItemCount());
    }
}
