package com.smarthuman.drstock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.finddreams.languagelib.OnChangeLanguageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by yuxuangu on 2018/4/14.
 */

public class SettingActivity extends TitleActivity{

    private int itemNum = 5;
    private Switch risingColour;
    private TextView risingDisplay;
    private boolean isEnable = true;
    private TableLayout settingTable;
    private TableRow[] settings = new TableRow[itemNum];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // START: update the data of TextViews [Samuel_GU]
//        setTitle("Each Stock");
//        setTitleBackground(R.color.titleBarDemo);
//        setTitleBackground(MainActivity.UpColor_);

        setTitle(R.string.setting);
        showBackward(getDrawable(R.drawable.ic_return), true);

        settingTable = findViewById(R.id.Table_setting);

        risingDisplay = findViewById(R.id.textView_risingColour);
        risingColour = findViewById(R.id.switch_risingColour);

        if (MainActivity.UpColor_ == MainActivity.Green) {
            risingDisplay.setText(getString(R.string.rise_green));
            risingColour.setChecked(true);
        } else {
            risingDisplay.setText(getString(R.string.rise_red));
            risingColour.setChecked(false);
        }

        risingColour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    risingDisplay.setText(getString(R.string.rise_green));
                    MainActivity.UpColor_ = MainActivity.Green;
                    MainActivity.DownColor_ = MainActivity.Red;
                } else {
                    risingDisplay.setText(getString(R.string.rise_red));
                    MainActivity.UpColor_ = MainActivity.Red;
                    MainActivity.DownColor_ = MainActivity.Green;
                }
            }
        });

        for (int i = 0; i < itemNum; i++) {
            settings[i] = (TableRow) settingTable.getChildAt(i);
            settings[i].setOnClickListener(this);
        }

        //setContentView(R.layout.activity_main);
        //setTitle(R.string.app_name);
        EventBus.getDefault().register(this);


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.tableRow_refresh:
                startActivity(new Intent(this, RefreshActivity.class));
                break;
            case R.id.tableRow_language:
//                startActivity(new Intent(this, LoadLanguage.class));
                startActivity(new Intent(this, LanguageActivity.class));
                break;
            case R.id.tableRow_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.tableRow_credits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeLanguageEvent(OnChangeLanguageEvent event){
        Log.d("onchange","ChangeLanguage");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
