package com.smarthuman.drstock;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jingle on 04/03/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    ViewFlipper viewFlipper;
    ImageView chatRoom_img;
    private View v;

    String API_KEY = "b0535990b1f343d79558e427bb29eec9"; // ### YOUE NEWS API HERE ###
    String NEWS_SOURCE = "business-insider";
    ListView listNews;
    ProgressBar loader;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home,
                null);

//        TextView indexSzText = (TextView) v.findViewById(R.id.sz_index);
//        indexSzText.setText(indexSz);
//        Log.d("sz_index", "onCreateView: sz_index");
//        TextView changeSzText = (TextView) v.findViewById(R.id.sz_change);
//        changeSzText.setText(changeSz);
//
//
//        TextView indexShText = (TextView) v.findViewById(R.id.sh_index);
//        indexShText.setText(indexSh);
//        TextView changeShText = (TextView) v.findViewById(R.id.sh_change);
//        changeShText.setText(changeSh);
//
//        TextView indexChuangText = (TextView) v.findViewById(R.id.chuang_index);
//        indexChuangText.setText(indexChuang);
//        TextView changeChuangText = (TextView) v.findViewById(R.id.chuang_change);
//        changeChuangText.setText(changeChuang);

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        viewFlipper.startFlipping();

        chatRoom_img = (ImageView) v.findViewById(R.id.to_chat_room);
        chatRoom_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    Intent intent = new Intent(getActivity(), MainChatActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Please sign in first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
//          news part
//        listNews = (ListView) v.findViewById(R.id.listNews);
//        loader = (ProgressBar) v.findViewById(R.id.loader);
//        listNews.setEmptyView(loader);
//
//
//
//        if(Function.isNetworkAvailable(getActivity().getApplicationContext()))
//        {
//            DownloadNews newsTask = new DownloadNews();
//            newsTask.execute();
//        }else{
//            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
//        }


        return v;
    }

//    public void updateThreeIndex() {
////        String stock_name = getResources().getString(R.string.stock_name);
////        String stock_id = getResources().getString(R.string.stock_id);
////        String stock_now = getResources().getString(R.string.stock_now);
//
////        ListView list = (ListView) findViewById(R.id.listView);
////
////        ArrayList<HashMap<String, String>> stockList = new ArrayList<>();
////        HashMap<String, String> mapTitle = new HashMap<>();
////        mapTitle.put(stock_name, getResources().getString(R.string.stock_name_title));
////        //mapTitle.put(stock_id, "");
////        mapTitle.put(stock_now, getResources().getString(R.string.stock_now_title));
////        stockList.add(mapTitle);
////
////        for(Stock stock : stocks)
////        {
////            HashMap<String, String> map = new HashMap<>();
////            map.put(stock_name, stock.name_);
////            String id = stock.id_.replaceAll("sh", "");
////            id = id.replaceAll("sz", "");
////            map.put(stock_id, id);
////            map.put(stock_now, stock.now_);
////            stockList.add(map);
////        }
////
////        SimpleAdapter adapter = new SimpleAdapter(this,
////                stockList,
////                R.layout.stock_listitem,
////                new String[] {stock_name, stock_id, stock_now},
////                new int[] {R.id.stock_name, R.id.stock_id, R.id.stock_now});
////        list.setAdapter(adapter);
//
//        Collection<MainActivity.Stock> stocks = MainActivity.stockMap.values();
//
//        for (MainActivity.Stock stock : stocks) {
//
//            if (stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)) {
//                Double dNow = Double.parseDouble(stock.now_);
//                Double dYesterday = Double.parseDouble(stock.yesterday_);
//                Double dIncrease = dNow - dYesterday;
//                Double dPercent = dIncrease / dYesterday * 100;
//                String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);
//
//                int indexId;
//                int changeId;
//                if (stock.id_.equals(ShIndex)) {
//                    indexId = R.id.sh_index;
//                    changeId = R.id.sh_change;
//                } else if (stock.id_.equals(SzIndex)) {
//                    indexId = R.id.sz_index;
//                    changeId = R.id.sz_change;
//                } else {
//                    indexId = R.id.chuang_index;
//                    changeId = R.id.chuang_change;
//                }
//
//                TextView indexText = (TextView) v.findViewById(indexId);
//                indexText.setText(stock.now_);
//                int color = Color.BLACK;
//                //System.out.println("-----|" + dIncrease + "|------");
//                if (dIncrease > 0) {
//                    color = UpColor_;
//                } else if (dIncrease < 0) {
//                    color = DownColor_;
//                }
//                //System.out.println("-----|" + color + "|------");
//                indexText.setTextColor(color);
//
//                TextView changeText = (TextView) v.findViewById(changeId);
//                changeText.setText(change);
//                changeText.setTextColor(color);
//
//                continue;
//            }
//        }
//    }

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            xml = Function.excuteGet("https://newsapi.org/v1/articles?source="+NEWS_SOURCE+"&sortBy=top&apiKey="+API_KEY, urlParameters);
            return  xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            if(xml.length()>10){ // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }

                ListNewsAdapter adapter = new ListNewsAdapter(getActivity(), dataList);
                listNews.setAdapter(adapter);

                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(getActivity(), DetailsActivity.class);
                        i.putExtra("url", dataList.get(+position).get(KEY_URL));
                        startActivity(i);
                    }
                });

            }else{
                Toast.makeText(getActivity().getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }



    }

}