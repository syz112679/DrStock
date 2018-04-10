package com.smarthuman.drstock;

/**
 * Created by shiyuzhou on 8/4/2018.
 */

public class StockSnippet {
    private String id;
    private double boughtPrice;
    private double amount;
    private double currentPrice;

    public StockSnippet(String id, double boughtPrice, double amount) {
        this.id = id;
        this.boughtPrice = boughtPrice;
        this.amount = amount;
    }

    public StockSnippet() {
        this.id = "00000";
        this.boughtPrice = 0.0;
        this.amount = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // TODO: get current Price
    public double getCurrentPrice() {
        return 0.0;
    }

    // TODO: get Stock Name
    public String getName() {
        return "Stock Name";
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
