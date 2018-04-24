package com.smarthuman.drstock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Li Shuhan on 2018/4/23.
 */

public class Top20Adapter extends ArrayAdapter<StockTop20> {
    public Top20Adapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    public Top20Adapter(Context context, ArrayList<StockTop20> array) {
        super(context, 0, array);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        StockTop20 stock = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.top20_listitem, parent, false);
        }
        // Lookup view for data population
        TextView tvID = (TextView) convertView.findViewById(R.id.tv_stock_id);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_stock_name);
        TextView tvChangePerc = (TextView) convertView.findViewById(R.id.tv_change_perc);
        TextView tvVol = (TextView) convertView.findViewById(R.id.tv_vol);
        TextView tvCurPrice = (TextView) convertView.findViewById(R.id.tv_current_price);

        // Populate the data into the template view using the data object
        tvID.setText(stock.getId());
        tvName.setText(stock.getName());
        tvChangePerc.setText(stock.getChangePerc());
        tvChangePerc.setTextColor(MainActivity.UpColor_);
        tvVol.setText(stock.getVol());
        tvCurPrice.setText(stock.getCurrentPrice());

        return convertView;
    }


}
