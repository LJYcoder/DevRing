package com.dev.base.mvp.view.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.mvp.model.entity.res.MovieRes;
import com.dev.base.mvp.view.adapter.base.LoadMoreBaseAdapter;
import com.dev.base.mvp.view.adapter.base.ViewHolder;
import com.ljy.devring.DevRing;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 正在上映/即将上映列表的适配器
 */

public class MovieAdapter extends LoadMoreBaseAdapter<MovieRes> {

    private int mMovieType;

    public MovieAdapter(int movieType, @NonNull List<MovieRes> mDataList) {
        super(mDataList);
        mMovieType = movieType;
    }

    @Override
    protected void bindDataForView_(ViewHolder holder, final MovieRes movieRes, int position) {

        ImageView ivMovie = holder.getView(R.id.iv_movie);
        TextView tvTitle = holder.getView(R.id.tv_title);
        TextView tvType = holder.getView(R.id.tv_type);
        TextView tvYear = holder.getView(R.id.tv_year);
        RatingBar rbAverage = holder.getView(R.id.rb_average);


        DevRing.imageManager().loadNet(movieRes.getImages().getMedium(), ivMovie);
        tvTitle.setText(movieRes.getTitle());
        String type = "";
        for (String s : movieRes.getGenres()) {
            type = type + s + "/";
        }
        tvType.setText(TextUtils.isEmpty(type) ? "暂无类型" : type.substring(0, type.length() - 1));
        tvYear.setText(movieRes.getYear());
        rbAverage.setRating(movieRes.getRating().getAverage() / 2);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieRes.setMovieType(mMovieType);
                DevRing.busManager().postEvent(movieRes);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder_(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }
}
