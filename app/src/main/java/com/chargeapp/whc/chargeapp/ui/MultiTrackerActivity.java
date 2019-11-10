/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chargeapp.whc.chargeapp.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertConsumeType;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertIncome;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertSpend;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.MyContextWrapper;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for the multi-tracker app.  This app detects faces and barcodes with the rear facing
 * camera, and draws overlay graphics to indicate the position, size, and ID of each face and
 * barcode.
 */
public final class MultiTrackerActivity extends AppCompatActivity {



    public static String action;



    /**
     * Initializes the UI and creates the detector pipeline.
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.scan_main);
        AdView adView=findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        action=getIntent().getStringExtra("action");
        switch (action)
        {
            case "setConsume":
            case "UpdateSpend":
            case "PriceHand":
            case "moreQRcode":
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment=new ScanFragment();
                Bundle bundle=getIntent().getBundleExtra("bundle");
                if(bundle==null)
                {
                    bundle=new Bundle();
                }
                bundle.putSerializable("action",action);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
                break;
        }

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(action.equals("setConsume"))
            {
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }else if(action.equals("UpdateSpend")){
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                intent.putExtra("bundle",getIntent().getBundleExtra("bundle"));
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }else if(action.equals("PriceHand"))
            {
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }else if(action.equals("moreQRcode"))
            {
                Bundle bundle= MainActivity.bundles.getLast();
                String action= (String) bundle.getSerializable("action");
                switch (action)
                {
                    case Common.scanFragment:
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        Fragment fragment=new ScanFragment();
                        bundle.putSerializable("action",bundle.getSerializable(Common.multiTrackerActivityWork));
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.body, fragment);
                        fragmentTransaction.commit();
                        return true;
                }

            }
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for(Fragment fragment:getSupportFragmentManager().getFragments())
        {
           if(fragment instanceof ScanFragment)
           {
               fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
           }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment fragment:getSupportFragmentManager().getFragments())
        {
            if(fragment instanceof ScanFragment)
            {
                fragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }



















}
