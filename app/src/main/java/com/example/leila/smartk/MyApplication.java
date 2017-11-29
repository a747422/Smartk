package com.example.leila.smartk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.example.leila.smartk.Utils.SharedPreferenceUtil;
import com.ezvizuikit.open.EZUIKit;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Leila on 2017/9/14.
 */

public class MyApplication extends Application {

    // user your appid the key.
    private static final String APP_ID = "2882303761517620606";
    // user your appid the key.
    private static final String APP_KEY = "5961762030606";

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "com.example.leila.smartk";
    private static final String APP_KEY_VIDEO = "689ca4c3c61845cc8aa163e07e66d94b";


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtil.init(getApplicationContext(), "user");
        x.Ext.init(this);
        //初始化push推送服务
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        /**
         * 初始化EZUIKit
         * @param application 应用application
         * @param appkey      开发者申请的appkey
         * @return
         */
        EZUIKit.initWithAppKey(this, APP_KEY_VIDEO);
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);

    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


}

