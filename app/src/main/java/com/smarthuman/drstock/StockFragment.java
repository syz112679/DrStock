package com.smarthuman.drstock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static android.content.Context.NOTIFICATION_SERVICE;

//--------------------------------------------------------------------------------------------------


public class StockFragment extends Fragment implements View.OnClickListener {

    private static HashSet<String> StockIds_ = new HashSet();
    private static Vector<String> SelectedStockItems_ = new Vector();
    private final static int UpColor_ = Color.RED;
    private final static int DownColor_ = Color.GREEN;
    private final static int BackgroundColor_ = Color.WHITE;
    private final static int HighlightColor_ = Color.rgb(210, 233, 255);
    private final static String ShIndex = "sh000001";
    private final static String SzIndex = "sz399001";
    private final static String ChuangIndex = "sz399006";
    private final static String StockIdsKey_ = "StockIds";
    private final static int StockLargeTrade_ = 1000000;
    private Button addStockBtn;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stock, container, false);


        initEdt();
        initPop();

        SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String idsStr = sharedPref.getString(StockIdsKey_, ShIndex + "," + SzIndex + "," + ChuangIndex);

        String[] ids = idsStr.split(",");
        StockIds_.clear();
        for (String id : ids) {
            StockIds_.add(id);
        }

        Timer timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStocks();
            }
        }, 0, 10000); // 10 seconds

        addStockBtn = v.findViewById(R.id.stockFrag_addStock);
        addStockBtn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        mCompositeDisposable.clear();
        mPop.dismiss();

        saveStocksToPreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveStocksToPreferences();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_delete){
            if(SelectedStockItems_.isEmpty())
                return true;

            for (String selectedId : SelectedStockItems_){
                StockIds_.remove(selectedId);
                TableLayout table = (TableLayout)v.findViewById(R.id.stock_table);
                int count = table.getChildCount();
                for (int i = 1; i < count; i++){
                    TableRow row = (TableRow)table.getChildAt(i);
                    LinearLayout nameId = (LinearLayout)row.getChildAt(0);
                    TextView idText = (TextView)nameId.getChildAt(1);
                    if(idText != null && idText.getText().toString() == selectedId){
                        table.removeView(row);
                        break;
                    }
                }
            }

            SelectedStockItems_.clear();
        } else if (id == R.id.action_detail) {
            // send intent to Kmap_activity
            if (!SelectedStockItems_.isEmpty()) {


//                Intent intent = new Intent(MainActivity.this, KChartActivity.class);
//                intent.putExtra("stockId", SelectedStockItems_.elementAt(0));
//                //System.out.println("----------------startActivity--------------");
//                startActivity(intent);
            }
        } else if (id == R.id.action_add) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveStocksToPreferences(){
        String ids = "";
        for (String id : StockIds_){
            ids += id;
            ids += ",";
        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(StockIdsKey_, ids);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stockFrag_addStock:
                Log.d("addstock", "here");
                addStock(v);
        }
    }

    // 浦发银行,15.06,15.16,15.25,15.27,14.96,15.22,15.24,205749026,3113080980,
    // 51800,15.22,55979,15.21,1404740,15.20,1016176,15.19,187800,15.18,300,15.24,457700,15.25,548900,15.26,712266,15.27,1057960,15.28,2015-09-10,15:04:07,00
    public class Stock {
        public String id_, name_;
        public String open_, yesterday_, now_, high_, low_;
        public String b1_, b2_, b3_, b4_, b5_;
        public String bp1_, bp2_, bp3_, bp4_, bp5_;
        public String s1_, s2_, s3_, s4_, s5_;
        public String sp1_, sp2_, sp3_, sp4_, sp5_;
        public String time_;
    }

    /*
var hq_str_sz000001="平安银行,9.170,9.190,9.060,9.180,9.050,9.060,9.070,42148125,384081266.460,624253,9.060,638540,9.050,210600,9.040,341700,9.030,2298300,9.020,227184,9.070,178200,9.080,188240,9.090,293536,9.100,295300,9.110,2016-09-14,15:11:03,00";
这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
0：“平安银行”，股票名字；                name
1：“9.170”，今日开盘价；                open
2：“9.190”，昨日收盘价；                yesterday
3：“9.060”，当前价格；                  now
4：“9.180”，今日最高价；                high
5：“9.050”，今日最低价；                low

6：“9.060”，竞买价，即“买一“报价；
7：“9.070”，竞卖价，即“卖一“报价；
8：“42148125”，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
9：“384081266.460”，成交金额，单位为“元“，为了一目了然，通常以“万元“为成交金额的单位，所以通常把该值除以一万；

10：“624253”，“买一”申请624253股，即6243手；       b1_
11：“9.060”，“买一”报价；                                  bp1_
12：“638540”，“买二”申报股数；                   b2_
13：“9.050”，“买二”报价；                                  bp2_
14：“210600”，“买三”申报股数；                   b3_
15：“9.040”，“买三”报价；                                  bp3_
16：“341700”，“买四”申报股数；                   b4_
17：“9.030”，“买四”报价；                                  bp4_
18：“2298300”，“买五”申报股数；                  b5_
19：“9.020”，“买五”报价；                                  bfp5_
20：“227184”，“卖一”申报227184股，即2272手；       s1_
21：“9.070”，“卖一”报价；                                  sp1_
(22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖五”的申报股数及其价格；
30：“2016-09-14”，日期；                         time_
31：“15:11:03”，时间；                           time_

作者：江湖人称_赫大侠
链接：https://www.jianshu.com/p/fabe3811a01d
來源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    * */

    public TreeMap<String, Stock> sinaResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        TreeMap<String, Stock> stockMap = new TreeMap();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            Stock stockNow = new Stock();
            stockNow.id_ = left.split("_")[2];

            String[] values = right.split(",");
            stockNow.name_ = values[0];
            stockNow.open_ = values[1];
            stockNow.yesterday_ = values[2];
            stockNow.now_ = values[3];
            stockNow.high_ = values[4];
            stockNow.low_ = values[5];
            stockNow.b1_ = values[10];
            stockNow.b2_ = values[12];
            stockNow.b3_ = values[14];
            stockNow.b4_ = values[16];
            stockNow.b5_ = values[18];
            stockNow.bp1_ = values[11];
            stockNow.bp2_ = values[13];
            stockNow.bp3_ = values[15];
            stockNow.bp4_ = values[17];
            stockNow.bp5_ = values[19];
            stockNow.s1_ = values[20];
            stockNow.s2_ = values[22];
            stockNow.s3_ = values[24];
            stockNow.s4_ = values[26];
            stockNow.s5_ = values[28];
            stockNow.sp1_ = values[21];
            stockNow.sp2_ = values[23];
            stockNow.sp3_ = values[25];
            stockNow.sp4_ = values[27];
            stockNow.sp5_ = values[29];
            stockNow.time_ = values[values.length - 3] + "_" + values[values.length - 2];
            stockMap.put(stockNow.id_, stockNow);
        }

        return stockMap;
    }

    public void querySinaStocks(String list){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateStockListView(sinaResponseToStocks(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        System.out.println("------\n"+stringRequest+"\n------\n");
        queue.add(stringRequest);
    }

    private void refreshStocks(){
        String ids = "";
        for (String id : StockIds_){
            ids += id;
            ids += ",";
        }
        querySinaStocks(ids);
    }

    public void addStock(View view) {
        Log.d("addstock", "add stock function here");
        EditText editText = (EditText) v.findViewById(R.id.editText_stockId);
        String stockId = editText.getText().toString();
        if(stockId.length() != 6)
            return;

        if (stockId.startsWith("6")) {
            stockId = "sh" + stockId;
        } else if (stockId.startsWith("0") || stockId.startsWith("3")) {
            stockId = "sz" + stockId;
        } else
            return;

        StockIds_.add(stockId);
        refreshStocks();
    }

    public void sendNotifation(int id, String title, String text){
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this.getActivity());
        nBuilder.setSmallIcon(R.drawable.ic_launcher);
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(text);
        nBuilder.setVibrate(new long[]{100, 100, 100});
        nBuilder.setLights(Color.RED, 1000, 1000);

        NotificationManager notifyMgr = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(id, nBuilder.build());
    }

    public void updateStockListView(TreeMap<String, Stock> stockMap){
//        String stock_name = getResources().getString(R.string.stock_name);
//        String stock_id = getResources().getString(R.string.stock_id);
//        String stock_now = getResources().getString(R.string.stock_now);

//        ListView list = (ListView) findViewById(R.id.listView);
//
//        ArrayList<HashMap<String, String>> stockList = new ArrayList<>();
//        HashMap<String, String> mapTitle = new HashMap<>();
//        mapTitle.put(stock_name, getResources().getString(R.string.stock_name_title));
//        //mapTitle.put(stock_id, "");
//        mapTitle.put(stock_now, getResources().getString(R.string.stock_now_title));
//        stockList.add(mapTitle);
//
//        for(Stock stock : stocks)
//        {
//            HashMap<String, String> map = new HashMap<>();
//            map.put(stock_name, stock.name_);
//            String id = stock.id_.replaceAll("sh", "");
//            id = id.replaceAll("sz", "");
//            map.put(stock_id, id);
//            map.put(stock_now, stock.now_);
//            stockList.add(map);
//        }
//
//        SimpleAdapter adapter = new SimpleAdapter(this,
//                stockList,
//                R.layout.stock_listitem,
//                new String[] {stock_name, stock_id, stock_now},
//                new int[] {R.id.stock_name, R.id.stock_id, R.id.stock_now});
//        list.setAdapter(adapter);

        // Table
        TableLayout table = (TableLayout)v.findViewById(R.id.stock_table);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        table.removeAllViews();

        // Title
        TableRow rowTitle = new TableRow(this.getActivity());

        TextView nameTitle = new TextView(this.getActivity());
        nameTitle.setText(getResources().getString(R.string.stock_name_title));
        rowTitle.addView(nameTitle);

        TextView nowTitle = new TextView(this.getActivity());
        nowTitle.setGravity(Gravity.CENTER);
        nowTitle.setText(getResources().getString(R.string.stock_now_title));
        rowTitle.addView(nowTitle);

        TextView percentTitle = new TextView(this.getActivity());
        percentTitle.setGravity(Gravity.CENTER);
        percentTitle.setText(getResources().getString(R.string.stock_increase_percent_title));
        rowTitle.addView(percentTitle);

        TextView increaseTitle = new TextView(this.getActivity());
        increaseTitle.setGravity(Gravity.CENTER);
        increaseTitle.setText(getResources().getString(R.string.stock_increase_title));
        rowTitle.addView(increaseTitle);

        table.addView(rowTitle);

        Collection<Stock> stocks = stockMap.values();

        for(Stock stock : stocks)
        {
            if(stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)){
                Double dNow = Double.parseDouble(stock.now_);
                Double dYesterday = Double.parseDouble(stock.yesterday_);
                Double dIncrease = dNow - dYesterday;
                Double dPercent = dIncrease / dYesterday * 100;
                String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);

                int indexId;
                int changeId;
                if(stock.id_.equals(ShIndex)) {
                    indexId = R.id.stock_sh_index;
                    changeId = R.id.stock_sh_change;
                }
                else if(stock.id_.equals(SzIndex)) {
                    indexId = R.id.stock_sz_index;
                    changeId = R.id.stock_sz_change;
                }
                else{
                    indexId = R.id.stock_chuang_index;
                    changeId = R.id.stock_chuang_change;
                }

                TextView indexText = (TextView)v.findViewById(indexId);
                indexText.setText(stock.now_);
                int color = Color.BLACK;
                //System.out.println("-----|" + dIncrease + "|------");
                if(dIncrease > 0) {
                    color = UpColor_;
                }
                else if(dIncrease < 0){
                    color = DownColor_;
                }
                //System.out.println("-----|" + color + "|------");
                indexText.setTextColor(color);

                TextView changeText = (TextView)v.findViewById(changeId);
                changeText.setText(change);
                changeText.setTextColor(color);

                continue;
            }

            TableRow row = new TableRow(this.getActivity());
            row.setMinimumHeight(200); //////////////////////////////////////////////
            row.setGravity(Gravity.CENTER_VERTICAL);
            //int color = R.color.colorSelected;
            if (SelectedStockItems_.contains(stock.id_)){
                row.setBackgroundColor(HighlightColor_);
            }

            LinearLayout nameId = new LinearLayout(this.getActivity());
            nameId.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(this.getActivity());
            name.setText(stock.name_);
            nameId.addView(name);

            TextView id = new TextView(this.getActivity());
            id.setTextSize(15);
            id.setText(stock.id_);
            nameId.addView(id);

            row.addView(nameId);

            TextView now = new TextView(this.getActivity());
            now.setGravity(Gravity.RIGHT);
            now.setText(stock.now_);
            row.addView(now);

            TextView percent = new TextView(this.getActivity());
            percent.setGravity(Gravity.RIGHT);
            TextView increaseValue = new TextView(this.getActivity());
            increaseValue.setGravity(Gravity.RIGHT);
            Double dOpen = Double.parseDouble(stock.open_);
            Double dB1 = Double.parseDouble(stock.bp1_);
            Double dS1 = Double.parseDouble(stock.sp1_);

            if(dOpen == 0 && dB1 == 0 && dS1 == 0) {
                percent.setText("--");
                increaseValue.setText("--");
            }
            else{
                Double dNow = Double.parseDouble(stock.now_);
                if(dNow == 0) {// before open
                    if(dS1 == 0) {
                        dNow = dB1;
                        now.setText(stock.bp1_);
                    }
                    else {
                        dNow = dS1;
                        now.setText(stock.sp1_);
                    }
                }
                Double dYesterday = Double.parseDouble(stock.yesterday_);
                Double dIncrease = dNow - dYesterday;
                Double dPercent = dIncrease / dYesterday * 100;
                percent.setText(String.format("%.2f", dPercent) + "%");
                increaseValue.setText(String.format("%.2f", dIncrease));
                int color = Color.BLACK;
                if(dIncrease > 0) {
                    color = UpColor_;
                }
                else if(dIncrease < 0){
                    color = DownColor_;
                }

                now.setTextColor(color);
                percent.setTextColor(color);
                increaseValue.setTextColor(color);
            }

            row.addView(percent);
            row.addView(increaseValue);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup group = (ViewGroup) v;
                    ViewGroup nameId = (ViewGroup) group.getChildAt(0);
                    TextView idText = (TextView) nameId.getChildAt(1);
                    //System.out.println("-----\n" + idText.getText().toString() + "\n-------");

                    // TODO : call funciton here



                    if (SelectedStockItems_.contains(idText.getText().toString())) {
                        v.setBackgroundColor(BackgroundColor_);
                        SelectedStockItems_.remove(idText.getText().toString());
                    } else {
                        v.setBackgroundColor(HighlightColor_);
                        SelectedStockItems_.add(idText.getText().toString());
                    }

                    Intent intent = new Intent(getActivity(), ChartActivity.class);
                    startActivity(intent);
                }
            });

            table.addView(row);
            //table.setMinimumHeight(60);

            String sid = stock.id_;
            sid = sid.replaceAll("sh", "");
            sid = sid.replaceAll("sz", "");

            String text = "";
            String sBuy = getResources().getString(R.string.stock_buy);
            String sSell = getResources().getString(R.string.stock_sell);
            if(Double.parseDouble(stock.b1_ )>= StockLargeTrade_) {
                text += sBuy + "1:" + stock.b1_ + ",";
            }
            if(Double.parseDouble(stock.b2_ )>= StockLargeTrade_) {
                text += sBuy + "2:" + stock.b2_ + ",";
            }
            if(Double.parseDouble(stock.b3_ )>= StockLargeTrade_) {
                text += sBuy + "3:" + stock.b3_ + ",";
            }
            if(Double.parseDouble(stock.b4_ )>= StockLargeTrade_) {
                text += sBuy + "4:" + stock.b4_ + ",";
            }
            if(Double.parseDouble(stock.b5_ )>= StockLargeTrade_) {
                text += sBuy + "5:" + stock.b5_ + ",";
            }
            if(Double.parseDouble(stock.s1_ )>= StockLargeTrade_) {
                text += sSell + "1:" + stock.s1_ + ",";
            }
            if(Double.parseDouble(stock.s2_ )>= StockLargeTrade_) {
                text += sSell + "2:" + stock.s2_ + ",";
            }
            if(Double.parseDouble(stock.s3_ )>= StockLargeTrade_) {
                text += sSell + "3:" + stock.s3_ + ",";
            }
            if(Double.parseDouble(stock.s4_ )>= StockLargeTrade_) {
                text += sSell + "4:" + stock.s4_ + ",";
            }
            if(Double.parseDouble(stock.s5_ )>= StockLargeTrade_) {
                text += sSell + "5:" + stock.s5_ + ",";
            }
            if(text.length() > 0)
                sendNotifation(Integer.parseInt(sid), stock.name_, text);
        }
    }

    //----------------------------------------------Followings are for search association-------------------------------------------------

    private static final String TAG = "MainActivity";
    private EditText editText;
    private CustomPopupWindow mPop; //显示搜索联想的pop
    private ListView searchLv; //搜索联想结果的列表
    private ArrayAdapter mAdapter; //ListView的适配器
    private List<String> mSearchList = new ArrayList<>(); //搜索结果的数据源
    private PublishSubject<String> mPublishSubject;
    private CompositeDisposable mCompositeDisposable;

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mCompositeDisposable.clear();
//        mPop.dismiss();
//    }

    private void initEdt() {
        editText = (EditText) v.findViewById(R.id.editText_stockId);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    mPop.dismiss();
                } else {
                    //输入内容非空的时候才开始搜索
                    startSearch(s.toString());
                }
            }
        });

        mPublishSubject = PublishSubject.create();
        mPublishSubject.debounce(200, TimeUnit.MILLISECONDS) //这里我们限制只有在输入字符200毫秒后没有字符没有改变时才去请求网络，节省了资源
                .filter(new Predicate<String>() { //对源Observable产生的结果按照指定条件进行过滤，只有满足条件的结果才会提交给订阅者

                    @Override
                    public boolean test(String s) throws Exception {
                        //当搜索词为空时，不发起请求
                        return s.length() > 0;
                    }
                })
                /**
                 * flatmap:把Observable产生的结果转换成多个Observable，然后把这多个Observable
                 “扁平化”成一个Observable，并依次提交产生的结果给订阅者

                 *concatMap:操作符flatMap操作符不同的是，concatMap操作符在处理产生的Observable时，
                 采用的是“连接(concat)”的方式，而不是“合并(merge)”的方式，
                 这就能保证产生结果的顺序性，也就是说提交给订阅者的结果是按照顺序提交的，不会存在交叉的情况

                 *switchMap:与flatMap操作符不同的是，switchMap操作符会保存最新的Observable产生的
                 结果而舍弃旧的结果
                 **/
                .switchMap(new Function<String, ObservableSource<String>>() {

                    @Override
                    public ObservableSource<String> apply(String query) throws Exception {
                        return (ObservableSource<String>) getSearchObservable(query);
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {

                    @Override
                    public void onNext(String s) {
                        //显示搜索联想的结果
                        showSearchResult(s);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(mCompositeDisposable);
    }

    //开始搜索
    private void startSearch(String query) {
        mPublishSubject.onNext(query);
    }

    private Observable<String> getSearchObservable(final String query) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                //注意：这里只是模仿求取服务器数据，实际开发中需要你根据这个输入的关键字query去请求数据
                Log.d(TAG, "开始请求，关键词为：" + query);
                try {
                    Thread.sleep(100); //模拟网络请求，耗时100毫秒
                } catch (InterruptedException e) {
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onError(e);
                    }
                }
                if (!(query.contains("科") || query.contains("耐") || query.contains("七"))) {
                    //没有联想结果，则关闭pop
                    mPop.dismiss();
                    return;
                }
                Log.d("SearchActivity", "结束请求，关键词为：" + query);
                observableEmitter.onNext(query);
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 显示搜索结果
     */
    private void showSearchResult(String keyWords) {
        mSearchList.clear(); //先清空数据源
        switch (keyWords) {
            case "科比":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.kobe)));
                break;
            case "耐克":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.nike)));
                break;
            case "七夕":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.qixi)));
                break;
        }
        mAdapter.notifyDataSetChanged();
        mPop.showAsDropDown(editText, 0, 0); //显示搜索联想列表的pop
    }

    /**
     * 初始化Pop，pop的布局是一个列表
     */
    private void initPop() {
        mPop = new CustomPopupWindow.Builder(this.getActivity())
                .setContentView(R.layout.pop_search)
                .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
                .setBackgroundAlpha(1f)
                .build();
        searchLv = (ListView) mPop.getItemView(R.id.search_list_lv);
        mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mSearchList);
        searchLv.setAdapter(mAdapter);
        searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("result", mSearchList.get(position));
                System.out.println("----------------startActivity--------------");
                startActivity(intent);

            }
        });
    }
}