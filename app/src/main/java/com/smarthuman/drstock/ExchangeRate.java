package com.smarthuman.drstock;

import java.util.TreeMap;

/**
 * Created by yuxuangu on 2018/4/17.
 */

public class ExchangeRate {

    public static final int currencyCount = 4;
    public static String[][] exchangeRate_ = new String[currencyCount][currencyCount];
    public static final String enquiryId = "h_RMBUSD,h_RMBGBP,h_RMBHKD,";

    public static final int USD = 0;
    public static final int GBP = 1;
    public static final int HKD = 2;
    public static final int RMB = 3;
    public static TreeMap<String, Integer> str2int;

    public static TreeMap<Integer, Currency> currencyTreeMap = new TreeMap<>();

//    public ExchangeRate() {
//        str2int.put("USD", USD);
//        str2int.put("GBP", GBP);
//        str2int.put("HKD", HKD);
//    }

    public void updateExchangeRate(String response) {
        String[] stocks = response.split(";");
        for (int i = 0; i < 3; i++) {
            String input = stocks[i];
            input = input.replaceAll(";", "");

            String[] LR = input.split("=");
            if (LR.length < 2)
                return;

            String left = LR[0];
            String[] lefts = left.split("_");
            int key = i; //

            String right = LR[1].replaceAll("\"", "");
            String[] rights = right.split(",");
            Currency value = new Currency(rights[0], lefts[3].substring(3), rights[1], rights[6], rights[7]);

            currencyTreeMap.put(key, value);

        }

        exchangeRate_[RMB][USD] = currencyTreeMap.get(0).value;
        exchangeRate_[RMB][GBP] = currencyTreeMap.get(1).value;
        exchangeRate_[RMB][HKD] = currencyTreeMap.get(2).value;
        exchangeRate_[USD][RMB] = String.format("%.3f", 10000 / Float.parseFloat(currencyTreeMap.get(USD).value));
        exchangeRate_[GBP][RMB] = String.format("%.3f", 10000 / Float.parseFloat(currencyTreeMap.get(GBP).value));
        exchangeRate_[HKD][RMB] = String.format("%.3f", 10000 / Float.parseFloat(currencyTreeMap.get(HKD).value));

        exchangeRate_[HKD][USD] = String.format("%.3f", 100 * Float.parseFloat(exchangeRate_[HKD][RMB]) / Float.parseFloat(exchangeRate_[USD][RMB]));
        exchangeRate_[HKD][GBP] = String.format("%.3f", 100 * Float.parseFloat(exchangeRate_[HKD][RMB]) / Float.parseFloat(exchangeRate_[GBP][RMB]));

        exchangeRate_[USD][HKD] = String.format("%.3f", 10000 / Float.parseFloat(exchangeRate_[HKD][USD]));
        exchangeRate_[GBP][HKD] = String.format("%.3f", 10000 / Float.parseFloat(exchangeRate_[HKD][GBP]));

        exchangeRate_[USD][GBP] = String.format("%.3f", 100 * Float.parseFloat(exchangeRate_[USD][RMB]) / Float.parseFloat(exchangeRate_[GBP][RMB]));
        exchangeRate_[GBP][USD] = String.format("%.3f", 100 * Float.parseFloat(exchangeRate_[GBP][RMB]) / Float.parseFloat(exchangeRate_[USD][RMB]));

        for (int i = 0; i < currencyCount; i++) {
            exchangeRate_[i][i] = "100.000";
        }

        for (int i = 0; i < currencyCount; i++) {
            for (int j = 0; j < currencyCount; j++) {
                System.out.println("exchange: " + i + " " + j + ": " + exchangeRate_[i][j]);
            }
        }
    }
    public String[][] getExchangeRate_() {
        return exchangeRate_;
    }

    public class Currency {
        public Currency(String name_chi, String name_eng, String value, String date, String time) {
            this.name_chi = name_chi;
            this.name_eng = name_eng;
            this.value = value;
            this.date = date;
            this.time = time;
        }
        String name_chi;
        String name_eng;
        String value;
        String date;
        String time;
    }
}
