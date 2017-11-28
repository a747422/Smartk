package com.example.leila.smartk;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.telephony.TelephonyManager;

import com.example.leila.smartk.Acitvity.SettingsActivity;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.Bean.MessageBean;
import com.example.leila.smartk.Utils.Base64Utils;
import com.example.leila.smartk.Utils.JellyInterpolatorUtils;
import com.example.leila.smartk.Utils.SharedPreferenceUtil;
import com.example.leila.smartk.Utils.SubmitButtonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
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
 * Created by Leila on 2017/9/28.
 */

public class LoginAcitvity extends AppCompatActivity {
    @BindView(R.id.ed_id)
    EditText mEdId;
    @BindView(R.id.ed_pwd)
    EditText mEdPwd;
    @BindView(R.id.rb_login_patriarch)
    RadioButton rbLoginPatriarch;
    @BindView(R.id.rb_login_teacher)
    RadioButton rbLoginTeacher;
    @BindView(R.id.rb_login_leader)
    RadioButton rbLoginLeader;
    @BindView(R.id.input_layout_id)
    RadioGroup inputLoginId;

    //    @BindView(R.id.main_btn_login)
//    TextView mBtnLogin;
    @BindView(R.id.submit_button)
    SubmitButtonUtils submitButton;
    @BindView(R.id.layout_progress)
    LinearLayout progress;
    @BindView(R.id.input_layout)
    LinearLayout mInputLayout;
    @BindView(R.id.input_layout_name)
    LinearLayout mName;
    @BindView(R.id.input_layout_pwd)
    LinearLayout mPwd;

    private float mWidth, mHeight;
    private String id = "", pwd = "";

    private String error = "", type = "", valid = "", nick = "", name = "", sName = "", mobile = "", email = "", address = "", sex = "", sClass = "", sSex = "";

    SharedPreferences sp;
    private final static String URI = "http://112.74.212.95/api/api/user_login";
    private final static String MAG = "LoginAcitvityLogD";
    HashMap<String, Object> hashMaps = new HashMap<String, Object>();
    ArrayList<LoginBean> loginBeans = new ArrayList<>();
//账号密码lolode  12345678

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_personal_login);
        ButterKnife.bind(this);
        mEdPwd.setText("123456");


        rbLoginPatriarch.setChecked(true);
        type = "user";

        inputLoginId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (rbLoginPatriarch.getId() == i) {
                    type = "user";
                }
                if (rbLoginTeacher.getId() == i) {
                    type = "teacher";
                }
                if (rbLoginLeader.getId() == i) {
                    type = "admin";
                }

                Toast.makeText(LoginAcitvity.this, "选择：" + type, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.submit_button)
    public void submitButtonOnCilck(View view) {
        id = mEdId.getText().toString();
        pwd = mEdPwd.getText().toString();

        pwd = Base64Utils.encodeString(pwd);
        Log.d("pwd加密后", pwd);
        progressAnimator(mInputLayout);

        String PushId = getIMEI(this);
        MiPushClient.setAlias(this, id, null);
        MiPushClient.setUserAccount(this, id, null);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("admin_username", id);
            jsonObject.put("admin_password", pwd);
            jsonObject.put("imei", PushId);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams requestParams = new RequestParams(URI);
        requestParams.setAsJsonContent(true);
        requestParams.setBodyContent(jsonObject.toString());
//        requestParams.addBodyParameter("admin_username", id);
//        requestParams.addParameter("admin_password", pwd);
        Log.d(MAG, "请求连接为" + requestParams.toString());
        x.http().post(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.d(MAG, "服务器请求成功，返回" + result);
                Toast.makeText(LoginAcitvity.this, "服务器请求成功，返回\"" + result + "\"", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(JSONTokener(result));
                    valid = jsonObject.getString("valid");
                    if (valid.equals("1")) {
                        submitButton.doResult(true);
                        SharedPreferenceUtil.SaveData("id", id);
                        SharedPreferenceUtil.SaveData("pwd", pwd);
                        SharedPreferenceUtil.SaveData("type", type);
                        if (type.equals("user")) {
                            EventBus.getDefault().post(new LoginBean(id, "user"));
                        } else if (type.equals("admin")) {
                            EventBus.getDefault().post(new LoginBean(id, "admin"));
                        }
                        finish();
                        Toast.makeText(LoginAcitvity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferenceUtil.SaveData("pwd", "");
                        submitButton.doResult(false);
                        submitButton.reset();
                        Toast.makeText(LoginAcitvity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                    JsonObject jsonObjects = new JsonParser().parse(result).getAsJsonObject();
//                    Gson gson = new Gson();
//
//                    JsonArray jsonArray = jsonObjects.getAsJsonArray("data");
//                    for (JsonElement res : jsonArray) {
//                        LoginBean labelBean = gson.fromJson(res, new TypeToken<LoginBean>() {
//                        }.getType());
//                        loginBeans.add(labelBean);
//
//                    }
//                    valid = jsonObject.getString("valid");
//                    for (LoginBean res : loginBeans) {
//                        name = res.getReal_name();
//                        sName = res.getS_name();
//                        nick = res.getNick();
//                        email = res.getEmail();
//                        mobile = res.getMobile();
//                        address = res.getAddress();
//                        sex = res.getSex();
//                        sClass = res.getS_class();
//                        sSex = res.getS_sex();
//                    }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                submitButton.doResult(false);
                submitButton.reset();
                SharedPreferenceUtil.SaveData("pwd", "");
                Log.d(MAG, "错误" + ex.toString());
                Toast.makeText(LoginAcitvity.this, "网络错误：" + ex.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });
        pwd = mEdPwd.getText().toString();

    }

    @OnClick({R.id.iv_cancel, R.id.tv_exit})
    public void OnClickCancel(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.tv_exit:
                AlertDialog.Builder exitDialog =
                        new AlertDialog.Builder(LoginAcitvity.this);
                exitDialog.setTitle("确定退出登录？");
                exitDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferenceUtil.SaveData("pwd", "");
                                if (type.equals("user")) {
                                    EventBus.getDefault().post(new LoginBean(id, "user"));
                                } else if (type.equals("admin")) {
                                    EventBus.getDefault().post(new LoginBean(id, "admin"));
                                }
                                finish();

                            }
                        });
                exitDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                exitDialog.show();
                break;
            default:
                break;
        }

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(500);
        animator3.setInterpolator(new JellyInterpolatorUtils());
        animator3.start();

    }

    public String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    public static String getIMEI(FragmentActivity context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }
//
//    public HashMap<String, Object> parseJSONWithGSON(String response) {
//        Gson gson = new Gson();
//        List<LoginBean> ressul = gson.fromJson(response, new TypeToken<List<LoginBean>>() {
//        }.getType());
//        for (LoginBean res : ressul) {
//            if (res.getValid().equals("1")) {
//                hashMaps.put("Valid", res.getValid());
//                hashMaps.put("UserName", res.getUserName());
//                hashMaps.put("Email", res.getEmail());
//            }
//        }
//        return hashMaps;
//    }
}
