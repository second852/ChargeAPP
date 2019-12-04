package com.chargeapp.whc.chargeapp.Control.SelectPicture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Adapter.DeleteDialogFragment;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateInvoice;
import com.chargeapp.whc.chargeapp.Control.Update.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectShowCircleDeList extends Fragment {

    //    private LineChart lineChart;
    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume ;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year,month,day;
    private int carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;

    private String key;
    private List<Object> objects;
    private Gson gson;
    private ProgressDialog progressDialog;
    private Calendar start,end;
    private int Statue,period,dweek;
    private String title;
    private TextView message;
    private int position;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private List<String> Okey;
    private Activity context;


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




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        gson=new Gson();
        setDB();
        carrierVOS=carrierDB.getAll();
        findViewById(view);
        progressDialog=new ProgressDialog(context);
        ShowConsume= (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier= (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier= (boolean) getArguments().getSerializable("noShowCarrier");
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        key= (String) getArguments().getSerializable("key");
        carrier= (int) getArguments().getSerializable("carrier");
        Statue=(int) getArguments().getSerializable("statue");
        period= (int) getArguments().getSerializable("period");
        dweek= (int) getArguments().getSerializable("dweek");
        position= (int) getArguments().getSerializable("position");
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            title = Common.sOne.format(new Date(start.getTimeInMillis()));
        } else if (Statue == 1) {
            start=new GregorianCalendar(year,month,day - dweek + 1,0,0,0);
            end=new GregorianCalendar(year,month,day - dweek + 1 + period-1,23,59,59);
            title = Common.sTwo.format(new Date(start.getTimeInMillis())) + "~" + Common.sTwo.format(new Date(end.getTimeInMillis()));
        } else if (Statue == 2) {
            start = new GregorianCalendar(year, month, 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            title = Common.sThree.format(new Date(start.getTimeInMillis()));
        } else {
            start = new GregorianCalendar(year, 0, 1, 0, 0, 0);
            end = new GregorianCalendar(year, 11, 31, 23, 59, 59);
            title = Common.sFour.format(new Date(start.getTimeInMillis()));
        }
        context.setTitle(title);
        choiceLayout();
        return view;
    }

    public void cancelshow(){
        progressDialog.cancel();
        Common.showToast(context,"財政部網路忙線~");
    }

    public void setOtherLayout()
    {
        objects=new ArrayList<>();
        for(String s:Okey)
        {
            if(ShowConsume)
            {
                consumeVOS=consumeDB.getSecondTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),s);
                objects.addAll(consumeVOS);
            }
            if(!noShowCarrier&&carrierVOS.size()>0)
            {
                if(ShowAllCarrier)
                {
                    invoiceVOS=invoiceDB.getInvoiceBytimeSecondType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),s);
                }else {
                    invoiceVOS = invoiceDB.getInvoiceBytimeSecondType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), s, carrierVOS.get(carrier).getCarNul());
                }
                objects.addAll(invoiceVOS);
            }
            ListAdapter adapter= (ListAdapter) listView.getAdapter();
            if(adapter==null)
            {
                listView.setAdapter(new ListAdapter(context,objects));
            }else {
                adapter.setObjects(objects);
                adapter.notifyDataSetChanged();
                listView.invalidate();
            }

            if(objects.size()<=0)
            {
                message.setText(title+"\n"+key+"種類 無資料!");
                message.setVisibility(View.VISIBLE);
            }else{

                listView.setSelection(position);
                message.setVisibility(View.GONE);
            }
            progressDialog.cancel();
        }
    }

    public void choiceLayout()
    {
        if(key.equals("其他"))
        {
            Okey=getArguments().getStringArrayList("OKey");
            setOtherLayout();
        }else {
            setLayout();
        }
    }


    public void setLayout()
    {
        objects=new ArrayList<>();
        if(ShowConsume)
        {
            consumeVOS=consumeDB.getSecondTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),key);
            objects.addAll(consumeVOS);
        }
        if(!noShowCarrier&&carrierVOS.size()>0)
        {
            if(ShowAllCarrier)
            {
                invoiceVOS=invoiceDB.getInvoiceBytimeSecondType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),key);
            }else {
                invoiceVOS = invoiceDB.getInvoiceBytimeSecondType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key, carrierVOS.get(carrier).getCarNul());
            }
            objects.addAll(invoiceVOS);
        }
        ListAdapter adapter= (ListAdapter) listView.getAdapter();
        if(adapter==null)
        {
            listView.setAdapter(new ListAdapter(context,objects));
        }else {
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }

        if(objects.size()<=0)
        {
            message.setText(title+"\n"+key+"種類 無資料!");
            message.setVisibility(View.VISIBLE);
        }else{
            listView.setSelection(position);
            message.setVisibility(View.GONE);
        }
        progressDialog.cancel();
    }

    private void findViewById(View view) {
        listView=view.findViewById(R.id.listCircle);
        message=view.findViewById(R.id.message);
    }

    private void setDB() {
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB);
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<Object> objects;

        ListAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        public List<Object> getObjects() {
            return objects;
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
            BootstrapButton update=itemView.findViewById(R.id.updateD);
            BootstrapButton deleteI=itemView.findViewById(R.id.deleteI);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            BootstrapButton fixT=itemView.findViewById(R.id.fixT);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            LinearLayout typeL=itemView.findViewById(R.id.typeL);
            BootstrapButton typeT=itemView.findViewById(R.id.typeT);

            //新增ele Type
            LinearLayout eleTypeL=itemView.findViewById(R.id.eleTypeL);
            BootstrapButton eleTypeT=itemView.findViewById(R.id.eleTypeT);


            final Object o=objects.get(position);
            StringBuffer sbDecribe=new StringBuffer();
            if(o instanceof InvoiceVO)
            {
                final InvoiceVO I= (InvoiceVO) o;

                //設定標籤
                remindL.setVisibility(View.GONE);
                fixL.setVisibility(View.GONE);

                typeL.setVisibility(View.VISIBLE);
                typeT.setText("雲端發票");
                typeT.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

                //設定電子發票種類
                try {
                    eleTypeL.setVisibility(View.VISIBLE);
                    eleTypeT.setText(Common.CardType(I.getSellerName().trim()));
                    eleTypeT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                }catch (Exception e)
                {
                    eleTypeL.setVisibility(View.GONE);
                }

                //set detail
                if(I.getDetail().equals("0"))
                {
                    update.setText("下載");
                    sbDecribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SelectShowCircleDeList.this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if(mNetworkInfo!=null)
                            {
                                new GetSQLDate(SelectShowCircleDeList.this,I).execute("reDownload");
                                progressDialog.setMessage("正在下傳資料,請稍候...");
                                progressDialog.show();
                            }else{
                                Common.showToast(SelectShowCircleDeList.this.context,"網路沒有開啟，無法下載!");
                            }
                        }
                    });
                }else{
                    update.setText("修改");
                    Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> js=gson.fromJson(I.getDetail(), cdType);
                    float amout,n;
                    for (JsonObject j : js) {
                        try {
                            amout=j.get("amount").getAsFloat();
                        } catch (Exception e) {
                            amout=0;
                        }
                        try {
                            n = j.get("quantity").getAsFloat();
                        } catch (Exception e) {
                            n=0;
                        }

                        if(n!=0)
                        {
                            sbDecribe.append(j.get("description").getAsString() + " : \n" + (int)(amout/n) + "X" + (int)n + "=" + (int)amout + "元\n");
                        }else{
                            sbDecribe.append(j.get("description").getAsString() + " : \n" + (int)amout + "X" + 1 + "=" + (int)amout + "元\n");
                        }
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment fragment=new UpdateInvoice();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("invoiceVO",I);
                            bundle.putSerializable("position",position);
                            switchFragment(fragment,bundle);
                        }
                    });
                }
                decribe.setText(sbDecribe.toString());
                title.setText(Common.setSecInvoiceTittle(I));
            }else{

                //設定Consume
                update.setText("修改");
                final ConsumeVO c= (ConsumeVO) o;

                //紙本無發票種類
                eleTypeL.setVisibility(View.GONE);

                typeL.setVisibility(View.VISIBLE);
                if(c.getNumber()==null||c.getNumber().trim().length()<=0)
                {
                    typeT.setText("無發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                }else{
                    typeT.setText("紙本發票");
                    typeT.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                }


                //set Notify
                if (Boolean.valueOf(c.getNotify())) {
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }


                //設定 title
                title.setText(Common.setSecConsumerTittlesDay(c));


                //設定 describe
                StringBuilder stringBuffer = new StringBuilder();
                fixL.setVisibility(View.GONE);
                if (c.isAuto()) {
                    fixT.setText("自動");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }


                if (c.getFixDate()!=null&&c.getFixDate().equals("true")) {

                    fixT.setText("固定");
                    fixT.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    fixL.setVisibility(View.VISIBLE);
                    try {
                        JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                        stringBuffer.append(js.get("choicestatue").getAsString().trim());
                        stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                        boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                        if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                            stringBuffer.append(" 假日除外");
                        }
                    }catch (Exception e)
                    {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append("\n");
                }

                stringBuffer.append((c.getDetailname()==null?"":c.getDetailname()));
                if(stringBuffer.indexOf("\n")==-1)
                {
                    stringBuffer.append("\n");
                }
                decribe.setText(stringBuffer.toString());

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment=new UpdateSpend();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("consumeVO",c);
                        bundle.putSerializable("position",position);
                        switchFragment(fragment,bundle);
                    }
                });
            }
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragment(SelectShowCircleDeList.this);
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



    private void switchFragment(Fragment fragment,Bundle bundle) {
        bundle.putSerializable("action","SelectShowCircleDeList");
        bundle.putSerializable("ShowConsume", ShowConsume);
        bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
        bundle.putSerializable("noShowCarrier", noShowCarrier);
        bundle.putSerializable("year", year);
        bundle.putSerializable("month", month);
        bundle.putSerializable("day", day);
        bundle.putSerializable("key", key);
        bundle.putSerializable("carrier", carrier);
        bundle.putSerializable("statue",Statue);
        bundle.putSerializable("period", period);
        bundle.putSerializable("dweek",dweek);
        bundle.putStringArrayList("OKey", SelectShowCircleDeList.this.getArguments().getStringArrayList("OKey"));
        fragment.setArguments(bundle);
        MainActivity.oldFramgent.add("SelectShowCircleDeList");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

}
