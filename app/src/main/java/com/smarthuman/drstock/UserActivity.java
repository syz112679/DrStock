package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shiyuzhou on 4/3/2018.
 */

public class UserActivity  extends AppCompatActivity {

    private com.smarthuman.drstock.UserInformation mUserInformation;
    private FirebaseUser mfirebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private String mUid;
    private TextView mUserName;
    private TextView mMoney;
    private ArrayList<String> mMyStock;
    private ArrayList<String> mFavorites;


    //sign in, sign out button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_account);

        mAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mAuth.getCurrentUser();
        if(mfirebaseUser == null) {
            findViewById(R.id.add_money_btn).setVisibility(View.INVISIBLE);
            findViewById(R.id.display_my_stock).setVisibility(View.INVISIBLE);
            findViewById(R.id.display_favorite).setVisibility(View.INVISIBLE);
            findViewById(R.id.user_sign_out).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_user_register).setVisibility(View.VISIBLE);
            findViewById(R.id.no_user_sign_in).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.add_money_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.display_my_stock).setVisibility(View.VISIBLE);
            findViewById(R.id.display_favorite).setVisibility(View.VISIBLE);
            findViewById(R.id.user_sign_out).setVisibility(View.VISIBLE);
            findViewById(R.id.no_user_register).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_user_sign_in).setVisibility(View.INVISIBLE);

            mUserName = (TextView) findViewById(R.id.display_username);
            mMoney = (TextView) findViewById(R.id.display_money);


            mUid = mfirebaseUser.getUid();

            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
                    double money = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);

                    mUserName.setText(userName);
                    mMoney.setText(String.valueOf(money));
                    mFavorites = (ArrayList<String>) dataSnapshot.child("users").child(mUid).child("favorites").getValue();
                    mMyStock = (ArrayList<String>) dataSnapshot.child("users").child(mUid).child("myStocks").getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addMoney(View v) {
        mDatabaseReference.child("users").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<String,Object>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(),snapshot.getValue());
                }
                double money = Double.parseDouble(mMoney.getText().toString());
                postValues.put("money", money+1000);
                mDatabaseReference.child("users").child(mUid).updateChildren(postValues);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void myStock(View v) {
        Toast.makeText(this, "To My Stocks...", Toast.LENGTH_SHORT).show();
    }

    public void favorites(View v) {

//        mDatabaseReference.child("users").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Object> postValues = new HashMap<String,Object>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    postValues.put(snapshot.getKey(),snapshot.getValue());
//                }
//
//                mDatabaseReference.child("users").child(mUid).updateChildren(postValues);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        Toast.makeText(this, "To My Favorites...", Toast.LENGTH_SHORT).show();
       // Log.d("Firebase", "get data:" + mFavorites );
    }

    public void signIn(View v){
        Intent intent = new Intent(UserActivity.this, com.smarthuman.drstock.LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public void signOut(View v) {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out ??
        findViewById(R.id.add_money_btn).setVisibility(View.INVISIBLE);
        findViewById(R.id.display_my_stock).setVisibility(View.INVISIBLE);
        findViewById(R.id.display_favorite).setVisibility(View.INVISIBLE);
        findViewById(R.id.user_sign_out).setVisibility(View.INVISIBLE);
        findViewById(R.id.no_user_register).setVisibility(View.VISIBLE);
        findViewById(R.id.no_user_sign_in).setVisibility(View.VISIBLE);

        mUserName.setText("Please sign in first");
        mMoney.setText("0.0");

    }

    public void register(View v) {
        Intent intent = new Intent(UserActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

//    public void toChatRoom(View v) {
//        Intent intent = new Intent(UserActivity.this, MainChatActivity.class);
//        finish();
//        startActivity(intent);
//    }
}
