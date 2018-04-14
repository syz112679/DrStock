package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by shiyuzhou on 5/4/2018.
 */

public class EachStockActivity extends TitleActivity {

    public static String stockId_Market;
    public static Stock myStock;

    private Button addButton;
    private Button backButton, forwardButton;

    public ViewPager mViewPager;
    public ChartPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eachstock);

        // START: update the data of TextViews [Samuel_GU]
//        setTitle("Each Stock");
//        setTitleBackground(R.color.titleBarDemo);
//        setTitleBackground(MainActivity.UpColor_);

        showBackwardView(R.string.text_back, true);
        showForwardView(R.string.text_forward, true);

//        setGridLayout();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        stockId_Market = intent.getStringExtra(StockFragment.EXTRA_MESSAGE);
        querySinaStocks(getEnqueryId(stockId_Market));

        System.out.println("stockId_Market: " + stockId_Market);

        addButton = findViewById(R.id.add_to_favorite);
        addButton.setOnClickListener(this);


        mAdapter = new ChartPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.chartviewpager);

        // END: update the data of TextViews [Samuel_GU]

    }



    // START: update the data of TextViews [Samuel_GU]

    @Override
    public void onClick(View v) {
        super.onClick(v);

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

        setupViewPager(mViewPager);
        setViewPager(0);

        updateContent();
    }

    public void updateContent() {
//        Log.d("updatecontent", "function called");

        setTitle(myStock.name_ + " (" + myStock.getId_Market() + ")");

        TextView id_Market = findViewById(R.id.eachstock_date);
        id_Market.setText(myStock.getDate());

        TextView stockName = findViewById(R.id.eachstock_time);
        stockName.setText(myStock.getTime());

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

    public String getStockId() {
        if(stockId_Market.isEmpty())
            return null;

        return stockId_Market.split("\\.")[0];
    }

    public String getMarketCode() {
        if(stockId_Market.isEmpty())
            return null;
        if (stockId_Market.toUpperCase().indexOf("HK")>0) {            // if stockI_M is already the enquryId
            return "HK";
        }

        return null;

    }

    public void setupViewPager(ViewPager viewPager) {
        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new StockMinChartFragment(), "MinChart");
        adapter.addFragment(new StockKLineChart_1monthFragment(), "OneMonth");
        adapter.addFragment(new StockKLineChart_3monthFragment(), "ThreeMonth");
        adapter.addFragment(new StockKLineChart_1yearFragment(), "OneYear");
        adapter.addFragment(new StockKLineChart_3yearFragment(), "ThreeYear");

        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int index) {
        Log.d("EachStockActivity", "setViewPager called:" + index);
        mViewPager.setCurrentItem(index);

    }

    // END: update the data of TextViews [Samuel_GU]




}