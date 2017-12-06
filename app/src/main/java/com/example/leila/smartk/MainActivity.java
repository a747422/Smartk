package com.example.leila.smartk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.leila.smartk.Acitvity.LoginAcitvity;
import com.example.leila.smartk.Adapter.MainActivityViewPagerAdapter;
import com.example.leila.smartk.Bean.LoginBean;
import com.example.leila.smartk.Frament.HomeFragment;
import com.example.leila.smartk.Frament.PersonalFragment;
import com.example.leila.smartk.Utils.Base64Utils;
import com.example.leila.smartk.Utils.SharedPreferenceUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * home页面的设置
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MainActivityViewPagerAdapter adapter;
    private BottomNavigationView navigation;
    private String id = "", pwd = "";
    private String type = "", valid = "";

    private final static String URI = "http://112.74.212.95/api/api/user_login";
    private final static String TAG = "MainAcitvityLogD";
    //记录第一次点击的时间
    private long clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MiPushClient.setAlias(MainActivity.this, "1", null);
        MiPushClient.setUserAccount(this, id, null);
        initView();

    }

    //初始化页面
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //将homeFrament添加到页面
        adapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new PersonalFragment());
        viewPager.setAdapter(adapter);

        if (SharedPreferenceUtil.getStringData("pwd").isEmpty()) {
            makeText("首次登录");
        } else {
            id = SharedPreferenceUtil.getStringData("id");
            pwd = SharedPreferenceUtil.getStringData("pwd");
            type = SharedPreferenceUtil.getStringData("type");
            pwd = Base64Utils.encodeString(pwd);
            Log.d("获取到", id + pwd + "");
            //设置小米推送的设置
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
            //post json数据
            RequestParams requestParams = new RequestParams(URI);
            requestParams.setAsJsonContent(true);
            requestParams.setBodyContent(jsonObject.toString());
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(JSONTokener(result));
                        valid = jsonObject.getString("valid");
                        if (valid.equals("1")) {
                            SharedPreferenceUtil.SaveData("id", id);
                            SharedPreferenceUtil.SaveData("pwd", pwd);
                            SharedPreferenceUtil.SaveData("type", type);
                            if (type.equals("user")) {
                                EventBus.getDefault().post(new LoginBean(id, "user"));
                            } else if (type.equals("admin")) {
                                EventBus.getDefault().post(new LoginBean(id, "admin"));
                            }
                            makeText("自动登录成功");
                        } else {
                            SharedPreferenceUtil.SaveData("pwd", "");
                            makeText("自动登录失败");
                            Intent intent = new Intent(MainActivity.this, LoginAcitvity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d(TAG, "错误" + ex.toString());
                    SharedPreferenceUtil.SaveData("pwd", "");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            pwd = SharedPreferenceUtil.getStringData("pwd");
        }
        viewPager.addOnPageChangeListener(mPageChangeListener);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //懵逼？
    public void getMessge() {
        Intent intent = getIntent();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("title", intent.getStringExtra("title"));
            if (jsonObject.toString().length() > 5) {
                makeText("您有新的信息到达！");
                viewPager.setCurrentItem(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //添加页更改侦听器
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //当ViewPager 滑动后设置   选中相应选项
            navigation.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    //导航项选定的侦听器
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);

                    break;

                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(1);
                    break;
            }
            return true;
        }
    };

    //读取单卡手机的imei，双卡不知道啥效果
    public static String getIMEI(FragmentActivity context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }


    private void makeText(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public String JSONTokener(String in) {
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    //点击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //判断是否退出
    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "提示：再按一次后退键退出程序",
                    Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            Log.e(TAG, "exit application");
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessge();
    }
}
