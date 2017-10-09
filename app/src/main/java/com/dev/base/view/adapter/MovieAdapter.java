package com.dev.base.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.base.view.adapter.base.RecyclerBaseAdapter;
import com.dev.base.view.adapter.base.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.dev.base.R;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.util.FrescoUtil;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/28
 * description:
 */

public class MovieAdapter extends RecyclerBaseAdapter<MovieRes> {

    private OnMovieClickListener mListener;

    public MovieAdapter(@NonNull Context context, @NonNull List<MovieRes> mDataList, OnMovieClickListener listener) {
        super(context, mDataList);
        mListener = listener;
    }

    @Override
    protected void bindDataForView(ViewHolder holder, final MovieRes movieRes, int position) {
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
        tvType.setText(type.substring(0, type.length() - 1));
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    public interface OnMovieClickListener {
        void onMovieClick(MovieRes movieRes);
    }
}
