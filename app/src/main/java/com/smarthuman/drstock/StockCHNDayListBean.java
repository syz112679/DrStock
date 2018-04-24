package com.smarthuman.drstock;

import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/22.
 */

public class StockCHNDayListBean {
    /*
    {
        "errorNo":0,
        "errorMsg":"SUCCESS",
        "timeLine":
        [
            {
                "date":20180420,
                "time":91500000,
                "price":6853.0048828125,
                "volume":0,
                "avgPrice":6851.74609375,
                "ccl":0,
                "netChangeRatio":0,
                "preClose":6851.74609375,
                "amount":0
            },
        ],
        "latestTimelineStamp":"20180420150000000",
        "preClose":6851.74609375,
        "version":1,
        "exchangeStatus":5
    }

    */
    private int errorNo;
    private String errorMsg;
    private List<StockBean> timeLine;
    private String latestTimelineStamp;
    private Float preClose;
    private int version;
    private int exchangeStatus;

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<StockBean> getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(List<StockBean> timeLine) {
        this.timeLine = timeLine;
    }

    public String getLatestTimelineStamp() {
        return latestTimelineStamp;
    }

    public void setLatestTimelineStamp(String latestTimelineStamp) {
        this.latestTimelineStamp = latestTimelineStamp;
    }

    public Float getPreClose() {
        return preClose;
    }

    public void setPreClose(Float preClose) {
        this.preClose = preClose;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(int exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    class StockBean {

        /**
         "date":20180420,
         "time":91500000,
         "price":6853.0048828125,
         "volume":0,
         "avgPrice":6851.74609375,
         "ccl":0,
         "netChangeRatio":0,
         "preClose":6851.74609375,
         "amount":0
         */

        private String date;
        private String time;
        private float price;
        private String volume;
        private float avgPrice;
        private int ccl;
        private float netChangeRatio;
        private float preClose;
        private float amount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public float getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(float avgPrice) {
            this.avgPrice = avgPrice;
        }

        public int getCcl() {
            return ccl;
        }

        public void setCcl(int ccl) {
            this.ccl = ccl;
        }

        public float getNetChangeRatio() {
            return netChangeRatio;
        }

        public void setNetChangeRatio(float netChangeRatio) {
            this.netChangeRatio = netChangeRatio;
        }

        public float getPreClose() {
            return preClose;
        }

        public void setPreClose(float preClose) {
            this.preClose = preClose;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }
}
