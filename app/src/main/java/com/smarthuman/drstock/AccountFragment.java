package com.smarthuman.drstock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingle on 04/03/2018.
 */

public class AccountFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    String mUid;
    public static TextView mUserName;
    private TextView mMoney, mTotalAsset; //money is balance
    private TextView mEarning;
    private Button mSignOutbtn;
    private Button mMyStockbtn;
    private Button mAdd1000;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        mUserName = view.findViewById(R.id.display_username);
        mMoney = view.findViewById(R.id.display_money);
        mTotalAsset = view.findViewById(R.id.total_assets);
        mEarning = view.findViewById(R.id.display_earning);


        ((MainActivity)getActivity()).mfirebaseUser = ((MainActivity)getActivity()).mAuth.getCurrentUser();
        if( ((MainActivity)getActivity()).mfirebaseUser != null)
            ((MainActivity)getActivity()).mUid = ((MainActivity)getActivity()).mfirebaseUser.getUid();

        mUid = ((MainActivity)getActivity()).mUid;

        mSignOutbtn = view.findViewById(R.id.user_sign_out);
        mSignOutbtn.setOnClickListener(this);
        mMyStockbtn = view.findViewById(R.id.display_my_stock);
        mMyStockbtn.setOnClickListener(this);
        mAdd1000 = view.findViewById(R.id.add_money_btn);
        mAdd1000.setOnClickListener(this);

        mUserName.setText(MainActivity.mUserName);
        mMoney.setText(String.format ("%.2f",MainActivity.mBalance));
        mTotalAsset.setText(String.format ("%.2f",MainActivity.mMoney));
        mEarning.setText(String.format ("%.2f",MainActivity.mEarning));


//       final GenericTypeIndicator<ArrayList<StockSnippet>> t = new GenericTypeIndicator<ArrayList<StockSnippet>>() {};

//        if( ((MainActivity)getActivity()).mfirebaseUser != null) {
//            ((MainActivity) getActivity()).mDatabaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String userName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
//                    money = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);
//                    earning  = dataSnapshot.child("users").child(mUid).child("earning").getValue(double.class);
//                    balance = dataSnapshot.child("users").child(mUid).child("balance").getValue(double.class);
//
//                    mUserName.setText(userName);
//                    mMoney.setText(String.valueOf(money));
//                    mEarning.setText(String.valueOf(earning));
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out:case R.id.user_sign_out:
                ((MainActivity)getActivity()).mAuth.signOut();

                Toast.makeText(getActivity(), "Signed Out...", Toast.LENGTH_SHORT).show();

                ((MainActivity)getActivity()).setViewPager(2);
                //((MainActivity)getActivity()).updateDatabase();
                ((MainActivity)getActivity()).clearLocalData();
                break;



            case R.id.display_my_stock:
                //Toast.makeText(getActivity(), "To My Stocks...", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setViewPager(4);
//                FragmentManager fm = getFragmentManager();
//                MyStockListFragment fragm = (MyStockListFragment)fm.findFragmentByTag("StockListFragment");
//                fragm.notifyAdapter();
                break;

            case R.id.no_user_sign_in:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.no_user_register:
                Intent intent2 = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent2);
                break;

            case R.id.add_money_btn:
                //addStockToAccount("000389", 1000, 38.5);

                ((MainActivity)getActivity()).mDatabaseReference.child("users").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> postValues = new HashMap<String,Object>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            postValues.put(snapshot.getKey(),snapshot.getValue());
                        }


                        ((MainActivity)getActivity()).mDatabaseReference.child("users").child(mUid).updateChildren(postValues);
                        ((MainActivity)getActivity()).mMoney += 1000;
                        ((MainActivity)getActivity()).mBalance += 1000;
                        postValues.put("money", ((MainActivity)getActivity()).mMoney);
                        postValues.put("balance", ((MainActivity)getActivity()).mBalance);
                        mMoney.setText(String.valueOf(MainActivity.mBalance));
                        mTotalAsset.setText(String.format ("%.2f",MainActivity.mMoney));

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }
    }

    @Override
    public void onResume() {

        super.onResume();
        mUserName.setText(MainActivity.mUserName);
        mMoney.setText(String.format ("%.2f",MainActivity.mBalance));
        mTotalAsset.setText(String.format ("%.2f",MainActivity.mMoney));
        mEarning.setText(String.format ("%.2f",MainActivity.mEarning));
    }
}
