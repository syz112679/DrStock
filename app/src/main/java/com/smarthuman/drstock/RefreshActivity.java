package com.smarthuman.drstock;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.TreeMap;

/**
 * Created by yuxuangu on 2018/4/14.
 */

public class RefreshActivity extends TitleActivity {

    private TableLayout tableLayout_mobile, tableLayout_wifi;
//    private TableRow[] tableRows_mobile = new TableRow[3];
//    private TableRow[] tableRows_wifi = new TableRow[];
//    private

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshsetting);

        setTitle(R.string.refreshActivity);
        showBackwardView(R.string.text_back, true);

        tableLayout_mobile = findViewById(R.id.TableLayout_mobile);
        tableLayout_wifi = findViewById(R.id.TableLayout_wifi);
        for (int i = 1; i < tableLayout_mobile.getChildCount(); i++) {
//            tableRows_mobile[i-1] = (TableRow) tableLayout_mobile.getChildAt(i);
//            tableRows_mobile[i-1].setOnClickListener(this);
            tableLayout_mobile.getChildAt(i).setOnClickListener(this);
        }
        for (int i = 1; i < tableLayout_wifi.getChildCount(); i++) {
//            tableRows_wifi[i-1] = (TableRow) tableLayout_wifi.getChildAt(i);
//            tableRows_wifi[i-1].setOnClickListener(this);
            tableLayout_wifi.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.mobile_noRefresh:
                MainActivity.enableMobileRefresh = false;
                Toast.makeText(this, "enableMobileRefresh = false", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_5s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 5;
                Toast.makeText(this, "mobileRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_15s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 15;
                Toast.makeText(this, "mobileRefreshTime = 15", Toast.LENGTH_LONG).show();

                break;
            case R.id.mobile_30s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 30;
                Toast.makeText(this, "mobileRefreshTime = 30", Toast.LENGTH_LONG).show();

                break;
            case R.id.mobile_60s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 60;
                Toast.makeText(this, "mobileRefreshTime = 60", Toast.LENGTH_LONG).show();

                break;
            case R.id.wifi_noRefresh:
                MainActivity.enableWifiRefresh = false;
                Toast.makeText(this, "enableWifiRefresh = false", Toast.LENGTH_LONG).show();

                break;
            case R.id.wifi_5s:
                MainActivity.enableWifiRefresh = true;
                MainActivity.wifiRefreshTime = 5;
                Toast.makeText(this, "wifiRefreshTime = 5", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
