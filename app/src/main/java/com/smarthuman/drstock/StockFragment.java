package com.smarthuman.drstock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

//--------------------------------------------------------------------------------------------------


public class StockFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    private final static int BackgroundColor_ = Color.WHITE;
    private final static int HighlightColor_ = Color.rgb(210, 233, 255);
    private final static String ShIndex = "sh000001";
    private final static String SzIndex = "sz399001";
    private final static String ChuangIndex = "sz399006";
    private final static String StockIdsKey_ = "StockIds";
    private Button addStockBtn;
    private View v;

    public static HashSet<String> StockIds_ = new HashSet();  // [sz000001] [hk02318] [gb_lx]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stock, container, false);

        StockIds_ = MainActivity.StockIds_;

        initEdt();
        initPop();

        addStockBtn = v.findViewById(R.id.stockFrag_addStock);
        addStockBtn.setOnClickListener(this);

        Log.d("mainActivity", "LIne 126");
        Timer timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStocks();
            }
        }, 0, 10000); // 10 seconds
        Log.d("mainActivity", "LIne 134");

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        mCompositeDisposable.clear();
        mPop.dismiss();

        saveStocksToPreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveStocksToPreferences();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
//                StockIds_.remove(selectedId);
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

    private void saveStocksToPreferences() {
        String ids = "";                        // sz000001,hk02318,gb_lx
        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(StockIdsKey_, ids);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stockFrag_addStock:
                Log.d("addstock", "here");
                addStock(v);
        }
    }

    public TreeMap<String, Stock> sinaResponseToStocks(String response) {
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");
        System.out.println("---------stocks:");
        for(String s:stocks) {
            System.out.println(s);
        }
        System.out.println("---------");

        TreeMap<String, Stock> stockMap = new TreeMap();
        for (String stock : stocks) {
            Stock stockNow = new Stock();

            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;
            String[] lefts = left.split("_");
            String market = lefts[2];
            if (market.length() == 2) { // US
                stockNow.marketId_ = "US";
                stockNow.size_ = 28;
            } else if (market.substring(0, 2).equals("hk")) {    // HK
                stockNow.marketId_ = market.substring(0, 2).toUpperCase();
                stockNow.size_ = 19;
            } else {
//                stockNow.marketId_ = market.substring(0, 2);
                stockNow.marketId_ = market.substring(0, 2).toUpperCase();
                stockNow.size_ = 33;
            }


            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;


            String[] values = right.split(",");
            for (int i = 0; i < values.length; i++) {
                stockNow.values[i] = values[i];
            }

            // TODO: English name for SH and SZ
            if (stockNow.marketId_.equals("US")) {
                stockNow.id_ = lefts[3];
                stockNow.name_ = lefts[3];

            } else if (stockNow.marketId_.equals("HK")) {
                stockNow.id_ = market.substring(2);
                stockNow.name_ = values[0];

            } else { // ZH & SH
                stockNow.id_ = market.substring(2);
                stockNow.name_ = values[0];

            }

            stockMap.put(stockNow.id_, stockNow);           // lx -> Stock
        }

        return stockMap;
    }

    public void querySinaStocks(String list) {          // sz000001,hk02318,gb_lx
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = "http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("***************************Response**************************");
//                        System.out.println(response);
//                        System.out.println("*****************************************************************");
                        updateStockListView(sinaResponseToStocks(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        queue.add(stringRequest);
    }

    public void refreshStocks() {

        String ids = "";
        for (String id : StockIds_) {
            ids += id;
            ids += ",";
        }

        querySinaStocks(ids);

    }

    public void addStock(View view) {
        Log.d("addstock", "add stock function here");
        EditText editText = (EditText) v.findViewById(R.id.editText_stockId);

        String inputID = editText.getText().toString();

        // TODO: US Market
        if (Character.isDigit(inputID.charAt(0))) {

            if (inputID.length() < 5 || inputID.length() > 6) {
                return;
            }

            if (inputID.length() == 5) {
                inputID = "hk" + inputID;
            } else if (inputID.startsWith("6")) {
                inputID = "sh" + inputID;
            } else if (inputID.startsWith("0") || inputID.startsWith("3")) {
                inputID = "sz" + inputID;
            } else
                return;
        } else {    // US
            inputID = "gb_" + inputID;
        }


        StockIds_.add(inputID);

        MainActivity.StockIds_ = StockIds_;

        refreshStocks();
    }

    //----------------------------------------------Followings are for search association-------------------------------------------------

    private static final String TAG = "MainActivity";
    private EditText editText;
    private CustomPopupWindow mPop; //显示搜索联想的pop
    private ListView searchLv; //搜索联想结果的列表
    private ArrayAdapter mAdapter; //ListView的适配器
    private List<String> mSearchList = new ArrayList<>(); //搜索结果的数据源
    private PublishSubject<String> mPublishSubject;
    private CompositeDisposable mCompositeDisposable;

    private void initEdt() {
        editText = (EditText) v.findViewById(R.id.editText_stockId);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    mPop.dismiss();
                } else {
                    //输入内容非空的时候才开始搜索
                    startSearch(s.toString());
                }
            }
        });

        mPublishSubject = PublishSubject.create();
        mPublishSubject.debounce(200, TimeUnit.MILLISECONDS) //这里我们限制只有在输入字符200毫秒后没有字符没有改变时才去请求网络，节省了资源
                .filter(new Predicate<String>() { //对源Observable产生的结果按照指定条件进行过滤，只有满足条件的结果才会提交给订阅者

                    @Override
                    public boolean test(String s) throws Exception {
                        //当搜索词为空时，不发起请求
                        return s.length() > 0;
                    }
                })
                /**
                 * flatmap:把Observable产生的结果转换成多个Observable，然后把这多个Observable
                 “扁平化”成一个Observable，并依次提交产生的结果给订阅者

                 *concatMap:操作符flatMap操作符不同的是，concatMap操作符在处理产生的Observable时，
                 采用的是“连接(concat)”的方式，而不是“合并(merge)”的方式，
                 这就能保证产生结果的顺序性，也就是说提交给订阅者的结果是按照顺序提交的，不会存在交叉的情况

                 *switchMap:与flatMap操作符不同的是，switchMap操作符会保存最新的Observable产生的
                 结果而舍弃旧的结果
                 **/
                .switchMap(new Function<String, ObservableSource<String>>() {

                    @Override
                    public ObservableSource<String> apply(String query) throws Exception {
                        return (ObservableSource<String>) getSearchObservable(query);
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {

                    @Override
                    public void onNext(String s) {
                        //显示搜索联想的结果
                        showSearchResult(s);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(mCompositeDisposable);
    }

    //开始搜索
    private void startSearch(String query) {
        mPublishSubject.onNext(query);
    }

    private Observable<String> getSearchObservable(final String query) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                //注意：这里只是模仿求取服务器数据，实际开发中需要你根据这个输入的关键字query去请求数据
                Log.d(TAG, "开始请求，关键词为：" + query);
                try {
                    Thread.sleep(100); //模拟网络请求，耗时100毫秒
                } catch (InterruptedException e) {
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onError(e);
                    }
                }
                if (!(query.contains("科") || query.contains("耐") || query.contains("七"))) {
                    //没有联想结果，则关闭pop
                    mPop.dismiss();
                    return;
                }
                Log.d("SearchActivity", "结束请求，关键词为：" + query);
                observableEmitter.onNext(query);
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 显示搜索结果
     */
    private void showSearchResult(String keyWords) {
        mSearchList.clear(); //先清空数据源
        switch (keyWords) {
            case "科比":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.kobe)));
                break;
            case "耐克":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.nike)));
                break;
            case "七夕":
                mSearchList.addAll(Arrays.asList(getResources().getStringArray(R.array.qixi)));
                break;
        }
        mAdapter.notifyDataSetChanged();
        mPop.showAsDropDown(editText, 0, 0); //显示搜索联想列表的pop
    }

    /**
     * 初始化Pop，pop的布局是一个列表
     */
    private void initPop() {
        mPop = new CustomPopupWindow.Builder(this.getActivity())
                .setContentView(R.layout.pop_search)
                .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
                .setBackgroundAlpha(1f)
                .build();
        searchLv = (ListView) mPop.getItemView(R.id.search_list_lv);
        mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mSearchList);
        searchLv.setAdapter(mAdapter);
        searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("result", mSearchList.get(position));
                //System.out.println("----------------startActivity--------------");
                startActivity(intent);

            }
        });
    }

    public void updateStockListView(TreeMap<String, Stock> stockMap) {

        // Table
        TableLayout table = v.findViewById(R.id.stock_table);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        table.removeAllViews();

        // Title
//        TableRow rowTitle = new TableRow(getActivity());
//
//        TextView nameTitle = new TextView(getActivity());
//        nameTitle.setText(getResources().getString(R.string.stock_name_title));
//        rowTitle.addView(nameTitle);
//
//        TextView nowTitle = new TextView(getActivity());
//        nowTitle.setGravity(Gravity.CENTER);
//        nowTitle.setText(getResources().getString(R.string.stock_now_title));
//        rowTitle.addView(nowTitle);
//
//        TextView percentTitle = new TextView(getActivity());
//        percentTitle.setGravity(Gravity.CENTER);
//        percentTitle.setText(getResources().getString(R.string.stock_increase_percent_title));
//        rowTitle.addView(percentTitle);
//
//        TextView increaseTitle = new TextView(getActivity());
//        increaseTitle.setGravity(Gravity.CENTER);
//        increaseTitle.setText(getResources().getString(R.string.stock_increase_title));
//        rowTitle.addView(increaseTitle);
//
//        table.addView(rowTitle);

        //

        Collection<Stock> stocks = stockMap.values();

        for (Stock stock : stocks) {
//            System.out.println("Stock stock");
//            if (stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)) {
//                Float dNow = Float.parseFloat(stock.now_);
//                Float dYesterday = Float.parseFloat(stock.yesterday_);
//                Float dIncrease = dNow - dYesterday;
//                Float dPercent = dIncrease / dYesterday * 100;
//                String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);
//
//                int indexId;
//                int changeId;
//                if (stock.id_.equals(ShIndex)) {
//                    indexId = R.id.stock_sh_index;
//                    changeId = R.id.stock_sh_change;
//                } else if (stock.id_.equals(SzIndex)) {
//                    indexId = R.id.stock_sz_index;
//                    changeId = R.id.stock_sz_change;
//                } else {
//                    indexId = R.id.stock_chuang_index;
//                    changeId = R.id.stock_chuang_change;
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

            TableRow row = new TableRow(getActivity());
            row.setMinimumHeight(200); //////////////////////////////////////////////
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout nameId = new LinearLayout(getActivity());
            nameId.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(getActivity());
            name.setText(stock.name_);
            nameId.addView(name);

            TextView id = new TextView(getActivity());
            id.setTextSize(15);
            String id_market = stock.id_ + "." + stock.marketId_;
            id.setText(id_market);
            nameId.addView(id);

            row.addView(nameId);

            TextView now = new TextView(getActivity());
            now.setGravity(Gravity.RIGHT);
            now.setText(stock.getCurrentPrice_());
            row.addView(now);

            TextView percent = new TextView(getActivity());
            percent.setGravity(Gravity.RIGHT);
            TextView increaseValue = new TextView(getActivity());
            increaseValue.setGravity(Gravity.RIGHT);

            percent.setText(stock.getChangePercent() + "%");
            increaseValue.setText("--");
            int color = Color.BLACK;
            if (stock.getChangePercent().charAt(0) == '-') {
                color = MainActivity.DownColor_;
            } else {
                color = MainActivity.UpColor_;
            }

            now.setTextColor(color);
            percent.setTextColor(color);
            increaseValue.setTextColor(color);

            row.addView(percent);
            row.addView(increaseValue);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup group = (ViewGroup) v;
                    ViewGroup nameId = (ViewGroup) group.getChildAt(0);
                    TextView idText = (TextView) nameId.getChildAt(1);
                    String stockID_Market = idText.getText().toString();

                    Intent intent = new Intent(getActivity(), EachStockActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, stockID_Market);
                    startActivity(intent);

                    System.out.println("-----\n" + idText.getText().toString() + "\n-------");
                }
            });

            table.addView(row);
        }
    }
}