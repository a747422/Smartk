package com.example.leila.smartk.Adapter;

import android.content.Context;
import android.util.Log;
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
 * Created by Leila on 2017/11/7.
 */

public class PersonalViewAdapter extends BaseAdapter {
    private final Context context;
    private final List<HashMap<String, Object>> list;
    private final int resource;

    public PersonalViewAdapter(Context context, List<HashMap<String, Object>> list, int resource) {
        this.context = context;
        this.list = list;
        this.resource = resource;
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
            viewHolder.IvMessgeIcon = (ImageView) view.findViewById(R.id.iv_personal_icon);
            viewHolder.TvMessgeTitle = (TextView) view.findViewById(R.id.tv_personal_title);
            viewHolder.TvMessgeConent = (TextView) view.findViewById(R.id.tv_personal_conent);
            viewHolder.TvMessgeTime = (TextView) view.findViewById(R.id.tv_personal_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.IvMessgeIcon.setImageResource(R.drawable.ic_messge_info);
        viewHolder.TvMessgeTitle.setText(map.get("title").toString());
        viewHolder.TvMessgeConent.setText(map.get("description").toString());
        viewHolder.TvMessgeTime.setText(map.get("time").toString());
        return view;
    }


    class ViewHolder {
        ImageView IvMessgeIcon;
        TextView TvMessgeTitle;
        TextView TvMessgeConent;
        TextView TvMessgeTime;
    }
}