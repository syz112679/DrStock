package com.smarthuman.drstock;

import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/22.
 */

public class StockCHNKLineBean {
    /*
{
    "errorNo":0,
    "errorMsg":"SUCCESS",
    "mashData":
    [

    ],
    "latestTimelineStamp":"20180420150000000",
    "preClose":6851.74609375,
    "version":1,
    "exchangeStatus":5
}

*/
    private int errorNo;
    private String errorMsg;
    private List<StockBean> mashData;

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

    public List<StockBean> getMashData() {
        return mashData;
    }

    public void setMashData(List<StockBean> mashData) {
        this.mashData = mashData;
    }

    class StockBean {

        /**
         * {
         * "date":20170822,
         * "kline":
         * {
         * "open":6.3200001716614,
         * "high":6.3299999237061,
         * "low":6.2699999809265,
         * "close":6.3000001907349,
         * "volume":7476332,
         * "amount":47076573,
         * "ccl":0,
         * "preClose":6.3200001716614,
         * "netChangeRatio":-0.31645537819713
         * },
         * "ma5":
         * {
         * "volume":9893153,
         * "avgPrice":6.2779998779297,
         * "ccl":null
         * },
         * "ma10":
         * {
         * "volume":10042480,
         * "avgPrice":6.2550001144409,
         * "ccl":null
         * },
         * "ma20":
         * {
         * "volume":13496458,
         * "avgPrice":6.3410000801086,
         * "ccl":null
         * },
         * "macd":
         * {
         * "diff":-0.067330458717762,
         * "dea":-0.07671969881693,
         * "macd":0.018778480198336
         * },
         * "kdj":
         * {
         * "k":58.337849150933,
         * "d":44.42347930579,
         * "j":86.16658884122
         * },
         * "rsi":
         * {
         * "rsi1":51.217570815279,
         * "rsi2":46.257649091829,
         * "rsi3":46.557681019784
         * }
         * }
         */

        private String date;
        private KLine kline;
        private Ma5 ma5;
        private Ma10 ma10;
        private Ma20 ma20;
        private Macd macd;
        private Kdj kdj;
        private Rsi rsi;


        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public KLine getKline() {
            return kline;
        }

        public void setKline(KLine kline) {
            this.kline = kline;
        }

        public Ma5 getMa5() {
            return ma5;
        }

        public void setMa5(Ma5 ma5) {
            this.ma5 = ma5;
        }

        public Ma10 getMa10() {
            return ma10;
        }

        public void setMa10(Ma10 ma10) {
            this.ma10 = ma10;
        }

        public Ma20 getMa20() {
            return ma20;
        }

        public void setMa20(Ma20 ma20) {
            this.ma20 = ma20;
        }

        public Macd getMacd() {
            return macd;
        }

        public void setMacd(Macd macd) {
            this.macd = macd;
        }

        public Kdj getKdj() {
            return kdj;
        }

        public void setKdj(Kdj kdj) {
            this.kdj = kdj;
        }

        public Rsi getRsi() {
            return rsi;
        }

        public void setRsi(Rsi rsi) {
            this.rsi = rsi;
        }

        /*
        private List<KLine> kline;
        "kline":
             {
                 "open":6.3200001716614,
                 "high":6.3299999237061,
                 "low":6.2699999809265,
                 "close":6.3000001907349,
                 "volume":7476332,
                 "amount":47076573,
                 "ccl":0,
                 "preClose":6.3200001716614,
                 "netChangeRatio":-0.31645537819713
             },
    */

        class KLine {
            private float open;
            private float high;
            private float low;
            private float close;
            private String volume;
            private String amount;
            private String ccl;
            private float preClose;
            private float netChangeRatio;

            public float getOpen() {
                return open;
            }

            public void setOpen(float open) {
                this.open = open;
            }

            public float getHigh() {
                return high;
            }

            public void setHigh(float high) {
                this.high = high;
            }

            public float getLow() {
                return low;
            }

            public void setLow(float low) {
                this.low = low;
            }

            public float getClose() {
                return close;
            }

            public void setClose(float close) {
                this.close = close;
            }

            public String getVolume() {
                return volume;
            }

            public void setVolume(String volume) {
                this.volume = volume;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getCcl() {
                return ccl;
            }

            public void setCcl(String ccl) {
                this.ccl = ccl;
            }

            public float getPreClose() {
                return preClose;
            }

            public void setPreClose(float preClose) {
                this.preClose = preClose;
            }

            public float getNetChangeRatio() {
                return netChangeRatio;
            }

            public void setNetChangeRatio(float netChangeRatio) {
                this.netChangeRatio = netChangeRatio;
            }
        }

        /*
            private List<Ma5> ma5;
                         "ma5":
                 {
                     "volume":9893153,
                     "avgPrice":6.2779998779297,
                     "ccl":null
                 },
    */
        class Ma5 {
            private String volume;
            private float avgPrice;
            private String ccl;

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

            public String getCcl() {
                return ccl;
            }

            public void setCcl(String ccl) {
                this.ccl = ccl;
            }
        }

        /*        private List<Ma10> ma10;
                             "ma10":
                     {
                         "volume":10042480,
                         "avgPrice":6.2550001144409,
                         "ccl":null
                     },
        */
        class Ma10 {
            private String volume;
            private float avgPrice;
            private String ccl;

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

            public String getCcl() {
                return ccl;
            }

            public void setCcl(String ccl) {
                this.ccl = ccl;
            }
        }

        /*      private List<Ma20> ma20;
                           "ma20":
                   {
                       "volume":13496458,
                       "avgPrice":6.3410000801086,
                       "ccl":null
                   },
      */
        class Ma20 {
            private String volume;
            private float avgPrice;
            private String ccl;

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

            public String getCcl() {
                return ccl;
            }

            public void setCcl(String ccl) {
                this.ccl = ccl;
            }
        }

        /*      private List<Macd> macd;
                         "macd":
                 {
                     "diff":-0.067330458717762,
                     "dea":-0.07671969881693,
                     "macd":0.018778480198336
                 },
     */
        class Macd {
            private float diff;
            private float dea;
            private float macd;

            public float getDiff() {
                return diff;
            }

            public void setDiff(float diff) {
                this.diff = diff;
            }

            public float getDea() {
                return dea;
            }

            public void setDea(float dea) {
                this.dea = dea;
            }

            public float getMacd() {
                return macd;
            }

            public void setMacd(float macd) {
                this.macd = macd;
            }
        }

        /*       private List<Kdj> kdj;
                            "kdj":
                    {
                        "k":58.337849150933,
                        "d":44.42347930579,
                        "j":86.16658884122
                    },
        */
        class Kdj {
            private float k;
            private float d;
            private float j;

            public float getK() {
                return k;
            }

            public void setK(float k) {
                this.k = k;
            }

            public float getD() {
                return d;
            }

            public void setD(float d) {
                this.d = d;
            }

            public float getJ() {
                return j;
            }

            public void setJ(float j) {
                this.j = j;
            }
        }

        /*       private List<Rsi> rsi;
                           "rsi":
                    {
                        "rsi1":51.217570815279,
                        "rsi2":46.257649091829,
                        "rsi3":46.557681019784
                    }
            */
        class Rsi {
            private float rsi1;
            private float rsi2;
            private float rsi3;

            public float getRsi1() {
                return rsi1;
            }

            public void setRsi1(float rsi1) {
                this.rsi1 = rsi1;
            }

            public float getRsi2() {
                return rsi2;
            }

            public void setRsi2(float rsi2) {
                this.rsi2 = rsi2;
            }

            public float getRsi3() {
                return rsi3;
            }

            public void setRsi3(float rsi3) {
                this.rsi3 = rsi3;
            }
        }
    }
}
