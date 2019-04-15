package com.example.sweeper.basetestdemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    private String TAG = "zwj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " + getClass().getSimpleName() + " TaskId: " + getTaskId() + " haseCode: " + hashCode());
        dumpTaskAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " + getClass().getSimpleName() + "  hashCode: " + hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:" + getClass().getSimpleName() + "  hashCode: " + hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " + getClass().getSimpleName() + "  hashCode: " + hashCode());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop :" + getClass().getSimpleName() + "  hashCode: " + hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy : " + getClass().getSimpleName() + "hashCode: " + hashCode());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent: " + getClass().getSimpleName() + " TaskId: " + getTaskId() + " haseCode: " + hashCode());
        dumpTaskAffinity();
    }

    protected void dumpTaskAffinity() {
        try {
            ActivityInfo info = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Log.i(TAG, "taskAffinity:" + info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
