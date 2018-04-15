package com.smarthuman.drstock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiyuzhou on 27/3/2018.
 */

public class MyStockListFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    String mUid = "";
    private ArrayList<StockSnippet> mMyStock = new ArrayList<StockSnippet>();
    private ListView mMyStockListView;
    StockItemAdapter mStockAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mystocklist, container, false);

        if(((MainActivity)getActivity()).mfirebaseUser != null) {
            mUid = ((MainActivity) getActivity()).mUid;
            mMyStockListView = view.findViewById(R.id.my_stocks_listview);


            mStockAdapter = new StockItemAdapter(getActivity(), MainActivity.mStockRecords);
            mMyStockListView.setAdapter(mStockAdapter);
            mStockAdapter.notifyDataSetChanged();
//            ((MainActivity) getActivity()).mDatabaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(getActivity()!=null) {
//                        String userName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
//                        double money = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);
//                        double earning = dataSnapshot.child("users").child(mUid).child("earning").getValue(double.class);
//                        mMyStock.clear();
//
//
//                        for (DataSnapshot child: dataSnapshot.child("users").child(mUid).child("myStocks").getChildren()) {
//                            mMyStock.add(child.getValue(StockSnippet.class));
//                        }
//
//                        mStockAdapter = new StockItemAdapter(getActivity(), mMyStock);
//                        mStockAdapter.notifyDataSetChanged();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
        return view;
    }
    @Override
    public void onClick(View v) {

    }
}
