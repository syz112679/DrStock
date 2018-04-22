package com.smarthuman.drstock;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by jingle on 21/04/2018.
 */

public class ExampleApplication extends Application {

    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate() {
        Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());     		// 初始化 JPush
    }
}