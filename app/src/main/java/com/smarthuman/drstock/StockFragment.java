package com.smarthuman.drstock;

import android.Manifest;
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


    private final static int BackgroundColor_ = Color.WHITE;
    private final static int HighlightColor_ = Color.rgb(210, 233, 255);
    private final static String ShIndex = "sh000001";
    private final static String SzIndex = "sz399001";
    private final static String ChuangIndex = "sz399006";
    private final static String StockIdsKey_ = "StockIds";
    private Button addStockBtn;
    private View v;

//    public static HashSet<String> MainActivity.StockIds_ = new HashSet();  // [sz000001] [hk02318] [gb_lx]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stock, container, false);

//        MainActivity.StockIds_ = MainActivity.MainActivity.StockIds_;

        initEdt();
        initPop();

        addStockBtn = v.findViewById(R.id.stockFrag_searchStock);
        addStockBtn.setOnClickListener(this);

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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        mCompositeDisposable.clear();
        mPop.dismiss();

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
        EditText editText = (EditText) v.findViewById(R.id.editText_stockId);
        String inputID = editText.getText().toString();
        String enquryId = inputID;

        MainActivity.searchHistory.add(enquryId);
        System.out.println("--------searchHistory:");
        System.out.println(MainActivity.searchHistory);
        System.out.println("--------------------");

        Intent intent = new Intent(getActivity(), EachStockActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, inputID);
        startActivity(intent);
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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).refreshStocks();
    }
}