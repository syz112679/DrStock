package com.smarthuman.drstock;

import java.util.TreeMap;

/**
 * Created by yuxuangu on 2018/4/16.
 */

public class StockIndex {

    public static final int totalNum = 13;
    public static TreeMap<String, Index> indexTreeMap;
    public static TreeMap<String, String> chi2eng = new TreeMap<>();
    public static final String enquiryId = "s_sh000001,s_sz399001,s_sz399300,s_sz399006,int_hangseng,int_dji,int_nasdaq,int_sp500,int_ftse,s_sz399005,int_nikkei,b_TWSE,b_FSSTI,";
    public static final String[] enquiryIds = {"", "s_sh000001","s_sz399001","s_sz399300","s_sz399006",
            "int_hangseng","int_dji","int_nasdaq","int_sp500","int_ftse","s_sz399005","int_nikkei",
            "b_TWSE","b_FSSTI"};
    private static final String[] name_engs = {"Shanghai Composite Index", "Shenzhen Component Index", ""};

    public StockIndex() {
        chi2eng.put("上证指数", "Shanghai Composite Index");
        chi2eng.put("深证成指", "Shenzhen Component Index");
        chi2eng.put("沪深300", "CSI 300");
        chi2eng.put("创业板指", "GEI");
        chi2eng.put("恒生指数", "HSI");
        chi2eng.put("道琼斯", "DJIA");
        chi2eng.put("纳斯达克", "NASDAQ");
        chi2eng.put("标普指数", "S&P 500");
        chi2eng.put("伦敦指数", "London Index");
        chi2eng.put("中小板指", "SSE SME COMPOSITE");
        chi2eng.put("日经指数", "N255");
        chi2eng.put("台湾台北指数", "TWII");
        chi2eng.put("富时新加坡海峡时报指数", "STI");
    }

    public void updateIdex(String inputStock) {
//        inputStock = inputStock.replaceAll("\n", "");
        String[] stocks = inputStock.split(";");
        indexTreeMap = new TreeMap<>();

        for (String input : stocks) {
            input = input.replaceAll(";", "");

            String[] LR = input.split("=");
            if (LR.length < 2)
                return;

            String left = LR[0];
            String[] lefts = left.split("_");
            String key = lefts[2] + "_" + lefts[3];

            String right = LR[1].replaceAll("\"", "");
            String[] rights = right.split(",");
            Index value = new Index(rights[0], chi2eng.get(rights[0]), rights[1], rights[2], rights[3]);

            indexTreeMap.put(key, value);
        }
        inputStock = inputStock.replaceAll(";", "");
    }

    public class Index {
        public Index(String name_chi, String name_eng, String value, String change, String percent) {
            this.name_chi = name_chi;
            this.name_eng = name_eng;
            this.value = value;
            this.change = change;
            this.percent = percent;
            isRising = percent.charAt(0) != '-';
        }

        String name_chi;
        String name_eng;
        String value;
        String change;
        String percent;
        boolean isRising;
    }

}
