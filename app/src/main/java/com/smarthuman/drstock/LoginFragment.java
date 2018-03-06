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

import java.util.ArrayList;

/**
 * Created by jingle on 04/03/2018.
 */

public class LoginFragment extends android.support.v4.app.Fragment implements  View.OnClickListener{

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
        return inflater.inflate(R.layout.fragment_login, null);
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
