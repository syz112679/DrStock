package com.smarthuman.drstock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlanItemAdapter extends ArrayAdapter<InvestmentPlan> {

    public PlanItemAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            }


    public PlanItemAdapter(Context context, ArrayList<InvestmentPlan> array) {
            super(context, 0, array);
            }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            InvestmentPlan plan = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_listitem, parent, false);
            }
            // Lookup view for data population
            TextView tvID = (TextView) convertView.findViewById(R.id.tv_stock_id_plan);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_stock_name_plan);
            TextView tvBaseVolume = (TextView) convertView.findViewById(R.id.tv_base_volume_plan);
            TextView tvFrequency = (TextView) convertView.findViewById(R.id.tv_frequency_plan);



            // Populate the data into the template view using the data object
            tvID.setText(plan.getEnquiryId());
            tvName.setText(plan.getName());
            tvBaseVolume.setText(String.format ("%.2f", plan.getBaseVolumn()));
            tvFrequency.setText( plan.getFrequency());

            // Return the completed view to render on screen
            //System.out.println("called here, Stock list adapter");
            return convertView;
            }

}