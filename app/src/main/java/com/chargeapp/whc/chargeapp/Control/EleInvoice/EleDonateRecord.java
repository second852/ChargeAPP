package com.chargeapp.whc.chargeapp.Control.EleInvoice;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class EleDonateRecord extends Fragment {
    private TextView DRmessage;
    private ListView donateRL;
    private RelativeLayout modelR;
    private Spinner choiceModel;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    public  int choiceca = 0;
    private Context context;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View view = inflater.inflate(R.layout.ele_setdenote_record, container, false);
        Common.setChargeDB(context);
        invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB);
        findViewById(view);
        carrierVOS=carrierDB.getAll();
        if(carrierVOS==null||carrierVOS.size()<=0)
        {
            DRmessage.setText("請新增載具!");
            DRmessage.setVisibility(View.VISIBLE);
            modelR.setVisibility(View.GONE);
            return view;
        }
        ArrayList<String> SpinnerItem = new ArrayList<>();
        for(CarrierVO c:carrierVOS)
        {
            SpinnerItem.add(c.getCarNul());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceModel.setAdapter(arrayAdapter);
        choiceModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choiceca=position;
                setLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setLayout();
        return view;
    }

    private void setLayout() {
        invoiceVOS=invoiceDB.getisDonated(carrierVOS.get(choiceca).getCarNul());
        if(invoiceVOS==null||invoiceVOS.size()<=0)
        {
            DRmessage.setText("沒有捐贈發票!");
            DRmessage.setVisibility(View.VISIBLE);
            return ;
        }
        ListAdapter adapter= (ListAdapter) donateRL.getAdapter();
        if(adapter==null)
        {
            donateRL.setAdapter(new ListAdapter(context,invoiceVOS));
        }else{
            adapter.setObjects(invoiceVOS);
            adapter.notifyDataSetChanged();
            donateRL.invalidate();
        }
    }

    private void findViewById(View view) {
        modelR=view.findViewById(R.id.modelR);
        donateRL=view.findViewById(R.id.donateRL);
        DRmessage=view.findViewById(R.id.DRmessage);
        choiceModel=view.findViewById(R.id.choiceModel);
    }


    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<InvoiceVO> invoiceVOS;

        ListAdapter(Context context, List<InvoiceVO> invoiceVOS) {
            this.context = context;
            this.invoiceVOS = invoiceVOS;
        }


        public void setObjects(List<InvoiceVO> invoiceVOS) {
            this.invoiceVOS = invoiceVOS;
        }

        @Override
        public int getCount() {
            return invoiceVOS.size();
        }

        @Override
        public Object getItem(int position) {
            return invoiceVOS.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, parent, false);
            }
            TextView Title=itemView.findViewById(R.id.listTitle);
            TextView describe=itemView.findViewById(R.id.listDetail);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            BootstrapButton remainT=itemView.findViewById(R.id.remainT);
            InvoiceVO invoiceVO=invoiceVOS.get(position);
            String title=invoiceVO.getInvNum();
            title=title.substring(0,7);
            Title.setText(title+"***");
            StringBuffer sb=new StringBuffer();
            sb.append("發票日期 : "+Common.sOne.format(new Date(invoiceVO.getTime().getTime()))+"\n");
            sb.append("捐贈機構 : ");
            if(invoiceVO.getHeartyteam()!=null)
            {
                if(invoiceVO.getHeartyteam().trim().length()>0)
                {
                    sb.append(invoiceVO.getHeartyteam());
                }else{
                    sb.append("無資料");
                }
            }else{
                sb.append("無資料");
            }
            describe.setText(sb.toString());
            remindL.setVisibility(View.VISIBLE);
            remainT.setText("以捐贈");
            return itemView;
        }
    }

}
