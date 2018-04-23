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
import java.util.Map;

/**
 * Created by shiyuzhou on 27/3/2018.
 */

public class MyStockListFragment extends android.support.v4.app.Fragment  {

    private ListView mMyStockListView;
    private ListView mMyPlanListView;
    StockItemAdapter mStockAdapter;
    PlanItemAdapter mPlanAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mystocklist, container, false);

        if(((MainActivity)getActivity()).mfirebaseUser != null) {

            mMyStockListView = view.findViewById(R.id.my_stocks_listview);
            mMyPlanListView = view.findViewById(R.id.my_plans_listview);
            // check every time to remove 00000
            if(!MainActivity.mStockRecords.isEmpty() && MainActivity.mStockRecords.get(0).getId().equals("00000")) {
                MainActivity.mStockRecords.remove(0);
            }

            //stocks
            mStockAdapter = new StockItemAdapter(getActivity(), MainActivity.mStockRecords);
            mMyStockListView.setAdapter(mStockAdapter);
            mStockAdapter.notifyDataSetChanged();

//            if(InvestmentPlan.planTreeMap != null ) {
//                ArrayList<InvestmentPlan> plans = new ArrayList<InvestmentPlan> ();
//                for(Map.Entry<String,InvestmentPlan> entry : InvestmentPlan.planTreeMap.entrySet()) {
//                    plans.add(entry.getValue());
//                }
                //plans
                mPlanAdapter = new PlanItemAdapter(getActivity(), MainActivity.mPlans);
                mMyPlanListView.setAdapter(mPlanAdapter);
                mPlanAdapter.notifyDataSetChanged();
            //}


        }

        // ListView setOnItemClickListener function apply here.

        mMyStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
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
        MainActivity.requireRefresh=true;
        getActivity().setTitle(R.string.title_my_stock);
        mStockAdapter.notifyDataSetChanged();
        mPlanAdapter.notifyDataSetChanged();


    }


}
