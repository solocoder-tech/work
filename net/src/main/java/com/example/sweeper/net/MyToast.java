package com.example.sweeper.net;

import android.content.Context;
import android.widget.Toast;

/**
 * 创建时间：2019/1/24  16:15
 * 作者：5#
 * 描述：TODO
 */
public class MyToast {
    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
