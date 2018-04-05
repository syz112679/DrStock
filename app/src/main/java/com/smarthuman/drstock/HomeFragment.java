package com.smarthuman.drstock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Collection;

/**
 * Created by jingle on 04/03/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    private final static int UpColor_ = Color.GREEN;
    private final static int DownColor_ = Color.RED;
    private final static int BackgroundColor_ = Color.WHITE;
    private final static int HighlightColor_ = Color.rgb(210, 233, 255);
    private final static String ShIndex = "sh000001";
    private final static String SzIndex = "sz399001";
    private final static String ChuangIndex = "sz399006";
    private final static String StockIdsKey_ = "StockIds";
    private final static int StockLargeTrade_ = 1000000;

    ViewFlipper viewFlipper;
    Button chatRoom_btn;
    private View v;

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

        chatRoom_btn = (Button) v.findViewById(R.id.to_chat_room);
        chatRoom_btn.setOnClickListener(new View.OnClickListener() {
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
        return v;
    }

    public void updateThreeIndex() {
//        String stock_name = getResources().getString(R.string.stock_name);
//        String stock_id = getResources().getString(R.string.stock_id);
//        String stock_now = getResources().getString(R.string.stock_now);

//        ListView list = (ListView) findViewById(R.id.listView);
//
//        ArrayList<HashMap<String, String>> stockList = new ArrayList<>();
//        HashMap<String, String> mapTitle = new HashMap<>();
//        mapTitle.put(stock_name, getResources().getString(R.string.stock_name_title));
//        //mapTitle.put(stock_id, "");
//        mapTitle.put(stock_now, getResources().getString(R.string.stock_now_title));
//        stockList.add(mapTitle);
//
//        for(Stock stock : stocks)
//        {
//            HashMap<String, String> map = new HashMap<>();
//            map.put(stock_name, stock.name_);
//            String id = stock.id_.replaceAll("sh", "");
//            id = id.replaceAll("sz", "");
//            map.put(stock_id, id);
//            map.put(stock_now, stock.now_);
//            stockList.add(map);
//        }
//
//        SimpleAdapter adapter = new SimpleAdapter(this,
//                stockList,
//                R.layout.stock_listitem,
//                new String[] {stock_name, stock_id, stock_now},
//                new int[] {R.id.stock_name, R.id.stock_id, R.id.stock_now});
//        list.setAdapter(adapter);

        Collection<MainActivity.Stock> stocks = MainActivity.stockMap.values();

        for (MainActivity.Stock stock : stocks) {

            if (stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)) {
                Double dNow = Double.parseDouble(stock.now_);
                Double dYesterday = Double.parseDouble(stock.yesterday_);
                Double dIncrease = dNow - dYesterday;
                Double dPercent = dIncrease / dYesterday * 100;
                String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);

                int indexId;
                int changeId;
                if (stock.id_.equals(ShIndex)) {
                    indexId = R.id.sh_index;
                    changeId = R.id.sh_change;
                } else if (stock.id_.equals(SzIndex)) {
                    indexId = R.id.sz_index;
                    changeId = R.id.sz_change;
                } else {
                    indexId = R.id.chuang_index;
                    changeId = R.id.chuang_change;
                }

                TextView indexText = (TextView) v.findViewById(indexId);
                indexText.setText(stock.now_);
                int color = Color.BLACK;
                //System.out.println("-----|" + dIncrease + "|------");
                if (dIncrease > 0) {
                    color = UpColor_;
                } else if (dIncrease < 0) {
                    color = DownColor_;
                }
                //System.out.println("-----|" + color + "|------");
                indexText.setTextColor(color);

                TextView changeText = (TextView) v.findViewById(changeId);
                changeText.setText(change);
                changeText.setTextColor(color);

                continue;
            }
        }
    }

}