package com.smarthuman.drstock;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/****************************************************/
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.Collection;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/*******************************************************************/


import android.content.IntentFilter;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

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
    public static boolean isPaused = false;

    public static StockIndex stockIndex = new StockIndex();
    public static ExchangeRate exchangeRate = new ExchangeRate();
    public static String[][] exchangeRate_ = new String[exchangeRate.currencyCount][exchangeRate.currencyCount];

    public static View fragment_home;
    public static View fragment_stock;
    public static View fragment_login;
    public static View fragment_account;
    public static View fragment_mystocklist;

    public static boolean inLoop = false;
    public static boolean initBeanCalled = false;
    public static boolean stockInit = false;


    //--------------------------------------------------------------------------------------------------

    public static FirebaseUser mfirebaseUser;
    public static FirebaseAuth mAuth;
    public static DatabaseReference mDatabaseReference;
    //Google
    public static GoogleApiClient mGoogleApiClient;
    public static boolean isGoogle = false;
    public static boolean isFacebook = true;

    // --------- user info -------
    public static String mUid;
    public static String mUserName = "", mEmail = "";
    public static double mMoney=0.0, mEarning=0.0, mBalance=0.0;
    public static ArrayList<StockSnippet> mStockRecords = new ArrayList<StockSnippet>();
    public static ArrayList<InvestmentPlan> mPlans = new ArrayList<InvestmentPlan>();
    public static ArrayList<String> mFavorites = new ArrayList<String>();

    public SectionStatePagerAdapter mSectionStatePagerAdapter;
    static public ViewPager mViewPager;


    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


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


        initialize();
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
        if (mAuth.getCurrentUser() != null || (AccessToken.getCurrentAccessToken() != null)) {
            updateDatabase();
        }
    }

    public void initialize() {
        setTitle(R.string.mainActivity);
        showBackward(getDrawable(R.drawable.ic_setup), true);
        context = getApplicationContext();

        System.out.println("fragment_home = " + fragment_home);

        isPaused = false;
        Log.d("mainActivity", "LIne 126");
        Timer timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                count += minPeriod;
                controlledRefreshStocks();
                System.out.println("hanziComplete = " + hanziComplete + "; inLoop = " + inLoop + ";");
                if (stockInit) {
                    if (!hanziComplete && !inLoop) {
                        initString();
                    }
                }
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

        mViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });



        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        FacebookSignOut();
        GoogleSignOut();
        mfirebaseUser = mAuth.getCurrentUser();
        if(mfirebaseUser!=null)
            mUid = mfirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setupViewPager(mViewPager);

        if (mfirebaseUser != null) {
            System.out.println("mfirebaseUser: " + mfirebaseUser);
            updateUserInfo();
        }
//qiqi
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);



        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        registerMessageReceiver();





//        setContentView(R.layout.activity_main);
//        setTitle(R.string.app_name);
//        EventBus.getDefault().register(this);



//        fragment_account = findViewById(R.id.fragment_account);
//        fragment_home = findViewById(R.id.fragment_home);
//        fragment_login = findViewById(R.id.fragment_login);
//        fragment_mystocklist = findViewById(R.id.fragment_mystocklist);
//        fragment_stock = findViewById(R.id.fragment_stock);
//

//        initViews();

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
        inLoop = false;
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");
        String test = stocks[0].split("=")[1];
        if (test.equals("\"\";\n") || test.equals("\"FAILED\";\n")) {
            return null;
        }

        TreeMap<String, Stock> stockMap = new TreeMap<>();
        String indexResponse = "", exchangeRateResponse = "";
        for (int i = 0; i < stocks.length; i++) {
            if (i < StockIndex.totalNum) {
                indexResponse += stocks[i] + ";";
                continue;
            }
            if (i < StockIndex.totalNum + 3) {
                exchangeRateResponse += stocks[i] + ";";
                continue;
            }

            //判断是否为：
            //var hq_str_sz0=""
            System.out.println("each Stock: " + stocks[i]);
            Stock stockNow = new Stock(stocks[i]);
            if (stockNow.id_ == null) {
                continue;
            }
            stockMap.put(stockNow.id_, stockNow);           // lx -> Stock

        }
        stockIndex.updateIdex(indexResponse);
        updateIndexView();

        exchangeRate.updateExchangeRate(exchangeRateResponse);
        exchangeRate_ = exchangeRate.getExchangeRate_();
//        if (!stockInit) {
            updateFlipper();
//        }

        stockMap_ = stockMap;

        System.out.println("-------------stockMap:\n" + stockMap + "----------");

        return stockMap;
    }

    public void updateFlipper() {
//        TextView rmb = (findViewById(R.id.fragment_home)).findViewById(R.id.rmb_value);
//        TextView usd = fragment_home.findViewById(R.id.usd_value);
//        TextView gbp = fragment_home.findViewById(R.id.gbp_value);
//
//        System.out.println("rmb1: " + rmb);

//        System.out.println("---stockInit: " + stockInit);
//        if (stockInit) {
//            return;
//        }

        TextView rmb = findViewById(R.id.rmb_value);
        TextView usd = findViewById(R.id.usd_value);
        TextView gbp = findViewById(R.id.gbp_value);

        System.out.println("rmb2: " + rmb);
        if (rmb == null)
            return;

        rmb.setText(exchangeRate_[exchangeRate.RMB][exchangeRate.HKD]);
        usd.setText(exchangeRate_[exchangeRate.USD][exchangeRate.HKD]);
        gbp.setText(exchangeRate_[exchangeRate.GBP][exchangeRate.HKD]);

//        stockInit = true;
//        System.out.println("---stockInit <- " + stockInit);
    }

    public void querySinaStocks(String list) {          // sz000001,hk02318,gb_lx
//        System.out.println("--------list: \n" + list + "\n------");

        // Instantiate the RequestQueue.
//        System.out.println(this.this);
//        Log.d("StockFragment", this);
        if (inLoop) {
            System.out.println("stockinloop: " + inLoop);
            return;
        }
        inLoop = true;


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        System.out.println("--------url: \n" + url + "\n------");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Main***************************Response**************************");
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
        // check every time to remove 00000
        if(!mStockRecords.isEmpty() && mStockRecords.get(0).getId().equals("00000")) {
            mStockRecords.remove(0);
        }
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

        String ids = stockIndex.enquiryId; // 1.
        ids += exchangeRate.enquiryId; // 2.
        // check every time to remove placeholder
        if(!mFavorites.isEmpty() && mFavorites.get(0).equals("placeholder")) {

            mFavorites.remove(0);
        }

        if (!mFavorites.isEmpty())
            System.out.println("mFavorites.get(0): "+mFavorites.get(0));

        StockIds_ = new HashSet<>(mFavorites);

        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }

        ids += getStockRecords(); // 4.

        System.out.println("ids: " + ids);

        querySinaStocks(ids);
    }

    public void controlledRefreshStocks() {
        if (requireRefresh) {
            refreshStocks();
            return;
        }

//        if (isPaused)
//            return;

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

    private static final String tag="QuickSearchActivity";
    public static AutoCompleteTextView search;
//    private SlidingDrawer mDrawer;

    public SearchAdapter adapter=null;//
    //需要读取
    private final int maxLength = 30000;
    public String[] hanzi = new String[maxLength];

    public void initString(){
        if (!responseComplete) {
            nowapiGetList();
            return;
        }

        if (hanziComplete) {
            return;
        }

        if (initBeanCalled) {
            return;
        }

        initBeanCalled = true;
        initBean();
        initHanzi();

        System.out.println("--hanzi[" + (hanziLength - 1) + "] = " + hanzi[hanziLength - 1]);

        if (!hanziComplete) {
            handler.sendEmptyMessage(0);
        }
//        initAdapter();
    }

    public void initAdapter() {
        //更新UI操作
        search = (AutoCompleteTextView) findViewById(R.id.editText_stockId);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub

//                Log.d(tag, "arg0:"+arg0);
//                Log.d(tag, "arg1:"+arg1);
//                Log.d(tag, "onItemClick:"+position);
//                Log.d(tag, "id:"+id);

//                System.out.println("---" + arg1.getId());
//                search.setText("");
            }

        });

        search.setThreshold(1);

        adapter = new SearchAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, hanzi, SearchAdapter.ALL);//速度优先
        System.out.println("***adapterComplete***");

        search.setAdapter(adapter);//

        hanziComplete = true;

        System.out.println("***haziComplete***");

        Toast.makeText(this, getString(R.string.toast_initi_complete), Toast.LENGTH_LONG).show();
//        mDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
//        stockInit = false;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    System.out.println("------handler: hanziComplete = " + hanziComplete + ";");
                    if (!hanziComplete) {
                        initAdapter();
                    }
                    break;
                default:break;
            }
        };
    };

    private void initBean() {

        for (int i = 0; i < 3; i++) {
//            System.out.println("stockMarket: " + i);
            fullList[i] = new Gson().fromJson(rawResponse[i], StockFullListBean.class);
        }

    }

    public static int hanziLength = 0;
    private void initHanzi() {
        hanziLength = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println("stockMarket: " + i);
            List<StockFullListBean.Result.Lists> lists = fullList[i].getResult().getLists();
            int length = Integer.parseInt(fullList[i].getResult().getTotline());
            for (int k = 0; k < length; k++) {
                hanzi[hanziLength] = lists.get(k).getSname() + " " + lists.get(k).getSymbol();
                hanziLength++;
            }
        }

        String[] tempHanzi = new String[hanziLength];
        for (int i = 0; i < hanziLength; i++) {
            tempHanzi[i] = hanzi[i];
        }

        hanzi = tempHanzi;


    }

    private static String app = "finance.stock_list";
    private static final String[] categories = {"hs", "hk", "us"};
    private static String category = "";
    private static String appkey = "33123";
    private static String sign = "72e5b6c5244d4624f0e268dbcee31be0";
    private static String format = "json";
    private static String[] rawResponse = new String[3];
    private StockFullListBean[] fullList = new StockFullListBean[3];
    private static int iteration = 0;
    private static boolean responseComplete = false;
    private static boolean hanziComplete = false;


    private void nowapiGetList() {
        if (inLoop) {
            System.out.println("newapiinloop: " + inLoop);
            return;
        }
        inLoop = true;

        RequestQueue queue = Volley.newRequestQueue(this);

        category = categories[iteration];

        String url = "http://api.k780.com/?app=" + app + "&category=" + category + "&appkey=" + appkey + "&sign=" + sign + "&format=" + format;

        System.out.println("--------url2: \n" + url + "\n------");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Stock***************************Response**************************");
                        System.out.println(response);
                        System.out.println("*****************************************************************");
                        rawResponse[iteration] = response;
//                        fullList[iteration] = new Gson().fromJson(response, StockFullListBean.class);
                        iteration++;
                        inLoop = false;
                        System.out.println("iteration = " + iteration);
                        if (iteration >= 3) {
                            responseComplete = true;
                        }
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

    public void updateIndexView() {
        StockIndex.Index index;
        LinearLayout eachLinearLayout;

        index = StockIndex.indexTreeMap.get("s_sh000001");
        eachLinearLayout = findViewById(R.id.sh_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent + "%");
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);

        index = StockIndex.indexTreeMap.get("s_sz399001");
        eachLinearLayout = findViewById(R.id.sz_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent + "%");
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);

        index = StockIndex.indexTreeMap.get("s_sz399006");
        eachLinearLayout = findViewById(R.id.chuang_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent + "%");
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);

        index = StockIndex.indexTreeMap.get("int_hangseng");
        eachLinearLayout = findViewById(R.id.hsi_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent);
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);

        index = StockIndex.indexTreeMap.get("int_dji");
        eachLinearLayout = findViewById(R.id.djia_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent + "%");
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);

        index = StockIndex.indexTreeMap.get("int_nasdaq");
        eachLinearLayout = findViewById(R.id.nsdk_index);
        if (index == null || eachLinearLayout == null)
            return;
        ((TextView)eachLinearLayout.getChildAt(1)).setText(index.value);
        ((TextView)eachLinearLayout.getChildAt(2)).setText(index.percent + "%");
        for (int i = 1; i <= 2; i++)
            ((TextView)eachLinearLayout.getChildAt(i)).setTextColor((index.isRising) ? MainActivity.UpColor_:MainActivity.DownColor_);
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

        stockInit = true;
        System.out.println("--stockInit: " + stockInit);
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
                if (mAuth.getCurrentUser() != null || GoogleSignIn.getLastSignedInAccount(this)!=null || (AccessToken.getCurrentAccessToken() != null)) {
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
        isPaused = false;

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

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        switch(mViewPager.getCurrentItem()){
            case 0:
                setTitle(R.string.title_home);
                break;
            case 1:
                setTitle(R.string.title_stock);
                break;
            case 2:
                setTitle(R.string.title_login);
                break;
            case 3:
                setTitle(R.string.title_account);
                break;
            case 4:
                setTitle(R.string.title_my_stock);
                break;
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


    static public void updateDatabase() { // upload data to database
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

        //TODO: need to change this
        if(InvestmentPlan.planTreeMap != null ) {
            mPlans = new ArrayList<InvestmentPlan>();
            for (Map.Entry<String, InvestmentPlan> entry : InvestmentPlan.planTreeMap.entrySet()) {
                mPlans.add(entry.getValue());
            }
        }
    }

    static public void planTreeMap2mPlan () {
        if(InvestmentPlan.planTreeMap != null ) {
            mPlans = new ArrayList<InvestmentPlan>();
            for (Map.Entry<String, InvestmentPlan> entry : InvestmentPlan.planTreeMap.entrySet()) {
                mPlans.add(entry.getValue());
            }
        }
    }

    void clearLocalData() {
        mMoney = 0.0;
        mEarning = 0.0;
        mBalance = 0.0;
        mFavorites.clear();
        mStockRecords.clear();
        StockIds_.clear();
        mUserName = null;

        //TODO: handle InvestmentPlan.planTreeMap here
        mPlans.clear();
        InvestmentPlan.planTreeMap.clear();
    }

    public void FacebookSignOut() {

        if(isFacebook) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
        }
    }

    public void GoogleSignOut() {
        if(mGoogleApiClient!=null && isGoogle) {
            // Google sign out
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {

                        }
                    });
        }
    }




    //for receive customer msg from jpush server

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }



}