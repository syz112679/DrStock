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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

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

        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);


        setupViewPager(mViewPager);


        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if(mfirebaseUser != null){

            mUid = mfirebaseUser.getUid();

        }

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new com.smarthuman.drstock.HomeFragment());
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


    private boolean loadFragment (Fragment fragment){

        if(fragment != null){

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

        switch(item.getItemId()){

            case R.id.navigation_home:
                //fragment = new com.smarthuman.drstock.HomeFragment();
                setViewPager(0);
                break;

            case R.id.navigation_stock:
                //fragment = new com.smarthuman.drstock.StockFragment();
                setViewPager(1);
                break;

            case R.id.navigation_account:
                if(mAuth.getCurrentUser()!=null) {
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
        Log.d("main " , "called here signIn");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: called");
    }
}
