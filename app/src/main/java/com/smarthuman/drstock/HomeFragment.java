package com.smarthuman.drstock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

//        TextView indexSzText = (TextView) v.findViewById(R.id.sz_index);
//        indexSzText.setText(indexSz);
//        Log.d("sz_index", "onCreateView: sz_index");
//        TextView changeSzText = (TextView) v.findViewById(R.id.sz_change);
//        changeSzText.setText(changeSz);
//
//
//        TextView indexShText = (TextView) v.findViewById(R.id.sh_index);
//        indexShText.setText(indexSh);
//        TextView changeShText = (TextView) v.findViewById(R.id.sh_change);
//        changeShText.setText(changeSh);
//
//        TextView indexChuangText = (TextView) v.findViewById(R.id.chuang_index);
//        indexChuangText.setText(indexChuang);
//        TextView changeChuangText = (TextView) v.findViewById(R.id.chuang_change);
//        changeChuangText.setText(changeChuang);

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        viewFlipper.startFlipping();
        return v;
    }
}