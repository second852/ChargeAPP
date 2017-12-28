package com.chargeapp.whc.chargeapp.Control;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private TextView month;
    private Calendar cal=Calendar.getInstance();
    private String title;
    private ImageView add,cut;
    private RecyclerView listinviuce;
    private ChargeAPPDB chargeAPPDB;
    private InvoiceDB invoiceDB;
    private Spinner carrierlist;
    private CarrierDB carrierDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        findviewbyid(view);
        download();
        title=cal.get(Calendar.YEAR)+"年"+cal.get(Calendar.MONTH)+"月";
        month.setText(title);
        ArrayList<String> sppineritem=new ArrayList<>();
        for(CarrierVO c:carrierDB.getAll())
        {
            sppineritem.add(c.getCarNul());
        }
        View t=inflater.inflate(R.layout.ele_setdenote, container, false);
        TextView tson=view.findViewById(R.id.spinnersonitem);
        tson.setTextSize(25);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,sppineritem);
        carrierlist.setAdapter(arrayAdapter);
       
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
        new GetSQLDate(EleDonate.class,chargeAPPDB).execute("GetToday");
    }

    public void setlayout()
    {

    }



    private void findviewbyid(View view)
    {
        month= view.findViewById(R.id.month);
        add=view.findViewById(R.id.add);
        cut=view.findViewById(R.id.cut);
        listinviuce=view.findViewById(R.id.recyclenul);
        carrierlist=view.findViewById(R.id.carrierlist);
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
            TextView day,carrier,nul,sell,amount;
            CheckBox checkdonate;
            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate=itemView.findViewById(R.id.checkdonate);
                day=itemView.findViewById(R.id.day);
                carrier=itemView.findViewById(R.id.carrier);
                nul=itemView.findViewById(R.id.nul);
                sell=itemView.findViewById(R.id.sell);
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
            View itemView = layoutInflater.inflate(R.layout.ele_main_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {

        }
    }


}
