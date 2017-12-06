package com.example.leila.smartk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leila.smartk.R;

import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by Leila on 2017/11/7.
 */

public class HomeViewAdapter extends BaseAdapter implements View.OnClickListener {
    private final Context context;
    private final List<HashMap<String, Object>> list;
    private final int resource;
    private ItemOnClickListener itemOnClickListener;

    public HomeViewAdapter(Context context, List<HashMap<String, Object>> list, int resource, ItemOnClickListener itemOnClickListener) {
        this.context = context;
        this.list = list;
        this.resource = resource;
        this.itemOnClickListener = itemOnClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view;
        ViewHolder viewHolder;
        HashMap<String, Object> map = list.get(i);
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.cardMoreVideo = (CardView) view.findViewById(R.id.card_more_video);
            viewHolder.cardTvMoreClass = (TextView) view.findViewById(R.id.card_tv_more_class);
            viewHolder.TvMoreName = (TextView) view.findViewById(R.id.tv_more_name);
            viewHolder.TvMoreSex = (TextView) view.findViewById(R.id.tv_more_sex);
            viewHolder.TvMoreClass = (TextView) view.findViewById(R.id.tv_more_class);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String babyClass = map.get("class").toString();
        viewHolder.cardTvMoreClass.setText(babyClass);
        viewHolder.TvMoreName.setText("姓名：" + map.get("name").toString());
        viewHolder.TvMoreSex.setText("性别：" + map.get("sex").toString());
        viewHolder.TvMoreClass.setText("所在班级：" + babyClass);
        viewHolder.TvMoreClass.setTag(i);
        viewHolder.cardMoreVideo.setOnClickListener(this);
        viewHolder.cardMoreVideo.setTag(i);
        return view;
    }


    class ViewHolder {
        CardView cardMoreVideo;
        TextView cardTvMoreClass;
        TextView TvMoreName;
        TextView TvMoreSex;
        TextView TvMoreClass;
    }

    @Override
    public void onClick(View v) {
        itemOnClickListener.ItemCilck(v);
    }

    //使用接口回调
    public interface ItemOnClickListener {
        void ItemCilck(View v);
    }
}