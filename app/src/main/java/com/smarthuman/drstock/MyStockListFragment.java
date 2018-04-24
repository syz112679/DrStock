package com.smarthuman.drstock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;

/**
 * Created by shiyuzhou on 27/3/2018.
 */

public class MyStockListFragment extends android.support.v4.app.Fragment  {

    private ListView mMyStockListView;
    private ListView mMyPlanListView;
    StockItemAdapter mStockAdapter;
    PlanItemAdapter mPlanAdapter;
    private PieChart mChart;
//    private String[] x = new String[] { "Stock", "Balance" };
//    private float[] y = {2f, 98f};
//    private float[] y1 = { (float)(100-(int)((MainActivity.mBalance/MainActivity.mMoney)*100)), (float)((int)((MainActivity.mBalance/MainActivity.mMoney)*100))
//    };
    //private float[] yData = { (float)(100-(int)((MainActivity.mBalance/MainActivity.mMoney)*100)), (float)((int)((MainActivity.mBalance/MainActivity.mMoney)*100))};
    private String[] xData = { "Stock", "Balance"};
    private float[] yData = { 0, 0};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mystocklist, container, false);
        mChart = view.findViewById(R.id.pie_chart);
        //initChart();
        yData[0] = (float)(100-(int)((MainActivity.mBalance/MainActivity.mMoney)*100));
        yData[1] = (float)((int)((MainActivity.mBalance/MainActivity.mMoney)*100));
        System.out.println("----balabce and assets----: " + (100-(int)((MainActivity.mBalance/MainActivity.mMoney)*100)) + " " + ((int)((MainActivity.mBalance/MainActivity.mMoney)*100)) );
        System.out.println("----yData----: " + yData[0] + " " + yData[1]);

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
            //setData(2);
            // configure pie chart
            mChart.setUsePercentValues(true);
            mChart.setDescription("User Assets Distribution");

            // enable hole and configure
            mChart.setDrawHoleEnabled(true);
            //mChart.setHoleColorTransparent(true);
            mChart.setHoleRadius(7);
            mChart.setTransparentCircleRadius(10);

            // enable rotation of the chart by touch
            mChart.setRotationAngle(0);
            mChart.setRotationEnabled(true);

            addData();

            // customize legends
            Legend l = mChart.getLegend();
            //l.setPosition(LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);
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

        mMyPlanListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                String enqueryId = MainActivity.mPlans.get(position).getEnquiryId();
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
       // getActivity().setTitle(R.string.title_my_stock);
        mStockAdapter.notifyDataSetChanged();
        mPlanAdapter.notifyDataSetChanged();


    }

/*
    private void setData(int count) {

        // 准备x"轴"数据：在i的位置，显示x[i]字符串
        ArrayList<String> xVals = new ArrayList<String>();

        // 真实的饼状图百分比分区。
        // Entry包含两个重要数据内容：position和该position的数值。
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int xi = 0; xi < count; xi++) {
            xVals.add(xi, x[xi]);

            // y[i]代表在x轴的i位置真实的百分比占
            yVals.add(new Entry(y[xi], xi));
        }

        PieDataSet yDataSet = new PieDataSet(yVals, "百分比占");

        // 每个百分比占区块绘制的不同颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        yDataSet.setColors(colors);

        // 将x轴和y轴设置给PieData作为数据源
        PieData data = new PieData(xVals, yDataSet);

        // 设置成PercentFormatter将追加%号
        data.setValueFormatter(new PercentFormatter());

        // 文字的颜色
        data.setValueTextColor(Color.BLACK);

        // 最终将全部完整的数据喂给PieChart
        mPieChart.setData(data);
        mPieChart.invalidate();
    }

*/


    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));
        System.out.println("----yData----: " + yData[0] + " " + yData[1]);
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "    Assets categories");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();
    }
}
