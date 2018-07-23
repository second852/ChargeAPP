package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.chargeapp.whc.chargeapp.Model.ChartEntry;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
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

    private int Statue;
    private Calendar start,end;
    private PieChart pieChart;
    private TextView detail;
    private List<Object> objects;
    private ProgressDialog progressDialog;
    private Gson gson;
    private String title;
    private int position;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private ArrayList<String> Okey;
    private boolean ShowZero;
    private List<PieEntry> yVals1;
    private int total;

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
           title=Common.sTwo.format(new Date(start.getTimeInMillis()));
        }else if(Statue==1)
        {
            start=new GregorianCalendar(year,month,day - dweek + 1,0,0,0);
            end=new GregorianCalendar(year,month,day - dweek + 1 + period-1,23,59,59);
            title=Common.sTwo.format(new Date(start.getTimeInMillis()))+" ~ "+Common.sTwo.format(new Date(end.getTimeInMillis()));
        }else if(Statue==2)
        {
            start=new GregorianCalendar(year,month,1,0,0,0);
            end=new GregorianCalendar(year,month,start.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
            title=Common.sThree.format(new Date(start.getTimeInMillis()));
            title=title.substring(0,title.indexOf("月")+1);
        }else
        {
            start=new GregorianCalendar(year,0,1,0,0,0);
            end=new GregorianCalendar(year,11,31,23,59,59);
            title=Common.sThree.format(new Date(start.getTimeInMillis()));
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
        Common.setChargeDB(getActivity());
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB.getReadableDatabase());
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private PieData addData() {
        Okey=new ArrayList<>();
        yVals1 = new ArrayList<PieEntry>();
        ChartEntry chartEntry=new ChartEntry("其他",0);
        double part=0.0;
        for(String s:hashMap.keySet())
        {
            if(total>0)
            {
                part=(hashMap.get(s)*100/total);
            }
            if(s.equals("O"))
            {
                chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                Okey.add(s);
            }else if(s.equals("0")){
                chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                Okey.add(s);
            }else {
                if(part>6)
                {
                    yVals1.add(new PieEntry(hashMap.get(s),s));
                }else{
                    chartEntry.setValue(chartEntry.getValue()+hashMap.get(s));
                    Okey.add(s);
                }
            }
        }

        if(yVals1.size()>5)
        {
            Collections.sort(yVals1, new Comparator<PieEntry>() {
                @Override
                public int compare(PieEntry pieEntry, PieEntry t1) {
                    return (int) -(pieEntry.getValue()-t1.getValue());
                }
            });
            while (yVals1.size()>5)
            {
                Okey.add(yVals1.get(4).getLabel());
                chartEntry.setValue((int) (chartEntry.getValue()+yVals1.get(4).getValue()));
                yVals1.remove(4);
            }
        }

        if(chartEntry.getValue()>0)
        {
            yVals1.add(new PieEntry(chartEntry.getValue(),chartEntry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        int size=yVals1.size();
        if(size<=0)
        {
            ShowZero=true;
            dataSet.setDrawValues(false);
            yVals1.add(new PieEntry(1, "無花費"));
            int[] c={Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        }else{
            ShowZero=false;
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

    private class pieValue implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (ShowZero) {
                return;
            }
            String key = yVals1.get((int) h.getX()).getLabel();
            Bundle bundle = new Bundle();
            Fragment fragment=new SelectShowCircleDeList();
            bundle.putStringArrayList("OKey", Okey);
            bundle.putSerializable("key",key);
            bundle.putSerializable("total",(int)h.getY());
            bundle.putSerializable("ShowConsume", ShowConsume);
            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
            bundle.putSerializable("noShowCarrier", noShowCarrier);
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("day", day);
            bundle.putSerializable("index", mainTitle);
            bundle.putSerializable("carrier",carrier);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("period", period);
            bundle.putSerializable("dweek",dweek);
            bundle.putSerializable("position",0);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    public void setLayout() {
        objects=new ArrayList<>();
        hashMap=new HashMap<>();
        total=0;
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
                total=total+c.getMoney();
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
                total= total+I.getAmount();
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
                    return -1;
                }else {
                    return 1;
                }
            }
        });

        pieChart.setData(addData());
        pieChart.highlightValues(null);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(60);
        pieChart.setRotationEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDescription(Common.getDeescription());
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new pieValue());
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
        getActivity().setTitle(title);
        detail.setText(mainTitle+" : 共"+Common.nf.format(total)+"元");
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
            TextView fixT=itemView.findViewById(R.id.fixT);
            LinearLayout remindL=itemView.findViewById(R.id.remindL);
            LinearLayout typeL=itemView.findViewById(R.id.typeL);
            TextView typeT=itemView.findViewById(R.id.typeT);

            //新增ele Type
            LinearLayout eleTypeL=itemView.findViewById(R.id.eleTypeL);
            TextView eleTypeT=itemView.findViewById(R.id.eleTypeT);


            final Object o=objects.get(position);
            StringBuffer sbTitle=new StringBuffer();
            StringBuffer sbDecribe=new StringBuffer();
            if(o instanceof InvoiceVO)
            {
                final InvoiceVO I= (InvoiceVO) o;

                //設定標籤
                remindL.setVisibility(View.GONE);
                fixL.setVisibility(View.GONE);

                typeL.setVisibility(View.VISIBLE);
                typeT.setText("雲端發票");
                typeT.setTextColor(Color.parseColor("#008844"));
                typeL.setBackgroundColor(Color.parseColor("#008844"));

                //設定電子發票種類
                try {
                    eleTypeL.setVisibility(View.VISIBLE);
                    eleTypeT.setText(Common.CardType().get(I.getCardType().trim()));
                    eleTypeT.setTextColor(Color.parseColor("#003377"));
                    eleTypeL.setBackgroundColor(Color.parseColor("#003377"));
                }catch (Exception e)
                {
                    eleTypeL.setVisibility(View.GONE);
                }

                //set Title
                sbTitle.append(Common.sDay.format(new Date(I.getTime().getTime()))+" ");
                //無法分類顯示其他
                if(I.getSecondtype().equals("O"))
                {
                    sbTitle.append("其他");
                }else if(I.getSecondtype().equals("0")){
                    sbTitle.append("未知");
                }else{
                    sbTitle.append(I.getSecondtype());
                }
                sbTitle.append(" 共"+Common.nf.format(I.getAmount())+"元");


                //set detail
                if(I.getDetail().equals("0"))
                {
                    update.setText("下載");
                    sbDecribe.append("無資料，請按下載\n  \n ");
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager mConnectivityManager = (ConnectivityManager) SelectShowCircleDe.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                            if(mNetworkInfo!=null)
                            {
                                new GetSQLDate(SelectShowCircleDe.this,I).execute("reDownload");
                                progressDialog.setMessage("正在下傳資料,請稍候...");
                                progressDialog.show();
                                SelectShowCircleDe.this.position=position;
                            }else {
                                Common.showToast(SelectShowCircleDe.this.getActivity(),"網路沒有開啟，無法下載!");
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
                title.setText(sbTitle.toString());
                decribe.setText(sbDecribe.toString());
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
                    typeT.setTextColor(Color.parseColor("#550088"));
                    typeL.setBackgroundColor(Color.parseColor("#550088"));
                }else{
                    typeT.setText("紙本發票");
                    typeT.setTextColor(Color.parseColor("#008888"));
                    typeL.setBackgroundColor(Color.parseColor("#008888"));
                }


                //set Notify
                if (Boolean.valueOf(c.getNotify())) {
                    remindL.setVisibility(View.VISIBLE);
                } else {
                    remindL.setVisibility(View.GONE);
                }


                //設定 title
                StringBuffer stringBuffer=new StringBuffer();
                stringBuffer.append(Common.sDay.format(c.getDate()));
                stringBuffer.append(" "+c.getSecondType());
                stringBuffer.append(" 共"+Common.nf.format(c.getMoney())+"元");
                title.setText(stringBuffer.toString());

                //設定 describe
                stringBuffer = new StringBuffer();
                fixL.setVisibility(View.GONE);
                if (c.isAuto()) {
                    fixT.setText("自動");
                    fixT.setTextColor(Color.parseColor("#7700BB"));
                    fixL.setBackgroundColor(Color.parseColor("#7700BB"));
                    fixL.setVisibility(View.VISIBLE);
                    JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                    stringBuffer.append(js.get("choicestatue").getAsString().trim());
                    stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                    boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                    if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                        stringBuffer.append(" 假日除外");
                    }
                    stringBuffer.append("\n");
                }


                if (c.getFixDate().equals("true")) {
                    fixT.setText("固定");
                    fixT.setTextColor(Color.parseColor("#003C9D"));
                    fixL.setBackgroundColor(Color.parseColor("#003C9D"));
                    fixL.setVisibility(View.VISIBLE);
                    JsonObject js = gson.fromJson(c.getFixDateDetail(), JsonObject.class);
                    stringBuffer.append(js.get("choicestatue").getAsString().trim());
                    stringBuffer.append(" " + js.get("choicedate").getAsString().trim());
                    boolean noweek = Boolean.parseBoolean(js.get("noweek").getAsString());
                    if (js.get("choicestatue").getAsString().trim().equals("每天") && noweek) {
                        stringBuffer.append(" 假日除外");
                    }
                    stringBuffer.append("\n");
                }

                stringBuffer.append((c.getDetailname()==null?"":c.getDetailname()));
                decribe.setText(stringBuffer.toString());


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
        MainActivity.oldFramgent.add("SelectShowCircleDe");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 :  getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }
}
