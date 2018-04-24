package com.smarthuman.drstock;

import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.smarthuman.drstock.StockDayBean;
import com.smarthuman.drstock.StockListBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/17.
 */
public class Model {
    public static String data = " ";
    public static String dataF = " ";
    public static String dataR = " ";
    public static int usitemnum;
    //k line
    public static void setData(String d) {
        data = d;
        System.out.println("-----Model setdata-----:"+data);
    }
    //fenshi
    public static void setDataF(String d) {
        dataF = d;
        System.out.println("-----Model setdataF-----:"+dataF);
    }

    public static List<String> getDate(){
        return new Gson().fromJson(data, StockListBean.class).getX_axis().getLabels();
    }

    public static List<String> getMin(){
        return new Gson().fromJson(dataF, StockListBean.class).getX_axis().getLabels();
    }

    public static List<Float> getPrice(){
        return new Gson().fromJson(dataF, StockDayBean.class).getPrice().getValues();
    }

    //Rsi chart
    public static void setData_Rsi(String d){
        dataR = d;
        System.out.println("-----Model setdataR-----:"+dataR);
    }

    public static List<String> getUSMin(){
        Gson gson = new Gson();
        List<String> dateStr = new ArrayList<>() ;
        StockUSDayListBean.response response = gson.fromJson(dataF, StockUSDayListBean.response.class);
        Map<String, StockUSDayListBean.eachTime> timeSeries = response.getTimeSeries();
        List<String> keys = new ArrayList(timeSeries.keySet());
        int index = 0;
        for (Map.Entry<String,StockUSDayListBean.eachTime> pair:timeSeries.entrySet()){//遍历取出键值对，调用getkey()，getvalue()取出key和value。
            String xaxis = pair.getKey().substring(11,16);
            dateStr.add(xaxis);
            System.out.println("-----KEY-----: "+xaxis);
            String daydate = keys.get(index).substring(8, 10);
            String nextdate = keys.get(index+1).substring(8, 10);
            if(!daydate.equals(nextdate)){
                break;
            }
            index++;
        }
        usitemnum = index;
        return dateStr;
    }

    public static List<String> getCHNMin(){
        List<String> dateStr = new ArrayList<>() ;
        List<StockCHNDayListBean.StockBean> rawData = getCHNDataF();
        for(int i = 0; i < rawData.size(); i++){
            StockCHNDayListBean.StockBean stock = rawData.get(i);
            String rawMin = stock.getTime();
            if(rawMin.length() == 8){
                String min = rawMin.substring(0,3);
                StringBuilder str = new StringBuilder(min);
                str.insert(1, ':');
                min = str.toString();
                dateStr.add(min);
            }
            else{
                String min = rawMin.substring(0,4);
                StringBuilder str = new StringBuilder(min);
                str.insert(2, ':');
                min = str.toString();
                dateStr.add(min);
            }
        }
        return dateStr;
    }

    public static List<StockCHNDayListBean.StockBean> getCHNDataF(){
        return new Gson().fromJson(dataF, StockCHNDayListBean.class).getTimeLine();
    }

    public static List<String> getUSDate(){
        Gson gson = new Gson();
        List<String> dateStr = new ArrayList<>() ;
        StockUSKLineBean.response response = gson.fromJson(data, StockUSKLineBean.response.class);
        Map<String, StockUSKLineBean.eachTime> timeSeries = response.getTimeSeries();
        for (Map.Entry<String,StockUSKLineBean.eachTime> pair:timeSeries.entrySet()){//遍历取出键值对，调用getkey()，getvalue()取出key和value。
            dateStr.add(pair.getKey());
            System.out.println("-----KEY-----: "+pair.getKey());
        }
        return dateStr;
    }

    public static List<String> getCHNDate(){
        List<StockCHNKLineBean.StockBean> rawData = getCHNData();
        List<String> dateStr = new ArrayList<>();
        for(int i = 0; i < rawData.size(); i++){
            String date = rawData.get(i).getDate();
            dateStr.add(date);
        }
        return dateStr;
    }


    public static List<StockUSDayListBean.eachTime> getUSDataF() {
        Gson gson = new Gson();
        //return new Gson().fromJson(data, StockListBean.class).getContent();
        System.out.println("-----Model gggetdata-----:"+dataF);
        StockUSDayListBean.response response = gson.fromJson(dataF, StockUSDayListBean.response.class);
        Map<String, StockUSDayListBean.eachTime> timeSeries = response.getTimeSeries();
        List<StockUSDayListBean.eachTime> retlist = new ArrayList<>() ;
        List<String> keys = new ArrayList(timeSeries.keySet());
        //for (Map.Entry<String,StockUSDayListBean.eachTime> pair:timeSeries.entrySet()){//遍历取出键值对，调用getkey()，getvalue()取出key和value。
        for(int i = 0; i < usitemnum; i ++){
            String key = keys.get(i);
            StockUSDayListBean.eachTime val = timeSeries.get(key);
            System.out.println("-----KEY-----: "+key);
            retlist.add(val);
        }
        return retlist;
    }

    public static List<StockUSKLineBean.eachTime> getUSData() {
        Gson gson = new Gson();
        StockUSKLineBean.response response = gson.fromJson(data, StockUSKLineBean.response.class);
        Map<String, StockUSKLineBean.eachTime> timeSeries = response.getTimeSeries();
        List<StockUSKLineBean.eachTime> retlist = new ArrayList<>() ;
        for (Map.Entry<String,StockUSKLineBean.eachTime> pair:timeSeries.entrySet()){//遍历取出键值对，调用getkey()，getvalue()取出key和value。
            System.out.println("-----KEY-----: "+pair.getKey());
            retlist.add(pair.getValue());
        }
        return retlist;

    }

    public static List<StockCHNKLineBean.StockBean> getCHNData(){
        return new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
    }

//fenshi chart us stock
public static List<Entry> getUSLineEntries(){
    List<StockUSDayListBean.eachTime> rawData = getUSDataF();
    List<Entry> entries = new ArrayList<>();
    for (int i = rawData.size()-1; i >= 0; i--) {
        Float price = rawData.get(i).getOpen();
        if(price!=null) {
            Entry entry = new Entry(price, rawData.size()-1 - i);
            entries.add(entry);
        }
        else{
            //Entry entry = new Entry(rawDataPrice.get(i-1), i);
            //entries.add(entry);
        }
    }
    return entries;
}

    //fenshi us barchart
    public static List<BarEntry> getUSBarEntries(){
        List<StockUSDayListBean.eachTime> rawData = getUSDataF();
        List<BarEntry> entries = new ArrayList<>();
        for (int i = rawData.size()-1; i >= 0; i--) {
            Integer vol = Integer.parseInt(rawData.get(i).getVolume());
            BarEntry entry = new BarEntry(vol, rawData.size()-1 - i);
            entries.add(entry);
        }
        return entries;
    }

    //fenshi chn linechart
    public static List<Entry> getCHNLineEntries(){
        List<StockCHNDayListBean.StockBean> rawData = getCHNDataF();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            Float price = rawData.get(i).getPrice();
            if(price!=null) {
                Entry entry = new Entry(price, i);
                entries.add(entry);
            }
            else{
                //Entry entry = new Entry(rawDataPrice.get(i-1), i);
                //entries.add(entry);
            }
        }
        return entries;
    }

    //fenshi chn barchart
    public static List<BarEntry> getCHNBarEntries(){
        List<StockCHNDayListBean.StockBean> rawData = getCHNDataF();
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            Integer vol = Integer.parseInt(rawData.get(i).getVolume());
            BarEntry entry = new BarEntry(vol, i);
            entries.add(entry);
        }
        return entries;
    }


    //fenshi linechart
    public static List<Entry> getLineEntries(){
        List<Float> rawDataPrice = new Gson().fromJson(dataF, StockDayBean.class).getPrice().getValues();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < rawDataPrice.size(); i++) {
            Float price = rawDataPrice.get(i);
            if(price!=null) {
                Entry entry = new Entry(price, i);
                entries.add(entry);
            }
            else{}
        }
        return entries;
    }

    //fenshi barchart
    public static List<BarEntry> getBarEntries(){
        List<Integer> rawDataVol = new Gson().fromJson(dataF, StockDayBean.class).getVol().getValues();
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < rawDataVol.size(); i++) {
            Integer vol = rawDataVol.get(i);
            BarEntry entry = new BarEntry(vol,i);
            entries.add(entry);
        }
        return entries;
    }

    //1 month us k candle chart
    public static List<CandleEntry> getUSCandleEntries_1month() {
        List<StockUSKLineBean.eachTime> rawData = getUSData();
        Log.d("-----TAG Model-----", rawData.get(0).getVolume());
        return getUSCandleEntries(rawData,21);
    }

    //3 month us k candle chart
    public static List<CandleEntry> getUSCandleEntries_3month() {
        List<StockUSKLineBean.eachTime> rawData = getUSData();
        Log.d("-----TAG Model-----", rawData.get(0).getVolume());
        return getUSCandleEntries(rawData,60);
    }

    //1 year us k candle chart
    public static List<CandleEntry> getUSCandleEntries_1year() {
        List<StockUSKLineBean.eachTime> rawData = getUSData();
        Log.d("-----TAG Model-----", rawData.get(0).getVolume());
        return getUSCandleEntries(rawData,246);
    }

    //3 year us k candle chart
    public static List<CandleEntry> getUSCandleEntries_3year() {
        List<StockUSKLineBean.eachTime> rawData = getUSData();
        Log.d("-----TAG Model-----", rawData.get(0).getVolume());
        return getUSCandleEntries(rawData,740);
    }

    //1 month chn k candle chart
    public static List<CandleEntry> getCHNCandleEntries_1month() {
        List<StockCHNKLineBean.StockBean> rawData = getCHNData();
        //Log.d("-----TAG Model-----", rawData.get(0).);
        return getCHNCandleEntries(rawData,21);
    }

    //3 month chn k candle chart
    public static List<CandleEntry> getCHNCandleEntries_3month() {
        List<StockCHNKLineBean.StockBean> rawData = getCHNData();
        //Log.d("-----TAG Model-----", rawData.get(0).);
        return getCHNCandleEntries(rawData,60);
    }

    //1 year chn k candle chart
    public static List<CandleEntry> getCHNCandleEntries_1year() {
        List<StockCHNKLineBean.StockBean> rawData = getCHNData();
        //Log.d("-----TAG Model-----", rawData.get(0).);
        return getCHNCandleEntries(rawData,246);
    }

    //3 year chn k candle chart
    public static List<CandleEntry> getCHNCandleEntries_3year() {
        List<StockCHNKLineBean.StockBean> rawData = getCHNData();
        //Log.d("-----TAG Model-----", rawData.get(0).);
        return getCHNCandleEntries(rawData,719);
    }

    //1 month k line chart candle entries
    public static List<CandleEntry> getCandleEntries_1month() {
        List<Float> rawDataHigh = new Gson().fromJson(data, StockListBean.class).getHigh().getValues();
        List<Float> rawDataLow = new Gson().fromJson(data, StockListBean.class).getLow().getValues();
        List<Float> rawDataOpen = new Gson().fromJson(data, StockListBean.class).getOpen().getValues();
        List<Float> rawDataClose = new Gson().fromJson(data, StockListBean.class).getPrice().getValues();
        String rawDataEname = new Gson().fromJson(data, StockListBean.class).getEng_name().getValues();
        return getCandleEntries(rawDataHigh, rawDataLow, rawDataOpen, rawDataClose,rawDataHigh.size()-21);
    }

    //3 months k line chart candle entries
    public static List<CandleEntry> getCandleEntries_3month() {
        List<Float> rawDataHigh = new Gson().fromJson(data, StockListBean.class).getHigh().getValues();
        List<Float> rawDataLow = new Gson().fromJson(data, StockListBean.class).getLow().getValues();
        List<Float> rawDataOpen = new Gson().fromJson(data, StockListBean.class).getOpen().getValues();
        List<Float> rawDataClose = new Gson().fromJson(data, StockListBean.class).getPrice().getValues();
        String rawDataEname = new Gson().fromJson(data, StockListBean.class).getEng_name().getValues();
        return getCandleEntries(rawDataHigh, rawDataLow, rawDataOpen, rawDataClose,rawDataHigh.size()-60);
    }

    //1 year k line chart candle entries
    public static List<CandleEntry> getCandleEntries_1year() {
        List<Float> rawDataHigh = new Gson().fromJson(data, StockListBean.class).getHigh().getValues();
        List<Float> rawDataLow = new Gson().fromJson(data, StockListBean.class).getLow().getValues();
        List<Float> rawDataOpen = new Gson().fromJson(data, StockListBean.class).getOpen().getValues();
        List<Float> rawDataClose = new Gson().fromJson(data, StockListBean.class).getPrice().getValues();
        String rawDataEname = new Gson().fromJson(data, StockListBean.class).getEng_name().getValues();
        return getCandleEntries(rawDataHigh, rawDataLow, rawDataOpen, rawDataClose,rawDataHigh.size()-246);
    }

    //3 year k line chart candle entries
    public static List<CandleEntry> getCandleEntries_3year() {
        List<Float> rawDataHigh = new Gson().fromJson(data, StockListBean.class).getHigh().getValues();
        List<Float> rawDataLow = new Gson().fromJson(data, StockListBean.class).getLow().getValues();
        List<Float> rawDataOpen = new Gson().fromJson(data, StockListBean.class).getOpen().getValues();
        List<Float> rawDataClose = new Gson().fromJson(data, StockListBean.class).getPrice().getValues();
        String rawDataEname = new Gson().fromJson(data, StockListBean.class).getEng_name().getValues();
        return getCandleEntries(rawDataHigh, rawDataLow, rawDataOpen, rawDataClose,0);
    }

    //chn stock candle data
    public static List<CandleEntry> getCHNCandleEntries(List<StockCHNKLineBean.StockBean> rawData, int endIndex){
        List<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            StockCHNKLineBean.StockBean.KLine stock = rawData.get(i).getKline();
            if (stock == null) {
                Log.e("xxx", "第" + i + "StockBean==null");
                continue;
            }
            //System.out.println("----CHN kline high----: " + stock.getHigh());
            CandleEntry entry = new CandleEntry(endIndex-i, stock.getHigh(), stock.getLow(), stock.getOpen(), stock.getClose());
            entries.add(entry);
        }

        return entries;
    }

    //us stock candle data
    public static List<CandleEntry> getUSCandleEntries(List<StockUSKLineBean.eachTime> rawData, int endIndex) {
        List<CandleEntry> entries = new ArrayList<>();

        for (int i = 0; i < rawData.size(); i++) {
            StockUSKLineBean.eachTime stock = rawData.get(i);
            if (stock == null) {
                Log.e("xxx", "第" + i + "StockBean==null");
                continue;
            }
            CandleEntry entry = new CandleEntry(endIndex-i, stock.getHigh(), stock.getLow(), stock.getOpen(), stock.getClose());
            entries.add(entry);
        }

        return entries;
    }

    //money18 candle data
    public static List<CandleEntry> getCandleEntries(List<Float> rawDataHigh,List<Float> rawDataLow, List<Float> rawDataOpen, List<Float> rawDataClose,int startIndex) {
        List<CandleEntry> entries = new ArrayList<>();


        for (int i = startIndex; i < rawDataHigh.size(); i++) {
            Float high = rawDataHigh.get(i);
            Float low = rawDataLow.get(i);
            Float open = rawDataOpen.get(i);
            Float close = rawDataClose.get(i);
            if (high == null||low == null) {
                Log.e("xxx", "第" + i + "StockBean==null");
                continue;
            }
            CandleEntry entry = new CandleEntry(i-startIndex, high, low, open, close);
            entries.add(entry);
        }

        return entries;
    }

    //chn and us stock 1 month ma line
    public static List<Entry> getMa5CHNEntries_1month(){
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa5Entries(entry, 21);
    }

    public static List<Entry> getMa10CHNEntries_1month() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa10Entries(entry, 21);
    }

    public static List<Entry> getMa20CHNEntries_1month() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa20Entries(entry, 21);
    }

    //chn and us stock 3 month ma line
    public static List<Entry> getMa5CHNEntries_3month(){
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa5Entries(entry, 60);
    }

    public static List<Entry> getMa10CHNEntries_3month() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa10Entries(entry, 60);
    }

    public static List<Entry> getMa20CHNEntries_3month() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa20Entries(entry, 60);
    }

    //chn and us stock 1 year ma line
    public static List<Entry> getMa5CHNEntries_1year(){
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa5Entries(entry, 246);
    }

    public static List<Entry> getMa10CHNEntries_1year() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa10Entries(entry, 246);
    }

    public static List<Entry> getMa20CHNEntries_1year() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa20Entries(entry, 246);
    }

    //chn and us stock 3 year ma line
    public static List<Entry> getMa5CHNEntries_3year(){
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa5Entries(entry, 720);
    }

    public static List<Entry> getMa10CHNEntries_3year() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa10Entries(entry, 720);
    }

    public static List<Entry> getMa20CHNEntries_3year() {
        List<StockCHNKLineBean.StockBean> entry = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getCHNMa20Entries(entry, 720);
    }

    public static List<Entry> getCHNMa5Entries(List<StockCHNKLineBean.StockBean> ma5Entries, int endIndex){

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            Float ma5 = ma5Entries.get(i).getMa5().getAvgPrice();
            Entry entry = new Entry(ma5,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getCHNMa10Entries(List<StockCHNKLineBean.StockBean> ma10Entries, int endIndex){

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            Float ma10 = ma10Entries.get(i).getMa10().getAvgPrice();
            Entry entry = new Entry(ma10,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getCHNMa20Entries(List<StockCHNKLineBean.StockBean> ma20Entries, int endIndex){

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            Float ma20 = ma20Entries.get(i).getMa20().getAvgPrice();
            Entry entry = new Entry(ma20,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }


    //1 month ma lines
    public static List<Entry> getMa10Entries_1month(){
        List<Float> ma10Entries = new Gson().fromJson(data, StockListBean.class).getSma10().getValues();
        return getMa10Entries(ma10Entries, ma10Entries.size()-21);
    }

    public static List<Entry> getMa20Entries_1month(){
        List<Float> ma20Entries = new Gson().fromJson(data, StockListBean.class).getSma20().getValues();
        return getMa20Entries(ma20Entries, ma20Entries.size()-21);
    }

    public static List<Entry> getMa50Entries_1month(){
        List<Float> ma50Entries = new Gson().fromJson(data, StockListBean.class).getSma50().getValues();
        return getMa50Entries(ma50Entries, ma50Entries.size()-21);
    }

    //3 months ma lines
    public static List<Entry> getMa10Entries_3month(){
        List<Float> ma10Entries = new Gson().fromJson(data, StockListBean.class).getSma10().getValues();
        return getMa10Entries(ma10Entries, ma10Entries.size()-60);
    }

    public static List<Entry> getMa20Entries_3month(){
        List<Float> ma20Entries = new Gson().fromJson(data, StockListBean.class).getSma20().getValues();
        return getMa20Entries(ma20Entries, ma20Entries.size()-60);
    }

    public static List<Entry> getMa50Entries_3month(){
        List<Float> ma50Entries = new Gson().fromJson(data, StockListBean.class).getSma50().getValues();
        return getMa50Entries(ma50Entries, ma50Entries.size()-60);
    }

    //1 year ma lines
    public static List<Entry> getMa10Entries_1year(){
        List<Float> ma10Entries = new Gson().fromJson(data, StockListBean.class).getSma10().getValues();
        return getMa10Entries(ma10Entries, ma10Entries.size()-246);
    }

    public static List<Entry> getMa20Entries_1year(){
        List<Float> ma20Entries = new Gson().fromJson(data, StockListBean.class).getSma20().getValues();
        return getMa20Entries(ma20Entries, ma20Entries.size()-246);
    }

    public static List<Entry> getMa50Entries_1year(){
        List<Float> ma50Entries = new Gson().fromJson(data, StockListBean.class).getSma50().getValues();
        return getMa50Entries(ma50Entries, ma50Entries.size()-246);
    }

    //1 year ma lines
    public static List<Entry> getMa10Entries_3year(){
        List<Float> ma10Entries = new Gson().fromJson(data, StockListBean.class).getSma10().getValues();
        return getMa10Entries(ma10Entries, 0);
    }

    public static List<Entry> getMa20Entries_3year(){
        List<Float> ma20Entries = new Gson().fromJson(data, StockListBean.class).getSma20().getValues();
        return getMa20Entries(ma20Entries, 0);
    }

    public static List<Entry> getMa50Entries_3year(){
        List<Float> ma50Entries = new Gson().fromJson(data, StockListBean.class).getSma50().getValues();
        return getMa50Entries(ma50Entries, 0);
    }





    public static List<Entry> getMa10Entries(List<Float> ma10Entries, int startIndex){

        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < ma10Entries.size(); i++) {
            Float ma10 = ma10Entries.get(i);
            Entry entry = new Entry(ma10,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getMa20Entries(List<Float> ma20Entries, int startIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < ma20Entries.size(); i++) {
            Float ma20 = ma20Entries.get(i);
            Entry entry = new Entry(ma20,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getMa50Entries(List<Float> ma50Entries, int startIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < ma50Entries.size(); i++) {
            Float ma50 = ma50Entries.get(i);
            Entry entry = new Entry(ma50,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

    //1 month
    public static List<Entry> getRsi6Entries_1month(){
        List<StockCHNKLineBean.StockBean> rsi_6Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi6Entries(rsi_6Entries, 21);
    }

    public static List<Entry> getRsi12Entries_1month(){
        List<StockCHNKLineBean.StockBean> rsi_12Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi12Entries(rsi_12Entries, 21);
    }

    public static List<Entry> getRsi24Entries_1month(){
        List<StockCHNKLineBean.StockBean> rsi_24Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi24Entries(rsi_24Entries, 21);
    }

    //3 month
    public static List<Entry> getRsi6Entries_3month(){
        List<StockCHNKLineBean.StockBean> rsi_6Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi6Entries(rsi_6Entries, 60);
    }

    public static List<Entry> getRsi12Entries_3month(){
        List<StockCHNKLineBean.StockBean> rsi_12Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi12Entries(rsi_12Entries, 60);
    }

    public static List<Entry> getRsi24Entries_3month(){
        List<StockCHNKLineBean.StockBean> rsi_24Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi24Entries(rsi_24Entries, 60);
    }

    //1 year
    public static List<Entry> getRsi6Entries_1year(){
        List<StockCHNKLineBean.StockBean> rsi_6Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi6Entries(rsi_6Entries, 246);
    }

    public static List<Entry> getRsi12Entries_1year(){
        List<StockCHNKLineBean.StockBean> rsi_12Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi12Entries(rsi_12Entries, 246);
    }

    public static List<Entry> getRsi24Entries_1year(){
        List<StockCHNKLineBean.StockBean> rsi_24Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi24Entries(rsi_24Entries, 246);
    }

    //3 year
    public static List<Entry> getRsi6Entries_3year(){
        List<StockCHNKLineBean.StockBean> rsi_6Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi6Entries(rsi_6Entries, 720);
    }

    public static List<Entry> getRsi12Entries_3year(){
        List<StockCHNKLineBean.StockBean> rsi_12Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi12Entries(rsi_12Entries, 720);
    }

    public static List<Entry> getRsi24Entries_3year(){
        List<StockCHNKLineBean.StockBean> rsi_24Entries = new Gson().fromJson(data, StockCHNKLineBean.class).getMashData();
        return getRsi24Entries(rsi_24Entries, 720);
    }

    public static List<Entry> getRsi6Entries(List<StockCHNKLineBean.StockBean> rsi_6Entries, int endIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            StockCHNKLineBean.StockBean d = rsi_6Entries.get(i);
            Float rsi_6 = d.getRsi().getRsi1();
            Entry entry = new Entry(rsi_6,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getRsi12Entries(List<StockCHNKLineBean.StockBean> rsi_12Entries, int endIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            StockCHNKLineBean.StockBean d = rsi_12Entries.get(i);
            Float rsi_12 = d.getRsi().getRsi2();
            Entry entry = new Entry(rsi_12,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getRsi24Entries(List<StockCHNKLineBean.StockBean> rsi_24Entries, int endIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            StockCHNKLineBean.StockBean d = rsi_24Entries.get(i);
            Float rsi_24 = d.getRsi().getRsi3();
            Entry entry = new Entry(rsi_24,endIndex - 1 - i);
            entries.add(entry);
        }
        return entries;
    }

    //k line RSI chart

    //1 month
    public static List<Entry> getRsi10Entries_1month(){
        List<Float> rsi_10Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi10().getValues();
        return getRsi10Entries(rsi_10Entries, rsi_10Entries.size()-21);
    }

    public static List<Entry> getRsi14Entries_1month(){
        List<Float> rsi_14Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi14().getValues();
        return getRsi14Entries(rsi_14Entries, rsi_14Entries.size()-21);
    }

    public static List<Entry> getRsi20Entries_1month(){
        List<Float> rsi_20Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi20().getValues();
        return getRsi20Entries(rsi_20Entries, rsi_20Entries.size()-21);
    }

    //3 months

    public static List<Entry> getRsi10Entries_3month(){
        List<Float> rsi_10Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi10().getValues();
        return getRsi10Entries(rsi_10Entries, rsi_10Entries.size()-60);
    }

    public static List<Entry> getRsi14Entries_3month(){
        List<Float> rsi_14Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi14().getValues();
        return getRsi14Entries(rsi_14Entries, rsi_14Entries.size()-60);
    }

    public static List<Entry> getRsi20Entries_3month(){
        List<Float> rsi_20Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi20().getValues();
        return getRsi20Entries(rsi_20Entries, rsi_20Entries.size()-60);
    }

    //1 year

    public static List<Entry> getRsi10Entries_1year(){
        List<Float> rsi_10Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi10().getValues();
        return getRsi10Entries(rsi_10Entries, rsi_10Entries.size()-246);
    }

    public static List<Entry> getRsi14Entries_1year(){
        List<Float> rsi_14Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi14().getValues();
        return getRsi14Entries(rsi_14Entries, rsi_14Entries.size()-246);
    }

    public static List<Entry> getRsi20Entries_1year(){
        List<Float> rsi_20Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi20().getValues();
        return getRsi20Entries(rsi_20Entries, rsi_20Entries.size()-246);
    }

    //3 years
    public static List<Entry> getRsi10Entries_3year(){
        List<Float> rsi_10Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi10().getValues();
        return getRsi10Entries(rsi_10Entries, 0);
    }

    public static List<Entry> getRsi14Entries_3year(){
        List<Float> rsi_14Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi14().getValues();
        return getRsi14Entries(rsi_14Entries, 0);
    }

    public static List<Entry> getRsi20Entries_3year(){
        List<Float> rsi_20Entries = new Gson().fromJson(dataR, StockRsiBean.class).getRsi20().getValues();
        return getRsi20Entries(rsi_20Entries, 0);
    }




    public static List<Entry> getRsi10Entries(List<Float> rsi_10Entries, int startIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < rsi_10Entries.size(); i++) {
            Float rsi_10 = rsi_10Entries.get(i);
            Entry entry = new Entry(rsi_10,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getRsi14Entries(List<Float> rsi_14Entries, int startIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < rsi_14Entries.size(); i++) {
            Float rsi_14 = rsi_14Entries.get(i);
            Entry entry = new Entry(rsi_14,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

    public static List<Entry> getRsi20Entries(List<Float> rsi_20Entries, int startIndex){
        List<Entry> entries = new ArrayList<>();
        for (int i = startIndex; i < rsi_20Entries.size(); i++) {
            Float rsi_20 = rsi_20Entries.get(i);
            Entry entry = new Entry(rsi_20,i-startIndex);
            entries.add(entry);
        }
        return entries;
    }

}