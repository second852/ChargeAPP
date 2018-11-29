package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.JsonObject;

import java.util.List;


public class SettingCurrency extends Fragment {
    private Activity context;
    private ListView listCurrency;
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
        listCurrency=view.findViewById(R.id.listCurrency);
        Common.setChargeDB(context);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());

        return view;
    }




    @Override
    public void onStart() {
        super.onStart();
        Common.setChargeDB(context);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        List<String> typeCurrency=currencyDB.getAllTypeName();
        listCurrency.setAdapter(new ListAdapter(context,typeCurrency));
    }




    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<String> typeCurrenct;

        ListAdapter(Context context,  List<String> typeCurrenct) {
            this.context = context;
            this.typeCurrenct = typeCurrenct;
        }



        @Override
        public int getCount() {
            return typeCurrenct.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.setting_currency_item, parent, false);
            }
            TextView tType=itemView.findViewById(R.id.tType);
            AwesomeTextView tMoney=itemView.findViewById(R.id.tMoney);
            CurrencyVO currencyVO=currencyDB.getOneByType(typeCurrenct.get(position));
            StringBuilder sb=new StringBuilder();
            sb.append(currencyVO.getName());
            sb.append(" "+currencyVO.getSymbol()+" 1 = NT$");
            tType.setText(sb.toString());
            tMoney.setText(currencyVO.getMoney());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return typeCurrenct.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}

