package com.chargeapp.whc.chargeapp.Control;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class EleDonate extends Fragment {

    private TextView carrier;
    private Calendar cal=Calendar.getInstance();
    private ImageView add,cut;
    private RecyclerView listinviuce;
    private ChargeAPPDB chargeAPPDB;
    private InvoiceDB invoiceDB;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOList;
    private CarrierVO carrierVO;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd");
    private int choiceca=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        findviewbyid(view);
        download();
        carrierVOList=carrierDB.getAll();
        carrierVO=carrierVOList.get(0);
        carrier.setText(carrierVO.getCarNul());
        add.setOnClickListener(new textOnClick(1));
        cut.setOnClickListener(new textOnClick(-1));
        return view;
    }

    private void download()
    {
        if(chargeAPPDB==null)
        {
            chargeAPPDB=new ChargeAPPDB(getActivity());
        }
        carrierDB=new CarrierDB(chargeAPPDB.getReadableDatabase());
        invoiceDB=new InvoiceDB(chargeAPPDB.getReadableDatabase());
        new GetSQLDate(this,chargeAPPDB).execute("GetToday");
    }

    public void setlayout()
    {
        List<InvoiceVO> invoiceVOList=invoiceDB.getCarrierDoAll(carrierVO.getCarNul());
        listinviuce.setLayoutManager(new LinearLayoutManager(getActivity()));
        listinviuce.setAdapter(new InvoiceAdapter(getActivity(),invoiceVOList));
    }



    private void findviewbyid(View view)
    {
        carrier=view.findViewById(R.id.carrier);
        add=view.findViewById(R.id.add);
        cut=view.findViewById(R.id.cut);
        listinviuce=view.findViewById(R.id.recyclenul);
    }


    private class InvoiceAdapter extends
            RecyclerView.Adapter<InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<InvoiceVO> invoiceVOList;


        InvoiceAdapter(Context context, List<InvoiceVO> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day,nul,amount;
            CheckBox checkdonate;
            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate=itemView.findViewById(R.id.checkdonate);
                day=itemView.findViewById(R.id.day);
                nul=itemView.findViewById(R.id.nul);
                amount=itemView.findViewById(R.id.amount);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_item,viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            InvoiceVO invoiceVO=invoiceVOList.get(position);
            viewHolder.day.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
            viewHolder.nul.setText(invoiceVO.getInvNum());
            String amout="NT$"+invoiceVO.getAmount();
            viewHolder.amount.setText(String.format(amout));
        }
    }


    private class textOnClick implements View.OnClickListener {

        textOnClick(int action)
        {
            choiceca=choiceca+action;
        }

        @Override
        public void onClick(View view) {
            if(choiceca<0)
            {
                choiceca=carrierVOList.size()-1;
            }
            if(choiceca>carrierVOList.size())
            {
                choiceca= 0;
            }
            carrierVO=carrierVOList.get(choiceca);
            setlayout();
        }
    }
}
