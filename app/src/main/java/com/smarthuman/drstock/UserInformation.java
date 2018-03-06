package com.smarthuman.drstock;

import java.util.ArrayList;


/**
 * Created by shiyuzhou on 4/3/2018.
 */

public class UserInformation {
    private String userName;
    private String email;
    private double initialMoney;
    private double earning;
    private double balance;
    ArrayList<String> myStocks;
    ArrayList<String> favorites;

    private boolean isSuperUser;

    public UserInformation(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.initialMoney = 0;
        this.balance = 0;
        this.earning = 0;
        this.myStocks = new ArrayList<String>();
        this.favorites = new ArrayList<String>();
        favorites.add("placeholder");
        myStocks.add("placeholder");
        isSuperUser = false;
    }

    public void addStock(String stock) {
        myStocks.add(stock);
    }

    public void addWishlist(String stock) {
        favorites.add(stock);
    }

    //setter
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMoney(double money) {
        this.initialMoney = money;
    }

    public void setMyStocks(ArrayList<String> myStocks) {
        this.myStocks = myStocks;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public void setSuperUser(boolean superUser) {  this.isSuperUser = superUser; }

    public void setEarning(double earning) {  this.earning = earning; }

    public void setBalance(double balance) {   this.balance = balance;  }

    //getter

    public double getEarning() {   return earning; }

    public double getBalance() {   return balance; }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public double getMoney() {
        return initialMoney;
    }

    public ArrayList<String> getMyStocks() {
        return myStocks;
    }

    public boolean getIsSuperUser() { return isSuperUser;  }
}
