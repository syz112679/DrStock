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
    private ArrayList<String> mMyStock = new ArrayList<>();
    private ArrayList<String> mFavorites = new ArrayList<>();
    private ListView mMyStockListView, mFavoritesListView;
    ArrayAdapter<String> mStockAdapter;
    ArrayAdapter<String> mFavoriteAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mystocklist, container, false);

        if(((MainActivity)getActivity()).mfirebaseUser != null) {
            mUid = ((MainActivity) getActivity()).mUid;
            mMyStockListView = view.findViewById(R.id.my_stocks_listview);
            mFavoritesListView = view.findViewById(R.id.my_favorite_listview);

            mStockAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mMyStock);
            mFavoriteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFavorites);

            mMyStockListView.setAdapter(mStockAdapter);
            mFavoritesListView.setAdapter(mFavoriteAdapter);

            ((MainActivity) getActivity()).mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("users").child(mUid).child("userName").getValue(String.class);
                    double money = dataSnapshot.child("users").child(mUid).child("money").getValue(double.class);
                    double earning = dataSnapshot.child("users").child(mUid).child("earning").getValue(double.class);


                    mFavorites = (ArrayList<String>) dataSnapshot.child("users").child(mUid).child("favorites").getValue();
                    mMyStock = (ArrayList<String>) dataSnapshot.child("users").child(mUid).child("myStocks").getValue();

                    mStockAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mMyStock);
                    mStockAdapter.notifyDataSetChanged();
                    mFavoriteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFavorites);
                    mFavoriteAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return view;
    }
    @Override
    public void onClick(View v) {

    }
}
