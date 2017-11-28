package com.example.leila.smartk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leila.smartk.Bean.VideoListBean;
import com.example.leila.smartk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频列表适配器
 * Created by Leila on 2017/9/14.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecVH> implements View.OnClickListener {
    List<VideoListBean> videoListBeans = new ArrayList<VideoListBean>();
    private OnItemClickListener mOnItemClickListener = null;

    //构造方法传入数据
    public RecyclerViewAdapter(List<VideoListBean> videoListBeans) {
        this.videoListBeans = videoListBeans;
    }

    //创建ViewHolder
    @Override
    public RecVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //把Item的layout转化成为view传给ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_home_recycler_item, parent, false);
        view.setOnClickListener(this);
        return new RecVH(view);
    }

    //将数据放入相应的位置
    @Override
    public void onBindViewHolder(RecVH holder, int position) {

        holder.tvTitle.setText(videoListBeans.get(position).getTvTitle());
        holder.ivPic.setImageResource(videoListBeans.get(position).getImg());
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return videoListBeans.size();
    }

    @Override
    public void onClick(View view) {
        //注意这里使用getTag方法获取position
        if (mOnItemClickListener != null) {
            mOnItemClickListener.OnItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //ViewHolder绑定控件
    public class RecVH extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvTitle;

        public RecVH(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.img);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public static interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}