package com.chargeapp.whc.chargeapp.Control.Property;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.List;


/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyInsert extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton save;
    private BootstrapEditText name;
    private PropertyDB propertyDB;
    private BootstrapDropDown currency;
    private String[] nameKey;
    private String[] nameValue;
    private List<BootstrapText> propertyTypes;
    private String currencyName;




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
        view = inflater.inflate(R.layout.property_insert, container, false);
        findViewById();
        currencyName="TWD";
        setDropDown();
        save.setOnClickListener(new saveData());
        Common.setChargeDB(activity);
        propertyDB=new PropertyDB( MainActivity.chargeAPPDB.getReadableDatabase());
        return view;
    }





    private void findViewById() {
        save=view.findViewById(R.id.save);
        name=view.findViewById(R.id.name);
        currency=view.findViewById(R.id.currency);
    }

    private void setDropDown() {
        nameKey= Common.getALLCurrencyKey().toArray(new String[Common.getALLCurrencyKey().size()]);
        nameValue= Common.getALLCurrencyValue().toArray(new String[Common.getALLCurrencyValue().size()]);
        currency.setDropdownData(nameValue);
        propertyTypes= Common.currecyData(activity,nameKey);
        currency.setBootstrapText(propertyTypes.get(0));
        currency.setOnDropDownItemClickListener(new choiceCurrencyName());
    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Editable nameE=name.getText();
            if(nameE==null||nameE.length()<=0)
            {
                name.setError("名稱不能空白!");
                return;
            }
            String nameP=nameE.toString().trim();
            if(nameP.isEmpty())
            {
                name.setError("名稱不能空白!");
                return;
            }
            PropertyVO old=propertyDB.findByName(nameP);
            if(old!=null)
            {
                name.setError("名稱重複!");
                return;
            }
            PropertyVO propertyVO=new PropertyVO();
            propertyVO.setName(nameP);
            propertyVO.setCurrency(currencyName);
            long id=propertyDB.insert(propertyVO);
            Fragment fragment=new PropertyTotal();
            Bundle bundle=new Bundle();
            bundle.putSerializable(Common.propertyID,id);
            fragment.setArguments(bundle);
            Common.switchFragment(fragment, Common.propertyMain,getFragmentManager());
        }
    }

    private class choiceCurrencyName implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            currencyName= Common.getALLCurrencyKey().get(id);
            currency.setBootstrapText(propertyTypes.get(id));
        }
    }
}
