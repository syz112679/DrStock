package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.CombinedChart;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li Shuhan on 2018/4/23.
 */

public class Top20_Activity extends TitleActivity {
    public ListView mTop20ListView;
    public Top20Adapter mStockAdapter_up;
    public Top20Adapter mStockAdapter_down;
    public Top20Adapter mStockAdapter_vol;
    public Top20Adapter mStockAdapter_turn;
    public Button top_up, top_down, top_vol, top_turn;
    public String storeRes;

    ArrayList<StockTop20> top_u = new ArrayList<>();
    ArrayList<StockTop20> top_d = new ArrayList<>();
    ArrayList<StockTop20> top_v = new ArrayList<>();
    ArrayList<StockTop20> top_t = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top20);
        setTitle("Top20");
        showBackward(getDrawable(R.drawable.ic_return), true);

        String money18url = "http://money18.on.cc/js/real/topStock_stock_s.js";
        mTop20ListView = findViewById(R.id.top20_listview);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, money18url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("-----Main setdata-----:"+response);
                        top_u = new ArrayList<StockTop20>();
                        top_d = new ArrayList<StockTop20>();
                        top_v = new ArrayList<StockTop20>();
                        top_t = new ArrayList<StockTop20>();

                        String[] parts = response.split("=");
                        String mRes = parts[1];
                        storeRes = mRes;

                        Gson gson = new Gson();
                        List<StockTop20ListBean.UpBean> upData = gson.fromJson(mRes, StockTop20ListBean.class).getUp();
                        System.out.println("-----upBean size-----: " + upData.size());
                        for (int i = 0; i < upData.size(); i++) {
                            //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                            StockTop20ListBean.UpBean entry = upData.get(i);
                            StockTop20 st20_up = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                            System.out.println("----------: " +st20_up.getName());
                            top_u.add(st20_up);
                        }

                        System.out.println("-----top_u size-----: " + top_u.size());
                        mStockAdapter_up = new Top20Adapter(getApplicationContext(), top_u);
                        mTop20ListView.setAdapter(mStockAdapter_up);
                        mStockAdapter_up.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(mTop20ListView);


                        mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                                String enqueryId_d = StockFragment.input2enqury(top_u.get(position).getId());
                                System.out.println("-----stock id-----: " + top_u.get(position).getId());
                                Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                                intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId_d);
                                startActivity(intent);



                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);

        top_up = (Button) findViewById(R.id.top_up);
        top_up.setOnClickListener(this);
        top_down = (Button) findViewById(R.id.top_down);
        top_down.setOnClickListener(this);
        top_vol = (Button) findViewById(R.id.top_vol);
        top_vol.setOnClickListener(this);
        top_turn = (Button) findViewById(R.id.top_turn);
        top_turn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Gson gson = new Gson();
        switch (v.getId()) {
            case R.id.top_up:

                List<StockTop20ListBean.UpBean> upData = gson.fromJson(storeRes, StockTop20ListBean.class).getUp();
                System.out.println("-----upBean size-----: " + upData.size());
                for (int i = 0; i < upData.size(); i++) {
                    //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                    StockTop20ListBean.UpBean entry = upData.get(i);
                    StockTop20 st20_up = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                    System.out.println("----------: " +st20_up.getName());
                    top_u.add(st20_up);
                }

                System.out.println("-----top_u size-----: " + top_u.size());
                mStockAdapter_up = new Top20Adapter(getApplicationContext(), top_u);
                mTop20ListView.setAdapter(mStockAdapter_up);
                mStockAdapter_up.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(mTop20ListView);

                mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                        String enqueryId_d = StockFragment.input2enqury(top_u.get(position).getId());
                        System.out.println("-----stock id-----: " + top_u.get(position).getId());
                        Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                        intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId_d);
                        startActivity(intent);



                    }
                });
                break;
            case R.id.top_down:

                List<StockTop20ListBean.DownBean> downData = gson.fromJson(storeRes, StockTop20ListBean.class).getDown();
                System.out.println("-----downBean size-----: " + downData.size());
                for (int i = 0; i < downData.size(); i++) {
                    //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                    StockTop20ListBean.DownBean entry = downData.get(i);
                    StockTop20 st20_down = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                    System.out.println("----------: " +st20_down.getName());
                    top_d.add(st20_down);
                }

                System.out.println("-----top_d size-----: " + top_d.size());
                mStockAdapter_down = new Top20Adapter(getApplicationContext(), top_d);
                mTop20ListView.setAdapter(mStockAdapter_down);
                mStockAdapter_down.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(mTop20ListView);

                mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                        String enqueryId_d = StockFragment.input2enqury(top_d.get(position).getId());
                        System.out.println("-----stock id-----: " + top_d.get(position).getId());
                        Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                        intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId_d);
                        startActivity(intent);



                    }
                });
                break;
            case R.id.top_vol:
                List<StockTop20ListBean.VolBean> volData = gson.fromJson(storeRes, StockTop20ListBean.class).getVol();
                System.out.println("-----volBean size-----: " + volData.size());
                for (int i = 0; i < volData.size(); i++) {
                    //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                    StockTop20ListBean.VolBean entry = volData.get(i);
                    StockTop20 st20_vol = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                    System.out.println("----------: " +st20_vol.getName());
                    top_v.add(st20_vol);
                }

                System.out.println("-----top_v size-----: " + top_v.size());
                mStockAdapter_vol = new Top20Adapter(getApplicationContext(), top_v);
                mTop20ListView.setAdapter(mStockAdapter_vol);
                mStockAdapter_vol.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(mTop20ListView);

                mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                        String enqueryId_d = StockFragment.input2enqury(top_v.get(position).getId());
                        System.out.println("-----stock id-----: " + top_v.get(position).getId());
                        Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                        intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId_d);
                        startActivity(intent);



                    }
                });
                break;
            case R.id.top_turn:
                List<StockTop20ListBean.TurnOverBean> turnData = gson.fromJson(storeRes, StockTop20ListBean.class).getTo();
                System.out.println("-----turnBean size-----: " + turnData.size());
                for (int i = 0; i < turnData.size(); i++) {
                    //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                    StockTop20ListBean.TurnOverBean entry = turnData.get(i);
                    StockTop20 st20_turn = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                    System.out.println("----------: " +st20_turn.getName());
                    top_t.add(st20_turn);
                }

                System.out.println("-----top_t size-----: " + top_t.size());
                mStockAdapter_turn = new Top20Adapter(getApplicationContext(), top_t);
                mTop20ListView.setAdapter(mStockAdapter_turn);
                mStockAdapter_turn.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(mTop20ListView);

                mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                        String enqueryId_d = StockFragment.input2enqury(top_t.get(position).getId());
                        System.out.println("-----stock id-----: " + top_t.get(position).getId());
                        Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                        intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId_d);
                        startActivity(intent);



                    }
                });
                break;

        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
