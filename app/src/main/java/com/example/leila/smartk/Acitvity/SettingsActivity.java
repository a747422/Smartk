package com.example.leila.smartk.Acitvity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leila.smartk.Bean.DateBean;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.DB.HelperDb;
import com.example.leila.smartk.R;
import com.example.leila.smartk.Utils.HelperUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leila on 2017/11/18.
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.tv_setttings_id)
    TextView tvSetttingsId;
    @BindView(R.id.tv_settings_name)
    TextView tvSettingsName;
    @BindView(R.id.tv_settings_nick)
    TextView tvSettingsNick;
    @BindView(R.id.tv_settings_email)
    TextView tvSetttingsEmail;
    @BindView(R.id.tv_settings_mobile)
    TextView tvSettingsMobile;
    @BindView(R.id.tv_settings_address)
    TextView tvSettingsAddress;
    @BindView(R.id.iv_settings_sex)
    ImageView ivSettingsSex;

    private Intent intent = new Intent();
    private String valid = "", id = "", type = "", nick = "", name = "", sName = "", mobile = "", email = "", address = "", sex = "", sClass = "", sSex = "";
    private final static String URI = "http://112.74.212.95/api/api/edit_user";
    private final static String USERURI = "http://112.74.212.95/api/api/user_data";
    HelperUtils utils = new HelperUtils();
    ArrayList<DateBean> dateBeans = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_settings);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        if (!id.equals("")) {
            sendInitview();
        }
    }

    //修改信息
    private void sendHttp(JSONObject jsonObject) {
        RequestParams requestParams = new RequestParams(URI);
        requestParams.setAsJsonContent(true);
        requestParams.setBodyContent(jsonObject.toString());
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    valid = jsonObject.getString("valid");
                    if (valid.equals("1")) {
                        sendInitview();
                    } else {
                        utils.sendmakeText(SettingsActivity.this, "修改信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                utils.sendmakeText(SettingsActivity.this, "失败返回" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //更新查询
    private void sendInitview() {
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
                    id = res.getUser_name();
                    name = res.getReal_name();
                    nick = res.getNick();
                    mobile = res.getMobile();
                    email = res.getEmail();
                    address = res.getAddress();
                    sex = res.getSex();
                }
                tvSetttingsId.setText(id);
                tvSettingsName.setText(name);
                tvSettingsNick.setText(nick);
                tvSetttingsEmail.setText(email);
                tvSettingsMobile.setText(mobile);
                tvSettingsAddress.setText(address);

                if (sex.equals("女")) {
                    ivSettingsSex.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_woman));
                } else {
                    ivSettingsSex.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_man));
                }
                if (type.equals("user")) {
                    EventBus.getDefault().post(new LoginBean(id, "user"));
                } else if (type.equals("admin")) {
                    EventBus.getDefault().post(new LoginBean(id, "admin"));
                }
                utils.sendmakeText(SettingsActivity.this, "信息修改成功");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("mmmm", "错误" + ex.toString());
                utils.sendmakeText(SettingsActivity.this,"网络错误：" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @OnClick({R.id.iv_cancel, R.id.ll_settings_nick, R.id.ll_settings_mobile, R.id.ll_settings_email, R.id.ll_settings_address})
    public void onClickSettings(View view) {
        switch (view.getId()) {
            case R.id.ll_settings_mobile:
                AlertDialog.Builder mobileDialog =
                        new AlertDialog.Builder(SettingsActivity.this);
                final View mobileView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_settings, null);
                mobileDialog.setTitle("请输入您要修改的信息");
                mobileDialog.setView(mobileView);
                TextView tvMobile = (TextView) mobileView.findViewById(R.id.tv_settings_);
                final EditText edMobile = (EditText) mobileView.findViewById(R.id.ed_settings_);
                tvMobile.setText("电话");
                mobileDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mobile = edMobile.getText().toString();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("ident", "update");
                                    jsonObject.put("id", id);
                                    jsonObject.put("name", name);
                                    jsonObject.put("sName", sName);
                                    jsonObject.put("nick", nick);
                                    jsonObject.put("email", email);
                                    jsonObject.put("mobile", mobile);
                                    jsonObject.put("address", address);
                                    jsonObject.put("sex", sex);
                                    jsonObject.put("sClass", sClass);
                                    jsonObject.put("sSex", sSex);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendHttp(jsonObject);
                            }
                        });
                mobileDialog.show();
                break;
            case R.id.ll_settings_nick:
                AlertDialog.Builder nickDialog =
                        new AlertDialog.Builder(SettingsActivity.this);
                final View nickView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_settings, null);
                nickDialog.setTitle("请输入您要修改的信息");
                nickDialog.setView(nickView);
                TextView tvNick = (TextView) nickView.findViewById(R.id.tv_settings_);
                final EditText edNick = (EditText) nickView.findViewById(R.id.ed_settings_);
                tvNick.setText("昵称");
                nickDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nick = edNick.getText().toString();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("ident", "update");
                                    jsonObject.put("id", id);
                                    jsonObject.put("name", name);
                                    jsonObject.put("sName", sName);
                                    jsonObject.put("nick", nick);
                                    jsonObject.put("email", email);
                                    jsonObject.put("mobile", mobile);
                                    jsonObject.put("address", address);
                                    jsonObject.put("sex", sex);
                                    jsonObject.put("sClass", sClass);
                                    jsonObject.put("sSex", sSex);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendHttp(jsonObject);
                            }
                        });
                nickDialog.show();
                break;
            case R.id.ll_settings_email:
                AlertDialog.Builder emailDialog =
                        new AlertDialog.Builder(SettingsActivity.this);
                final View emailView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_settings, null);
                emailDialog.setTitle("请输入您要修改的信息");
                emailDialog.setView(emailView);
                TextView tvEmail = (TextView) emailView.findViewById(R.id.tv_settings_);
                final EditText edEmail = (EditText) emailView.findViewById(R.id.ed_settings_);
                tvEmail.setText("邮箱");
                emailDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                email = edEmail.getText().toString();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("ident", "update");
                                    jsonObject.put("id", id);
                                    jsonObject.put("name", name);
                                    jsonObject.put("sName", sName);
                                    jsonObject.put("nick", nick);
                                    jsonObject.put("email", email);
                                    jsonObject.put("mobile", mobile);
                                    jsonObject.put("address", address);
                                    jsonObject.put("sex", sex);
                                    jsonObject.put("sClass", sClass);
                                    jsonObject.put("sSex", sSex);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendHttp(jsonObject);
                            }
                        });
                emailDialog.show();
                break;
            case R.id.ll_settings_address:
                AlertDialog.Builder addressDialog =
                        new AlertDialog.Builder(SettingsActivity.this);
                final View adderssView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_settings, null);
                addressDialog.setTitle("请输入您要修改的信息");
                addressDialog.setView(adderssView);
                TextView tvAddress = (TextView) adderssView.findViewById(R.id.tv_settings_);
                final EditText edAddress = (EditText) adderssView.findViewById(R.id.ed_settings_);
                tvAddress.setText("地址");
                addressDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                address = edAddress.getText().toString();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("ident", "update");
                                    jsonObject.put("id", id);
                                    jsonObject.put("name", name);
                                    jsonObject.put("sName", sName);
                                    jsonObject.put("nick", nick);
                                    jsonObject.put("email", email);
                                    jsonObject.put("mobile", mobile);
                                    jsonObject.put("address", address);
                                    jsonObject.put("sex", sex);
                                    jsonObject.put("sClass", sClass);
                                    jsonObject.put("sSex", sSex);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendHttp(jsonObject);
                            }
                        });
                addressDialog.show();
                break;
            case R.id.iv_cancel:
                finish();
                break;

        }
    }


}
