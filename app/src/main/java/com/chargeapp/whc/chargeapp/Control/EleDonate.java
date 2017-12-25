package com.chargeapp.whc.chargeapp.Control;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.R;

import java.util.Calendar;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class EleDonate extends Fragment {

    private TextView month;
    private Calendar cal=Calendar.getInstance();
    private String title;
    private ImageView add,cut;
    private RecyclerView listinviuce;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        findviewbyid(view);
        title=cal.get(Calendar.YEAR)+"年"+cal.get(Calendar.MONTH)+"月";
        month.setText(title);
        return view;
    }

    private void findviewbyid(View view)
    {
        month= view.findViewById(R.id.month);
        add=view.findViewById(R.id.add);
        cut=view.findViewById(R.id.cut);
    }


}
