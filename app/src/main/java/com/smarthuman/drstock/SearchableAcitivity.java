package com.smarthuman.drstock;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jingle on 06/03/2018.
 */

public class SearchableAcitivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //… …
        super.onCreate(savedInstanceState);
        doSearchQuery(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {  //activity重新置顶
        super.onNewIntent(intent);
        doSearchQuery(intent);
    }

    // 对searchable activity的调用仍是标准的intent，我们可以从intent中获取信息，即要搜索的内容
    private void doSearchQuery(Intent intent){
        if(intent == null)
            return;

        String queryAction = intent.getAction();
        if( Intent.ACTION_SEARCH.equals( intent.getAction())){  //如果是通过ACTION_SEARCH来调用，即如果通过搜索调用
            String queryString = intent.getStringExtra(SearchManager.QUERY); //获取搜索内容
            //… …
        }

    }
    //… …
}