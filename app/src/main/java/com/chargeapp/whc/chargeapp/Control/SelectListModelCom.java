package com.chargeapp.whc.chargeapp.Control;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class SelectListModelCom extends Fragment {
    private ImageView DRadd,DRcut;
    private TextView DRcarrier;
    private ListView listView;
    private InvoiceDB invoiceDB=new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private List<Object> objects;
    public static int month,year;
    private ProgressDialog progressDialog;
    private Gson gson=new Gson();
    public static int p;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_list, container, false);
        findViewById(view);
        progressDialog=new ProgressDialog(getActivity());
        DRadd.setOnClickListener(new addOnClick());
        DRcut.setOnClickListener(new cutOnClick());
        setLayout();
        return view;
    }

    public void cancelshow(){
        progressDialog.cancel();
        Common.showToast(getActivity(),"財政部網路忙線~");
    }

    private class cutOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p=0;
            month=month-1;
            if (month < 0) {
                month = 11;
                year=year-1;
            }
            setLayout();
        }
    }

    private class addOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            p=0;
            month=month+1;
            if (month > 11) {
                month = 0;
                year=year+1;
            }
            setLayout();
        }
    }


    public void setLayout() {
        objects=new ArrayList<>();
        Calendar start=new GregorianCalendar(year,month,1,0,0,0);
        Calendar end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
        List<ConsumeVO> consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        List<InvoiceVO> invoiceVOS=invoiceDB.getInvoiceBytime(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()));
        objects.addAll(consumeVOS);
        objects.addAll(invoiceVOS);
        Collections.sort(objects, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                long t1=(o1 instanceof ConsumeVO)?(((ConsumeVO) o1).getDate().getTime()):(((InvoiceVO) o1).getTime().getTime());
                long t2=(o2 instanceof ConsumeVO)?(((ConsumeVO) o2).getDate().getTime()):(((InvoiceVO) o2).getTime().getTime());
                if(t1>t2)
                {
                    return 1;
                }else {
                    return -1;
                }
            }
        });
        if(listView.getAdapter()!=null)
        {
            ListAdapter adapter= (ListAdapter) listView.getAdapter();
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
        }else {
            listView.setAdapter(new ListAdapter(getActivity(),objects));
        }
        DRcarrier.setText(Common.sThree.format(new Date(start.getTimeInMillis())));
        listView.setSelection(p);
        progressDialog.cancel();
    }

    private void findViewById(View view) {
        DRadd=view.findViewById(R.id.DRadd);
        DRcut=view.findViewById(R.id.DRcut);
        DRcarrier=view.findViewById(R.id.DRcarrier);
        listView=view.findViewById(R.id.list);
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_list_item, parent, false);
            }
            TextView title=itemView.findViewById(R.id.listTitle);
            TextView decribe=itemView.findViewById(R.id.listDetail);
            Button update=itemView.findViewById(R.id.updateD);
            Button deleteI=itemView.findViewById(R.id.deleteI);
            final Object o=objects.get(position);
            StringBuffer sbTitle=new StringBuffer();
            StringBuffer sbDecribe=new StringBuffer();
            if(o instanceof InvoiceVO)
            {
                final InvoiceVO I= (InvoiceVO) o;
                sbTitle.append(Common.sDay.format(new Date(I.getTime().getTime())));
                sbTitle.append(I.getSecondtype().equals("O")?"其他":I.getSecondtype());
                sbTitle.append("  共"+I.getAmount()+"元  ");
                sbTitle.append("(電子發票)");
                if(I.getDetail().equals("0"))
                {
                    update.setText("下載");
                    sbDecribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GetSQLDate(SelectListModelCom.this,I).execute("reDownload");
                            progressDialog.setMessage("正在下傳資料,請稍候...");
                            progressDialog.show();
                        }
                    });
                }else{
                    update.setText("修改");
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> js=gson.fromJson(I.getDetail(), cdType);
                    int price,n;
                    for(JsonObject j:js)
                    {
                        try {
                            n=j.get("amount").getAsInt();
                            price=j.get("unitPrice").getAsInt();
                            sbDecribe.append(j.get("description").getAsString()+" : \n"+price+"X"+n/price+"="+n+"元\n");
                        }catch (Exception e)
                        {
                            sbDecribe.append(j.get("description").getAsString()+" : \n"+0+"X"+0+"="+0+"元\n");
                        }
                    }
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p=position;
                            SelectListModelActivity.page=4;
                            Fragment fragment=new UpdateInvoice();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("invoiceVO",I);
                            bundle.putSerializable("action","SelectListModelCom");
                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                }
            }else{
                update.setText("修改");
                final ConsumeVO c= (ConsumeVO) o;
                sbTitle.append(Common.sDay.format((c.getDate())));
                sbTitle.append(c.getSecondType());
                sbTitle.append("  共"+c.getMoney()+"元  ");
                sbTitle.append("(本地)");
                sbDecribe.append(c.getDetailname());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p=position;
                        SelectListModelActivity.page=4;
                        Fragment fragment=new UpdateSpend();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("consumeVO",c);
                        bundle.putSerializable("action","SelectListModelCom");
                        bundle.putSerializable("position",position);
                        bundle.putSerializable("page",6);
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }
            title.setText(sbTitle.toString());
            decribe.setText(sbDecribe.toString());
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragement(SelectListModelCom.this);
                    aa.show(getFragmentManager(),"show");
                }
            });
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}