package com.smarthuman.drstock;

import java.util.List;

/**
 * Created by yuxuangu on 2018/4/24.
 */

public class StockFullListBean {

    private String success;
    private Result result;

    class Result {

        String totline;
        String disline;
        String page;
        private List<Lists> lists;

        class Lists {

            String stoid;
            String symbol;
            String sname;

            public String getStoid() {
                return stoid;
            }

            public void setStoid(String stoid) {
                this.stoid = stoid;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getSname() {
                return sname;
            }

            public void setSname(String sname) {
                this.sname = sname;
            }
        }


        public String getTotline() {
            return totline;
        }

        public void setTotline(String totline) {
            this.totline = totline;
        }

        public String getDisline() {
            return disline;
        }

        public void setDisline(String disline) {
            this.disline = disline;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public List<Lists> getLists() {
            return lists;
        }

        public void setLists(List<Lists> lists) {
            this.lists = lists;
        }
    }


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
