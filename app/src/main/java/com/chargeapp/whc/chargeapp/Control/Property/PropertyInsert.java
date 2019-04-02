package com.chargeapp.whc.chargeapp.Control.Property;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.chargeapp.whc.chargeapp.R;

/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyInsert extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapLabel propertyFrom;
    private LinearLayout propertyL;


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
        setListener();
        return view;
    }

    private void setListener() {
        propertyFrom.setOnClickListener(new AddNewSource());
    }

    private void findViewById() {
        propertyFrom=view.findViewById(R.id.propertyFrom);
        propertyL=view.findViewById(R.id.propertyL);
    }

    private class AddNewSource implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            View child = getLayoutInflater().inflate(R.layout.property_insert_item, null);
            propertyL.addView(child);
        }
    }
}
