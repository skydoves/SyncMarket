package com.skydoves.syncmarketdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skydoves.syncmarket.SyncMarket;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SyncMarket.init(this);

        SyncMarket.setPackageName("com.skydoves.waterdays");

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressbar);
        TextView tv_ver = (TextView)findViewById(R.id.tv_ver);
        SyncMarket.getVersionObservable()
                .subscribe(ver -> tv_ver.setText(ver));

        TextView tv_pub = (TextView)findViewById(R.id.tv_pub);
        SyncMarket.getPublishedDateObservable()
                .subscribe(pub -> tv_pub.setText(pub));

        TextView tv_down = (TextView)findViewById(R.id.tv_down);
        SyncMarket.getDownloadsObservable()
                .subscribe(down -> tv_down.setText(down));

        TextView tv_oper = (TextView)findViewById(R.id.tv_oper);
        SyncMarket.getDownloadsObservable()
                .subscribe(oper -> tv_oper.setText(oper));

        TextView tv_rec = (TextView)findViewById(R.id.tv_rec);
        SyncMarket.getRecentChangesObservable()
                .doOnTerminate(() -> progressBar.setVisibility(View.GONE))
                .subscribe(recs -> {
                    for(int i =0; i<recs.length; i++)
                        tv_rec.append(recs[i] + "\n");
                });
    }

    @OnClick({R.id.btn_market})
    public void goMarket(View v) {
        SyncMarket.gotoMarket();
    }
}