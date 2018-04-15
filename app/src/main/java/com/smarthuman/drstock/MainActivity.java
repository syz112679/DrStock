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
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    //--------------------------------------------------------------------------------------------------

    private final static String searchHistoryKey_ = "SearchHistory";
    public static HashSet<String> searchHistory  = new HashSet<>();

    private final static String StockIdsKey_ = "StockIds";
    public static HashSet<String> StockIds_ = new HashSet<>();        // [sz000001] [hk02318] [gb_lx]

    public static int UpColor_ = R.color.green;
    public static int DownColor_ = R.color.red;

    public static boolean enableMobileRefresh = true;
    public static int mobileRefreshTime = 15;
    public static boolean enableWifiRefresh = true;
    public static int wifiRefreshTime = 5;

    //--------------------------------------------------------------------------------------------------

    public static FirebaseUser mfirebaseUser;
    public static FirebaseAuth mAuth;
    public static DatabaseReference mDatabaseReference;
    public static String mUid;
    public static String mUserName = "";
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
        showBackwardView(R.string.setting, true);
//        setBackward(getResources().getDrawable(R.drawable.ic_settings_black_24dp), "");

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
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mfirebaseUser != null) {
            updateUserInfo();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onBackward(View backwardView) {
//        Log.d("each", "onBackward");
        Toast.makeText(this, "点击返回，可在此处调用finish()", Toast.LENGTH_LONG).show();
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

//    public void querySinaStocks(String list) {          // sz000001,hk02318,gb_lx
//
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "http://hq.sinajs.cn/list=" + list;
//        //http://hq.sinajs.cn/list=sh600000,sh600536
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
////                        System.out.println("***************************Response**************************");
////                        System.out.println(response);
////                        System.out.println("*****************************************************************");
//                        searchHistory = StockFragment.sinaResponseToStocks(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                });
//
//        queue.add(stringRequest);
//    }

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
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            updateUserInfo();
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

    public void clearSharedPref() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit().remove("userName");
        sharedPref.edit().remove("money");
        sharedPref.edit().remove("earning");
        sharedPref.edit().remove("balance");
        sharedPref.edit().remove("isSuperUser");
        Log.d("MainActivity", "clear shared pref called");

        sharedPref.edit().commit();
    }

//    public void updateDatabase() {
//        if(mfirebaseUser != null) {
//            Log.d("MainActivity", "UpdateDatabase called");
//            SharedPreferences SharedPref = getPreferences(Context.MODE_PRIVATE);
//            String username = SharedPref.getString("username","");
//            String email = SharedPref.getString("email","");
//
//            ArrayList<StockSnippet> myStocks = new ArrayList<>();
//            ArrayList<String> favorites = new ArrayList<>();
//            for (String id : StockIds_){
//                favorites.add(id);
//            }
//
//            UserInformation userInfo = new UserInformation(username, email);
//            userInfo.setFavorites(favorites);
//            userInfo.setMyStocks(myStocks);
//            userInfo.setBalance(Double.parseDouble(SharedPref.getString("balance", "0")));
//            userInfo.setMoney(Double.parseDouble(SharedPref.getString("money", "0")));
//            userInfo.setEarning(Double.parseDouble(SharedPref.getString("earning", "0")));
//            userInfo.setSuperUser(false);
//
//            mDatabaseReference.child("users").child(mUid).setValue(userInfo);
//
//        }
//    }

    static void updateUserInfo() {
        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final GenericTypeIndicator<ArrayList<String>> favorite_t = new GenericTypeIndicator<ArrayList<String>>() {};
        final GenericTypeIndicator<ArrayList<StockSnippet>> stockRecord_t = new GenericTypeIndicator<ArrayList<StockSnippet>>() {};

        mUid = mfirebaseUser.getUid();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
                mMoney = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);
                mEarning  = dataSnapshot.child("users").child(mUid).child("earning").getValue(double.class);
                mBalance = dataSnapshot.child("users").child(mUid).child("balance").getValue(double.class);

                mFavorites = dataSnapshot.child("users").child(mUid).child("favorites").getValue(favorite_t);
                mStockRecords = dataSnapshot.child("users").child(mUid).child("myStocks").getValue(stockRecord_t);
                Log.d("MainActivity", "single event listener called, mUserName=" + mUserName);
                System.out.println(mFavorites);
                System.out.println(mStockRecords);
                System.out.println("***********************************");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void clearLocalData() {
        mMoney = 0.0;
        mEarning = 0.0;
        mBalance = 0.0;
        mFavorites = new ArrayList<String>();
        mStockRecords = new ArrayList<StockSnippet>();
        mUserName = null;
    }

}