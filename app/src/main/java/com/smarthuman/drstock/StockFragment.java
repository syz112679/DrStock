package com.smarthuman.drstock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

//--------------------------------------------------------------------------------------------------


public class StockFragment extends Fragment implements View.OnClickListener {


    private final static int BackgroundColor_ = Color.WHITE;
    private final static int HighlightColor_ = Color.rgb(210, 233, 255);
    private final static String ShIndex = "sh000001";
    private final static String SzIndex = "sz399001";
    private final static String ChuangIndex = "sz399006";
    private final static String StockIdsKey_ = "StockIds";
    private Button searchBtn;
    private ImageView top20Img;
    private RefreshLayout refreshLayout;
    private View v;


//    public static HashSet<String> MainActivity.StockIds_ = new HashSet();  // [sz000001] [hk02318] [gb_lx]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stock, container, false);

//        MainActivity.StockIds_ = MainActivity.MainActivity.StockIds_;

//        initEdt();
//        initPop();

        searchBtn = v.findViewById(R.id.stockFrag_searchStock);
        searchBtn.setOnClickListener(this);

        top20Img = v.findViewById(R.id.top_20);
        top20Img.setOnClickListener(this);

        refreshLayout = v.findViewById(R.id.fragment_stock);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                MainActivity.requireRefresh = true;
                System.out.println("requireRefresh: " + MainActivity.requireRefresh);
                refreshlayout.finishRefresh(2000);
            }
        });

//        refreshStocks();

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stockFrag_searchStock:
                Log.d("addstock", "here");
//                addStock(v);
                searchStock(v);
                break;
            case R.id.top_20:
                Intent intent = new Intent(getActivity(), Top20_Activity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

//        mCompositeDisposable.clear();
//        mPop.dismiss();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        else if(id == R.id.action_delete){
//            if(MainActivity.SelectedStockItems_.isEmpty())
//                return true;
//
//            for (String selectedId : MainActivity.SelectedStockItems_){
//                MainActivity.StockIds_.remove(selectedId);
//                TableLayout table = (TableLayout)v.findViewById(R.id.stock_table);
//                int count = table.getChildCount();
//                for (int i = 1; i < count; i++){
//                    TableRow row = (TableRow)table.getChildAt(i);
//                    LinearLayout nameId = (LinearLayout)row.getChildAt(0);
//                    TextView idText = (TextView)nameId.getChildAt(1);
//                    if(idText != null && idText.getText().toString() == selectedId){
//                        table.removeView(row);
//                        break;
//                    }
//                }
//            }
//
//            MainActivity.SelectedStockItems_.clear();
//        } else if (id == R.id.action_detail) {
//            // send intent to Kmap_activity
//            if (!MainActivity.SelectedStockItems_.isEmpty()) {
//
//
////                Intent intent = new Intent(MainActivity.this, KChartActivity.class);
////                intent.putExtra("stockId", SelectedStockItems_.elementAt(0));
////                //System.out.println("----------------startActivity--------------");
////                startActivity(intent);
//            }
//        } else if (id == R.id.action_add) {
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }




    public static String input2enqury(String inputID) {

        if (Character.isDigit(inputID.charAt(0))) {

            if (inputID.length() < 5 || inputID.length() > 6) {
                return null;
            }

            if (inputID.length() == 5) {
                inputID = "hk" + inputID;
            } else if (inputID.startsWith("6")) {
                inputID = "sh" + inputID;
            } else if (inputID.startsWith("0") || inputID.startsWith("3")) {
                inputID = "sz" + inputID;
            } else
                return null;
        } else {    // US
            inputID = "gb_" + inputID;
        }

        return inputID;
    }

    public void searchStock(View view) {
        MainActivity.search = v.findViewById(R.id.editText_stockId);
        String inputID = MainActivity.search.getText().toString();

        // TODO: inputId to enqueryId

        String enquryId = input2enqury(inputID);
        if (enquryId == null) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.toast_invalid_input, Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.searchHistory.add(enquryId);
        System.out.println("--------searchHistory:");
        System.out.println(MainActivity.searchHistory);
        System.out.println("--------------------");

        Intent intent = new Intent(getActivity(), EachStockActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, enquryId);
        startActivity(intent);
    }

    //----------------------------------------------Followings are for search association-------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.requireRefresh=true;
        //getActivity().setTitle(R.string.title_stock);
    }
}