package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by jingle on 04/03/2018.
 */

public class AccountFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    String mUid;
    private TextView mUserName;
    private TextView mMoney;
    private ArrayList<String> mMyStock;
    private ArrayList<String> mFavorites;
    private Button mSignInbtn;
    private Button mSignOutbtn;
    private Button mRegisterbtn;
    private Button mMyStockbtn;
    private Button mFavoritebtn;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        mUserName = view.findViewById(R.id.display_username);
        mMoney = view.findViewById(R.id.display_money);



        if(((MainActivity)getActivity()).mfirebaseUser == null) {
            view = inflater.inflate(R.layout.fragment_login, container, false);
            mSignInbtn = view.findViewById(R.id.no_user_sign_in);
            mSignInbtn.setOnClickListener(this);
            mRegisterbtn = view.findViewById(R.id.no_user_register);
            mRegisterbtn.setOnClickListener(this);
        } else {
            mUid = ((MainActivity)getActivity()).mUid;

            mSignOutbtn = view.findViewById(R.id.user_sign_out);
            mSignOutbtn.setOnClickListener(this);
            mMyStockbtn = view.findViewById(R.id.display_my_stock);
            mMyStockbtn.setOnClickListener(this);
            mFavoritebtn = view.findViewById(R.id.display_favorite);
            mFavoritebtn.setOnClickListener(this);

            ((MainActivity) getActivity()).mDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        return view;
    }




    public void register(View v) {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out:case R.id.user_sign_out:
                ((MainActivity)getActivity()).mAuth.signOut();

                Toast.makeText(getActivity(), "Signed Out...", Toast.LENGTH_SHORT).show();

                mUserName.setText("Please sign in first");
                mMoney.setText("0.0");
                break;

            case R.id.display_favorite:
                Toast.makeText(getActivity(), "To My Favorites...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.display_my_stock:
                Toast.makeText(getActivity(), "To My Stocks...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.no_user_sign_in:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;


        }
    }
}
