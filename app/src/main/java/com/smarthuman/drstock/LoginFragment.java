package com.smarthuman.drstock;

import android.app.Activity;
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


    private Button mSignInbtn;
    private Button mRegisterbtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mSignInbtn = view.findViewById(R.id.no_user_sign_in);
        mSignInbtn.setOnClickListener(this);
        mRegisterbtn = view.findViewById(R.id.no_user_register);
        mRegisterbtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.no_user_register:
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.no_user_sign_in:
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent2);
                break;


        }
    }
}
