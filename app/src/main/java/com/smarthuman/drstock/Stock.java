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
        inputStock = inputStock.replaceAll(";", "");
        String[] leftRight = inputStock.split("=");
        System.out.println(leftRight[0] + "[=]" + leftRight[1]);
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

    public String getDate() {
        switch (marketId_) {
            case "US":
                return values[3].split(" ")[0];
            case "HK":
                return values[17];
            default:
                return values[30];
        }
    }
    public String getTime() {
        switch (marketId_) {
            case "US":
                return values[3].split(" ")[1];
            case "HK":
                return values[18];
            default:
                return values[31];
        }
    }

    public String getHigh() {
        switch (marketId_) {
            case "US":
                return values[6];
            case "HK":
                return values[4];
            default:
                return values[4];
        }
    }
    public String getLow() {
        switch (marketId_) {
            case "US":
                return values[7];
            case "HK":
                return values[5];
            default:
                return values[5];
        }
    }

    public String getClose() {
        switch (marketId_) {
            case "US":
                return values[26];
            case "HK":
                return values[3];
            default:
                return values[2];
        }
    }
    public String getOpen() {
        switch (marketId_) {
            case "US":
                return values[5];
            case "HK":
                return values[2];
            default:
                return values[1];
        }
    }

    public String getVolume() {
        switch (marketId_) {
            case "US":
                return String.format("%.0f", Float.parseFloat(values[10]) / (float)1000) + "k";
            case "HK":
                return String.format("%.0f", Float.parseFloat(values[12]) / (float)1000) + "k";
            default:
                return String.format("%.0f", Float.parseFloat(values[8]) / (float)1000) + "k";
        }
    }
    public String getTurnover() {
        switch (marketId_) {
            case "US":
                return "--";
            case "HK":
                return String.format("%.0f", Float.parseFloat(values[11]) / (float)1000) + "k";
            default:
                return String.format("%.0f", Float.parseFloat(values[9]) / (float)1000) + "k";
        }
    }

    public String getBid1() {
        switch (marketId_) {
            case "US":
//                return "--";
                return getCurrentPrice_();
            case "HK":
                return values[9];
            default:
                return values[11];
        }
    }
    public String getSell1() {
        switch (marketId_) {
            case "US":
//                return "--";
                return getCurrentPrice_();
            case "HK":
                return values[10];
            default:
                return values[21];
        }
    }

}