package com.smarthuman.drstock;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

public class EachStockActivity extends AppCompatActivity implements View.OnClickListener  {

    public static String stockId_Market;
    public static Stock myStock;

    private Button addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eachstock);

        setGridLayout();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        stockId_Market = intent.getStringExtra(StockFragment.EXTRA_MESSAGE);

        System.out.println("stockId_Market: " + stockId_Market);

        addButton = findViewById(R.id.add_to_favorite);
        addButton.setOnClickListener(this);

        querySinaStocks(getEnqueryId(stockId_Market));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_to_favorite:
                Log.d("addstock", "here");
                addStock(v);
//                searchStock(v);
        }
    }

    public void addStock(View v) {
        MainActivity.StockIds_.add(myStock.getEnqueryId());

        System.out.println("add favourite: " + myStock.getEnqueryId() + ";");
    }

    public String getEnqueryId(String stockI_M) {
        System.out.println("stockI_M: " + stockI_M);

        String[] stockI_Ms = stockI_M.split("\\.");

        if (stockI_Ms.length == 1) {            // if stockI_M is already the enquryId
            return stockI_M;
        }


        if (stockI_Ms[1].equals("US")) {
            return "gb_" + stockI_Ms[0];
        } else {
            return stockI_Ms[1].toLowerCase() + stockI_Ms[0];
        }
    }

    public void setGridLayout() {
        GridLayout mGridLayout = (GridLayout) findViewById(R.id.eachStock_gridLayout);
        int columnCount = mGridLayout.getColumnCount();
        int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
//        Log.e(TAG, "column:" + columnCount + ";  screenwidth:" + screenWidth);
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            TextView button = (TextView) mGridLayout.getChildAt(i);
            button.setWidth(screenWidth / columnCount);
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
                        Log.d("", "---------response:\n" + response + "------------\n");
                        sinaResponseToStocks(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("", "---------error:\n" + error + "------------\n");
                    }
                });
        queue.add(stringRequest);
    }

    public void sinaResponseToStocks(String response) {
        response = response.replaceAll("\n", "");

        Stock stockNow = new Stock(response);

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

//        TextView currency = findViewById(R.id.eachstock_currency);
//        currency.setText(myStock.getCurrency());

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