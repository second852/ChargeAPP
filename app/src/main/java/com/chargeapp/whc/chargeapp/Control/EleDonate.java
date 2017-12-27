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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chargeapp.whc.chargeapp.ChargeDB.ChargeAPPDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote, container, false);
        findviewbyid(view);
        download();
        title=cal.get(Calendar.YEAR)+"年"+cal.get(Calendar.MONTH)+"月";
        month.setText(title);
        return view;
    }

    private void download()
    {
        if(chargeAPPDB==null)
        {
            chargeAPPDB=new ChargeAPPDB(getActivity());
        }
        invoiceDB=new InvoiceDB(chargeAPPDB.getReadableDatabase());
        invoiceDB.deleteBytime(Timestamp.valueOf("2017-12-01 00:00:00"));
        List<InvoiceVO> invoicelist=invoiceDB.getCarrierAll("/2RDO8+P");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for(InvoiceVO i:invoicelist)
        {
            Log.d("XXXXX", i.getInvNum()+":::::::"+sf.format(new Date(i.getTime().getTime())));
        }

        new GetSQLDate(EleDonate.class,chargeAPPDB).execute("GetToday");
    }


    private void findviewbyid(View view)
    {
        month= view.findViewById(R.id.month);
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
