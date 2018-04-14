package com.smarthuman.drstock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by yuxuangu on 2018/4/14.
 */

public class SettingActivity extends TitleActivity{

    private int itemNum = 4;
    private Switch risingColour;
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
        showBackwardView(R.string.text_back, true);

        settingTable = findViewById(R.id.Table_setting);
        risingColour = findViewById(R.id.switch_risingColour);
        risingColour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    MainActivity.UpColor_ = R.color.green;
                    MainActivity.DownColor_ = R.color.red;
                } else {
                    MainActivity.UpColor_ = R.color.red;
                    MainActivity.DownColor_ = R.color.green;
                }
            }
        });

        for (int i = 0; i < itemNum; i++) {
            settings[i] = (TableRow) settingTable.getChildAt(i);
            settings[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.tableRow_refresh:
                startActivity(new Intent(this, RefreshActivity.class));
                break;
            case R.id.tableRow_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.tableRow_credits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;
        }
    }

}
