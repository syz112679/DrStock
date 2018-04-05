package com.smarthuman.drstock;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/****************************************************/
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
/*******************************************************************/

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    //--------------------------------------------------------------------------------------------------
    public static HashSet<String> StockIds_ = new HashSet();
    public static Vector<String> SelectedStockItems_ = new Vector();
    static TreeMap<String, Stock> stockMap = new TreeMap<String, Stock>();
    public final static int UpColor_ = Color.GREEN;
    public final static int DownColor_ = Color.RED;
    public final static int BackgroundColor_ = Color.WHITE;
    public final static int HighlightColor_ = Color.rgb(210, 233, 255);
    public final static String ShIndex = "sh000001";
    public final static String SzIndex = "sz399001";
    public final static String ChuangIndex = "sz399006";
    public final static String StockIdsKey_ = "StockIds";
    public final static int StockLargeTrade_ = 1000000;
    //--------------------------------------------------------------------------------------------------

    boolean isLogin = false;
    public FirebaseUser mfirebaseUser;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseReference;
    public String mUid;
    public SectionStatePagerAdapter mSectionStatePagerAdapter;
    static public ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------------------------------------------------------------------------------------------

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String idsStr = sharedPref.getString(StockIdsKey_, ShIndex + "," + SzIndex + "," + ChuangIndex);

        String[] ids = idsStr.split(",");
        StockIds_.clear();
        for (String id : ids) {
            StockIds_.add(id);
        }

        Log.d("mainActivity", "LIne 126");
        Timer timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStocks();
            }
        }, 0, 10000); // 10 seconds
        Log.d("mainActivity", "LIne 134");
        //--------------------------------------------------------------------------------------------------


        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);


        setupViewPager(mViewPager);


        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mfirebaseUser != null) {

            mUid = mfirebaseUser.getUid();

        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new com.smarthuman.drstock.StockFragment());


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // 加入含有search view的菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar, menu);
        // 获取SearchView对象
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView == null) {
            Log.e("SearchView", "Fail to get Search View.");
            return true;
        }
        searchView.setIconifiedByDefault(false); // 缺省值就是true，可能不专门进行设置，false和true的效果图如下，true的输入框更大

        // 获取搜索服务管理器
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // searchable activity的component name，由此系统可通过intent进行唤起
        ComponentName cn = new ComponentName(this, SearchableAcitivity.class);
        // 通过搜索管理器，从searchable activity中获取相关搜索信息，就是searchable的xml设置。如果返回null，表示该activity不存在，或者不是searchable
        SearchableInfo info = searchManager.getSearchableInfo(cn);
        if (info == null) {
            Log.e("SearchableInfo", "Fail to get search info.");
        }
        // 将searchable activity的搜索信息与search view关联
        searchView.setSearchableInfo(info);

        return true;
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_home:
                //fragment = new com.smarthuman.drstock.HomeFragment();
                setViewPager(0);
                break;

            case R.id.navigation_stock:
                //fragment = new com.smarthuman.drstock.StockFragment();
                setViewPager(1);
                break;

            case R.id.navigation_account:
                if (mAuth.getCurrentUser() != null) {
                    //fragment = new com.smarthuman.drstock.AccountFragment();
                    setViewPager(3);
                } else {
                    //fragment = new com.smarthuman.drstock.LoginFragment();
                    setViewPager(2);
                }
                break;

        }

        return true;
    }

    public void signIn(View v) {
        Log.d("main ", "called here signIn");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public void setupViewPager(ViewPager viewPager) {
        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new HomeFragment(), "HomeFragment");
        adapter.addFragment(new StockFragment(), "StockFragment");
        adapter.addFragment(new LoginFragment(), "LoginFragment");
        adapter.addFragment(new AccountFragment(), "AccountFragment");
        adapter.addFragment(new MyStockListFragment(), "StockListFragment");

        viewPager.setAdapter(adapter);
    }

    static public void setViewPager(int index) {

        Log.d("setViewPager", "called:" + index);
        mViewPager.setCurrentItem(index);
        if(index==0) {
            HomeFragment frag0 = (HomeFragment)mViewPager
                    .getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            frag0.updateThreeIndex();
        }
        else if(index==1) {
            StockFragment frag1 = (StockFragment)mViewPager
                    .getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            frag1.updateStockListView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: called");
    }

    // from GU's stock
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        saveStocksToPreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveStocksToPreferences();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void saveStocksToPreferences() {
        String ids = "";
        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(StockIdsKey_, ids);
        editor.commit();
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

    public TreeMap<String, MainActivity.Stock> sinaResponseToStocks(String response) {
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        TreeMap<String, MainActivity.Stock> stockMap = new TreeMap();
        for (String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            MainActivity.Stock stockNow = new MainActivity.Stock();
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

    public void querySinaStocks(String list) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stockMap = sinaResponseToStocks(response);
                        if(mViewPager.getCurrentItem()==0) {
                            HomeFragment frag0 = (HomeFragment)mViewPager
                                    .getAdapter()
                                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
                            frag0.updateThreeIndex();
                        }else if(mViewPager.getCurrentItem()==1) {
                            StockFragment frag1 = (StockFragment)mViewPager
                                    .getAdapter()
                                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
                            frag1.updateStockListView();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        System.out.println("------\n" + stringRequest + "\n------\n");
        queue.add(stringRequest);
    }

    public void refreshStocks() {
        String ids = "";
        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }
        querySinaStocks(ids);
    }

}