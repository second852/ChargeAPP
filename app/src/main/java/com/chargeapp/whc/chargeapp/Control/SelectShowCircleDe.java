package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectShowCircleDe extends Fragment {


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume ;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year,month,day,period,dweek;
    private int  carrier;
    private  List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private String mainTitle;
    private HashMap<String,Integer> hashMap;
    private String key;
    private int Statue;
    private Calendar start,end;
    private PieChart pieChart;
    private TextView detail;
    private List<Object> objects;
    private ProgressDialog progressDialog;
    private Gson gson;
    private String title;
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat sY=new SimpleDateFormat("yyyy 年 MM 月");
    private int position;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_circle_detail, container, false);
        setDB();
        carrierVOS=carrierDB.getAll();
        findViewById(view);
        gson=new Gson();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        progressDialog=new ProgressDialog(getActivity());
        ShowConsume= (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier= (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier= (boolean) getArguments().getSerializable("noShowCarrier");
        year= (int) getArguments().getSerializable("year");
        month= (int) getArguments().getSerializable("month");
        day= (int) getArguments().getSerializable("day");
        key= (String) getArguments().getSerializable("index");
        carrier= (int) getArguments().getSerializable("carrier");
        Statue= (int) getArguments().getSerializable("statue");
        mainTitle= (String) getArguments().getSerializable("index");
        period= (int) getArguments().getSerializable("period");
        dweek= (int) getArguments().getSerializable("dweek");
        position=(int) getArguments().getSerializable("position");
        if(Statue==0)
        {
           start=new GregorianCalendar(year,month,day,0,0,0);
           end=new GregorianCalendar(year,month,day,23,59,59);
           title=sf.format(new Date(start.getTimeInMillis()));
        }else if(Statue==1)
        {
            start=new GregorianCalendar(year,month,day - dweek + 1,0,0,0);
            end=new GregorianCalendar(year,month,day - dweek + 1 + period-1,23,59,59);
            title=sf.format(new Date(start.getTimeInMillis()))+" ~ "+sf.format(new Date(end.getTimeInMillis()));
        }else if(Statue==2)
        {
            start=new GregorianCalendar(year,month,1,0,0,0);
            end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            title=sY.format(new Date(start.getTimeInMillis()));
            title=title.substring(0,title.indexOf("月")+1);
        }else
        {
            start=new GregorianCalendar(year,0,1,0,0,0);
            end=new GregorianCalendar(year,11,31,23,59,59);
            title=sY.format(new Date(start.getTimeInMillis()));
            title=title.substring(0,title.indexOf("年")+1);
        }
        setLayout();
        return view;
    }

    public void cancelshow(){
        progressDialog.cancel();
        Common.showToast(getActivity(),"財政部網路忙線~");
    }



    private void findViewById(View view) {
        listView=view.findViewById(R.id.listDetail);
        pieChart=view.findViewById(R.id.chart_pie);
        detail=view.findViewById(R.id.detail);
    }

    private void setDB() {
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData() {
        List<PieEntry> yVals1 = new ArrayList<PieEntry>();
        int i=0;
        for(String s:hashMap.keySet())
        {
            if(s.equals("O"))
            {
                yVals1.add(new PieEntry(hashMap.get(s),"其他"));
            }else{
                yVals1.add(new PieEntry(hashMap.get(s),s));
            }
            i++;
        }
        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        int size=yVals1.size();
        if(size<=0)
        {
            dataSet.setDrawValues(false);
            yVals1.add(new PieEntry(1, "無花費"));
            int[] c={Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        }else{
            dataSet.setColors(Common.getColor(size));
            dataSet.setDrawValues(true);
            dataSet.setValueLinePart1OffsetPercentage(90.f);
            dataSet.setValueLinePart1Length(1f);
            dataSet.setValueLinePart2Length(.2f);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        }


        // create pie data set
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(20);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        return data;
    }

    public  void setLayout() {
        objects=new ArrayList<>();
        hashMap=new HashMap<>();
        int total=0;
        if(ShowConsume)
        {
            consumeVOS=consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle);
            for(ConsumeVO c:consumeVOS)
            {
                if(hashMap.get(c.getSecondType())==null)
                {
                    hashMap.put(c.getSecondType(),Integer.valueOf(c.getMoney()));
                }else{
                    hashMap.put(c.getSecondType(),hashMap.get(c.getSecondType())+Integer.valueOf(c.getMoney()));
                }
                total=total+Integer.parseInt(c.getMoney());
            }
            objects.addAll(consumeVOS);
        }
        if(!noShowCarrier&&carrierVOS.size()>0)
        {
            if(ShowAllCarrier)
            {
                invoiceVOS=invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle);
            }else{
                invoiceVOS=invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()),new Timestamp(end.getTimeInMillis()),mainTitle,carrierVOS.get(carrier).getCarNul());
            }
            for(InvoiceVO I:invoiceVOS)
            {
                if(hashMap.get(I.getSecondtype())==null)
                {
                    hashMap.put(I.getSecondtype(),Integer.valueOf(I.getAmount()));
                }else{
                    hashMap.put(I.getSecondtype(),hashMap.get(I.getSecondtype())+Integer.valueOf(I.getAmount()));
                }
                total= total+Integer.parseInt(I.getAmount());
            }
            objects.addAll(invoiceVOS);
        }

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

        pieChart.setData(addData());
        pieChart.highlightValues(null);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDescription(Common.getDeescription());
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        getActivity().setTitle(title);
        detail.setText(mainTitle+" : 共"+total+"元");
        if(listView.getAdapter()!=null)
        {
            ListAdapter adapter= (ListAdapter) listView.getAdapter();
            adapter.setObjects(objects);
            adapter.notifyDataSetChanged();
        }else {
            listView.setAdapter(new ListAdapter(getActivity(),objects));
        }
        listView.setSelection(position);
        progressDialog.cancel();
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
            Button update=itemView.findViewById(R.id.updateD);
            Button deleteI=itemView.findViewById(R.id.deleteI);
            LinearLayout fixL=itemView.findViewById(R.id.fixL);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            TextView remainT=itemView.findViewById(R.id.remainT);
            final Object o=objects.get(position);
            StringBuffer sbTitle=new StringBuffer();
            StringBuffer sbDecribe=new StringBuffer();
            if(o instanceof InvoiceVO)
            {
                final InvoiceVO I= (InvoiceVO) o;

                //設定標籤
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("電子發票");
                remainT.setTextColor(Color.parseColor("#66FF66"));
                remindL.setBackgroundColor(Color.parseColor("#66FF66"));

                sbTitle.append(sf.format(new Date(I.getTime().getTime()))+" ");
                sbTitle.append(I.getSecondtype().equals("O")?"其他":I.getSecondtype());
                sbTitle.append("  共"+I.getAmount()+"元");
                remindL.setVisibility(View.VISIBLE);
                remainT.setText("電子發票");
                if(I.getDetail().equals("0"))
                {
                    update.setText("下載");
                    sbDecribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GetSQLDate(SelectShowCircleDe.this,I).execute("reDownload");
                            progressDialog.setMessage("正在下傳資料,請稍候...");
                            progressDialog.show();
                            SelectShowCircleDe.this.position=position;
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
                            Fragment fragment=new UpdateInvoice();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("invoiceVO",I);
                            bundle.putSerializable("ShowConsume", ShowConsume);
                            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
                            bundle.putSerializable("noShowCarrier", noShowCarrier);
                            bundle.putSerializable("year", year);
                            bundle.putSerializable("month", month);
                            bundle.putSerializable("day", day);
                            bundle.putSerializable("index", mainTitle);
                            bundle.putSerializable("carrier", carrier);
                            bundle.putSerializable("statue", Statue);
                            bundle.putSerializable("period", period);
                            bundle.putSerializable("dweek",dweek);
                            bundle.putSerializable("position",position);
                            bundle.putSerializable("action","SelectShowCircleDe");
                            fragment.setArguments(bundle);
                            switchFragment(fragment);
                        }
                    });
                }
            }else{
                //設定Consume
                update.setText("修改");
                final ConsumeVO c= (ConsumeVO) o;

                if(c.isAuto())
                {
                    remainT.setText("自動");
                    remainT.setTextColor(Color.parseColor("#EE7700"));
                    remindL.setBackgroundColor(Color.parseColor("#EE7700"));
                    remindL.setVisibility(View.VISIBLE);
                }else{
                    remindL.setVisibility(View.GONE);
                }

                if(c.getNotify().equals("true"))
                {
                    remindL.setVisibility(View.VISIBLE);
                }else{
                    remindL.setVisibility(View.GONE);
                }



                StringBuffer stringBuffer=new StringBuffer();
                //設定 title
                stringBuffer.append(Common.sTwo.format(c.getDate()));
                stringBuffer.append(" "+c.getMaintype());
                stringBuffer.append("\n共"+c.getMoney()+"元");
                title.setText(stringBuffer.toString());

                //設定 describe
                if(c.getFixDate().equals("true"))
                {
                    fixL.setVisibility(View.VISIBLE);
                    stringBuffer=new StringBuffer();
                    JsonObject js=gson.fromJson(c.getFixDateDetail(),JsonObject.class);
                    stringBuffer.append(js.get("choicestatue").getAsString().trim());
                    stringBuffer.append(" "+js.get("choicedate").getAsString().trim());
                    boolean noweek= Boolean.parseBoolean(js.get("noweek").getAsString());
                    if(js.get("choicestatue").getAsString().trim().equals("每天")&&noweek)
                    {
                        stringBuffer.append(" 假日除外");
                    }
                    decribe.setText(stringBuffer.toString()+" \n"+c.getDetailname());
                }else{
                    fixL.setVisibility(View.GONE);
                    decribe.setText(c.getDetailname());
                }

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment=new UpdateSpend();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("consumeVO",c);
                        bundle.putSerializable("ShowConsume", ShowConsume);
                        bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
                        bundle.putSerializable("noShowCarrier", noShowCarrier);
                        bundle.putSerializable("year", year);
                        bundle.putSerializable("month", month);
                        bundle.putSerializable("day", day);
                        bundle.putSerializable("index", mainTitle);
                        bundle.putSerializable("carrier", carrier);
                        bundle.putSerializable("statue", Statue);
                        bundle.putSerializable("period", period);
                        bundle.putSerializable("dweek",dweek);
                        bundle.putSerializable("position",position);
                        bundle.putSerializable("action","SelectShowCircleDe");
                        fragment.setArguments(bundle);
                        switchFragment(fragment);
                    }
                });
            }
            deleteI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDialogFragment aa= new DeleteDialogFragment();
                    aa.setObject(o);
                    aa.setFragement(SelectShowCircleDe.this);
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
