package com.smarthuman.drstock;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

public class MainActivity extends TitleActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //--------------------------------------------------------------------------------------------------

    public final static String searchHistoryKey_ = "SearchHistory";
    public static HashSet<String> searchHistory  = new HashSet<>();
    private final static String StockIdsKey_ = "StockIds";
    private static HashSet<String> StockIds_ = new HashSet<>();        // [sz000001] [hk02318] [gb_lx]
    public static TreeMap<String, Stock> stockMap_ = new TreeMap<>();   // inputId -> Stock

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static final int Green = Color.rgb(0, 153, 102);
    public static final int Red = Color.rgb(255, 102, 102);
    public static int UpColor_ = Green;
    public static int DownColor_ = Red;

    public static boolean enableMobileRefresh = true;
    public static int mobileRefreshTime = 15;
    public static boolean enableWifiRefresh = true;
    public static int wifiRefreshTime = 15;
    private Context context;

    private static int count = 0;
    private static final int minPeriod = 2;
    public static boolean requireRefresh = false;

    StockIndex stockIndex = new StockIndex();
    //--------------------------------------------------------------------------------------------------

    public static FirebaseUser mfirebaseUser;
    public static FirebaseAuth mAuth;
    public static DatabaseReference mDatabaseReference;

    // --------- user info -------
    public static String mUid;
    public static String mUserName = "", mEmail = "";
    public static double mMoney=0.0, mEarning=0.0, mBalance=0.0;
    public static ArrayList<StockSnippet> mStockRecords = new ArrayList<StockSnippet>();
    public static ArrayList<String> mFavorites = new ArrayList<String>();

    public SectionStatePagerAdapter mSectionStatePagerAdapter;
    static public ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TEST
//        System.out.println("------------TEST:");
//        String s_ = "abcd";
//        String[] ss_ = s_.split("_");
//        for (String s : ss_) {
//            System.out.println(s);
//        }
//        System.out.println("------------");
        // END

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.mainActivity);
        showBackward(getDrawable(R.drawable.ic_setup), true);
//        setBackward(getResources().getDrawable(R.drawable.ic_settings_black_24dp), "");
        context = getApplicationContext();

        Log.d("mainActivity", "LIne 126");
        Timer timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                count += minPeriod;
                controlledRefreshStocks();
            }
        }, 0, minPeriod * 1000); // 10 seconds
        Log.d("mainActivity", "LIne 134");

        //--------------------------------------------------------------------------------------------------

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String idsStr = sharedPref.getString(StockIdsKey_, "");
        String histories = sharedPref.getString(searchHistoryKey_, "");

        String[] ids = idsStr.split(",");
        StockIds_.clear();
        for (String id : ids) {
            if (id.equals(null))
                continue;
            StockIds_.add(id);
        }
        System.out.println("----------StockIds\n" + StockIds_ + "\n----------");
        // update the searchHistory
        ids = histories.split(",");
        searchHistory.clear();
        for (String id : ids) {
            if (id.equals(null))
                continue;
            searchHistory.add(id);
        }


        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);

        setupViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mfirebaseUser != null) {
            System.out.println("mfirebaseUser: " + mfirebaseUser);
            updateUserInfo();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    public static HashSet<String> getStockIds_() {
        return StockIds_;
    }

    public static void addStockIds_(String stockIds_) {
        if (stockIds_ == null)
            return;
        StockIds_.add(stockIds_);
        mFavorites=new ArrayList<>(StockIds_);
        System.out.println("addStockIds_:" + StockIds_ + ";");
    }

    public static void removeStockIds_(String stockIds_) {
        if (stockIds_ == null)
            return;
        StockIds_.remove(stockIds_);
        mFavorites=new ArrayList<>(StockIds_);
        System.out.println("removeStockIds_:" + StockIds_ + ";");
    }

    public static boolean checkStatus(String stockIds_){
        for (String id : StockIds_) {
            if(stockIds_.compareTo(id) == 0)
                return true;
        }
        return false;
    }


//    @Override
//    public void setTitle(int titleId) {
//        TextView temp = (TextView) findViewById(R.id.text_title);
//        temp.setText(titleId);
//    }
//
//    //设置标题内容
//    @Override
//    public void setTitle(CharSequence title) {
//        TextView temp = (TextView) findViewById(R.id.text_title);
//        temp.setText(title);
//    }



    @Override
    protected void onBackward(View backwardView) {
//        Log.d("each", "onBackward");
//        Toast.makeText(this, "点击返回", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SettingActivity.class));
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


    // Samuel GU START

    public TreeMap<String, Stock> sinaResponseToStocks(String response) {
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");
        String test = stocks[0].split("=")[1];
        if (test.equals("\"\";\n") || test.equals("\"FAILED\";\n")) {
            return null;
        }

        TreeMap<String, Stock> stockMap = new TreeMap<>();
        String indexResponse = "";
        for (int i = 0; i < stocks.length; i++) {
            if (i < StockIndex.totalNum) {
                indexResponse += stocks[i] + ";";
                continue;
            }

            //判断是否为：
            //var hq_str_sz0=""
            System.out.println("each Stock: " + stocks[i]);
            Stock stockNow = new Stock(stocks[i]);
            stockMap.put(stockNow.id_, stockNow);           // lx -> Stock

        }
        stockIndex.updateIdex(indexResponse);

        stockMap_ = stockMap;

        System.out.println("-------------stockMap:\n" + stockMap + "----------");

        return stockMap;
    }

    public void querySinaStocks(String list) {          // sz000001,hk02318,gb_lx
//        System.out.println("--------list: \n" + list + "\n------");

        // Instantiate the RequestQueue.
//        System.out.println(this.this);
//        Log.d("StockFragment", this);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        System.out.println("--------url: \n" + url + "\n------");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("***************************Response**************************");
                        System.out.println(response);
                        System.out.println("*****************************************************************");
                        updateStockListView(sinaResponseToStocks(response));
                        //updateIndexView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        queue.add(stringRequest);
    }

    public String getStockRecords() {
        String bought = "", enquiry = "";
        for (StockSnippet ss : mStockRecords) {
            enquiry = StockFragment.input2enqury(ss.getId());
            System.out.println("ss.getId():" + enquiry);
            bought += enquiry + ",";
        }
        return bought;
    }

    public void refreshStocks() {
        count = 0;
        requireRefresh = false;

        System.out.println("--------refreshStocks: \n" + StockIds_ + "\n------");

//        if (StockIds_.size() == 0)
//            return;

        String ids = stockIndex.enquiryId;
        StockIds_ = new HashSet<>(mFavorites);
        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }

        ids += getStockRecords();
        System.out.println("ids: " + ids);

        querySinaStocks(ids);
    }

    public void controlledRefreshStocks() {
        if (requireRefresh) {
            refreshStocks();
            return;
        }

        switch (NetWorkUtils.getAPNType(context)) {
            case NetWorkUtils.networkNo:
                break;
            case NetWorkUtils.networkWifi:
                if (enableWifiRefresh && count >= wifiRefreshTime) {
                    refreshStocks();
                }
                break;
            case NetWorkUtils.network2G:
            case NetWorkUtils.network3G:
            case NetWorkUtils.network4G:
                if (enableMobileRefresh && count >= mobileRefreshTime) {
                    refreshStocks();
                }
                break;
        }
    }

    public void updateIndexView() {
        TextView sse = findViewById(R.id.stock_sh_index);
        sse.setText(StockIndex.indexTreeMap.get("s_sh000001").value);
        TextView szse = findViewById(R.id.stock_sz_index);
        szse.setText(StockIndex.indexTreeMap.get("s_sz399001").value);
        TextView gei = findViewById(R.id.stock_chuang_index);
        gei.setText(StockIndex.indexTreeMap.get("s_sz399006").value);
        TextView hsi = findViewById(R.id.stock_hsi_index);
        hsi.setText(StockIndex.indexTreeMap.get("int_hangseng").value);
        TextView dji = findViewById(R.id.stock_djia_index);
        dji.setText(StockIndex.indexTreeMap.get("int_dji").value);
        TextView nasdaq = findViewById(R.id.stock_nsdk_index);
        nasdaq.setText(StockIndex.indexTreeMap.get("int_nasdaq").value);
    }

    protected void updateStockListView(TreeMap<String, Stock> stockMap) {

        // Table
        TableLayout table = findViewById(R.id.stock_table);
        if(table==null)
            return;
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        table.removeAllViews();

        // Title
        TableRow rowTitle = new TableRow(this);

        TextView nameTitle = new TextView(this);
        nameTitle.setText(getResources().getString(R.string.stock_name_title));
        rowTitle.addView(nameTitle);

        TextView nowTitle = new TextView(this);
        nowTitle.setGravity(Gravity.RIGHT);
        nowTitle.setText(getResources().getString(R.string.stock_now_title));
        rowTitle.addView(nowTitle);

        TextView percentTitle = new TextView(this);
        percentTitle.setGravity(Gravity.RIGHT);
        percentTitle.setText(getResources().getString(R.string.stock_increase_percent_title));
        rowTitle.addView(percentTitle);

        TextView increaseTitle = new TextView(this);
        increaseTitle.setGravity(Gravity.RIGHT);
        increaseTitle.setText(getResources().getString(R.string.stock_increase_title));
        rowTitle.addView(increaseTitle);

        table.addView(rowTitle);

        //

        if (stockMap == null) {
            stockMap = stockMap_;
        }
        Collection<Stock> stocks = stockMap.values();

        for (Stock stock : stocks) {
            boolean isIn = false;
            for (String s : StockIds_) {
                System.out.println("s: " + s + "; id_:" + stock.id_);
                if (s.equals(StockFragment.input2enqury(stock.id_))) {
                    isIn = true;
                    break;
                }
            }
            System.out.println("isIn: " + isIn);
            if (!isIn) {
                continue;
            }

//            System.out.println("Stock stock");
//            if (stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)) {
//                Float dNow = Float.parseFloat(stock.now_);
//                Float dYesterday = Float.parseFloat(stock.yesterday_);
//                Float dIncrease = dNow - dYesterday;
//                Float dPercent = dIncrease / dYesterday * 100;
//                String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);
//
//                int indexId;
//                int changeId;
//                if (stock.id_.equals(ShIndex)) {
//                    indexId = R.id.stock_sh_index;
//                    changeId = R.id.stock_sh_change;
//                } else if (stock.id_.equals(SzIndex)) {
//                    indexId = R.id.stock_sz_index;
//                    changeId = R.id.stock_sz_change;
//                } else {
//                    indexId = R.id.stock_chuang_index;
//                    changeId = R.id.stock_chuang_change;
//                }
//
//                TextView indexText = (TextView) v.findViewById(indexId);
//                indexText.setText(stock.now_);
//                int color = Color.BLACK;
//                //System.out.println("-----|" + dIncrease + "|------");
//                if (dIncrease > 0) {
//                    color = UpColor_;
//                } else if (dIncrease < 0) {
//                    color = DownColor_;
//                }
//                //System.out.println("-----|" + color + "|------");
//                indexText.setTextColor(color);
//
//                TextView changeText = (TextView) v.findViewById(changeId);
//                changeText.setText(change);
//                changeText.setTextColor(color);
//
//                continue;
//            }

            TableRow row = new TableRow(this);
            row.setMinimumHeight(200); //////////////////////////////////////////////
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout nameId = new LinearLayout(this);
            nameId.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(this);
            name.setText(stock.name_);
            nameId.addView(name);

            TextView id = new TextView(this);
            id.setTextSize(15);
            String id_market = stock.id_ + "." + stock.marketId_;
            id.setText(id_market);
            nameId.addView(id);

            row.addView(nameId);

            TextView now = new TextView(this);
            now.setGravity(Gravity.RIGHT);
            now.setText(stock.getCurrentPrice_());
            row.addView(now);

            TextView percent = new TextView(this);
            percent.setGravity(Gravity.RIGHT);
            TextView increaseValue = new TextView(this);
            increaseValue.setGravity(Gravity.RIGHT);

            percent.setText(stock.getChangePercent() + "%");
            increaseValue.setText(stock.getPriceChange());
            int color = Color.BLACK;
            if (stock.isRising()) {
                color = UpColor_;
            } else {
                color = DownColor_;
            }

            now.setTextColor(color);
            percent.setTextColor(color);
            increaseValue.setTextColor(color);

            row.addView(percent);
            row.addView(increaseValue);
            row.setOnClickListener(this);

            table.addView(row);
            System.out.println("addRow!!!");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v instanceof TableRow) {
            ViewGroup group = (ViewGroup) v;
            ViewGroup nameId = (ViewGroup) group.getChildAt(0);
            TextView idText = (TextView) nameId.getChildAt(1);
            //System.out.println("-----\n" + idText.getText().toString() + "\n-------");

            String stockID_Market = idText.getText().toString();

            System.out.println(stockID_Market);

            Intent intent = new Intent(this, EachStockActivity.class);
            intent.putExtra(EXTRA_MESSAGE, stockID_Market);
            startActivity(intent);

            System.out.println("-----\n" + idText.getText().toString() + "\n-------");
        }
    }

    // Samuel GU END


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_home:
                //fragment = new com.smarthuman.drstock.HomeFragment();
                setTitle(R.string.title_home);
                setViewPager(0);
                break;

            case R.id.navigation_stock:
                //fragment = new com.smarthuman.drstock.StockFragment();
                setTitle(R.string.title_stock);
                setViewPager(1);
                break;

            case R.id.navigation_account:
                if (mAuth.getCurrentUser() != null) {
                    //fragment = new com.smarthuman.drstock.AccountFragment();
                    setTitle(R.string.title_account);
                    setViewPager(3);
                } else {
                    //fragment = new com.smarthuman.drstock.LoginFragment();
                    setTitle(R.string.title_login);
                    setViewPager(2);
                }
                break;

        }

        return true;
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

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: called");

        refreshStocks();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null)
//            updateUserInfo();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null)
//            getInfoFromDatabase();
        if(FirebaseAuth.getInstance().getCurrentUser() != null && mUserName!=null && mUserName.length()>0 ){
            updateUserInfo();
            updateDatabase();
        }
    }

    // from GU's stock
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
        //saveStocksToPreferences();
        if(FirebaseAuth.getInstance().getCurrentUser() != null && mUserName!=null && mUserName.length()>0 ) {
            updateDatabase();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        System.out.println("onSaveInstanceState;");
        //saveStocksToPreferences();
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void saveStocksToPreferences() {
        String ids = "";
        for (String id : StockIds_){
            ids += id;
            ids += ",";
        }

        String histories = "";
        for (String s:searchHistory) {
            histories += s + ",";
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(StockIdsKey_, ids);
        editor.putString(searchHistoryKey_, histories);
        editor.apply();
    }


    public void updateDatabase() { // upload data to database
        if(mfirebaseUser != null) {
            Log.d("MainActivity", "UpdateDatabase called" + mUserName + " " + mEmail);
            mFavorites = new ArrayList<String>(StockIds_);

            if (mFavorites.isEmpty()) {
                mFavorites.add("placeholder");
            }

            if(mStockRecords.isEmpty()) {
                mStockRecords.add(new StockSnippet());
            }

            UserInformation userInfo = new UserInformation(mUserName, mEmail);
            userInfo.setFavorites(mFavorites);
            userInfo.setMyStocks(mStockRecords);
            userInfo.setBalance(mBalance);
            userInfo.setMoney(mMoney);
            userInfo.setEarning(mEarning);
            userInfo.setSuperUser(false);

            mDatabaseReference.child("users").child(mUid).setValue(userInfo);

        }
    }

    static void updateUserInfo() {  // download data from Database
        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final GenericTypeIndicator<ArrayList<String>> favorite_t = new GenericTypeIndicator<ArrayList<String>>() {};
        final GenericTypeIndicator<ArrayList<StockSnippet>> stockRecord_t = new GenericTypeIndicator<ArrayList<StockSnippet>>() {};


        mUid = mfirebaseUser.getUid();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
                mEmail = dataSnapshot.child("users").child(mUid).child("email").getValue(String.class);
                mMoney = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);
                mEarning  = dataSnapshot.child("users").child(mUid).child("earning").getValue(double.class);
                mBalance = dataSnapshot.child("users").child(mUid).child("balance").getValue(double.class);

                mFavorites = dataSnapshot.child("users").child(mUid).child("favorites").getValue(favorite_t);
                mStockRecords = dataSnapshot.child("users").child(mUid).child("myStocks").getValue(stockRecord_t);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!mFavorites.isEmpty() && mFavorites.get(0).equals("placeholder")) {
            mFavorites.remove(0);
        }
        if (!mStockRecords.isEmpty() && mStockRecords.get(0).getId().equals("00000")) {
            mStockRecords.remove(0);
        }

        System.out.println("mFavorites: " + mFavorites);
        System.out.println("mStockRecords: " + mStockRecords);

        if(mFavorites!=null)
            StockIds_= new HashSet<>(mFavorites);

    }

    void clearLocalData() {
        mMoney = 0.0;
        mEarning = 0.0;
        mBalance = 0.0;
        mFavorites.clear();
        mStockRecords.clear();
        StockIds_.clear();
        mUserName = null;
    }

}