package com.smarthuman.drstock;

import android.annotation.SuppressLint;

/**
 * Created by yuxuangu on 2018/4/8.
 */

public class Stock {
    public String marketId_;        // HK
    public String id_;              // 02318
    public String name_;            // PING AN



    public int size_ = 33;
    public String[] values = new String[size_];

    public String getId_Market() {
        return id_ + "." + marketId_;
    }

    public String getEnqueryId() {
        if (marketId_.equals("US"))
            return "gb_" + id_;
        else
            return marketId_.toLowerCase() + id_;
    }

    public String getCurrentPrice_() {
        switch (marketId_) {
            case "US":
                return values[1];
            case "HK":
                return values[6];
            default:
                return values[3];
        }
    }

    public String getPriceChange() {
        switch (marketId_) {
            case "US":
                return values[4];
            case "HK":
                return values[7];
            default:
                return Float.toString(Float.parseFloat(values[3]) - Float.parseFloat(values[2]));
        }
    }

    @SuppressLint("DefaultLocale")
    public String getChangePercent() {
        switch (marketId_) {
            case "US":
                return values[2];
            case "HK":
                return values[8];
            default:
                float percent = (Float.parseFloat(values[3]) - Float.parseFloat(values[2]))
                        / Float.parseFloat(values[2]) * 100;
                return String.format("%.2f", percent);
        }
    }

    public boolean isRising() {
        return getPriceChange().charAt(0) != '-';
    }

    public String getCurrency() {
        switch (marketId_) {
            case "US":
                return "USD";
            case "HK":
                return "HKD";
            default:
                return "CNY";
        }
    }

}