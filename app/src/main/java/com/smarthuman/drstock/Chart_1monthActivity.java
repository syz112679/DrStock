package com.smarthuman.drstock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/15.
 */

public class Chart_1monthActivity extends AppCompatActivity implements  View.OnClickListener{

    public static String stockId;

    private String TAG = "qqq";
    private CombinedChart mChart;
    private int itemcount;
    private LineData lineData;
    private LineChart rsiChart;
    private CandleData candleData;
    private CombinedData combinedData;
    private ArrayList<String> xVals;
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private int colorHomeBg;
    private int colorLine;
    private int colorText;
    private int colorMa5;
    private int colorMa10;
    private int colorMa20;
    private Button rsi10_Btn, rsi14_Btn, rsi20_Btn;
    public String storedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscapechart);

        Intent intent = getIntent();
        stockId = intent.getStringExtra(StockKLineChart_1monthFragment.EXTRA_MESSAGE);

        String money18url = "http://money18.on.cc/chartdata/full/price/" + stockId + "_price_full.txt";
        String money18_rsi_url = "http://money18.on.cc/chartdata/full/rsi/" + stockId + "_rsi_full.txt";
        mChart = (CombinedChart) findViewById(R.id.kline_chart);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, money18url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("-----Main setdata-----:"+response);


                        Model.setData(response);

                        initChart();

                        loadChartData();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);

        rsiChart = (LineChart) findViewById(R.id.RSI_chart);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, money18_rsi_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("-----Main setdata-----:"+response);
                        storedData = response;

                        Model.setData_Rsi(response);

                        initChart_Rsi();

                        loadChartData_Rsi10();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        RequestQueue mQueue1 = Volley.newRequestQueue(this);
        mQueue1.add(stringRequest1);

        rsi10_Btn = (Button) findViewById(R.id.rsi_10_btn);
        rsi10_Btn.setOnClickListener(this);
        rsi14_Btn = (Button) findViewById(R.id.rsi_14_btn);
        rsi14_Btn.setOnClickListener(this);
        rsi20_Btn = (Button) findViewById(R.id.rsi_20_btn);
        rsi20_Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rsi_10_btn:
                Log.d("render rsi 10 chart", "here");
                Model.setData_Rsi(storedData);
                Log.d("stored data", storedData);
                initChart_Rsi();

                loadChartData_Rsi10();
                break;
            case R.id.rsi_14_btn:
                Log.d("render rsi 14 chart", "here");
                Model.setData_Rsi(storedData);
                Log.d("stored data", storedData);
                initChart_Rsi();

                loadChartData_Rsi14();
                break;
            case R.id.rsi_20_btn:
                Log.d("render rsi 20 chart", "here");
                Model.setData_Rsi(storedData);
                Log.d("stored data", storedData);
                initChart_Rsi();

                loadChartData_Rsi20();
                break;
        }
    }


    //k line
    private void initChart() {
        colorHomeBg = getResources().getColor(R.color.home_page_bg);
        colorLine = getResources().getColor(R.color.common_divider);
        colorText = getResources().getColor(R.color.text_grey_light);
        colorMa5 = getResources().getColor(R.color.ma5);
        colorMa10 = getResources().getColor(R.color.ma10);
        colorMa20 = getResources().getColor(R.color.ma20);

        mChart.setDescription("");
        mChart.setDrawGridBackground(true);
        mChart.setBackgroundColor(colorHomeBg);
        mChart.setGridBackgroundColor(colorHomeBg);
        mChart.setScaleYEnabled(false);
        mChart.setPinchZoom(true);
        mChart.setDrawValueAboveBar(false);
        mChart.setNoDataText("加载中...");
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE});
        mChart.setTouchEnabled(true);
        mChart.setDrawMarkerViews(true);
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.mymarkerview);
        mChart.setMarkerView(mv);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(colorLine);
        xAxis.setTextColor(colorText);
        xAxis.setSpaceBetweenLabels(4);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(4, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setGridColor(colorLine);
        leftAxis.setTextColor(colorText);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        int[] colors = {colorMa5, colorMa10, colorMa20};
        String[] labels = {"MA10", "MA20", "MA50"};
        Legend legend = mChart.getLegend();
        legend.setCustom(colors, labels);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        legend.setTextColor(Color.BLACK);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
//                CandleEntry candleEntry = (CandleEntry) entry;
//                float change = (candleEntry.getClose() - candleEntry.getOpen()) / candleEntry.getOpen();
//                NumberFormat nf = NumberFormat.getPercentInstance();
//                nf.setMaximumFractionDigits(2);
//                String changePercentage = nf.format(Double.valueOf(String.valueOf(change)));
//                Log.d("qqq", "最高" + candleEntry.getHigh() + " 最低" + candleEntry.getLow() +
//                        " 开盘" + candleEntry.getOpen() + " 收盘" + candleEntry.getClose() +
//                        " 涨跌幅" + changePercentage);
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }



    private void loadChartData() {
        mChart.resetTracking();

        candleEntries = Model.getCandleEntries_1month();

        itemcount = candleEntries.size();
        System.out.println("----itemcount : "+itemcount);
        //List<StockListBean.eachTime> stockBeans = Model.getData();
        List<String> DateInfo = Model.getDate();
        xVals = new ArrayList<>();
        for (int i = DateInfo.size()-21; i < DateInfo.size(); i++) {
            xVals.add(DateInfo.get(i));
        }

        combinedData = new CombinedData(xVals);

        /*k line*/
        candleData = generateCandleData();
        combinedData.setData(candleData);

        /*ma10*/
        List<Entry> ma10Entries = Model.getMa10Entries_1month();
        /*ma20*/
        List<Entry> ma20Entries = Model.getMa20Entries_1month();
        /*ma50*/
        List<Entry> ma50Entries = Model.getMa50Entries_1month();

        lineData = generateMultiLineData(
                generateLineDataSet(ma10Entries, colorMa5, "ma10"),
                generateLineDataSet(ma20Entries, colorMa10, "ma20"),
                generateLineDataSet(ma50Entries, colorMa20, "ma50"));

        combinedData.setData(lineData);
        mChart.setData(combinedData);//当前屏幕会显示所有的数据
        setOffset();
        mChart.setOnChartGestureListener(new CoupleChartGestureListener(
                mChart, rsiChart));
        rsiChart.setOnChartGestureListener(new CoupleChartGestureListener(
                rsiChart, mChart));
        mChart.invalidate();
    }

    private LineDataSet generateLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f);
        set.setDrawCubic(true);//圆滑曲线
        set.setHighlightEnabled(true);
        set.setDrawHighlightIndicators(true);
        set.setHighLightColor(Color.BLACK);
        set.setLineWidth(2);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawFilled(false);
        //Drawable drawable = ContextCompat.getDrawable(this.getActivity(), R.drawable.fade_red);
        //set.setFillDrawable(drawable);

        return set;
    }

    private LineData generateMultiLineData(LineDataSet... lineDataSets) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < lineDataSets.length; i++) {
            dataSets.add(lineDataSets[i]);
        }

        List<String> xVals = new ArrayList<String>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add("" + (1990 + i));
        }

        LineData data = new LineData(xVals, dataSets);

        return data;
    }

    private CandleData generateCandleData() {

        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(MainActivity.DownColor_);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(MainActivity.UpColor_);
        set.setIncreasingPaintStyle(Paint.Style.STROKE);
        set.setNeutralColor(Color.RED);
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.5f);
        set.setHighLightColor(Color.WHITE);
        set.setDrawValues(false);

        CandleData candleData = new CandleData(xVals);
        candleData.addDataSet(set);

        return candleData;
    }

    private void initChart_Rsi(){
        rsiChart.setScaleEnabled(false);
        rsiChart.setDrawBorders(false);
        rsiChart.setBorderWidth(1);
        rsiChart.setBorderColor(getResources().getColor(R.color.edit_text_underline));
        rsiChart.setDescription("");
        Legend lineChartLegend = rsiChart.getLegend();
        lineChartLegend.setEnabled(false);
        rsiChart.setDrawMarkerViews(true);
        rsiChart.setTouchEnabled(true); // 设置是否可以触摸
        rsiChart.setDragEnabled(true);// 是否可以拖拽
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.mymarkerview);
        rsiChart.setMarkerView(mv);

        rsiChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        rsiChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        rsiChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        rsiChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        rsiChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        rsiChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        rsiChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

        //x轴
        XAxis xAxis = rsiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(59);



        //左边y
        YAxis axisLeft = rsiChart.getAxisLeft();
        axisLeft.setLabelCount(5, true);
        axisLeft.setDrawLabels(true);

        //右边y
        YAxis axisRight = rsiChart.getAxisRight();
        axisRight.setEnabled(false);

        //y轴样式
        axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });

        //背景线
        xAxis.setGridColor(getResources().getColor(R.color.edit_text_underline));
        xAxis.setAxisLineColor(getResources().getColor(R.color.edit_text_underline));
        axisLeft.setGridColor(getResources().getColor(R.color.edit_text_underline));

        rsiChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//                barChart.setHighlightValue(new Highlight(h.getXIndex(), 0));

                mChart.highlightValue(new Highlight(h.getXIndex(), 0));

                // lineChart.setHighlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                mChart.highlightValue(null);
            }
        });
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                rsiChart.highlightValue(new Highlight(h.getXIndex(), 0));
                // lineChart.setHighlightValue(new Highlight(h.getXIndex(), 0));//此函数已经返回highlightBValues的变量，并且刷新，故上面方法可以注释
                //barChart.setHighlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                rsiChart.highlightValue(null);
            }
        });


    }

    private void loadChartData_Rsi10(){
        rsiChart.resetTracking();
        List<Entry> lineEntries = Model.getRsi10Entries_1month();

        itemcount = lineEntries.size();
        System.out.println("----itemcount : "+itemcount);
//        List<String> DateInfo = Model.getDate();
        xVals = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(" ");
        }

        /*k line*/
        LineDataSet set = generateLineDataSet(lineEntries, colorMa20, "rsi10");
        LineData data = new LineData(xVals,set);
        rsiChart.setData(data);
        //setOffset();
        rsiChart.invalidate();
    }

    private void loadChartData_Rsi14(){
        rsiChart.resetTracking();
        List<Entry> lineEntries = Model.getRsi14Entries_1month();

        itemcount = lineEntries.size();
        System.out.println("----itemcount : "+itemcount);
//        List<String> DateInfo = Model.getDate();
        xVals = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(" ");
        }

        /*k line*/
        LineDataSet set = generateLineDataSet(lineEntries, colorMa20, "rsi14");
        LineData data = new LineData(xVals,set);
        rsiChart.setData(data);
        //setOffset();
        rsiChart.invalidate();
    }

    private void loadChartData_Rsi20(){
        rsiChart.resetTracking();
        List<Entry> lineEntries = Model.getRsi20Entries_1month();

        itemcount = lineEntries.size();
        System.out.println("----itemcount : "+itemcount);
//        List<String> DateInfo = Model.getDate();
        xVals = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(" ");
        }

        /*k line*/
        LineDataSet set = generateLineDataSet(lineEntries, colorMa20, "rsi20");
        LineData data = new LineData(xVals,set);
        rsiChart.setData(data);
        //setOffset();
        rsiChart.invalidate();
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = rsiChart.getViewPortHandler().offsetLeft();
        float combinedLeft = mChart.getViewPortHandler().offsetLeft();
        float lineRight = rsiChart.getViewPortHandler().offsetRight();
        float combinedRight = mChart.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (combinedLeft < lineLeft) {
            offsetLeft = Utils.convertPixelsToDp(lineLeft-combinedLeft);
            mChart.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = Utils.convertPixelsToDp(combinedLeft-lineLeft);
            rsiChart.setExtraLeftOffset(offsetLeft);
        }
  /*注：setExtra...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (combinedRight < lineRight) {
            offsetRight = Utils.convertPixelsToDp(lineRight);
            mChart.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = Utils.convertPixelsToDp(combinedRight);
            rsiChart.setExtraRightOffset(offsetRight);
        }

    }

    public class CoupleChartGestureListener implements OnChartGestureListener {

        private Chart srcChart;
        private Chart dstChart;

        public CoupleChartGestureListener(Chart srcChart, Chart dstChart) {
            this.srcChart = srcChart;
            this.dstChart = dstChart;
        }


        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

        }

        @Override
        public void onChartLongPressed(MotionEvent me) {

        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
//            Intent intent = new Intent (getActivity(), Chart_1monthActivity.class);
//            intent.putExtra(EXTRA_MESSAGE, stock.id_);
//            startActivity(intent);
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {

        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            //Log.d(TAG, "onChartScale " + scaleX + "/" + scaleY + " X=" + me.getX() + "Y=" + me.getY());
            syncCharts();
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            //Log.d(TAG, "onChartTranslate " + dX + "/" + dY + " X=" + me.getX() + "Y=" + me.getY());
            syncCharts();
        }

        public void syncCharts() {
            Matrix srcMatrix;
            float[] srcVals = new float[9];
            Matrix dstMatrix;
            float[] dstVals = new float[9];

            // get src chart translation matrix:
            srcMatrix = srcChart.getViewPortHandler().getMatrixTouch();
            srcMatrix.getValues(srcVals);

            // apply X axis scaling and position to dst charts:

            if (dstChart.getVisibility() == View.VISIBLE) {
                dstMatrix = dstChart.getViewPortHandler().getMatrixTouch();
                dstMatrix.getValues(dstVals);
                dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
                dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
                dstMatrix.setValues(dstVals);
                dstChart.getViewPortHandler().refresh(dstMatrix, dstChart, true);
            }

        }

    }

    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                tvContent.setText("High: " + ce.getHigh() + "\nLow:" +ce.getLow() + "\nOpen:" +ce.getOpen() + "\nClose:" +ce.getClose());
            }
            else {
                tvContent.setText("" + e.getVal()); // set the entry-value as the display text
            }
        }

        @Override
        public int getXOffset(float xpos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }

    }


}
