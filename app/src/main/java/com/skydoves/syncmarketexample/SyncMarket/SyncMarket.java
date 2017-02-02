
/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.syncmarketexample.SyncMarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SyncMarket {

    private static Context mContext;
    private static boolean isExistUrl = false;

    private static final int timeout = 5000;
    public static final int DialogResult = 65535;

    private static final String TAG = "SyncMarket";

    public static void Initialize(Context context){
        mContext = context;

        SharedPreferences prefs = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
        isExistUrl = prefs.getBoolean("isExistUrl", false);
        if(!isExistUrl){
            CheckIsExistUrl checkIsExistUrl = new CheckIsExistUrl();
            checkIsExistUrl.execute();
        }
    }

    public static String getMarketUrl(){
        return "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
    }

    public static String getMarketVersion(){
        String version = null;
        try {
            version = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            MarketInformation marketInformation = new MarketInformation();
            String marketVersion = marketInformation.execute(mContext.getPackageName(), "softwareVersion").get();
            return marketVersion;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return version;
    }

    public static boolean isVersionEqual(){
        boolean isEqual = false;
        try {
            String version = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            MarketInformation marketInformation = new MarketInformation();
            String marketVersion = marketInformation.execute(mContext.getPackageName(), "softwareVersion").get();
            if(version.equals(marketVersion)) isEqual = true;
            return isEqual;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return isEqual;
    }

    public static String getMarketDownloads(){
        try {
            MarketInformation marketInformation = new MarketInformation();
            String marketDownloads = marketInformation.execute(mContext.getPackageName(), "numDownloads").get();
            return marketDownloads;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getMarketPublishedDate(){
        try {
            MarketInformation marketInformation = new MarketInformation();
            String marketPublishedDate = marketInformation.execute(mContext.getPackageName(), "datePublished").get();
            return marketPublishedDate;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getMarketOperatingSystems(){
        try {
            MarketInformation marketInformation = new MarketInformation();
            String marketOperatingSystems = marketInformation.execute(mContext.getPackageName(), "operatingSystems").get();
            return marketOperatingSystems;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getMarketRecentChange(){
        try {
            MarketRecentChange marketRecentChange = new MarketRecentChange();
            String marketRecentChangeData = marketRecentChange.execute(mContext.getPackageName()).get();
            return marketRecentChangeData;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getMarketRecentChangeArray(){
        try {
            MarketRecentChangeArray marketRecentChangeArray = new MarketRecentChangeArray();
            String[] marketRecentChangeData = marketRecentChangeArray.execute(mContext.getPackageName()).get();
            return marketRecentChangeData;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void gotoMarket(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }

    private static class MarketInformation extends AsyncTask<String, String, String> {
        String parsingdata = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                if(isNetworkAvailable() && isExistUrl) {
                    parsingdata = Jsoup.connect("https://play.google.com/store/apps/details?id=" + params[0])
                            .timeout(timeout)
                            .ignoreHttpErrors(true)
                            .referrer("http://www.google.com").get()
                            .select("div[itemprop=" + params[1] + "]").first() // .recent-change
                            .ownText();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return parsingdata;
        }
    }

    private static class MarketRecentChange extends AsyncTask<String, String, String> {
        int size = 0;

        @Override
        protected String doInBackground(String... params) {
            try {
                if(isNetworkAvailable() && isExistUrl) {
                    size = Jsoup.connect("https://play.google.com/store/apps/details?id=" + params[0])
                            .timeout(timeout)
                            .ignoreHttpErrors(true)
                            .referrer("http://www.google.com").get()
                            .select(".recent-change").size();

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        String parsingdata = Jsoup.connect("https://play.google.com/store/apps/details?id=" + params[0])
                                .timeout(timeout)
                                .referrer("http://www.google.com").get()
                                .select(".recent-change").get(i).ownText();
                        stringBuilder.append(parsingdata + "\n");
                    }
                    return stringBuilder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class MarketRecentChangeArray extends AsyncTask<String, String, String[]> {
        int size = 0;

        @Override
        protected String[] doInBackground(String... params) {
            try {
                if(isNetworkAvailable() && isExistUrl) {
                    size = Jsoup.connect("https://play.google.com/store/apps/details?id=" + params[0])
                            .timeout(timeout)
                            .ignoreHttpErrors(true)
                            .referrer("http://www.google.com").get()
                            .select(".recent-change").size();

                    String[] parsingdataArray = new String[size];
                    for (int i = 0; i < size; i++) {
                        String parsingdata = Jsoup.connect("https://play.google.com/store/apps/details?id=" + params[0])
                                .timeout(timeout)
                                .referrer("http://www.google.com").get()
                                .select(".recent-change").get(i).ownText();
                        parsingdataArray[i] = parsingdata;
                    }
                    return parsingdataArray;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class CheckIsExistUrl extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setRequestMethod("GET");
                huc.connect();
                if(isNetworkAvailable() && huc.getResponseCode() == 200){
                    isExistUrl = true;

                    SharedPreferences prefs = mContext.getSharedPreferences(TAG, mContext.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isExistUrl", true).commit();
                }
                else
                    Log.e(TAG, "Used not exist market url : this app is not published at Google Play Store");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean isNetworkAvailable() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}