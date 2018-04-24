package com.smarthuman.drstock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/21.
 */

public class USChart_Min_Activity extends AppCompatActivity {
    private String TAG = "qqq";
    private LineChart lineChart;
    private BarChart barChart;
    private int itemcount;
    private int linechartcount;
    private LineData lineData;
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
    public static String stockId;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape_min);

        Intent intent = getIntent();
        stockId = intent.getStringExtra(StockMinChartFragment.EXTRA_MESSAGE);
        String alpha_url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockId + "&interval=1min&outputsize=full&apikey=OFUAIBJU9MLJZEKX";
        Log.d("url", "url is "+alpha_url);
        //http://money18.on.cc/chartdata/d1/price/02318_price_d1.txt
        lineChart = (LineChart) findViewById(R.id.lchart);       //http://money18.on.cc/chartdata/full/price/00700_price_full.txt
        barChart = (BarChart) findViewById(R.id.bchart);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, alpha_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("-----Main setdata-----:"+response);
                        Model.setDataF(response);

                        initChartF();

                        loadChartUSDataF();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);
    }

    private void initChartF() {
        lineChart.setScaleEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setBorderWidth(1);
        lineChart.setBorderColor(getResources().getColor(R.color.edit_text_underline));
        lineChart.setDescription("");
        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(false);
        lineChart.setDrawMarkerViews(true);
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.mymarkerview);
        lineChart.setMarkerView(mv);

        lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        lineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

        barChart.setScaleEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        barChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        barChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
      /*  barChart.setBorderWidth(1);
        barChart.setBorderColor(getResources().getColor(R.color.grayLine));*/
        barChart.setDescription("");
        barChart.setMarkerView(mv);
        Legend barChartLegend = barChart.getLegend();
        barChartLegend.setEnabled(false);


        //x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(59);



        //左边y
        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setLabelCount(5, true);
        axisLeft.setDrawLabels(true);

        //右边y
        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setEnabled(false);

        //bar x y轴
        XAxis xAxisBar = barChart.getXAxis();
        xAxisBar.setDrawLabels(false);
        xAxisBar.setDrawGridLines(false);

        YAxis axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setDrawGridLines(false);


        YAxis axisRightBar = barChart.getAxisRight();
        // axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);

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
        //axisRight.setAxisLineColor(getResources().getColor(R.color.edit_text_underline));
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//                barChart.setHighlightValue(new Highlight(h.getXIndex(), 0));

                barChart.highlightValue(new Highlight(h.getXIndex(), 0));

                // lineChart.setHighlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValue(null);
            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                lineChart.highlightValue(new Highlight(h.getXIndex(), 0));
                // lineChart.setHighlightValue(new Highlight(h.getXIndex(), 0));//此函数已经返回highlightBValues的变量，并且刷新，故上面方法可以注释
                //barChart.setHighlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValue(null);
            }
        });



    }

    //render us stock Min chart
    private void loadChartUSDataF(){
        lineChart.resetTracking();
        barChart.resetTracking();
        List<String> MinInfo = Model.getUSMin();
        List<Entry> lineEntries = Model.getUSLineEntries();
        List<BarEntry> barEntries = Model.getUSBarEntries();

        itemcount = barEntries.size();
        linechartcount = lineEntries.size();
        System.out.println("----itemcount : "+itemcount);
        //List<StockListBean.eachTime> stockBeans = Model.getData();

        xVals = new ArrayList<>();
        List<Float> priceArr = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(MinInfo.get(itemcount - 1 - i));
            priceArr.add(lineEntries.get(i).getVal());
        }
        List<Integer> colorArray = new ArrayList<>();
        //List<Float> priceArr = Model.getPrice();
        colorArray.add(getResources().getColor(R.color.text_grey));
        for(int i = 1; i < linechartcount; i ++){
            System.out.println("----check barchart values-----: barEntries[i-1]: "+ priceArr.get(i-1) + "; barEntries[i]: " + priceArr.get(i-1));
            if(priceArr.get(i)>priceArr.get(i-1)){
                colorArray.add(getResources().getColor(R.color.text_green));
            }
            else{
                colorArray.add(getResources().getColor(R.color.text_red));
            }
        }
        BarDataSet set2 = generateBarDataSet(barEntries, colorArray, "minVol");
        set2.setHighlightEnabled(true);
        //set2.setDrawHighlightIndicators(true);
        set2.setHighLightColor(Color.BLACK);
        BarData data2 = new BarData(xVals, set2);
        barChart.setData(data2);

        LineDataSet set1 = generateLineDataSet(lineEntries, getResources().getColor(R.color.line_chart_color), "minPrice");


        LineData data = new LineData(xVals, set1);
        lineChart.setData(data);



        setOffset();
        lineChart.setOnChartGestureListener(new CoupleChartGestureListener(
                lineChart, barChart));
        barChart.setOnChartGestureListener(new CoupleChartGestureListener(
                barChart, lineChart));
        lineChart.invalidate();
        barChart.invalidate();
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = lineChart.getViewPortHandler().offsetLeft();
        float barLeft = barChart.getViewPortHandler().offsetLeft();
        float lineRight = lineChart.getViewPortHandler().offsetRight();
        float barRight = barChart.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
            offsetLeft = Utils.convertPixelsToDp(lineLeft-barLeft);
            barChart.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft-lineLeft);
            lineChart.setExtraLeftOffset(offsetLeft);
        }
  /*注：setExtra...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
            offsetRight = Utils.convertPixelsToDp(lineRight);
            barChart.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight);
            lineChart.setExtraRightOffset(offsetRight);
        }

    }

    private BarDataSet generateBarDataSet(List<BarEntry> entries, List<Integer> colorArr, String label){
        BarDataSet set = new BarDataSet(entries, label);
        set.setColors(colorArr);
        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);//不显示网格

        barChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        barChart.getAxisLeft().setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙
        barChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格


        barChart.animateXY(1000, 2000);//设置动画


        return set;
    }

    private LineDataSet generateLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f);
        //set.setDrawCubic(true);//圆滑曲线
        set.setHighlightEnabled(true);
        set.setDrawHighlightIndicators(true);
        set.setHighLightColor(Color.BLACK);
        set.setLineWidth(2);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
        set.setFillDrawable(drawable);

        return set;
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
            tvContent.setText("" + e.getVal()); // set the entry-value as the display text
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
//            if(stock.marketId_.equals("HK")) {
//                Intent intent = new Intent(getActivity(), Chart_MinActivity.class);
//                intent.putExtra(EXTRA_MESSAGE, stock.id_);
//                startActivity(intent);
//            }
//            if(stock.marketId_.equals("US")) {
//                Intent intent = new Intent(getActivity(), USChart_Min_Activity.class);
//                intent.putExtra(EXTRA_MESSAGE, stock.id_);
//                startActivity(intent);
//            }
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {

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



}
