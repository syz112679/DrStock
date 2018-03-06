package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if(mfirebaseUser == null) {
            isLogin = false;
        } else {
            isLogin = true;
            mUid = mfirebaseUser.getUid();


        }

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new com.smarthuman.drstock.HomeFragment());
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

        Fragment fragment = null;

        switch(item.getItemId()){

            case R.id.navigation_home:
                fragment = new com.smarthuman.drstock.HomeFragment();
                break;

            case R.id.navigation_stock:
                fragment = new com.smarthuman.drstock.StockFragment();
                break;

            case R.id.navigation_account:
                if(isLogin)
                    fragment = new com.smarthuman.drstock.AccountFragment();
                else
                    fragment = new com.smarthuman.drstock.LoginFragment();
                break;

        }

        return loadFragment(fragment);
    }

    public void signIn(View v) {
        Log.d("main " , "called here signIn");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
