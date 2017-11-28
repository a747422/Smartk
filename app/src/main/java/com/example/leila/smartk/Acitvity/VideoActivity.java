package com.example.leila.smartk.Acitvity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leila.smartk.Bean.DateBean;
import com.example.leila.smartk.Bean.VideoBean;
import com.example.leila.smartk.Frament.PersonalFragment;
import com.example.leila.smartk.R;
import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leila on 2017/10/27.
 */

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.player_ui)
    EZUIPlayer mPlayer;
    @BindView(R.id.spinner_video)
    Spinner mSpinner;
    @BindView(R.id.ib_play)
    ImageButton mIbPlay;
    @BindView(R.id.btn_realplay_sound)
    ImageButton mIbVoice;
    @BindView(R.id.ib_realplay_full_screen)
    ImageButton mIbFullScreen;
    @BindView(R.id.ll_progress_bar)
    LinearLayout mLlProgressBar;
    @BindView(R.id.ib_video_cancel)
    ImageButton mIbVideoCancel;
    @BindView(R.id.tv_video_error)
    TextView mTvVideoError;

    private static String playHDUrl = "";
    private static String playUrl = "";


    private String accessToken = "at.3ruz424f4bkhe7eycv309ovw9hionezl-6ihimh3dyc-1cdhxnt-ctps0vxav";

    private static Boolean mboolean = true, mbool = true;

    ArrayList<VideoBean> videoBeans = new ArrayList<>();

    private int screenWidth;
    private int screenHeight;
    private boolean sensor_flag = true;
    private boolean stretch_flag = true;
    private final static String TAG = "VideoActivityLogD";
    private String Voice = "高清", sClass = "";
    private boolean cancel = true;
    private Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home_recycler_item_video);
        ButterKnife.bind(this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        intent = getIntent();
        sClass = intent.getStringExtra("class");
        accessToken = intent.getStringExtra("accessToken");

        setURL(sClass);
        initView();
        mOnCilck();
    }


    //获取教室后判断视频连接
    private void setURL(String sClass) {
        switch (sClass) {
            case "小二班":

                playHDUrl = "ezopen://open.ys7.com/818891142/1.hd.live";
                playUrl = "ezopen://open.ys7.com/818891142/1.live";


                break;
            case "小一班":
                playHDUrl = "ezopen://open.ys7.com/112186720/1.hd.live";
                playUrl = "ezopen://open.ys7.com/112186720/1.live";

                break;
            default:
                break;
        }

    }

    //填充页面连接
    private void initView() {

        //设置播放按钮，将ImageButton的背景设置为透明
        // mIbPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_play));
        mIbPlay.getBackground().setAlpha(0);
        //设置声音按钮
        mIbVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_voice));
        mIbVoice.getBackground().setAlpha(0);
        //设置全屏按钮
        mIbFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_full_screen));
        mIbFullScreen.getBackground().setAlpha(0);
        //设置关闭全屏按钮
        mIbVideoCancel.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_cancel));
        mIbVideoCancel.getBackground().setAlpha(0);
        // mIbVideoCancel.setVisibility(View.INVISIBLE);
        Log.d(TAG, accessToken + "最后");
        //设置授权token
        EZUIKit.setAccessToken(accessToken);


        //设置宽高setSurfaceSize(dm.widthPixels, 0);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mPlayer.setSurfaceSize(screenWidth, 0);

        //设置播放参数
        mPlayer.setUrl(playHDUrl);
        Log.d(TAG, playHDUrl + "最后");

        //设置播放回调callBack
        setmPlayer();


    }

    private void mOnCilck() {
        //设置清晰度选择
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] languages = getResources().getStringArray(R.array.spinner_video);
                Voice = languages[pos];
                Toast.makeText(VideoActivity.this, "你点击的是:" + Voice, Toast.LENGTH_SHORT).show();
                if (Voice.equals("清晰")) {
                    //设置播放参数
                    mPlayer.setUrl(playUrl);
                } else {
                    //设置播放参数
                    mPlayer.setUrl(playHDUrl);
                }
                //设置播放回调callBack
                setmPlayer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //设置点击全屏
        mIbFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  sm.unregisterListener(listener);
                //  Toast.makeText(getApplicationContext(), "点击切换屏幕", Toast.LENGTH_SHORT).show();
                cancel = false;
                stretch_flag = true;
                mPlayer.releasePlayer();
                //切换成横屏
                VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mLlProgressBar.setVisibility(View.INVISIBLE);

                //   mIbVideoCancel.setVisibility(View.VISIBLE);
                mPlayer.setSurfaceSize(screenWidth, screenHeight);


                //设置播放参数
                mPlayer.setUrl(playHDUrl);

                //设置播放回调callBack
                setmPlayer();
            }
        });

        //关闭全屏视频
        mIbVideoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (cancel) {
                    mPlayer.releasePlayer();
//                    cancel = false;
                    finish();
                } else {
                    cancel = true;
                    stretch_flag = false;
                    mPlayer.releasePlayer();
                    //切换成竖屏
                    VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mPlayer.setSurfaceSize(screenWidth, 0);
                    mLlProgressBar.setVisibility(View.VISIBLE);

                    //设置播放参数
                    mPlayer.setUrl(playHDUrl);

                    //设置播放回调callBack
                    setmPlayer();
                    //  mIbVideoCancel.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //播放按钮点击事件
    @OnClick(R.id.ib_play)
    public void OnClickPlay() {
        if (mboolean) {
            mPlayer.startPlay();
            mIbPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_pause));
            if (Voice.equals("清晰")) {
                //设置播放参数
                mPlayer.setUrl(playUrl);
            } else {
                //设置播放参数
                mPlayer.setUrl(playHDUrl);
            }
            //设置播放回调callBack
            setmPlayer();

            mboolean = false;
        } else {
            mPlayer.stopPlay();
            mIbPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_play));
            mboolean = true;
        }
    }

    @OnClick(R.id.btn_realplay_sound)
    public void OnClickVoice() {

        if (mbool) {
            mIbVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_mute));
            mbool = false;
            String str = "?mute=false";
            if (Voice.equals("清晰")) {
                //设置播放参数
                mPlayer.setUrl(playHDUrl + str);
            } else {
                //设置播放参数
                mPlayer.setUrl(playUrl + str);
            }
            //设置播放回调callBack
            setmPlayer();
        } else {

            mIbVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_voice));
            mbool = true;
            if (Voice.equals("清晰")) {
                //设置播放参数
                mPlayer.setUrl(playHDUrl);
            } else {
                //设置播放参数
                mPlayer.setUrl(playUrl);
            }
            //设置播放回调callBack
            setmPlayer();

        }
    }


    private void setmPlayer() {
        //设置播放回调callBack
        mPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {
                String error = ezuiError.getErrorString();
                error = errorMessge(error);
                Log.d(TAG, error);
                mTvVideoError.setText("信息提示：" + error);
                mIbPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_play));
            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {
                //设置播放
                mPlayer.startPlay();
                mTvVideoError.setText("信息提示：播放成功");
            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {

            }
        });
    }

    //点击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPlayer.releasePlayer();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String errorMessge(String error) {
        switch (error) {
            case "UE001":
                error = "accesstoken异常或失效，需要重新获取accesstoken，并传入到sdk。";
                break;
            case "UE002":
                error = "appkey和AccessToken不匹配,建议更换appkey或者AccessToken。";
                break;
            case "UE004":
                error = "通道不存在，设备参数错误，建议重新获取播放地址。";
                break;
            case "UE005":
                error = "设备不存在，设备参数错误，建议重新获取播放地址。";
                break;
            case "UE006":
                error = "参数错误，建议重新获取播放地址。";
                break;
            case "UE007":
                error = "播放地址错误,建议重新获取播放地址。";
                break;
            case "UE101":
                error = "设备连接数过大，升级设备固件版本,海康设备可咨询客服获取升级流程。";
                break;
            case "UE102":
                error = "设备不在线，确认设备上线之后重试。";
                break;
            case "UE103":
                error = "播放失败，请求连接设备超时，检测设备网路连接是否正常。";
                break;
            case "UE104":
                error = "视频验证码错误，建议重新获取url地址增加验证码。";
                break;
            case "UE105":
                error = "视频播放失败。";
                break;
            case "UE106":
                error = "当前账号开启了终端绑定，只允许指定设备登录操作。";
                break;
            case "UE107":
                error = "设备信息异常为空，建议重新获取播放地址。";
                break;
            case "UE108":
                error = "未查找到录像文件。";
                break;
            case "UE109":
                error = "取流并发路数限制。";
                break;
            default:
                error = "未知错误。";
                break;
        }
        return error;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止播放
        mPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mPlayer.releasePlayer();
    }

}
