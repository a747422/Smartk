package com.example.leila.smartk.Frament;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leila.smartk.Acitvity.VideoActivity;
import com.example.leila.smartk.Adapter.HomeViewAdapter;
import com.example.leila.smartk.Bean.DateBean;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.DB.HelperDb;
import com.example.leila.smartk.R;
import com.example.leila.smartk.Utils.HelperUtils;
import com.example.leila.smartk.Utils.SharedPreferenceUtil;
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
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面
 * 当要继承support.v4.app.FragmentManager,与app.Fragment有区别
 * Created by Leila on 2017/11/24.
 */

public class HomeFragment extends Fragment implements HomeViewAdapter.ItemOnClickListener {
    @BindView(R.id.lv_more)
    ListView lvMore;
    @BindView(R.id.tv_baby_hint)
    TextView tvBabyHint;
    @BindView(R.id.im_baby_hint)
    ImageView imBabyHint;
    View contentView;
    private final static String USERURI = "http://112.74.212.95/api/api/user_data";
    HelperUtils utils = new HelperUtils();
    ArrayList<DateBean> dateBeans = new ArrayList<>();
    private List<HashMap<String, Object>> list;
    HomeViewAdapter homeViewAdapter;
    private String id = "", type = "";
    private final static String TAG = "BabyMoreActivityLogD";
    private static final String APP_KEY_VIDEO = "689ca4c3c61845cc8aa163e07e66d94b";
    private static final String secret = "fc13f9b766b4fb7466fb398ba7ef96a1";
    private static final String URI = "https://open.ys7.com/api/lapp/token/get";
    long end = 1510136083, begin = 0;
    int days = 0, hour = 0, minutes = 0, second = 0;
    private String accessToken = "";
    private SQLiteDatabase sqlDb = null;
    private HelperDb db = null;
    private Cursor cursor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.home_baby_more, null);
        ButterKnife.bind(this, contentView);
        initListview();
        return contentView;
    }

    private void initListview() {

        //创建数据库
        db = new HelperDb(getActivity());
        sqlDb = db.getWritableDatabase();
        accessToken = getAccessToken();
    }

    //设置登录后页面
    private void sendInitview(String id) {
        if (!SharedPreferenceUtil.getStringData("pwd").equals("")) {
            lvMore.setVisibility(View.VISIBLE);
            tvBabyHint.setVisibility(View.GONE);
            imBabyHint.setVisibility(View.GONE);
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
                    list = new ArrayList<>();
                    JsonObject jsonObjects = new JsonParser().parse(result).getAsJsonObject();
                    Gson gson = new Gson();

                    JsonArray jsonArray = jsonObjects.getAsJsonArray("data");
                    //将bean添加到ArrayList里面,先把原有的datebeans Arraylist清空
                    dateBeans.clear();
                    for (JsonElement res : jsonArray) {
                        DateBean dateBean = gson.fromJson(res, new TypeToken<DateBean>() {
                        }.getType());
                        dateBeans.add(dateBean);
                    }
                    for (DateBean res : dateBeans) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", res.getS_name());
                        map.put("class", res.getS_class());
                        map.put("sex", res.getS_sex());
                        list.add(map);
                    }
                    Log.d(TAG, "list数据" + list.toString());

                    homeViewAdapter = new HomeViewAdapter(getActivity(), list, R.layout.home_baby_more_item, HomeFragment.this);
                    homeViewAdapter.notifyDataSetChanged();
                    lvMore.setAdapter(homeViewAdapter);

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
            lvMore.setVisibility(View.GONE);
            tvBabyHint.setVisibility(View.VISIBLE);
            imBabyHint.setVisibility(View.VISIBLE);
        }
    }


    //登录页面订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginBean loginBean) {
        id = loginBean.getUser_name();
        type = loginBean.getType();
        if (type.equals("user")) {
            sendInitview(id);
        } else if (type.equals("admin")) {

        }

    }

    /**
     * listView  item内部点击
     *
     * @param v
     */

    @Override
    public void ItemCilck(View v) {
        int position;
        position = (Integer) v.getTag();
        String sClass = (String) list.get(position).get("class");
        switch (v.getId()) {
            case R.id.card_more_video:
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("class", sClass);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
            default:
                break;
        }

    }

    //获取令牌
    public String getAccessToken() {
        cursor = sqlDb.rawQuery("select * from smark_EZUI where secret=?", new String[]{secret});
        //getCount()获取到查询返回结果数
        int count = cursor.getCount();
        if (count >= 1) {
            while (cursor.moveToNext()) {
                end = Integer.valueOf(cursor.getString(cursor.getColumnIndex("expireTime")));
            }
            Log.d(TAG, end + "");
            begin = System.currentTimeMillis() / 1000;
            days = (int) (begin - end) / 86400;
            hour = (int) (begin - end) % 86400 / 3600;
            minutes = (int) (begin - end) % 86400 % 3600 / 60;
            second = (int) (begin - end) % 86400 % 3600 % 60;
            Log.d(TAG, "相差时间" + days + "天" + hour + "小时" + minutes + "分钟" + second + "秒");
        } else {
            begin = System.currentTimeMillis() / 1000;
            days = (int) (begin - end) / 86400;
            hour = (int) (begin - end) % 86400 / 3600;
            minutes = (int) (begin - end) % 86400 % 3600 / 60;
            second = (int) (begin - end) % 86400 % 3600 % 60;
            Log.d("时间", "相差时间" + days + "天" + hour + "小时" + minutes + "分钟" + second + "秒");
        }

        if (days >= 6) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("appKey", APP_KEY_VIDEO);
                jsonObject.put("appSecret", secret);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestParams requestParams = new RequestParams(URI);
            requestParams.addBodyParameter("appKey", APP_KEY_VIDEO);
            requestParams.addParameter("appSecret", secret);
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JsonObject jsonObjects = new JsonParser().parse(result).getAsJsonObject();

                    Log.d(TAG, jsonObjects.get("msg").toString() + "");
                    Log.d(TAG, jsonObjects.get("data").toString());
                    JsonObject json = new JsonParser().parse(jsonObjects.get("data").toString()).getAsJsonObject();


                    accessToken = json.get("accessToken").toString();
                    accessToken = accessToken.replace("\"", "");
                    Log.d(TAG, accessToken);
                    end = System.currentTimeMillis() / 1000;
                    Log.d(TAG, end + "");
                    //插入数据库
                    ContentValues values = new ContentValues();
                    values.put("accessToken", accessToken);
                    values.put("expireTime", end);
                    values.put("secret", secret);
                    cursor = sqlDb.rawQuery("select * from smark_EZUI where secret=?", new String[]{secret});
                    //getCount()获取到查询返回结果数
                    int count = cursor.getCount();
                    if (count >= 1) {
                        sqlDb.update("smark_EZUI", values, "secret=?", new String[]{secret});
                    } else {

                        sqlDb.insert("smark_EZUI", null, values);
                    }
                    values.clear();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d(TAG, "错误" + ex.toString());

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            return accessToken;
        } else {
            //小于7天的时候直接查询数据库
            cursor = sqlDb.rawQuery("select * from smark_EZUI where secret=?", new String[]{secret});
            while (cursor.moveToNext()) {
                accessToken = cursor.getString(cursor.getColumnIndex("accessToken"));
            }
            return accessToken;
        }


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

}
