package com.sogou.xshuo.btvirtualcomm;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.sogou.xshuo.btvirtualcomm.common.activities.ActivityBase;

public class MainActivity extends ActivityBase {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "---onCreate()---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "---onStart()---");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "---onResume()---");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "---onCreate()---");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
         Log.d(TAG, "---onStart()---");
         super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "---onStop()---");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "---onDestroy()---");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "---onConfigurationChanged()---");
        super.onConfigurationChanged(newConfig);
    }
}
