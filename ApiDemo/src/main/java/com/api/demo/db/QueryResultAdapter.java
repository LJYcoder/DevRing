package com.api.demo.db;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.api.demo.R;

import java.util.List;

/**
 * author:  ljy
 * date:    2018/12/13
 * description: 查询结果列表的适配器
 */

public class QueryResultAdapter extends RecyclerView.Adapter<QueryResultAdapter.ViewHoler>{

    private List<User> mListData;

    public QueryResultAdapter(List<User> listData) {
        this.mListData = listData;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_db_result, parent, false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
        User user = mListData.get(position);
        holder.tvId.setText("ID：" + user.getId());
        holder.tvName.setText("姓名：" + user.getName());
        holder.tvAge.setText("年龄：" + user.getAge());
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    public class ViewHoler extends RecyclerView.ViewHolder{
        TextView tvId;
        TextView tvName;
        TextView tvAge;

        public ViewHoler(View view) {
            super(view);
            tvId = view.findViewById(R.id.tv_id);
            tvName = view.findViewById(R.id.tv_name);
            tvAge = view.findViewById(R.id.tv_age);
        }
    }
}
