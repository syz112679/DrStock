package com.smarthuman.drstock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.smarthuman.drstock.MainActivity.KEY_EXTRAS;
import static com.smarthuman.drstock.MainActivity.KEY_MESSAGE;



/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainActivity.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            String messge = intent.getStringExtra(KEY_MESSAGE);
            String extras = intent.getStringExtra(KEY_EXTRAS);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
            if (!ExampleUtil.isEmpty(extras)) {
                showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
            }

            // Toast.makeText(this, "0000"+showMsg.toString(),Toast.LENGTH_LONG).show();
        }
    }
}
