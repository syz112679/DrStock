package com.smarthuman.drstock;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by shiyuzhou on 10/4/2018.
 */

public class StockItemAdapter extends ArrayAdapter<StockSnippet> {
    public StockItemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    public StockItemAdapter(Context context, ArrayList<StockSnippet> array) {
        super(context, 0, array);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        StockSnippet stock = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_listitem, parent, false);
        }
        // Lookup view for data population
        TextView tvID = (TextView) convertView.findViewById(R.id.tv_stock_id);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_stock_name);
        TextView tvBoughtPrice = (TextView) convertView.findViewById(R.id.tv_bought_price);
        TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_amount);
        TextView tvCurPrice = (TextView) convertView.findViewById(R.id.tv_current_price);


        // Populate the data into the template view using the data object
        tvID.setText(stock.getId());
        tvName.setText(stock.getName());
        tvBoughtPrice.setText(String.format ("%.2f", stock.getBoughtPrice()) + stock.getCurrency());
        tvAmount.setText(String.format ("%.2f", stock.getAmount()));
        tvCurPrice.setText(String.format ("%.2f", stock.getCurrentPrice()) + stock.getCurrency());

        // Return the completed view to render on screen
        //System.out.println("called here, Stock list adapter");
        return convertView;
    }

}
