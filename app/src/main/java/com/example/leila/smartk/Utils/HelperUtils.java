package com.example.leila.smartk.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Leila on 2017/11/19.
 */

public class HelperUtils {
    public void sendmakeText(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
