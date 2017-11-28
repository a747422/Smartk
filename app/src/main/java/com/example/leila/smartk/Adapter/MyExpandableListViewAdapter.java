package com.example.leila.smartk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leila.smartk.MainActivity;
import com.example.leila.smartk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leila on 2017/11/24.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Map<String, List> dataset = new HashMap<>();
    private String[] parentList = new String[]{};
    private List<String> childList = new ArrayList<>();

    private Context context;

    public MyExpandableListViewAdapter( Context context) {
        this.context = context;
        initialData();
    }

    private void initialData() {
        int i = 0;
        while (parentList.length >= 1 || !childList.isEmpty()) {
            dataset.put(parentList[i], childList);
            i++;
        }

    }

    //  获得某个父项的某个子项
    @Override
    public Object getChild(int parentPos, int childPos) {
        return dataset.get(parentList[parentPos]).get(childPos);
    }

    //  获得父项的数量
    @Override
    public int getGroupCount() {
        return dataset.size();
    }

    //  获得某个父项的子项数目
    @Override
    public int getChildrenCount(int parentPos) {
        return dataset.get(parentList[parentPos]).size();
    }

    //  获得某个父项
    @Override
    public Object getGroup(int parentPos) {
        return dataset.get(parentList[parentPos]);
    }

    //  获得某个父项的id
    @Override
    public long getGroupId(int parentPos) {
        return parentPos;
    }

    //  获得某个父项的某个子项的id
    @Override
    public long getChildId(int parentPos, int childPos) {
        return childPos;
    }

    //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
    @Override
    public boolean hasStableIds() {
        return false;
    }


    //  获得父项显示的view
    @Override
    public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_personal_parent_item, null);
        }
        view.setTag(R.layout.main_personal_parent_item, parentPos);
        view.setTag(R.layout.main_personal_child_item, -1);
        TextView text = (TextView) view.findViewById(R.id.tv_personal_title);
        text.setText(parentList[parentPos]);
        return view;
    }

    //  获得子项显示的view
    @Override
    public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_personal_child_item, null);
        }
        view.setTag(R.layout.main_personal_parent_item, parentPos);
        view.setTag(R.layout.main_personal_child_item, childPos);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_personal_conent);
//        dataset.get(parentList[parentPos]).get(parentList[parentPos]);
        tvContent.setText(dataset.get(parentList[parentPos]).get(childPos)+"");
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点到了内置的textview", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}