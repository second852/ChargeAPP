package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.List;


public class SettingCurrency extends Fragment {
    private Activity context;
    private AwesomeTextView USD,JPY,AUD,EUR,GBP,CNY;
    private View view;
    private AdView adView;
    private CurrencyDB currencyDB;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        context.setTitle("當日匯率查詢");
        view = inflater.inflate(R.layout.update_currency, container, false);
        findViewByid(view);
        return view;
    }



    public void findViewByid(View view) {
        USD=view.findViewById(R.id.USD);
        JPY=view.findViewById(R.id.JPY);
        AUD=view.findViewById(R.id.AUD);
        EUR=view.findViewById(R.id.EUR);
        GBP=view.findViewById(R.id.GBP);
        CNY=view.findViewById(R.id.CNY);
        adView=view.findViewById(R.id.adView);
        Common.setAdView(adView, context);
    }

    @Override
    public void onStart() {
        super.onStart();
        Common.setChargeDB(context);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        USD.setText(currencyDB.getOneByType("USD").getMoney());
        JPY.setText(currencyDB.getOneByType("JPY").getMoney());
        AUD.setText(currencyDB.getOneByType("AUD").getMoney());
        EUR.setText(currencyDB.getOneByType("EUR").getMoney());
        GBP.setText(currencyDB.getOneByType("GBP").getMoney());
        CNY.setText(currencyDB.getOneByType("CNY").getMoney());
    }
}

