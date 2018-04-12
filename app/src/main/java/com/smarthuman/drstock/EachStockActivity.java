package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarthuman.drstock.R;

import java.util.TreeMap;

/**
 * Created by shiyuzhou on 5/4/2018.
 */

public class EachStockActivity extends AppCompatActivity {

    public static String stockId_Market;
    public static Stock myStock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eachstock);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        stockId_Market = intent.getStringExtra(StockFragment.EXTRA_MESSAGE);


        System.out.println("stockId_Market: " + stockId_Market);

        querySinaStocks(getEnqueryId(stockId_Market));


    }

    public String getEnqueryId(String stockI_M) {
        System.out.println("stockI_M: " + stockI_M);

        String[] stockI_Ms = stockI_M.split("\\.");

        System.out.println("---------StockI_Ms:");
        for(String s:stockI_Ms) {
            System.out.println(s);
        }
        System.out.println("---------");


        if (stockI_Ms[1].equals("US")) {
            return "gb_" + stockI_Ms[0];
        } else {
            return stockI_Ms[1].toLowerCase() + stockI_Ms[0];
        }
    }

    public void querySinaStocks(String queryId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://hq.sinajs.cn/list=" + queryId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sinaResponseToStocks(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(stringRequest);
    }

    public void sinaResponseToStocks(String response) {
        response = response.replaceAll("\n", "");

        Stock stockNow = new Stock();

        String[] leftRight = response.split("=");
        if (leftRight.length < 2)
            return;

        String left = leftRight[0];
        if (left.isEmpty())
            return;
        String[] lefts = left.split("_");
        String market = lefts[2];
        if (market.length() == 2) { // US
            stockNow.marketId_ = "US";
            stockNow.size_ = 28;
        } else if (market.substring(0, 2).equals("hk")) {    // HK
            stockNow.marketId_ = market.substring(0, 2).toUpperCase();
            stockNow.size_ = 19;
        } else {
            stockNow.marketId_ = market.substring(0, 2).toUpperCase();
            stockNow.size_ = 33;
        }

        String right = leftRight[1].replaceAll("\"", "");
        if (right.isEmpty())
            return;


        String[] values = right.split(",");
        for (int i = 0; i < values.length; i++) {
            stockNow.values[i] = values[i];
        }

        // TODO: English name for SH and SZ
        if (stockNow.marketId_.equals("US")) {
            stockNow.id_ = lefts[3];
            stockNow.name_ = lefts[3];

        } else if (stockNow.marketId_.equals("HK")) {
            stockNow.id_ = market.substring(2);
            stockNow.name_ = values[0];

        } else { // ZH & SH
            stockNow.id_ = market.substring(2);
            stockNow.name_ = values[0];

        }

        myStock = stockNow;

        updateContent();
    }

    public void updateContent() {
        Log.d("updatecontent", "function called");
        TextView id_Market = findViewById(R.id.stock_id);
        id_Market.setText(myStock.getId_Market());

        TextView stockName = findViewById(R.id.stock_company_name);
        stockName.setText(myStock.name_);

        TextView currentPrice = findViewById(R.id.eachstock_price);
        currentPrice.setText(myStock.getCurrentPrice_());

        TextView priceChange = findViewById(R.id.eachstock_pricechange);
        priceChange.setText(myStock.getPriceChange());

        TextView changePercent = findViewById(R.id.eachstock_percent);
        changePercent.setText(myStock.getChangePercent() + "%");

        TextView currency = findViewById(R.id.eachstock_currency);
        currency.setText(myStock.getCurrency());

        if (myStock.isRising()) {
            currentPrice.setTextColor(MainActivity.UpColor_);
            priceChange.setTextColor(MainActivity.UpColor_);
            changePercent.setTextColor(MainActivity.UpColor_);
        } else {
            currentPrice.setTextColor(MainActivity.DownColor_);
            priceChange.setTextColor(MainActivity.DownColor_);
            changePercent.setTextColor(MainActivity.DownColor_);
        }
    }
}