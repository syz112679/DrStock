package com.smarthuman.drstock;

import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/23.
 */

public class StockTop20ListBean {
    private List<UpBean> up;
    private List<DownBean> down;
    private List<VolBean> vol;
    private List<TurnOverBean> to;

    public List<UpBean> getUp() {
        return up;
    }

    public void setUp(List<UpBean> up) {
        this.up = up;
    }

    public List<DownBean> getDown() {
        return down;
    }

    public void setDown(List<DownBean> down) {
        this.down = down;
    }

    public List<VolBean> getVol() {
        return vol;
    }

    public void setVol(List<VolBean> vol) {
        this.vol = vol;
    }

    public List<TurnOverBean> getTo() {
        return to;
    }

    public void setTo(List<TurnOverBean> to) {
        this.to = to;
    }
/*
     "up" :[
        {
            "s" : "00575",
            "cn" : "åŠ±æ™¶å¤ªå¹³æ´‹",
            "en" : "REGENT PACIFIC",
            "price" : "0.285",
            "chg" : "+0.080",
            "chgP" : "+39.02",
            "vol" : "121785816",
            "to" : "34919539.89",
            "dh" : "0.325",
            "dl" : "0.230"
        }
    ]
    */
    class UpBean {
        private String s;
        private String cn;
        private String en;
        private String price;
        private String chg;
        private String chgP;
        private String vol;
        private String to;
        private String dh;
        private String dl;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChg() {
        return chg;
    }

    public void setChg(String chg) {
        this.chg = chg;
    }

    public String getChgP() {
        return chgP;
    }

    public void setChgP(String chgP) {
        this.chgP = chgP;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }
}

    class DownBean {
        private String s;
        private String cn;
        private String en;
        private String price;
        private String chg;
        private String chgP;
        private String vol;
        private String to;
        private String dh;
        private String dl;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getChg() {
            return chg;
        }

        public void setChg(String chg) {
            this.chg = chg;
        }

        public String getChgP() {
            return chgP;
        }

        public void setChgP(String chgP) {
            this.chgP = chgP;
        }

        public String getVol() {
            return vol;
        }

        public void setVol(String vol) {
            this.vol = vol;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getDh() {
            return dh;
        }

        public void setDh(String dh) {
            this.dh = dh;
        }

        public String getDl() {
            return dl;
        }

        public void setDl(String dl) {
            this.dl = dl;
        }
    }

    class VolBean {
        private String s;
        private String cn;
        private String en;
        private String price;
        private String chg;
        private String chgP;
        private String vol;
        private String to;
        private String dh;
        private String dl;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getChg() {
            return chg;
        }

        public void setChg(String chg) {
            this.chg = chg;
        }

        public String getChgP() {
            return chgP;
        }

        public void setChgP(String chgP) {
            this.chgP = chgP;
        }

        public String getVol() {
            return vol;
        }

        public void setVol(String vol) {
            this.vol = vol;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getDh() {
            return dh;
        }

        public void setDh(String dh) {
            this.dh = dh;
        }

        public String getDl() {
            return dl;
        }

        public void setDl(String dl) {
            this.dl = dl;
        }
    }

    class TurnOverBean {
        private String s;
        private String cn;
        private String en;
        private String price;
        private String chg;
        private String chgP;
        private String vol;
        private String to;
        private String dh;
        private String dl;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getChg() {
            return chg;
        }

        public void setChg(String chg) {
            this.chg = chg;
        }

        public String getChgP() {
            return chgP;
        }

        public void setChgP(String chgP) {
            this.chgP = chgP;
        }

        public String getVol() {
            return vol;
        }

        public void setVol(String vol) {
            this.vol = vol;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getDh() {
            return dh;
        }

        public void setDh(String dh) {
            this.dh = dh;
        }

        public String getDl() {
            return dl;
        }

        public void setDl(String dl) {
            this.dl = dl;
        }
    }
}
