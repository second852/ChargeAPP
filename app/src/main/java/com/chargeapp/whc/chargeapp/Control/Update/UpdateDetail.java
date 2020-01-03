package com.chargeapp.whc.chargeapp.Control.Update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/1/29.
 */

public class UpdateDetail extends Fragment {
    private BootstrapEditText store, address;
    private Activity context;
    private BootstrapButton backP;
    private TextView detail;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.updae_detail, container, false);
        detail = view.findViewById(R.id.detail);
        store = view.findViewById(R.id.store);
        address = view.findViewById(R.id.address);
        backP=view.findViewById(R.id.backP);
        address.setShowSoftInputOnFocus(false);
        store.setShowSoftInputOnFocus(false);
        detail.setShowSoftInputOnFocus(false);


        final InvoiceVO invoiceVO = (InvoiceVO) getArguments().getSerializable("invoiceVO");
        Gson gson = new Gson();
        Type cdType = new TypeToken<List<JsonObject>>() {
        }.getType();
        List<JsonObject> js = gson.fromJson(invoiceVO.getDetail(), cdType);
        String currency = Common.getCurrency(invoiceVO.getCurrency());
        float price, amout, n;
        StringBuilder stringBuilder=new StringBuilder();
        for (JsonObject j : js) {
            try {
                amout = j.get("amount").getAsFloat();
            } catch (Exception e) {
                amout = 0;
            }
            try {
                n = j.get("quantity").getAsFloat();
            } catch (Exception e) {
                n = 0;
            }
            try {
                price = j.get("unitPrice").getAsFloat();
            } catch (Exception e) {
                price = 0;
            }

            if (price == 0) {
                stringBuilder.append(j.get("description").getAsString() + " : \n" + (int) (amout / n) + "X" + (int) n + "= " + currency + (int) amout + "\n");
            } else {
                stringBuilder.append(j.get("description").getAsString() + " : \n" + (int) price + "X" + (int) n + "= " + currency + (int) amout + "\n");
            }
        }
        context.setTitle("細節");
        detail.setText(stringBuilder.toString());
        address.setText(invoiceVO.getSellerAddress());
        store.setText(invoiceVO.getSellerName());


        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = (String) getArguments().getSerializable("action");
                Fragment fragment = new UpdateInvoice();
                Bundle bundle = new Bundle();
                bundle.putSerializable("action", action);
                bundle.putSerializable("invoiceVO", invoiceVO);
                if (action.equals("SelectDetList")) {
                    bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
                    bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
                    bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
                    bundle.putSerializable("year", getArguments().getSerializable("year"));
                    bundle.putSerializable("month", getArguments().getSerializable("month"));
                    bundle.putSerializable("day", getArguments().getSerializable("day"));
                    bundle.putSerializable("key", getArguments().getSerializable("key"));
                    bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
                    bundle.putSerializable("statue", getArguments().getSerializable("statue"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    bundle.putSerializable("period", getArguments().getSerializable("period"));
                    bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
                } else if (action.equals("SelectShowCircleDe")) {
                    bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
                    bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
                    bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
                    bundle.putSerializable("year", getArguments().getSerializable("year"));
                    bundle.putSerializable("month", getArguments().getSerializable("month"));
                    bundle.putSerializable("day", getArguments().getSerializable("day"));
                    bundle.putSerializable("index", getArguments().getSerializable("index"));
                    bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
                    bundle.putSerializable("statue", getArguments().getSerializable("statue"));
                    bundle.putSerializable("period", getArguments().getSerializable("period"));
                    bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                } else if (action.equals("SelectShowCircleDeList")) {
                    bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
                    bundle.putSerializable("ShowAllCarrier", getArguments().getSerializable("ShowAllCarrier"));
                    bundle.putSerializable("noShowCarrier", getArguments().getSerializable("noShowCarrier"));
                    bundle.putSerializable("year", getArguments().getSerializable("year"));
                    bundle.putSerializable("month", getArguments().getSerializable("month"));
                    bundle.putSerializable("day", getArguments().getSerializable("day"));
                    bundle.putSerializable("key", getArguments().getSerializable("key"));
                    bundle.putSerializable("carrier", getArguments().getSerializable("carrier"));
                    bundle.putSerializable("statue", getArguments().getSerializable("statue"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    bundle.putSerializable("period", getArguments().getSerializable("period"));
                    bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
                    bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
                } else if (action.equals("HomePagetList")) {
                    bundle.putStringArrayList("OKey", getArguments().getStringArrayList("OKey"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    bundle.putSerializable("key", getArguments().getSerializable("key"));
                } else if (action.equals(Common.searchMainString)) {
                    bundle.putAll(getArguments());
                }
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 : getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }


}
