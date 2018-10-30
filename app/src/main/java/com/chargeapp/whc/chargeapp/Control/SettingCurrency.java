package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.R;






public class SettingCurrency extends Fragment {
    private Activity context;
    private AwesomeTextView USD,JPY,AUD,EUR,GBP,CNY;
    private View view;
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

