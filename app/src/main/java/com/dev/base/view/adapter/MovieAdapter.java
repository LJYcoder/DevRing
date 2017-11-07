package com.dev.base.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.util.FrescoUtil;
import com.dev.base.view.adapter.base.LoadMoreBaseAdapter;
import com.dev.base.view.adapter.base.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 正在上映/即将上映列表的适配器
 */

public class MovieAdapter extends LoadMoreBaseAdapter<MovieRes> {

    private OnMovieClickListener mListener;

    public MovieAdapter(@NonNull Context context, @NonNull List<MovieRes> mDataList, OnMovieClickListener listener) {
        super(context, mDataList);
        mListener = listener;
    }

    @Override
    protected void bindDataForView_(ViewHolder holder, final MovieRes movieRes, int position) {
        //initView
        SimpleDraweeView sdvMovie = holder.getView(R.id.sdv_movie);
        TextView tvTitle = holder.getView(R.id.tv_title);
        TextView tvType = holder.getView(R.id.tv_type);
        TextView tvYear = holder.getView(R.id.tv_year);
        RatingBar rbAverage = holder.getView(R.id.rb_average);

        //obtainData
        FrescoUtil.getInstance().loadNetImage(sdvMovie, movieRes.getImages().getMedium());
        tvTitle.setText(movieRes.getTitle());
        String type = "";
        for (String s : movieRes.getGenres()) {
            type = type + s + "/";
        }
        tvType.setText(TextUtils.isEmpty(type) ? "暂无类型" : type.substring(0, type.length() - 1));
        tvYear.setText(movieRes.getYear());
        rbAverage.setRating(movieRes.getRating().getAverage() / 2);

        //initEvent
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMovieClick(movieRes);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder_(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    public interface OnMovieClickListener {
        void onMovieClick(MovieRes movieRes);
    }
}
