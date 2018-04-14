package com.smarthuman.drstock;

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

import android.content.SharedPreferences;
import android.graphics.Color;
import java.util.HashSet;

/*******************************************************************/

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    //--------------------------------------------------------------------------------------------------

    private final static String searchHistoryKey_ = "SearchHistory";
    public static HashSet<String> searchHistory  = new HashSet<>();

    private final static String StockIdsKey_ = "StockIds";
    public static HashSet<String> StockIds_ = new HashSet<>();        // [sz000001] [hk02318] [gb_lx]

    public final static int UpColor_ = Color.GREEN;
    public final static int DownColor_ = Color.RED;

    //--------------------------------------------------------------------------------------------------

    boolean isLogin = false;
    public FirebaseUser mfirebaseUser;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseReference;
    public String mUid;
    public SectionStatePagerAdapter mSectionStatePagerAdapter;
    static public ViewPager mViewPager;
    public SharedPreferences mSharedPref;

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

        //--------------------------------------------------------------------------------------------------

        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        String idsStr = mSharedPref.getString(StockIdsKey_, "");
        String histories = mSharedPref.getString(searchHistoryKey_, "");

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

            mUid = mfirebaseUser.getUid();

        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


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
        editor.commit();
    }

}