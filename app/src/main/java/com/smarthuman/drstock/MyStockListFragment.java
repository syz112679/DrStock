package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiyuzhou on 27/3/2018.
 */

public class MyStockListFragment extends android.support.v4.app.Fragment  {

    private ListView mMyStockListView;
    StockItemAdapter mStockAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mystocklist, container, false);

        if(((MainActivity)getActivity()).mfirebaseUser != null) {

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

        // ListView setOnItemClickListener function apply here.

        mMyStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(),"clicked "+MainActivity.mStockRecords.get(position).getId(), Toast.LENGTH_SHORT).show();
                String enqueryId = StockFragment.input2enqury(MainActivity.mStockRecords.get(position).getId());
                Intent intent = new Intent(getActivity(), EachStockActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId);
                startActivity(intent);

            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_account);
        mStockAdapter.notifyDataSetChanged();

    }
}
