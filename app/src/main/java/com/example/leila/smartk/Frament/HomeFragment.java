package com.example.leila.smartk.Frament;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leila.smartk.Acitvity.AboutActivity;
import com.example.leila.smartk.Acitvity.BabyMoreActivity;
import com.example.leila.smartk.Acitvity.SettingsActivity;
import com.example.leila.smartk.Acitvity.VideoActivity;
import com.example.leila.smartk.Adapter.RecyclerViewAdapter;
import com.example.leila.smartk.Bean.DateBean;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.Bean.VideoListBean;
import com.example.leila.smartk.R;
import com.example.leila.smartk.Utils.SharedPreferenceUtil;
import com.ezvizuikit.open.EZUIKit;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 碎片化的设置
 * Created by Leila on 2017/9/14.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.ll_baby_info)
    LinearLayout llBabyInfo;
    @BindView(R.id.tv_baby_name)
    TextView tvBabyName;
    @BindView(R.id.tv_baby_class)
    TextView tvBabyClass;
    @BindView(R.id.tv_baby_sex)
    TextView tvBabySex;
    @BindView(R.id.card_baby_video)
    CardView cardBabyVideo;
    @BindView(R.id.card_tv_baby_class)
    TextView cardTvBabyClass;
    @BindView(R.id.tv_baby_hint)
    TextView tvBabyHint;
    @BindView(R.id.ll_baby_meg)
    LinearLayout llBabyMeg;
    View contentView;
    List<VideoListBean> pro = new ArrayList<VideoListBean>();
    private final static String USERURI = "http://112.74.212.95/api/api/user_data";

    private String id = "", type = "";
    ArrayList<DateBean> dateBeans = new ArrayList<>();
    ArrayList<String> sName = new ArrayList<>();
    ArrayList<String> sClass = new ArrayList<>();
    ArrayList<String> sSex = new ArrayList<>();
    private final static String TAG = "HomeFragmentLogD";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.main_home, null);

        ButterKnife.bind(this, contentView);
        initListview();
        return contentView;
    }

    private void initListview() {
        if (!SharedPreferenceUtil.getStringData("pwd").equals("")) {
            if (SharedPreferenceUtil.getStringData("type").equals("user")) {
                tvBabyHint.setVisibility(View.INVISIBLE);
                llBabyMeg.setVisibility(View.VISIBLE);
            } else {
                llBabyInfo.setVisibility(View.INVISIBLE);


            }
        } else {
            llBabyInfo.setVisibility(View.VISIBLE);
            tvBabyHint.setVisibility(View.VISIBLE);
            llBabyMeg.setVisibility(View.INVISIBLE);
        }


        initvar();
        //设置并列两行的layoutManager
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //设置adapter
        RecyclerViewAdapter rec = new RecyclerViewAdapter(pro);
        recyclerView.setAdapter(rec);
        rec.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                if (!SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
                    if (!SharedPreferenceUtil.getStringData("type").equals("user")) {
                        Intent intent = new Intent(getContext(), VideoActivity.class);
                        intent.putExtra("class", sClass);
                        startActivity(intent);
                        makeText("点击了" + position);
                    } else {
                        makeText("对不起，您没有权限！");
                    }
                } else {
                    makeText("请您登录后查看视频！");
                }
            }
        });

    }


    //家长页面

    private void sendInitview(String id) {
        if (!SharedPreferenceUtil.getStringData("pwd").equals("")) {
            if (SharedPreferenceUtil.getStringData("type").equals("user")) {

                tvBabyHint.setVisibility(View.INVISIBLE);
                llBabyMeg.setVisibility(View.VISIBLE);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_name", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestParams requestParams = new RequestParams(USERURI);
                requestParams.setAsJsonContent(true);
                requestParams.setBodyContent(jsonObject.toString());
                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("emmmm", "shoudao " + result);
                        JsonObject jsonObjects = new JsonParser().parse(result).getAsJsonObject();
                        Gson gson = new Gson();

                        JsonArray jsonArray = jsonObjects.getAsJsonArray("data");
                        //将bean添加到ArrayList里面
                        for (JsonElement res : jsonArray) {
                            DateBean dateBean = gson.fromJson(res, new TypeToken<DateBean>() {
                            }.getType());
                            dateBeans.add(dateBean);
                        }

                        for (DateBean res : dateBeans) {
                            sName.add(res.getS_name());
                            sClass.add(res.getS_class());
                            sSex.add(res.getS_sex());

                        }

                        tvBabyName.setText("姓名：" + sName.get(0));
                        tvBabyClass.setText("班级：" + sClass.get(0));
                        tvBabySex.setText("性别：" + sSex.get(0));
                        cardTvBabyClass.setText(sClass.get(0));
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d("mmmm", "错误" + ex.toString());
                        Toast.makeText(getActivity(), "网络错误：" + ex.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

            } else {
                llBabyInfo.setVisibility(View.INVISIBLE);


            }
        } else {
            llBabyInfo.setVisibility(View.VISIBLE);
            tvBabyHint.setVisibility(View.VISIBLE);
            llBabyMeg.setVisibility(View.INVISIBLE);
        }


    }


    @OnClick({R.id.card_baby_video, R.id.ll_baby_more})
    public void onClickBaby(View view) {
        switch (view.getId()) {
            case R.id.card_baby_video:

                if (SharedPreferenceUtil.getStringData("type").equals("user")) {
                    Intent intent = new Intent(getContext(), VideoActivity.class);
                    intent.putExtra("class", sClass);
                    startActivity(intent);
                } else {
                    makeText("对不起，您没有权限！");
                }

                break;
            case R.id.ll_baby_more:
                if (sName.size() > 1) {
                    Intent msg = new Intent(getActivity(), BabyMoreActivity.class);
                    msg.putExtra("id", id);
                    msg.putExtra("type", type);
                    startActivity(msg);

                } else {
                    makeText("您目前在园内只有一个小孩");
                }
                break;
            default:
                break;
        }

    }


    private void initvar() {
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
        pro.add(new VideoListBean(R.drawable.bg, "小二班"));
    }

    //登录页面订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginBean loginBean) {
        id = loginBean.getUser_name();
        type = loginBean.getType();
        if (type.equals("user")) {
            sendInitview(loginBean.getUser_name());
        } else if (type.equals("admin")) {

        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void makeText(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
