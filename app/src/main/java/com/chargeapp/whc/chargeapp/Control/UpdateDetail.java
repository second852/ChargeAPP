package com.chargeapp.whc.chargeapp.Control;

import android.app.Activity;
import android.content.Context;
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
    private Activity context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }


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
       context.setTitle("細節");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action= (String) getArguments().getSerializable("action");
                Fragment fragment=new UpdateInvoice();
                Bundle bundle=new Bundle();
                bundle.putSerializable("action",action);
                bundle.putSerializable("invoiceVO",invoiceVO);
                if(action.equals("SelectDetList"))
                {
                    bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
                    bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
                    bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
                    bundle.putSerializable("year",  getArguments().getSerializable("year"));
                    bundle.putSerializable("month",  getArguments().getSerializable("month"));
                    bundle.putSerializable("day",  getArguments().getSerializable("day"));
                    bundle.putSerializable("key",  getArguments().getSerializable("key"));
                    bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
                    bundle.putSerializable("statue", getArguments().getSerializable("statue"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    bundle.putSerializable("period",  getArguments().getSerializable("period"));
                    bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
                }else if(action.equals("SelectShowCircleDe"))
                {
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
                    bundle.putSerializable("dweek",getArguments().getSerializable("dweek"));
                    bundle.putSerializable("position",getArguments().getSerializable("position"));
                }else if(action.equals("SelectShowCircleDeList"))
                {
                    bundle.putSerializable("ShowConsume", getArguments().getSerializable("ShowConsume"));
                    bundle.putSerializable("ShowAllCarrier",  getArguments().getSerializable("ShowAllCarrier"));
                    bundle.putSerializable("noShowCarrier",  getArguments().getSerializable("noShowCarrier"));
                    bundle.putSerializable("year",  getArguments().getSerializable("year"));
                    bundle.putSerializable("month",  getArguments().getSerializable("month"));
                    bundle.putSerializable("day",  getArguments().getSerializable("day"));
                    bundle.putSerializable("key",  getArguments().getSerializable("key"));
                    bundle.putSerializable("carrier",  getArguments().getSerializable("carrier"));
                    bundle.putSerializable("statue", getArguments().getSerializable("statue"));
                    bundle.putSerializable("position", getArguments().getSerializable("position"));
                    bundle.putSerializable("period",  getArguments().getSerializable("period"));
                    bundle.putSerializable("dweek", getArguments().getSerializable("dweek"));
                    bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
                }else if(action.equals("HomePagetList"))
                {
                    bundle.putStringArrayList("OKey",getArguments().getStringArrayList("OKey"));
                    bundle.putSerializable("position",getArguments().getSerializable("position"));
                    bundle.putSerializable("key", getArguments().getSerializable("key"));
                }
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
