package com.smarthuman.drstock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 展示搜索结果
 */
public class SearchResultActivity extends AppCompatActivity {

    private TextView resultTv; //搜索结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        System.out.println("open SearchResultActivity");

        resultTv = (TextView) findViewById(R.id.search_result_tv);
        System.out.println("get resultTv");

        String result = getIntent().getStringExtra("result");

        System.out.println("result: "+result);
        resultTv.setText(result);
    }
}
