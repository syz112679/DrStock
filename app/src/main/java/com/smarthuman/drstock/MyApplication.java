package com.smarthuman.drstock;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import com.finddreams.languagelib.MultiLanguageUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by liujunqi on 22/4/18.
 */

public class MyApplication extends Application {

    private static final String TAG = "JIGUANG-Example";
    @Override
    public void onCreate() {
        super.onCreate();
        //Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());     		// 初始化 JPush


        MultiLanguageUtil.init(this);
//        MultiLanguageUtil.getInstance().setConfiguration();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
//                MultiLanguageUtil.getInstance().setConfiguration();
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        MultiLanguageUtil.getInstance().setConfiguration();
    }


}
