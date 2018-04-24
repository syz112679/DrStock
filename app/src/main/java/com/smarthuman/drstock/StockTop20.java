package com.smarthuman.drstock;

/**
 * Created by Li Shuhan on 2018/4/23.
 */

public class StockTop20 {
    private String id;
    private String name;
    private String changePerc;
    private String vol;
    private String currentPrice;

    public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){
        this.id = id_;
        this.name = name_;
        this.changePerc = changePerc_;
        this.vol = vol_;
        this.currentPrice = currentPrice_;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChangePerc() {
        return changePerc;
    }

    public void setChangePerc(String changePerc) {
        this.changePerc = changePerc;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }
}
