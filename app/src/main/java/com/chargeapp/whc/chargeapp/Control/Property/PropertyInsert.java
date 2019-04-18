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
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;


/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyInsert extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton save;
    private BootstrapEditText name;
    private PropertyDB propertyDB;




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
        save.setOnClickListener(new saveData());
        Common.setChargeDB(activity);
        propertyDB=new PropertyDB( MainActivity.chargeAPPDB.getReadableDatabase());
        return view;
    }





    private void findViewById() {
        save=view.findViewById(R.id.save);
        name=view.findViewById(R.id.name);
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
            propertyVO.setPropertyType(PropertyType.Positive);
            propertyVO.setNowMoney("0");
            propertyVO.setName(nameP);
            long id=propertyDB.insert(propertyVO);
            Fragment fragment=new PropertyMoneyList();
            Bundle bundle=new Bundle();
            bundle.putSerializable(Common.propertyID,String.valueOf(id));
            fragment.setArguments(bundle);
            Common.switchFragment(fragment,Common.propertyMain,getFragmentManager());
        }
    }
}
