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
    private TableRow selectedMobileRow, newMobileRow;
    private TableRow selectedWifiRow, newWifiRow;

    private final int index_imageView = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshsetting);

        setTitle(R.string.refreshActivity);
        showBackward(getDrawable(R.drawable.ic_return), true);

        initialize();
    }

    void initialize() {
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

        if (MainActivity.enableMobileRefresh) {
            switch (MainActivity.mobileRefreshTime) {
                case 5:
                    selectedMobileRow = (TableRow) tableLayout_mobile.getChildAt(2);
                    break;
                case 15:
                    selectedMobileRow = (TableRow) tableLayout_mobile.getChildAt(3);
                    break;
                case 30:
                    selectedMobileRow = (TableRow) tableLayout_mobile.getChildAt(4);
                    break;
                case 60:
                    selectedMobileRow = (TableRow) tableLayout_mobile.getChildAt(5);
                    break;
            }
        } else {
            selectedMobileRow = (TableRow) tableLayout_mobile.getChildAt(1);
        }
        selectedMobileRow.getChildAt(index_imageView).setVisibility(View.VISIBLE);

        if (MainActivity.enableWifiRefresh) {
            switch (MainActivity.wifiRefreshTime) {
                case 5:
                    selectedWifiRow = (TableRow) tableLayout_wifi.getChildAt(2);
                    break;
            }
        } else {
            selectedWifiRow = (TableRow) tableLayout_wifi.getChildAt(1);
        }
        selectedWifiRow.getChildAt(index_imageView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.mobile_noRefresh:
                MainActivity.enableMobileRefresh = false;
                newMobileRow = (TableRow) tableLayout_mobile.getChildAt(1);
//                Toast.makeText(this, "enableMobileRefresh = false", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_5s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 5;
                newMobileRow = (TableRow) tableLayout_mobile.getChildAt(2);
//                Toast.makeText(this, "mobileRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_15s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 15;
                newMobileRow = (TableRow) tableLayout_mobile.getChildAt(3);
//                Toast.makeText(this, "mobileRefreshTime = 15", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_30s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 30;
                newMobileRow = (TableRow) tableLayout_mobile.getChildAt(4);
//                Toast.makeText(this, "mobileRefreshTime = 30", Toast.LENGTH_LONG).show();
                break;
            case R.id.mobile_60s:
                MainActivity.enableMobileRefresh = true;
                MainActivity.mobileRefreshTime = 60;
                newMobileRow = (TableRow) tableLayout_mobile.getChildAt(5);
//                Toast.makeText(this, "mobileRefreshTime = 60", Toast.LENGTH_LONG).show();
                break;
            case R.id.wifi_noRefresh:
                MainActivity.enableWifiRefresh = false;
                newWifiRow = (TableRow) tableLayout_wifi.getChildAt(1);
//                Toast.makeText(this, "enableWifiRefresh = false", Toast.LENGTH_LONG).show();
                break;
            case R.id.wifi_5s:
                MainActivity.enableWifiRefresh = true;
                MainActivity.wifiRefreshTime = 5;
                newWifiRow = (TableRow) tableLayout_wifi.getChildAt(2);
//                Toast.makeText(this, "wifiRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
            case R.id.wifi_15s:
                MainActivity.enableWifiRefresh = true;
                MainActivity.wifiRefreshTime = 15;
                newWifiRow = (TableRow) tableLayout_wifi.getChildAt(3);
//                Toast.makeText(this, "wifiRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
            case R.id.wifi_30s:
                MainActivity.enableWifiRefresh = true;
                MainActivity.wifiRefreshTime = 30;
                newWifiRow = (TableRow) tableLayout_wifi.getChildAt(4);
//                Toast.makeText(this, "wifiRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
            case R.id.wifi_60s:
                MainActivity.enableWifiRefresh = true;
                MainActivity.wifiRefreshTime = 60;
                newWifiRow = (TableRow) tableLayout_wifi.getChildAt(5);
//                Toast.makeText(this, "wifiRefreshTime = 5", Toast.LENGTH_LONG).show();
                break;
        }

        if (newMobileRow != null) {
            if (newMobileRow != selectedMobileRow) {
                selectedMobileRow.getChildAt(index_imageView).setVisibility(View.INVISIBLE);
                newMobileRow.getChildAt(index_imageView).setVisibility(View.VISIBLE);
                selectedMobileRow = newMobileRow;
            }
            newMobileRow = null;
        }

        if (newWifiRow != null) {
            if (newWifiRow != selectedWifiRow) {
                selectedWifiRow.getChildAt(index_imageView).setVisibility(View.INVISIBLE);
                newWifiRow.getChildAt(index_imageView).setVisibility(View.VISIBLE);
                selectedWifiRow = newWifiRow;
            }
            newWifiRow = null;
        }

    }
}
