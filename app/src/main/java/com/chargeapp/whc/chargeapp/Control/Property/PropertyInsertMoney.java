package com.chargeapp.whc.chargeapp.Control.Property;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.R;

import java.lang.reflect.Array;
import java.util.List;

public class PropertyInsertMoney extends Fragment {

    private BootstrapDropDown choicePropertyFrom;
    private BootstrapButton currency;
    private BankDB bankDB;
    private Activity activity;
    private View view;
    private String[] nameData;
    private List<BootstrapText> propertyTypes;

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
        view = inflater.inflate(R.layout.property_insert_money, container, false);
        findViewById();
        setDataBase();
        setDropDown();
        return view;
    }

    private void setDropDown() {
        List<String> nameList=bankDB.getAllName();
        nameData=nameList.toArray(new String[nameList.size()]);
        choicePropertyFrom.setDropdownData(nameData);
        propertyTypes=Common.propertyInsertMoneyData(activity,nameData);
        choicePropertyFrom.setBootstrapText(propertyTypes.get(0));
        choicePropertyFrom.setOnDropDownItemClickListener(new choiceMoneyName());

    }

    private void setDataBase() {
        Common.setChargeDB(activity);
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());

    }

    private void findViewById() {
        choicePropertyFrom=view.findViewById(R.id.choicePropertyFrom);
        currency=view.findViewById(R.id.currency);
    }


    private class choiceMoneyName implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            choicePropertyFrom.setBootstrapText(propertyTypes.get(id));
        }
    }
}
