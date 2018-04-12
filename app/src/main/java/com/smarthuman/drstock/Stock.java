package com.smarthuman.drstock;

import android.annotation.SuppressLint;

/**
 * Created by yuxuangu on 2018/4/8.
 */

public class Stock {
    public String marketId_;        // HK
    public String id_;              // 02318
    public String name_;            // PING AN
    private boolean isRising_ = true;
//    private String changePercent_ =


    public int size_ = 33;
    public String[] values = new String[size_];

    public Stock(String inputStock) {
        String[] leftRight = inputStock.split("=");
        if (leftRight.length < 2)
            return;

        String left = leftRight[0];
        if (left.isEmpty())
            return;
        String[] lefts = left.split("_");
        String market = lefts[2];
        if (market.length() == 2) { // US
            marketId_ = "US";
            size_ = 28;
        } else if (market.substring(0, 2).equals("hk")) {    // HK
            marketId_ = market.substring(0, 2).toUpperCase();
            size_ = 19;
        } else {
//                marketId_ = market.substring(0, 2);
            marketId_ = market.substring(0, 2).toUpperCase();
            size_ = 33;
        }


        String right = leftRight[1].replaceAll("\"", "");
        if (right.isEmpty())
            return;


        String[] values = right.split(",");
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i];
        }

        if (marketId_.equals("US")) {
            id_ = lefts[3];
            name_ = lefts[3];

        } else if (marketId_.equals("HK")) {
            id_ = market.substring(2);
            name_ = values[0];

        } else { // ZH & SH
            id_ = market.substring(2);
            name_ = values[0];

        }
    }

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
        System.out.println("----matketId_:\n" + marketId_ + "\n----");
        switch (marketId_) {
            case "US":
                return values[4];
            case "HK":
                return values[7];
            default:
                return Float.toString(Float.parseFloat(values[3]) - Float.parseFloat(values[2]));
        }
    }

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
//        System.out.println(getPriceChange());
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