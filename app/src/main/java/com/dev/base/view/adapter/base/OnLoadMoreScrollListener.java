package com.dev.base.view.adapter.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView滑动监听，用于监听滑动到底部以便触发加载更多的方法。
 */
public abstract class OnLoadMoreScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //获取最后一个完全显示的itemPosition
        int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
        int itemCount = manager.getItemCount();

        // 判断是否滑动到了最后一个item，并且是向上滑动
        // dy大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        if (lastItemPosition == (itemCount - 1) && dy > 0) {
            //加载更多
            onLoadMore();
        }
    }

    /**
     * 加载更多回调
     */
    public abstract void onLoadMore();
}
