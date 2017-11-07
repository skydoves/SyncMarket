
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 skydoves
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.skydoves.syncmarket;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SyncMarket {

    private static Context mContext;

    private static String packageName;
    private static int timeout = 5000;
    public static final int DialogResult = 65535;

    private static final String TAG = "SyncMarket";

    public static void init(Context context) {
        mContext = context;
        packageName = mContext.getPackageName();
    }

    public static String getMarketUrl() {
        return "https://play.google.com/store/apps/details?id=" + packageName;
    }

    public static void gotoMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +packageName));
        mContext.startActivity(intent);
    }

    public static String getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Observable<String> getVersionObservable() {
        return getCallableObservable("div[itemprop=softwareVersion]");
    }

    public static Observable<String> getDownloadsObservable() {
        return getCallableObservable("div[itemprop=numDownloads]");
    }

    public static Observable<String> getPublishedDateObservable() {
        return getCallableObservable("div[itemprop=datePublished]");
    }

    public static Observable<String> getOperatingSystemsObservable() {
        return getCallableObservable("div[itemprop=operatingSystems]");
    }

    public static Observable<String[]> getRecentChangesObservable() {
       return getCallableObservableArray(".recent-change");
    }

    private static Observable<String> getCallableObservable(final String selects) {
        if(isNetworkAvailable()) {
            Observable<String> observable = Observable.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return getAttrFromNetwork(selects);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            return observable;
        } else
            Log.e(TAG, "check your network connection");
        return Observable.just("");
    }

    private static Observable<String[]> getCallableObservableArray(final String selects) {
        if(isNetworkAvailable()) {
            Observable<String[]> observable = Observable.fromCallable(new Callable<String[]>() {
                @Override
                public String[] call() throws Exception {
                    String[] parsedArray;

                    Elements elements = getElementsFromNetwork(selects);
                    if(elements == null) return new String[]{};

                    parsedArray = new String[elements.size()];
                    for(int i=0; i<parsedArray.length; i++)
                        parsedArray[i] = elements.get(i).ownText();

                    return parsedArray;
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            return observable;
        } else
            Log.e(TAG, "check your network connection");
        return Observable.just(new String[]{});
    }

    private static Elements getElementsFromNetwork(final String selects) {
        try {
            Elements elements = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName)
                    .timeout(timeout)
                    .ignoreHttpErrors(true)
                    .referrer("http://www.google.com").get()
                    .select(selects);
            if(elements.size() != 0)
                return elements;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getAttrFromNetwork(final String selects) {
        String result = "none";
        try {
            Elements elements = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName)
                    .timeout(timeout)
                    .ignoreHttpErrors(true)
                    .referrer("http://www.google.com").get()
                    .select(selects);
            if(elements.size() != 0)
                result = elements.first().ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNetworkAvailable() {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null
                && netInfo.getState() == NetworkInfo.State.CONNECTED) {
            status = true;
        } else {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED)
                status = true;
        }
        return status;
    }

    public static void setPackageName(String packageName_) {
        packageName = packageName_;
    }

    public static void setTimeout(int timeout_) {
        timeout = timeout_;
    }
}