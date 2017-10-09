package com.dev.base.view.adapter.base;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.dev.base.util.log.LogUtil;


/**
 * author：   zp
 * date：     2015/8/27 0027 15:21
 * version    1.0
 * description recycler view 的基类view holder
 * modify by
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = ViewHolder.class.getSimpleName();
    private SparseArray<View> mHolder = null;

    public ViewHolder(View itemView) {
        super(itemView);
        mHolder = new SparseArray<>();
    }

    public <T extends View> T getView(@IdRes int id) {

        View childView = mHolder.get(id);
        if (null != childView) {
            return (T) childView;
        }
        childView = itemView.findViewById(id);
        if (null == childView) {
            LogUtil.e(TAG, "no view that id is " + id);
            return null;
        }
        mHolder.put(id, childView);

        return (T) childView;
    }

}
