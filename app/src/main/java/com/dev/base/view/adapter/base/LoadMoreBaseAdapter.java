package com.dev.base.view.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.base.R;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/11/6
 * description: 包含有"上拉加载更多footer"的adapter基类，通过setLoadState来控制footer的显示状态
 */

public abstract class LoadMoreBaseAdapter<T> extends RecyclerBaseAdapter<T> {

    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public static final int LOADING = 1;
    // 加载完成
    public static final int LOADING_COMPLETE = 2;
    // 加载到底了（全部数据加载完毕）
    public static final int LOADING_END = 3;

    public LoadMoreBaseAdapter(@NonNull Context context, @NonNull List<T> mDataList) {
        super(context, mDataList);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    @Override
    protected void bindDataForView(ViewHolder holder, T t, int position) {
        if (holder.getItemViewType() == TYPE_FOOTER) {
            ProgressBar pbLoading = holder.getView(R.id.pb_loading);
            TextView tvLoading = holder.getView(R.id.tv_loading);
            LinearLayout llEnd = holder.getView(R.id.ll_end);

            switch (loadState) {
                case LOADING: // 正在加载
                    pbLoading.setVisibility(View.VISIBLE);
                    tvLoading.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    pbLoading.setVisibility(View.INVISIBLE);
                    tvLoading.setVisibility(View.INVISIBLE);
                    llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    pbLoading.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;

            }
        } else {
            bindDataForView_(holder, t, position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more_footer, parent, false);
            return new ViewHolder(view);
        } else {
            return onCreateViewHolder_(parent, viewType);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    protected abstract void bindDataForView_(ViewHolder holder, T t, int position);

    protected abstract ViewHolder onCreateViewHolder_(ViewGroup parent, int viewType);
}
