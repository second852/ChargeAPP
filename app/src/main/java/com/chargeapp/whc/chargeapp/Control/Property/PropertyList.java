package com.chargeapp.whc.chargeapp.Control.Property;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.util.List;

public class PropertyList extends Fragment {

    private View view;
    private ListView list;
    private PropertyDB propertyDB;
    private CurrencyDB currencyDB;
    private PropertyFromDB propertyFromDB;
    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.activity=(Activity) context;
        }else{
            this.activity=getActivity();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.property_list, container, false);
        Common.setChargeDB(activity);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        list=view.findViewById(R.id.list);
        List<PropertyVO> propertyVOS=propertyDB.getAll();
        list.setAdapter(new ListAdapter(activity,propertyVOS));
        return view;
    }

    //Adapter
    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<PropertyVO> propertyVOS;

        public ListAdapter(Context context, List<PropertyVO> propertyVOS) {
            this.context = context;
            this.propertyVOS = propertyVOS;
        }

        @Override
        public int getCount() {
            return propertyVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.property_list_from_detail, parent, false);
            }

            TextView listTitle=itemView.findViewById(R.id.listTitle);
            TextView detail=itemView.findViewById(R.id.listDetail);
            PropertyVO propertyVO=propertyVOS.get(position);
            CurrencyVO currencyVO=currencyDB.getOneByType(propertyVO.getCurrency());
            Double consume=propertyFromDB.totalConsume(propertyVO.getId(), PropertyType.Negative);
            Double income=propertyFromDB.totalConsume(propertyVO.getId(), PropertyType.Positive);
            Double total=income-consume;
            String title=propertyVO.getName()+" "+Common.CurrencyResult(total,currencyVO);
            String detaile="收入 :"+Common.CurrencyResult(income,currencyVO)+"\n" +
                           "支出 :"+Common.CurrencyResult(consume,currencyVO);
            listTitle.setText(title);
            detail.setText(detaile);
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return propertyVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
