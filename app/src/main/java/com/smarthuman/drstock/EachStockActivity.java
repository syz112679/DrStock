package com.smarthuman.drstock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Collection;

/**
 * Created by shiyuzhou on 5/4/2018.
 */

public class EachStockActivity extends TitleActivity {

    public static String stockId_Market;
    public static Stock myStock;
    public boolean invalidInput = false, inPlan = false;

    private Button buyButton, sellButton;
    private ImageView addImg, add_to_plan;
    private RefreshLayout refreshLayout;
    private Button minBtn, oneMonthBtn, threeMonthBtn, oneYearBtn, threeYearBtn, changePlan, planDetails;

    public ViewPager mViewPager;
    public ChartPagerAdapter mAdapter;

    public double exchangeRate = 1;
    public boolean isHKstock = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eachstock);

        // START: update the data of TextViews [Samuel_GU]
//        setTitle("Each Stock");
//        setTitleBackground(R.color.titleBarDemo);
//        setTitleBackground(MainActivity.UpColor_);
        invalidInput = false;
        showBackward(getDrawable(R.drawable.ic_return), true);

//        setGridLayout();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        stockId_Market = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        querySinaStocks(getEnqueryId(stockId_Market));

        System.out.println("stockId_Market: " + stockId_Market);

        refreshLayout = findViewById(R.id.refreshLayout_eachStock);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                querySinaStocks(getEnqueryId(stockId_Market));
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
            }
        });

        addImg = findViewById(R.id.add_to_favorite);
        addImg.setOnClickListener(this);
        buyButton = findViewById(R.id.eachstock_buy_btn);
        buyButton.setOnClickListener(this);
        sellButton = findViewById(R.id.eachstock_sell_btn);
        sellButton.setOnClickListener(this);

        mAdapter = new ChartPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.chartviewpager);

        // END: update the data of TextViews [Samuel_GU]
        minBtn = (Button) findViewById(R.id.min_graph_btn);
        minBtn.setOnClickListener(this);
        minBtn.setTextColor(Color.parseColor("#FF8559"));
        oneMonthBtn = (Button) findViewById(R.id.one_month_btn);
        oneMonthBtn.setOnClickListener(this);
        threeMonthBtn = (Button) findViewById(R.id.three_month_btn);
        threeMonthBtn.setOnClickListener(this);
        oneYearBtn = (Button) findViewById(R.id.one_year_btn);
        oneYearBtn.setOnClickListener(this);
        threeYearBtn = (Button) findViewById(R.id.three_year_btn);
        threeYearBtn.setOnClickListener(this);

        add_to_plan = findViewById(R.id.add_to_plan);
        changePlan = findViewById(R.id.changePlan);
        changePlan.setOnClickListener(this);
        planDetails = findViewById(R.id.planDetails);
        planDetails.setOnClickListener(this);
    }

    // START: update the data of TextViews [Samuel_GU]

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.add_to_favorite:
                Log.d("addstock", "here");
                if(MainActivity.mfirebaseUser != null && MainActivity.mUserName!=null) {
                    if(MainActivity.checkStatus(myStock.getEnqueryId())){
                        removeStock(v);
                        addImg.setImageResource(R.drawable.ic_favourite);
                        Toast.makeText(getApplicationContext(), R.string.toast_delete_from_watchlist, Toast.LENGTH_SHORT).show();

                    }else{
                        addStock(v);
                        addImg.setImageResource(R.drawable.ic_favourite_solid);
                        Toast.makeText(getApplicationContext(), R.string.toast_added_to_watchlist, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_signin_first, Toast.LENGTH_SHORT).show();
                }
//                searchStock(v);
                break;
            case R.id.min_graph_btn:
                minBtn.setTextColor(Color.parseColor("#FF8559"));
                oneMonthBtn.setTextColor(Color.parseColor("#000000"));
                threeMonthBtn.setTextColor(Color.parseColor("#000000"));
                oneYearBtn.setTextColor(Color.parseColor("#000000"));
                threeYearBtn.setTextColor(Color.parseColor("#000000"));
                setViewPager(0);
                break;
            case R.id.one_month_btn:
                oneMonthBtn.setTextColor(Color.parseColor("#FF8559"));
                minBtn.setTextColor(Color.parseColor("#000000"));
                threeMonthBtn.setTextColor(Color.parseColor("#000000"));
                oneYearBtn.setTextColor(Color.parseColor("#000000"));
                threeYearBtn.setTextColor(Color.parseColor("#000000"));
                setViewPager(1);
                break;
            case R.id.three_month_btn:
                oneMonthBtn.setTextColor(Color.parseColor("#000000"));
                minBtn.setTextColor(Color.parseColor("#000000"));
                oneYearBtn.setTextColor(Color.parseColor("#000000"));
                threeYearBtn.setTextColor(Color.parseColor("#000000"));
                threeMonthBtn.setTextColor(Color.parseColor("#FF8559"));
                setViewPager(2);
                break;
            case R.id.one_year_btn:
                oneYearBtn.setTextColor(Color.parseColor("#FF8559"));
                oneMonthBtn.setTextColor(Color.parseColor("#000000"));
                threeMonthBtn.setTextColor(Color.parseColor("#000000"));
                minBtn.setTextColor(Color.parseColor("#000000"));
                threeYearBtn.setTextColor(Color.parseColor("#000000"));
                setViewPager(3);
                break;
            case R.id.three_year_btn:
                threeYearBtn.setTextColor(Color.parseColor("#FF8559"));
                oneMonthBtn.setTextColor(Color.parseColor("#000000"));
                threeMonthBtn.setTextColor(Color.parseColor("#000000"));
                oneYearBtn.setTextColor(Color.parseColor("#000000"));
                minBtn.setTextColor(Color.parseColor("#000000"));
                setViewPager(4);
                break;
            case R.id.eachstock_buy_btn:
                if(MainActivity.mfirebaseUser != null && MainActivity.mUserName!=null) {
                    Log.d("buy btn", "buy btn pressed");
                    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    //final EditText edittext = new EditText(this);
                    String message = "\n" + getString(R.string.enter_the_amount_buy) + "\n";
                    if(!isHKstock) {
                        message += getString(R.string.text_the_exchange_rate) + " " + String.format("%.3f", exchangeRate) + "HKD";
                    }
                    alert.setMessage(message);
                    alert.setTitle(R.string.buy);
                    LinearLayout buyDialog= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_sip,null);
                    alert.setView(buyDialog);
                    //alert.setView(edittext);
                    final EditText buy_amount_et = (EditText) buyDialog.findViewById(R.id.buy_et_input);
                    TextView sip = (TextView) buyDialog.findViewById(R.id.sip_tv);
                    final EditText buy_price_et = (EditText) buyDialog.findViewById(R.id.buy_price_et_input);
                    final String sell1price;
                    if(myStock.getSell1()!=null) {
                        sell1price = myStock.getSell1();
                    } else {
                        sell1price = myStock.getCurrentPrice_();
                    }
                    buy_price_et.setText(sell1price);
                    sip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getApplicationContext(),"go to SIP", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), InvestmentPlanDetails.class));
                        }
                    });

                    alert.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String inputAmount = buy_amount_et.getText().toString();
                            String inputPrice = buy_price_et.getText().toString();
                            Log.d("Alert","input is " + inputAmount);

                            double amount = Double.parseDouble(inputAmount);
                            double price = Double.parseDouble(inputPrice);
                            double sell1price_db = Double.parseDouble(sell1price);
                            double precision = amount - ((int) amount);
                            if(amount<=0) {
                                Toast.makeText(getApplicationContext(), R.string.toast_invalid_input, Toast.LENGTH_SHORT).show();
                            } else if(amount*sell1price_db > MainActivity.mBalance){
                                Toast.makeText(getApplicationContext(), R.string.toast_you_dont_have_enough_money, Toast.LENGTH_SHORT).show();
                                Log.d("EachStockActivity", "your money:" + MainActivity.mMoney + ", needed:" + amount*sell1price_db);
                            } else if(precision < 0.01 && precision > 0){
                                Toast.makeText(getApplicationContext(), R.string.text_precision_error, Toast.LENGTH_SHORT).show();
                            } else {
                                if(Math.abs(sell1price_db-price)<0.01) {
                                    StockSnippet newStock = new StockSnippet(myStock.id_, sell1price_db, amount);
                                    double cost = amount * sell1price_db * exchangeRate;
                                    MainActivity.mBalance -= cost;
                                    boolean found = false;
                                    for (int i = 0; i < MainActivity.mStockRecords.size(); i++) {
                                        if (MainActivity.mStockRecords.get(i).getId().equals(myStock.id_)) {
                                            found = true;
                                            double oldAmount = MainActivity.mStockRecords.get(i).getAmount();
                                            double oldPrice = MainActivity.mStockRecords.get(i).getBoughtPrice();

                                            MainActivity.mStockRecords.get(i).setBoughtPrice((oldAmount * oldPrice + amount * sell1price_db) / (oldAmount + amount));
                                            MainActivity.mStockRecords.get(i).setAmount(oldAmount + amount);
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        MainActivity.mStockRecords.add(newStock);
                                        MainActivity.stockMap_.put(myStock.id_, myStock);
//                                      MainActivity.requireRefresh = true;
                                    }
                                    Toast.makeText(getApplicationContext(), getString(R.string.toast_buy_successfully) + String.valueOf(cost), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.your_price_too_low, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.

                        }
                    });

                    alert.show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_signin_first, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.eachstock_sell_btn:
                if(MainActivity.mfirebaseUser != null && MainActivity.mUserName!=null) {
                    Log.d("buy btn", "buy btn pressed");

                    AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                    //final EditText edittext2 = new EditText(this);
                    String message = "\n" + getString(R.string.enter_the_amount_sell) + "\n";
                    if(!isHKstock) {
                        message += getString(R.string.text_the_exchange_rate) + " " + String.format("%.3f", exchangeRate) + "HKD";
                    }
                    alert2.setMessage(message);
                    alert2.setTitle(R.string.sell);

                    LinearLayout sellDialog= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_sell,null);
                    alert2.setView(sellDialog);
                    final EditText sell_price_et = sellDialog.findViewById(R.id.sell_price_et);
                    final String bidprice;
                    if(myStock.getSell1()!=null) {
                        bidprice = myStock.getBid1();
                    } else {
                        bidprice = myStock.getCurrentPrice_();
                    }

                    sell_price_et.setText(bidprice);
                    final EditText sell_amount_et = sellDialog.findViewById(R.id.sell_amount_et);

                    alert2.setPositiveButton(R.string.sell, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String inputAmount = sell_amount_et.getText().toString();
                            String inputPrice = sell_price_et.getText().toString();
                            Log.d("Alert","input is " + inputAmount);
                            double amount = Double.parseDouble(inputAmount);
                            double price = Double.parseDouble(inputPrice);
                            double bidprice_db = Double.parseDouble(bidprice);
                            double precision = amount - ((int) amount);
                            if(precision < 0.01 && precision > 0){
                                Toast.makeText(getApplicationContext(), R.string.text_precision_error, Toast.LENGTH_SHORT).show();
                            } else {
                                boolean found =false;
                                int i=0;
                                for(; i<MainActivity.mStockRecords.size(); i++) {
                                    if(MainActivity.mStockRecords.get(i).getId().equals(myStock.id_)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    Toast.makeText(getApplicationContext(), R.string.toast_you_dont_have_this_stock, Toast.LENGTH_SHORT).show();
                                } else {
                                    if(amount > MainActivity.mStockRecords.get(i).getAmount()) {
                                        Toast.makeText(getApplicationContext(), R.string.toast_you_cant_sell_more_than_you_have, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(Math.abs(bidprice_db-price)<0.01) {
                                            double oldamount = MainActivity.mStockRecords.get(i).getAmount();
                                            double oldprice = MainActivity.mStockRecords.get(i).getBoughtPrice();
                                            double earning = amount * bidprice_db * exchangeRate - amount * oldprice * exchangeRate;
                                            MainActivity.mStockRecords.get(i).setAmount(oldamount - amount);
                                            MainActivity.mBalance += amount * bidprice_db * exchangeRate;
                                            MainActivity.mMoney += earning;
                                            MainActivity.mEarning += earning;
                                            if (earning >= 0) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.toast_you_have_earned) + " " + String.format("%.2f", (earning)), Toast.LENGTH_SHORT).show();
                                                System.out.println(R.string.toast_you_have_earned + " " + String.valueOf(earning));
                                            } else {
                                                Toast.makeText(getApplicationContext(), getString(R.string.toast_you_have_lost) + " " + String.format("%.2f", (-earning)), Toast.LENGTH_SHORT).show();
                                            }

                                            if (oldamount == amount) {
                                                MainActivity.mStockRecords.remove(i);
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(),R.string.your_price_too_high,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                            }
                        }
                    });

                    alert2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.

                        }
                    });

                    alert2.show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_signin_first, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.changePlan:
                if(MainActivity.mfirebaseUser != null && MainActivity.mUserName!=null) {
                    if (inPlan) {
                        InvestmentPlan plan = InvestmentPlan.planTreeMap.get(myStock.getEnqueryId());
                        double oldVolumn;
                        if (plan == null) {
                            oldVolumn = 0;
                        } else {
                            oldVolumn = plan.baseVolumn;
                        }

                        final AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
                        final EditText input_et = new EditText(this);
                        String message = "\n" + getString(R.string.plan_current_volume_is) + String.valueOf(oldVolumn) + "\n"
                                +getString(R.string.enter_new_plan_zero_for_deleting)+ "\n";

                        alert3.setMessage(message);
                        alert3.setTitle(R.string.text_change_SIP);
                        alert3.setView(input_et);

                        alert3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String baseVolume = input_et.getText().toString();
                                double baseVolume_db = Double.parseDouble(baseVolume);
                                //TODO: change to plans
                                InvestmentPlan p = InvestmentPlan.planTreeMap.get(myStock.getEnqueryId());
                                if (p != null) {
                                    if ( baseVolume_db == 0) {
                                        InvestmentPlan.planTreeMap.remove(myStock.getEnqueryId());
                                        inPlan = false;
                                        add_to_plan.setImageResource(R.drawable.ic_collection);
                                    } else {
                                        p.baseVolumn = baseVolume_db;
                                        Toast.makeText(getApplicationContext(), R.string.toast_plan_change, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });

                        alert3.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                               //TODO: delete
                                // what ever you want to do with No option.
                                InvestmentPlan p = InvestmentPlan.planTreeMap.get(myStock.getEnqueryId());
                                if (p != null) {
                                    InvestmentPlan.planTreeMap.remove(myStock.getEnqueryId());
                                    inPlan = false;
                                    add_to_plan.setImageResource(R.drawable.ic_collection);
                                    // TODO: mystock remove
                                }
                            }
                        });

                        alert3.show();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_add_first, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_signin_first, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_to_plan:
                if(MainActivity.mfirebaseUser != null && MainActivity.mUserName!=null) {
                    if (!inPlan) {
                        final AlertDialog.Builder alert4 = new AlertDialog.Builder(this);
                        final EditText input_et = new EditText(this);
                        String message = "\n" + getString(R.string.text_enter_the_basevolume) + "\n";

                        alert4.setMessage(message);
                        alert4.setTitle(R.string.text_SIP);
                        alert4.setView(input_et);

                        alert4.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String baseVolume = input_et.getText().toString();
                                double baseVolume_db = Double.parseDouble(baseVolume);
                                //TODO: add to plans
                                add_to_plan.setImageResource(R.drawable.ic_collection_fill);
                                inPlan = true;
                                InvestmentPlan.addPlan(new InvestmentPlan(myStock.getEnqueryId(), myStock.name_, baseVolume_db, "W"));

                            }
                        });

                        alert4.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                                // do nothing
                            }
                        });

                        alert4.show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_click_change, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_signin_first, Toast.LENGTH_SHORT).show();
                }
            case R.id.planDetails:
                startActivity(new Intent(this, InvestmentPlanDetails.class));
                break;
        }
    }

    public void addStock(View v) {
       MainActivity.addStockIds_(myStock.getEnqueryId());


        System.out.println("add favourite: " + myStock.getEnqueryId() + ";");
        System.out.println("StockIds_: " + MainActivity.getStockIds_() + ";");
    }

    public void removeStock(View v) {
        MainActivity.removeStockIds_(myStock.getEnqueryId());


        System.out.println("remove favourite: " + myStock.getEnqueryId() + ";");
        System.out.println("StockIds_: " + MainActivity.getStockIds_() + ";");
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
        System.out.println("---------url: " + url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("---------response:\n" + response + "------------\n");
//                        Log.d("", "---------response:\n" + response + "------------\n");
                        String right = response.split("=")[1];
                        System.out.println("right: " + right);
                        if (right.equals("\"\";\n") || right.equals("\"FAILED\";\n")) {
                            invalidInput = true;
                            Toast.makeText(getApplicationContext(), R.string.toast_invalid_input, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            invalidInput = false;
                            sinaResponseToStocks(response);
                        }
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

        // if it is favorited
        int i=0;
        for (String id: MainActivity.getStockIds_()) {
            i++;
            if(StockFragment.input2enqury(myStock.id_).equals( id) ) {
                addImg.setImageResource(R.drawable.ic_favourite_solid);
                System.out.println(" ****************it is favorited, i=" + i);
            }
        }

        if(InvestmentPlan.planTreeMap != null) {
            Collection<InvestmentPlan> plans = InvestmentPlan.planTreeMap.values();
            for (InvestmentPlan p : plans) {
                if (myStock.getEnqueryId().equals(p.enquiryId)) {
                    add_to_plan.setImageResource(R.drawable.ic_collection_fill);
                    inPlan = true;
                }
            }
        }

        switch (myStock.marketId_) {
            case "US":
                Log.d("EachStockActivity", "US market stock");
                exchangeRate = Double.parseDouble(MainActivity.exchangeRate_[ExchangeRate.HKD][ExchangeRate.USD]) / 100;
                isHKstock = false;
                break;
            case "SZ":
                Log.d("EachStockActivity", "SZ market stock");
                exchangeRate = Double.parseDouble(MainActivity.exchangeRate_[ExchangeRate.HKD][ExchangeRate.RMB]) / 100;
                isHKstock = false;
                break;
            case "SH":
                Log.d("EachStockActivity", "SH market stock");
                exchangeRate = Double.parseDouble(MainActivity.exchangeRate_[ExchangeRate.HKD][ExchangeRate.RMB]) / 100;
                isHKstock = false;
                break;
            case "HK":
                Log.d("EachStockActivity", "HK market stock");
                exchangeRate = 1;
                isHKstock = true;
                break;

        }



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

        TextView high = findViewById(R.id.high_data);
        high.setText(myStock.getHigh());
        TextView low = findViewById(R.id.low_data);
        low.setText(myStock.getLow());

        TextView close = findViewById(R.id.close_data);
        close.setText(myStock.getClose());
        TextView open = findViewById(R.id.open_data);
        open.setText(myStock.getOpen());

        TextView volume = findViewById(R.id.volume_data);
        volume.setText(myStock.getVolume());
        TextView turnover = findViewById(R.id.turnover_data);
        turnover.setText(myStock.getTurnover());

        TextView bid1 = findViewById(R.id.bid1_data);
        bid1.setText(myStock.getBid1());
        TextView sell1 = findViewById(R.id.sell1_data);
        sell1.setText(myStock.getSell1());

        if (myStock.isRising()) {
            currentPrice.setTextColor(MainActivity.UpColor_);
            priceChange.setTextColor(MainActivity.UpColor_);
            changePercent.setTextColor(MainActivity.UpColor_);
        } else {
            currentPrice.setTextColor(MainActivity.DownColor_);
            priceChange.setTextColor(MainActivity.DownColor_);
            changePercent.setTextColor(MainActivity.DownColor_);
        }

        // if it is favorited
        int i=0;
        for (String id: MainActivity.getStockIds_()) {
            i++;
            if(StockFragment.input2enqury(myStock.id_).equals( id) ) {
                //TODO: it is favorited
                System.out.println(" ****************it is favorited, i=" + i);
            }
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