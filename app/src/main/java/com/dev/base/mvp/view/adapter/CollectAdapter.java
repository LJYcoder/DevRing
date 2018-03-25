package com.dev.base.mvp.view.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.view.adapter.base.RecyclerBaseAdapter;
import com.dev.base.mvp.view.adapter.base.ViewHolder;
import com.ljy.devring.DevRing;
import com.ljy.devring.image.support.LoadOption;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 我的收藏列表的适配器
 */

public class CollectAdapter extends RecyclerBaseAdapter<MovieCollect> {

    public CollectAdapter(@NonNull List<MovieCollect> mDataList) {
        super(mDataList);
    }

    @Override
    protected void bindDataForView(ViewHolder holder, final MovieCollect movieCollect, final int position) {

        ImageView ivMovie = holder.getView(R.id.iv_movie);
        TextView tvTitle = holder.getView(R.id.tv_title);
        TextView tvYear = holder.getView(R.id.tv_year);


        //加载图片，且效果为 圆角&灰白&模糊
        DevRing.imageManager().loadNet(movieCollect.getMovieImage(), ivMovie,
                new LoadOption().setRoundRadius(80).setIsGray(true).setBlurRadius(5));
        tvTitle.setText(movieCollect.getTitle());
        tvYear.setText(movieCollect.getYear());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知CollectActivity将数据库的该条数据删除
                DevRing.busManager().postEvent(movieCollect);
                //从界面中移除
                removeItem(position);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collect, parent, false);
        return new ViewHolder(view);
    }

}
