package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    public Top20Adapter mStockAdapter;

    ArrayList<StockTop20> top = new ArrayList<>();

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
                        top = new ArrayList<StockTop20>();

                        String[] parts = response.split("=");
                        String mRes = parts[1];
                        Gson gson = new Gson();
                        List<StockTop20ListBean.UpBean> upData = gson.fromJson(mRes, StockTop20ListBean.class).getUp();
                        System.out.println("-----upBean size-----: " + upData.size());
                        for (int i = 0; i < upData.size(); i++) {
                            //public StockTop20(String id_, String name_, String changePerc_, String vol_, String currentPrice_){

                            StockTop20ListBean.UpBean entry = upData.get(i);
                            StockTop20 st20 = new StockTop20(entry.getS(), entry.getEn(), entry.getChgP(), entry.getVol(), entry.getPrice());
                            System.out.println("----------: " +st20.getName());
                            top.add(st20);
                        }
                        System.out.println("-----top size-----: " + top.size());
                        mStockAdapter = new Top20Adapter(getApplicationContext(), top);
                        mTop20ListView.setAdapter(mStockAdapter);
                        mStockAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(mTop20ListView);

                        mTop20ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                //Toast.makeText(getContext(),MainActivity.mStockRecords.get(position).getName(), Toast.LENGTH_SHORT).show();
                                String enqueryId = StockFragment.input2enqury(top.get(position).getId());
                                Intent intent = new Intent(getApplicationContext(), EachStockActivity.class);
                                intent.putExtra(MainActivity.EXTRA_MESSAGE, enqueryId);
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
