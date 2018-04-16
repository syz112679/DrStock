package com.smarthuman.drstock;

/**
 * Created by shiyuzhou on 8/4/2018.
 */

public class StockSnippet {
    private String id;
    private double boughtPrice;
    private double amount;
    private double currentPrice;

    private Stock stock;

    public StockSnippet(String id, double boughtPrice, double amount) {
        this.id = id;
        this.boughtPrice = boughtPrice;
        this.amount = amount;
        stock = MainActivity.stockMap_.get(id);
    }

    public StockSnippet() {
        this.id = "00000";
        this.boughtPrice = 0.0;
        this.amount = 0.0;
        stock = null;
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
        stock = MainActivity.stockMap_.get(id);
        return Double.parseDouble(stock.getCurrentPrice_());
    }

    // TODO: get Stock Name
    public String getName() {
        stock = MainActivity.stockMap_.get(id);
        return stock.name_;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
