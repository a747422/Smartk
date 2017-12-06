package com.example.leila.smartk.Acitvity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.leila.smartk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Leila
 * @version 1.0
 */

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_abouts);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_abouts_cancel)
    public void onClickAbouts() {
        finish();
    }
}
