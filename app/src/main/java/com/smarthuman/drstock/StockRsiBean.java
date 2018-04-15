package com.smarthuman.drstock;

import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/14.
 */

public class StockRsiBean {

    private Rsi_10 rsi10;
    private Rsi_14 rsi14;
    private Rsi_20 rsi20;

    class Rsi_10 {
        public List<Float> getValues() {
            return values;
        }

        public void setValues(List<Float> values) {
            this.values = values;
        }

        private List<Float> values;
    }

    class Rsi_14 {
        public List<Float> getValues() {
            return values;
        }

        public void setValues(List<Float> values) {
            this.values = values;
        }

        private List<Float> values;
    }

    class Rsi_20 {
        public List<Float> getValues() {
            return values;
        }

        public void setValues(List<Float> values) {
            this.values = values;
        }

        private List<Float> values;
    }

    public Rsi_10 getRsi10() {
        return rsi10;
    }

    public void setRsi10(Rsi_10 rsi10) {
        this.rsi10 = rsi10;
    }

    public Rsi_14 getRsi14() {
        return rsi14;
    }

    public void setRsi14(Rsi_14 rsi14) {
        this.rsi14 = rsi14;
    }

    public Rsi_20 getRsi20() {
        return rsi20;
    }

    public void setRsi20(Rsi_20 rsi20) {
        this.rsi20 = rsi20;
    }
}
