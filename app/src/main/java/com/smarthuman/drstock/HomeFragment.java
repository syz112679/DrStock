package com.smarthuman.drstock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

/**
 * Created by jingle on 04/03/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    ViewFlipper viewFlipper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,
                null);
        viewFlipper = (ViewFlipper)v.findViewById(R.id.flipper);
        viewFlipper.startFlipping();
        return v;
    }
}
