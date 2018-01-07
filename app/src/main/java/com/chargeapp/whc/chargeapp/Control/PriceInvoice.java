package com.chargeapp.whc.chargeapp.Control;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceInvoice extends Fragment {
    private ImageView DRadd,DRcut,PIdateAdd,PIdateCut;
    private TextView DRcarrier,DRmessage,PIdateTittle;
    private RecyclerView donateRL;
    private InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private CarrierDB carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private List<CarrierVO> carrierVOS;
    private List<InvoiceVO> invoiceVOS;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    public  int choiceca = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_setdenote_record, container, false);
        findViewById(view);
        setlayout();
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
        swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                setlayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            choiceca--;
            if (choiceca < 0) {
                choiceca = carrierVOS.size() - 1;
            }
            setlayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceca++;
            if (choiceca > (carrierVOS.size() - 1)) {
                choiceca = 0;
            }
            setlayout();
        }
    }


    private void setlayout() {
        carrierVOS=carrierDB.getAll();
        if(carrierVOS==null||carrierVOS.size()<=0)
        {
            DRmessage.setText("請新增載具!");
            DRmessage.setVisibility(View.VISIBLE);
            return ;
        }
        DRcarrier.setText(carrierVOS.get(choiceca).getCarNul());
        invoiceVOS=invoiceDB.getisDonated(carrierVOS.get(choiceca).getCarNul());
        if(invoiceVOS==null||invoiceVOS.size()<=0)
        {
            DRmessage.setText("沒有捐贈發票~");
            DRmessage.setVisibility(View.VISIBLE);
            return ;
        }
        donateRL.setLayoutManager(new LinearLayoutManager(getActivity()));
        donateRL.setAdapter(new PriceInvoice.InvoiceAdapter(getActivity(), invoiceVOS));
    }

    private void findViewById(View view) {
        DRadd=view.findViewById(R.id.DRadd);
        DRcut=view.findViewById(R.id.DRcut);
        DRcarrier=view.findViewById(R.id.DRcarrier);
        donateRL=view.findViewById(R.id.donateRL);
        DRmessage=view.findViewById(R.id.DRmessage);
        PIdateAdd=view.findViewById(R.id.PIdateAdd);
        PIdateCut=view.findViewById(R.id.PIdateCut);
        PIdateTittle=view.findViewById(R.id.PIdateTittle);
    }

    private class InvoiceAdapter extends
            RecyclerView.Adapter<PriceInvoice.InvoiceAdapter.MyViewHolder> {
        private Context context;
        private List<InvoiceVO> invoiceVOList;


        InvoiceAdapter(Context context, List<InvoiceVO> memberList) {
            this.context = context;
            this.invoiceVOList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView day, nul,checkdonate;


            MyViewHolder(View itemView) {
                super(itemView);
                checkdonate = itemView.findViewById(R.id.DRdate);
                day = itemView.findViewById(R.id.DRnul);
                nul = itemView.findViewById(R.id.DRamout);
            }
        }

        @Override
        public int getItemCount() {
            return invoiceVOList.size();
        }

        @Override
        public PriceInvoice.InvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_setdenote_record_item, viewGroup, false);
            return new PriceInvoice.InvoiceAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PriceInvoice.InvoiceAdapter.MyViewHolder viewHolder, int position) {
            final InvoiceVO invoiceVO = invoiceVOList.get(position);
            viewHolder.checkdonate.setText(sf.format(new Date(invoiceVO.getTime().getTime())));
            viewHolder.day.setText(invoiceVO.getInvNum());
            viewHolder.nul.setText(invoiceVO.getHeartyteam());
        }
    }
}
