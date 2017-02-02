package com.skydoves.syncmarketexample;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skydoves.syncmarketexample.SyncMarket.SyncMarket;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SyncMarket.Initialize(this);
    }

    @OnClick({R.id.elasticbtn0, R.id.elasticbtn1, R.id.elasticbtn2})
    public void ElasticButtons(View v) {
        switch (v.getId()){
            case R.id.elasticbtn0 :
                if(!SyncMarket.isVersionEqual()){
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                    alertDlg.setTitle("Update");
                    alertDlg.setPositiveButton("update now", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            dialog.dismiss();
                            SyncMarket.gotoMarket();
                        }
                    });

                    alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            dialog.dismiss();
                        }
                    });

                    alertDlg.setMessage(String.format("A new version(v" + SyncMarket.getMarketVersion() + ") of the app is available! \nDo you want to update it now?"));
                    alertDlg.show();
                }
                break;

            case R.id.elasticbtn1 :
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                alertDlg.setTitle("Recent change list");
                alertDlg.setPositiveButton("okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        dialog.dismiss();
                    }
                });
                alertDlg.setMessage(String.format("This is recent change list:\n\n" + SyncMarket.getMarketRecentChange()));
                alertDlg.show();
                break;

            case R.id.elasticbtn2 :
                AlertDialog.Builder alertDlg2 = new AlertDialog.Builder(this);
                alertDlg2.setTitle("Rate this app");
                alertDlg2.setPositiveButton("Rate now", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        dialog.dismiss();
                        SyncMarket.gotoMarket();
                    }
                });

                alertDlg2.setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        dialog.dismiss();
                    }
                });

                alertDlg2.setMessage(String.format("If you enjoy this app, please take a moment to rate this app."));
                alertDlg2.show();
                break;
        }
    }
}
