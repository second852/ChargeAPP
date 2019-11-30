package com.chargeapp.whc.chargeapp.Control.Property;

import android.animation.Animator;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.CurrencyResult;
import static com.chargeapp.whc.chargeapp.Control.Common.propertyCurrency;


/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyConsumeShow extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton currency;
    private PropertyDB propertyDB;
    private SharedPreferences sharedPreferences;
    private String nowCurrency;
    private PopupMenu popupMenu;
    private CurrencyDB currencyDB;
    private CurrencyVO currencyVO;
    private Calendar start,end;
    private PropertyFromDB propertyFromDB;
    private String propertyId;
    private String mainType;
    private double total;
    private List<PropertyFromVO> propertyFromVOS;
    private TextView name,totalString;
    private PropertyVO propertyVO;
    private FloatingActionButton fab;
    private LinearLayout insertMoney,insertConsume,returnMain;
    boolean isFABOpen=false;
    private View fabBGLayout;
    private PieChart chartNegative;
    private List<PieEntry>  consume;
    private Bundle bundle;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.activity=(Activity) context;
        }else{
            this.activity=getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.property_consume_total, container, false);
        bundle=getArguments();
        if(bundle==null)
        {
            Common.homePageFragment(getFragmentManager(),activity);
            return view;
        }
        propertyId= (String) bundle.getSerializable(Common.propertyID);
        mainType= (String) bundle.getSerializable(Common.propertyMainType);
        Common.setChargeDB(activity);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        propertyVO=propertyDB.findById(propertyId);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Utils.init(activity);
        findViewById();
        setPopupMenu();
        setNowMoney();
        setCircle(chartNegative, PropertyType.Negative);
        chartNegative.setOnChartValueSelectedListener(new choiceData(PropertyConsumeShow.this.getString(R.string.string_export)));
    }

    private void setCircle(PieChart pieChart, PropertyType propertyType) {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(30);
        pieChart.setRotationEnabled(true);
        pieChart.setDescription(Common.getDeescription());
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setData(getChartDataSet(propertyType));
        pieChart.highlightValues(null);
        pieChart.setBackgroundColor(Color.parseColor("#f5f5f5"));



        Legend l = pieChart.getLegend();
        PieDataSet dataSet = (PieDataSet) pieChart.getData().getDataSet();
        switch (Common.screenSize){
            case xLarge:
                dataSet.setValueTextSize(25f);
                pieChart.setEntryLabelTextSize(25f);
                l.setTextSize(20f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(20f);
                break;
            case large:
                dataSet.setValueTextSize(20f);
                pieChart.setEntryLabelTextSize(20f);
                l.setTextSize(15f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(15f);
                break;
            case normal:
                dataSet.setValueTextSize(12f);
                l.setTextSize(12f);
                l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
                l.setYEntrySpace(5f);
                l.setFormSize(12f);
                break;
        }
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();

    }

    private PieData getChartDataSet(PropertyType propertyType)
    {

        consume=new ArrayList<>();
        Map<String,Double> dataMap=propertyFromDB.getPieDataSecondType(propertyType,mainType,propertyId);
        List<PieEntry> pieEntries=new ArrayList<>();
        PieEntry pieEntry;
        for(String key:dataMap.keySet())
        {

            if(key.equals("O"))
            {

                pieEntry=new PieEntry(dataMap.get(key).floatValue(),"其他");
            }else{
                pieEntry=new PieEntry(dataMap.get(key).floatValue(),key);
            }
            pieEntries.add(pieEntry);
            consume.add(pieEntry);
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(pieEntries, "種類");
        if (pieEntries.isEmpty()) {
            dataSet.setDrawValues(false);
            pieEntries.add(new PieEntry(1, "無支出"));
            int[] c = {Color.parseColor("#CCEEFF")};
            dataSet.setColors(c);
        } else {
            dataSet.setColors(Common.getColor(pieEntries.size()));
            dataSet.setDrawValues(true);
        }

        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(30);
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        return data;
    }



    private void setNowMoney() {
        totalString.setText(mainType+"總支出");
        total=0.0;
        propertyFromVOS=propertyFromDB.findByPropertyMainType(mainType,propertyId);
        for(PropertyFromVO propertyFromVO:propertyFromVOS)
        {
            CurrencyVO currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),propertyFromVO.getSourceCurrency());
            total=total+Double.valueOf(propertyFromVO.getSourceMoney())*Double.valueOf(currencyVO.getMoney());
        }
        currency.setText(Common.CurrencyResult(total,currencyVO));
    }

    private void setPopupMenu() {
        //找出現在選擇Currency
        start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MINUTE,0);
        end=Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY,23);
        end.set(Calendar.MINUTE,59);
        end.set(Calendar.SECOND,59);
        sharedPreferences = activity.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        nowCurrency = sharedPreferences.getString(propertyCurrency, "TWD");
        currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
        popupMenu=new PopupMenu(activity,currency);
        Common.createCurrencyPopMenu(popupMenu, activity);
        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());
    }

    private void findViewById() {
        chartNegative=view.findViewById(R.id.chart_negative);
        name=view.findViewById(R.id.name);
        name.setText(propertyVO.getName());
        currency=view.findViewById(R.id.currency);
        fabBGLayout=view.findViewById(R.id.fabBGLayout);
        insertConsume=view.findViewById(R.id.insertConsume);
        insertMoney= view.findViewById(R.id.insertMoney);
        returnMain= view.findViewById(R.id.returnMain);
        totalString=view.findViewById(R.id.totalString);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });


        insertConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
                if(consumeDB.getAllMoney()<=0)
                {
                    Common.showToast(activity,"沒有消費，無法歸類!");
                    closeFABMenu();
                    return;
                }
                Fragment fragment=new PropertyInsertConsume();
                fragment.setArguments(bundle);
                Common.switchFragment(fragment, Common.PropertyTotalString,getFragmentManager());
            }
        });




        insertMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDB bankDB=new BankDB(MainActivity.chargeAPPDB);
                PropertyFromDB propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);

                Double remainMoney=(bankDB.getAllTotal()-propertyFromDB.getTotalAll());
                if(remainMoney<=0)
                {
                    Common.showToast(activity,"沒有資金，請增加收入!");
                    closeFABMenu();
                    return;
                }
                Fragment fragment=new PropertyInsertMoney();
                fragment.setArguments(bundle);
                Common.switchFragment(fragment, Common.PropertyTotalString,getFragmentManager());
            }
        });

        //返回
        returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= Common.returnFragment(view);
                Common.switchConfirmFragment(fragment,getFragmentManager());
            }
        });

    }


    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String title= (String) menuItem.getTitle();
            switch (title) {
                case "新台幣":
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(CurrencyResult(total,currencyVO));
                case "離開":
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(CurrencyResult(total,currencyVO));
                    break;
            }
            return true;
        }
    }




    private void showFABMenu(){
        isFABOpen=true;
        insertConsume.setVisibility(View.VISIBLE);
        insertMoney.setVisibility(View.VISIBLE);
        returnMain.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if (fab.getRotation() != 180) {
                    fab.setRotation(180);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        insertMoney.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        insertConsume.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        returnMain.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        insertMoney.animate().translationY(0);
        fab.animate().rotationBy(-180);
        insertConsume.animate().translationY(0);
        returnMain.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    insertMoney.setVisibility(View.GONE);
                    returnMain.setVisibility(View.GONE);
                    insertConsume.setVisibility(View.GONE);
                }
                if (fab.getRotation() != -180) {
                    fab.setRotation(-180);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private class choiceData implements com.github.mikephil.charting.listener.OnChartValueSelectedListener {

        String type;

        public choiceData(String type) {
            this.type = type;
        }

        @Override
        public void onValueSelected(Entry entry, Highlight highlight) {
            int index= (int) highlight.getX();
            Fragment fragment=new PropertyMoneyList();
            bundle.putSerializable(Common.propertySecondType,consume.get(index).getLabel());
            bundle.putSerializable(Common.propertyFragment, Common.propertyConsumeShowString);
            fragment.setArguments(bundle);
            Common.switchFragment(fragment, Common.propertyConsumeShowString,getFragmentManager());
        }

        @Override
        public void onNothingSelected() {

        }
    }
}
