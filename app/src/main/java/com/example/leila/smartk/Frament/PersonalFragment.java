package com.example.leila.smartk.Frament;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.leila.smartk.Acitvity.AboutActivity;
import com.example.leila.smartk.Acitvity.SettingsActivity;
import com.example.leila.smartk.Adapter.PersonalViewAdapter;
import com.example.leila.smartk.Bean.DateBean;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.Bean.MessageBean;
import com.example.leila.smartk.DB.HelperDb;
import com.example.leila.smartk.Acitvity.LoginAcitvity;
import com.example.leila.smartk.MainActivity;
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
import butterknife.OnClick;


/**
 * Created by Leila on 2017/9/20.
 */

public class PersonalFragment extends Fragment {

    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.im_personal_hint)
    ImageView imPersonalHint;
    @BindView(R.id.tv_personal_hint)
    TextView tvPersonalHint;
    @BindView(R.id.tv_personal_user)
    TextView TvPersonalUser;
    @BindView(R.id.tv_personal_in)
    TextView TvPersonalIn;

    private View contentView;

    private final static String TAG = "PersonalFragmentLogD";
    private String description = "", title = "", time = "", id = "";
    private String type = "", nick = "", sName = "", mobile = "", email = "";

    private List<HashMap<String, Object>> list;
    private ArrayList<String> spinnerList = new ArrayList<>();
    private SQLiteDatabase sqlDb = null;
    private HelperDb db = null;
    private Cursor cursor;
    private Integer mHour = 0, mMinute = 0;
    private final static String USERURI = "http://112.74.212.95/api/api/user_data";
    private final static String URI = "http://112.74.212.95/api/api/leave";
    HelperUtils helperUtils = new HelperUtils();
    ArrayList<DateBean> dateBeans = new ArrayList<>();

    //使用线程查询数据库，然后更新切换主线程更新UI
    public class MyRunnable implements Runnable {

        @Override
        public void run() {

            cursor = sqlDb.rawQuery("select * from smark_messge where user=?", new String[]{id});
            updateUI(getContext());
        }

    }

    public void updateUI(Context context) {
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendRefresh();
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.main_personal, null);
        ButterKnife.bind(this, contentView);
        initView();
        return contentView;
    }

    //初始化个人信息
    private void initView() {
        //创建数据库
        db = new HelperDb(getActivity());
        sqlDb = db.getWritableDatabase();
        //item长按删除信息
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tvTime = view.findViewById(R.id.tv_personal_time);
                final String str = tvTime.getText().toString();
                new AlertDialog.Builder(getActivity())
                        .setTitle("是否删除信息")
                        .setIcon(R.drawable.ic_personal_warn)

                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int delete = sqlDb.delete("smark_messge", "time=?", new String[]{str});
                                if (delete >= 1) {
                                    new Thread(new MyRunnable()).start();
                                } else {
                                    helperUtils.sendmakeText(getContext(), "删除失败！");
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return false;
            }
        });
    }

    //接收到推送后刷新list事件
    private void sendListView(MessageBean messageBean) {
        if (SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
            imPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setText("登陆后可查看信息");
            listView.setVisibility(View.GONE);

            TvPersonalUser.setText("登陆后可查看昵称");
            TvPersonalIn.setText("登陆后可查看个人信息");
        } else {

            id = SharedPreferenceUtil.getStringData("id");

            if (messageBean.getTitle().length() < 1) {
                new Thread(new MyRunnable()).start();
            } else {


                title = messageBean.getTitle();
                description = messageBean.getDescription();
                time = messageBean.getTime();
                Log.d("真的奇怪嘤嘤嘤", time + "");
                //插入数据库
                ContentValues values = new ContentValues();
                values.put("user", id);
                values.put("title", title);
                values.put("description", description);
                values.put("time", time);
                sqlDb.insert("smark_messge", null, values);
                values.clear();
                new Thread(new MyRunnable()).start();

            }
        }

    }


    //推送信息的列表
    private void sendRefresh() {
        list = new ArrayList<HashMap<String, Object>>();

        //getCount()获取到查询返回结果数
        int count = cursor.getCount();
        int i = 0;
        //>0表示存在
        if (count >= 1) {
            imPersonalHint.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            tvPersonalHint.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                HashMap<String, Object> map = new HashMap<>();
                //  parentList[i] = cursor.getString(cursor.getColumnIndex("title"));
                map.put("title", cursor.getString(cursor.getColumnIndex("title")));
                map.put("description", cursor.getString(cursor.getColumnIndex("description")));
                map.put("time", cursor.getString(cursor.getColumnIndex("time")));
                Log.d("真的奇怪", cursor.getString(cursor.getColumnIndex("time")) + "//" + cursor.getString(cursor.getColumnIndex("description")));
                list.add(map);

            }

            PersonalViewAdapter personalViewAdapter = new PersonalViewAdapter(getActivity(), list, R.layout.main_personal_item);
            listView.setAdapter(personalViewAdapter);
            personalViewAdapter.notifyDataSetChanged();

        } else {
            imPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setText("暂无信息");
            listView.setVisibility(View.GONE);

        }

    }

    //家长信息
    private void sendInitview(String user) {
        if (SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
            imPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setVisibility(View.VISIBLE);
            tvPersonalHint.setText("登陆后可查看信息");
            listView.setVisibility(View.GONE);
            TvPersonalUser.setText("登陆后可查看昵称");
            TvPersonalIn.setText("登陆后可查看个人信息");
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_name", user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestParams requestParams = new RequestParams(USERURI);
            requestParams.setAsJsonContent(true);
            requestParams.setBodyContent(jsonObject.toString());
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JsonObject jsonObjects = new JsonParser().parse(result).getAsJsonObject();
                    Gson gson = new Gson();

                    JsonArray jsonArray = jsonObjects.getAsJsonArray("data");
                    //将bean添加到ArrayList里面
                    dateBeans.clear();
                    spinnerList.clear();
                    for (JsonElement res : jsonArray) {
                        DateBean dateBean = gson.fromJson(res, new TypeToken<DateBean>() {
                        }.getType());
                        dateBeans.add(dateBean);
                    }

                    for (DateBean res : dateBeans) {
                        nick = res.getNick();
                        mobile = res.getMobile();
                        email = res.getEmail();
                        sName = res.getS_name();
                        id = res.getUser_name();
                        String spinnerName = res.getS_name();
                        spinnerList.add(spinnerName);
                        Log.d(TAG, "spinner数据" + spinnerList.toString());
                    }

                    TvPersonalUser.setText("昵称：" + nick);
                    TvPersonalIn.setText(mobile + "/" + email);
                    new Thread(new MyRunnable()).start();
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
        }
    }

    @OnClick({R.id.ll_personal_switch_account, R.id.ll_personal_in, R.id.ll_personal_about})
    public void onClickSwitchAccount(View view) {
        switch (view.getId()) {
            case R.id.ll_personal_switch_account:
                Intent account = new Intent(getActivity(), LoginAcitvity.class);
                startActivity(account);
                break;
            case R.id.ll_personal_in:
                if (!SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
                    Intent msg = new Intent(getActivity(), SettingsActivity.class);
                    msg.putExtra("id", id);
                    msg.putExtra("type", type);
                    Log.d(TAG, "传递数据为" + id);
                    startActivity(msg);
                } else {
                    helperUtils.sendmakeText(getActivity(), "请您登录后查看个人信息！");
                }
                break;
            case R.id.ll_personal_about:
                Intent about = new Intent(getActivity(), AboutActivity.class);
                startActivity(about);
                break;

            default:
                break;
        }

    }

    //请假dialog
    @OnClick(R.id.ll_personal_leave)
    public void OnClickLeave() {
        if (!SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
            AlertDialog.Builder dialogs =
                    new AlertDialog.Builder(getActivity());
            final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_menu_in, null);
            dialogs.setTitle("填写请假信息");
            dialogs.setView(dialogView);

            final Spinner spStudent = (Spinner) dialogView.findViewById(R.id.sp_student);
            final EditText edCause = (EditText) dialogView.findViewById(R.id.ed_cause);
            final EditText edTime = (EditText) dialogView.findViewById(R.id.ed_time);
            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.tp_picker);
            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.dp_picker);

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, R.id.textview, spinnerList);

            spStudent.setAdapter(adapter);
            spStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sName = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            adapter.notifyDataSetChanged();

            //初始化时间
            timePicker.setIs24HourView(true);
            mHour = timePicker.getCurrentHour();
            mMinute = timePicker.getCurrentMinute();
            //日期格式
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("%d-%02d-%02d",
                    datePicker.getYear(),
                    datePicker.getMonth() + 1,
                    datePicker.getDayOfMonth()));
            //时间小于10的数字 前面补0 如01:12:00
            final String leaveTime = sb + " " + (new StringBuilder().append(mHour < 10 ? "0" + mHour : mHour).append(":")
                    .append(mMinute < 10 ? "0" + mMinute : mMinute).append(":00")).toString();

            dialogs.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sname = sName;
                            String cause = edCause.getText().toString();
                            String time = edTime.getText().toString();
                            if (!cause.equals("") && !time.equals("") && !sname.equals("")) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("name", sname + "");
                                    jsonObject.put("reason", edCause.getText().toString() + "");
                                    jsonObject.put("time", edTime.getText().toString() + "");
                                    jsonObject.put("leave_time", leaveTime + "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RequestParams requestParams = new RequestParams(URI);
                                requestParams.setAsJsonContent(true);
                                requestParams.setBodyContent(jsonObject.toString());
                                Log.d(TAG, "请求连接为" + jsonObject.toString());
                                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        helperUtils.sendmakeText(getActivity(), "请假已成功提交");

                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        helperUtils.sendmakeText(getActivity(), "请假失败返回系统返回" + ex.toString());
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                            } else {
                                helperUtils.sendmakeText(getActivity(), "请您检查请假信息是否留空，如留空请重新填写");
                            }
                        }
                    });
            dialogs.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialogs.show();
        } else {
            helperUtils.sendmakeText(getActivity(), "请您登录后请假！");
        }

    }

    //订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean messageBean) {
        Log.d("onEvent", messageBean.toString());
        helperUtils.sendmakeText(getActivity(), "您有新的信息到达，请点击个人页面查看！");
        sendListView(messageBean);

    }

    //登录页面订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginBean loginBean) {
        type = loginBean.getType();
        if (type.equals("user")) {
            sendInitview(loginBean.getUser_name());
        } else if (type.equals("admin")) {

        }


    }


    //向EventBus注册，成为订阅者以及解除注册
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
