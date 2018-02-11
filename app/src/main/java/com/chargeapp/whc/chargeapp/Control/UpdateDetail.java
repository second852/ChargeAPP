package com.chargeapp.whc.chargeapp.Control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
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
    private TextView detail;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.updae_detail, container, false);
        detail=view.findViewById(R.id.detail);
        final InvoiceVO invoiceVO= (InvoiceVO) getArguments().getSerializable("invoiceVO");
        Gson gson=new Gson();
        Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
        List<JsonObject> js=gson.fromJson(invoiceVO.getDetail(), cdType);
        int price,n;
        for(JsonObject j:js)
        {
            try {
                n=j.get("amount").getAsInt();
                price=j.get("unitPrice").getAsInt();
                detail.append(j.get("description").getAsString()+" : \n"+price+"X"+n/price+"="+n+"元\n");
            }catch (Exception e)
            {
                detail.append(j.get("description").getAsString()+" : \n"+0+"X"+0+"="+0+"元\n");
            }
        }
        getActivity().setTitle("細節");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new UpdateInvoice();
                Bundle bundle=new Bundle();
                bundle.putSerializable("invoiceVO",invoiceVO);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 :  getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }



}
