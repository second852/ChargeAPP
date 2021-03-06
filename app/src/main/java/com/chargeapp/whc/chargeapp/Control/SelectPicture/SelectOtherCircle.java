package com.chargeapp.whc.chargeapp.Control.SelectPicture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.InvoiceDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.InvoiceVO;
import com.chargeapp.whc.chargeapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;

import org.jsoup.internal.StringUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.chargeapp.whc.chargeapp.Control.Common.choiceCurrency;


/**
 * Created by 1709008NB01 on 2017/12/7.
 */

public class SelectOtherCircle extends Fragment {


    private InvoiceDB invoiceDB;
    private ConsumeDB consumeDB;
    private ListView listView;
    private boolean ShowConsume;
    private boolean ShowAllCarrier;
    private boolean noShowCarrier;
    private int year, month, day;
    private int carrier;
    private List<InvoiceVO> invoiceVOS;
    private List<ConsumeVO> consumeVOS;
    private ArrayList<String> Okey, ListKey;
    private Calendar start, end;
    private int Statue;
    private int  period, dweek;
    private List<Integer> totalList;
    private HashMap<String, HashMap<String, Double>> mapHashMap;
    private TextView message;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private String title;
    private Activity context;
    private AwesomeTextView otherMessage;
    private BootstrapButton setCurrency;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private double total;
    private LinearLayout resultL;
    private PopupMenu popupMenu;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_con_detail, container, false);
        sharedPreferences=context.getSharedPreferences("Charge_User",Context.MODE_PRIVATE);
        nowCurrency=sharedPreferences.getString(Common.choiceCurrency,"TWD");
        setDB();
        carrierVOS = carrierDB.getAll();
        ListKey = new ArrayList<>();
        listView = view.findViewById(R.id.listCircle);
        listView.setY(20f);
        message = view.findViewById(R.id.message);
        resultL=view.findViewById(R.id.resultL);
        resultL.setY(10f);
        //設定顯示幣別
        setCurrency=view.findViewById(R.id.setCurrency);
        popupMenu=new PopupMenu(context,setCurrency);
        Common.createCurrencyPopMenu(popupMenu, context);
        otherMessage=view.findViewById(R.id.otherMessage);
        setCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());


        ShowConsume = (boolean) getArguments().getSerializable("ShowConsume");
        ShowAllCarrier = (boolean) getArguments().getSerializable("ShowAllCarrier");
        noShowCarrier = (boolean) getArguments().getSerializable("noShowCarrier");
        year = (int) getArguments().getSerializable("year");
        month = (int) getArguments().getSerializable("month");
        day = (int) getArguments().getSerializable("day");
        carrier = (int) getArguments().getSerializable("carrier");
        Okey = getArguments().getStringArrayList("OKey");
        Statue = (int) getArguments().getSerializable("statue");
        period = (int) getArguments().getSerializable("period");
        dweek = (int) getArguments().getSerializable("dweek");
        if (Statue == 0) {
            start = new GregorianCalendar(year, month, day, 0, 0, 0);
            end = new GregorianCalendar(year, month, day, 23, 59, 59);
            title = Common.sOne.format(new Date(start.getTimeInMillis()));
        } else if (Statue == 1) {
            start = new GregorianCalendar(year, month, day - dweek + 1, 0, 0, 0);
            end = new GregorianCalendar(year, month, day - dweek + 1 + period - 1, 23, 59, 59);
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
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        Common.setScreen(Common.screenSize,context);
        setLayout();
        return view;
    }

    private void setLayout() {
        //設定顯示幣別

        totalList = new ArrayList<>();
        mapHashMap = new HashMap<>();
        HashMap<String, Double> second;
        HashMap<String, Double> totalOther = new HashMap<>();
        total = 0;
        double value=0.0;
        for (int i = 0; i < Okey.size(); i++) {
            String key = Okey.get(i);
            value=0.0;
            if (ShowConsume) {
                consumeVOS = consumeDB.getTimePeriod(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);

                for (ConsumeVO c : consumeVOS) {
                 CurrencyVO  currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),c.getCurrency());

                 if(StringUtil.isBlank(c.getRealMoney()))
                 {
                     c.setRealMoney(String.valueOf(c.getRealMoney()));
                     consumeDB.update(c);
                 }

                 if (mapHashMap.get(c.getMaintype()) == null) {
                        second = new HashMap<>();
                        second.put(c.getSecondType(), Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                    } else {
                        second = mapHashMap.get(c.getMaintype());
                        if (second.get(c.getSecondType()) == null) {
                            second.put(c.getSecondType(), Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                        } else {
                            second.put(c.getSecondType(), second.get(c.getSecondType()) + Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney()));
                        }
                    }
                    mapHashMap.put(c.getMaintype(), second);
                    value=Double.valueOf(c.getRealMoney())*Double.valueOf(currencyVO.getMoney());
                    total = total + value;
                }
            }

            if (!noShowCarrier && carrierVOS.size() > 0) {
                if (ShowAllCarrier) {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key);
                } else {
                    invoiceVOS = invoiceDB.getInvoiceBytimeMainType(new Timestamp(start.getTimeInMillis()), new Timestamp(end.getTimeInMillis()), key, carrierVOS.get(carrier).getCarNul());
                }


                for (InvoiceVO I : invoiceVOS) {
                    CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),I.getCurrency());
                    if (mapHashMap.get(I.getMaintype()) == null) {
                        second = new HashMap<>();
                        second.put(I.getSecondtype(), Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                    } else {
                        second = mapHashMap.get(I.getMaintype());
                        if (second.get(I.getSecondtype()) == null) {
                            second.put(I.getSecondtype(), Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                        } else {
                            second.put(I.getSecondtype(), second.get(I.getSecondtype()) + Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney()));
                        }
                    }
                    mapHashMap.put(I.getMaintype(), second);
                    value= Double.valueOf(I.getRealAmount())*Double.valueOf(currencyVO.getMoney());
                    total = total +value;
                }
            }

            if (value > 0) {
                ListKey.add(Okey.get(i));
                totalOther.put(key, total);
            }
        }
        //此頁開頭
        context.setTitle(title);
        //此頁訊息
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        otherMessage.setBootstrapBrand(null);
        otherMessage.setTextColor(Color.BLACK);
        otherMessage.setText(" 其他雜項 : 總共");
        setCurrency.setText(Common.CurrencyResult(total,currencyVO));

        if (ListKey.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new ListAdapter(context, ListKey));
        } else {
            message.setText(title + "\n其他種類 無資料!");
            message.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

    }


    private void setDB() {
        Common.setChargeDB(context);
        invoiceDB = new InvoiceDB(MainActivity.chargeAPPDB);
        consumeDB = new ConsumeDB(MainActivity.chargeAPPDB);
        carrierDB = new CarrierDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
    }

    private PieData addData(String key, TextView detail, HashMap<String, Double> hashMap) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        Double total = 0.0;

        for (String s : hashMap.keySet()) {
            if (s.equals("O")) {
                yVals1.add(new PieEntry(hashMap.get(s).floatValue(), "其他"));
            } else if (s.equals("0")) {
                yVals1.add(new PieEntry(hashMap.get(s).floatValue(), "未知"));
            } else {
                yVals1.add(new PieEntry(hashMap.get(s).floatValue(), s));
            }
            total = total + hashMap.get(s);
        }

        if (key.equals("O")) {
            detail.setText("其他" + " : " + Common.CurrencyResult(total,currencyVO));
        } else if (key.equals("total")) {
            detail.setText("其他細項 : " +Common.CurrencyResult(total,currencyVO));
        } else if (key.equals("0")) {
            detail.setText("未知" + " : " + Common.CurrencyResult(total,currencyVO));
        } else {
            detail.setText(key + " : " + Common.CurrencyResult(total,currencyVO));
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "種類");
        dataSet.setDrawValues(true);
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setColors(Common.getColor(yVals1.size()));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(35);
        dataSet.setValueTextColor(Color.BLACK);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        return data;
    }

    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<String> KeyList;

        ListAdapter(Context context, List<String> KeyList) {
            this.context = context;
            this.KeyList = KeyList;
        }

        @Override
        public int getCount() {
            return KeyList.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.select_con_detail_item, parent, false);
            }
            final PieChart pieChart = itemView.findViewById(R.id.pieChart);
            TextView detail = itemView.findViewById(R.id.detail);
            String key = KeyList.get(position);
            HashMap<String, Double> hashMap = mapHashMap.get(key);
            if (hashMap != null) {
                pieChart.setData(addData(key, detail, hashMap));
                pieChart.setOnChartValueSelectedListener(new changeToNewF(key));
                pieChart.highlightValues(null);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(7);
                pieChart.setTransparentCircleRadius(10);
                pieChart.setRotationAngle(60);
                pieChart.setRotationEnabled(true);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.getLegend().setEnabled(false);
                pieChart.setDescription(Common.getDeescription());
                pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));
                PieDataSet dataSet = (PieDataSet) pieChart.getData().getDataSet();
                switch (Common.screenSize){
                    case xLarge:
                        dataSet.setValueTextSize(25f);
                        pieChart.setEntryLabelTextSize(25f);
                        break;
                    case large:
                        dataSet.setValueTextSize(20f);
                        pieChart.setEntryLabelTextSize(20f);
                        break;
                    case normal:
                        dataSet.setValueTextSize(12f);
                        pieChart.setEntryLabelTextSize(12f);
                        break;
                }
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return KeyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private class changeToNewF implements com.github.mikephil.charting.listener.OnChartValueSelectedListener {
        private String key;

        public changeToNewF(String key) {
            this.key = key;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Fragment fragment = new SelectDetList();
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "SelectOtherCircle");
            bundle.putSerializable("ShowConsume", ShowConsume);
            bundle.putSerializable("ShowAllCarrier", ShowAllCarrier);
            bundle.putSerializable("noShowCarrier", noShowCarrier);
            bundle.putSerializable("year", year);
            bundle.putSerializable("month", month);
            bundle.putSerializable("key", key);
            bundle.putSerializable("day", day);
            bundle.putSerializable("carrier", carrier);
            bundle.putSerializable("statue", Statue);
            bundle.putSerializable("position", 0);
            bundle.putSerializable("period", period);
            bundle.putSerializable("dweek", dweek);
            bundle.putStringArrayList("OKey", ListKey);
            fragment.setArguments(bundle);
            switchFragment(fragment);
        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("SelectOtherCircle");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String title= (String) menuItem.getTitle();
            switch (title) {
                case "新台幣":
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                case "離開":
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(choiceCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    break;
            }
            setLayout();
            return true;
        }
    }




}
