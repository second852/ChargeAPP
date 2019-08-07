package com.chargeapp.whc.chargeapp.Control.Property;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.CurrencyResult;
import static com.chargeapp.whc.chargeapp.Control.Common.PropertyMoneyList;
import static com.chargeapp.whc.chargeapp.Control.Common.propertyCurrency;


/**
 * Created by Wang on 2019/3/12.
 */

public class PropertyTotal extends Fragment {

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
    private Long propertyId;
    private double total;
    private List<PropertyFromVO> propertyFromVOS;
    private TextView name,namePositive,nameNagative;
    private PropertyVO propertyVO;
    private FloatingActionButton fab;
    private LinearLayout insertMoney,insertConsume,returnMain;
    boolean isFABOpen=false;
    private View fabBGLayout;
    private PieChart chartPositive,chartNegative;


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
        view = inflater.inflate(R.layout.property_total, container, false);
        Object object=getArguments().getSerializable(Common.propertyID);
        if(object==null)
        {
            Common.homePageFragment(getFragmentManager(),activity);
            return view;
        }
        propertyId= (Long) object;
        Common.setChargeDB(activity);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyDB=new PropertyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyVO=propertyDB.findById(propertyId);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        findViewById();
        setPopupMenu();
        setNowMoney();
        setListView();
        setCircle(chartNegative,PropertyType.Negative);
        setCircle(chartPositive,PropertyType.Positive);
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

        Map<String,Double> dataMap=propertyFromDB.getPieDataMaiType(propertyType);
        List<PieEntry> pieEntries=new ArrayList<>();
        for(String key:dataMap.keySet())
        {
            if(key.equals("O"))
            {
                pieEntries.add(new PieEntry(dataMap.get(key).floatValue(),"其他"));
            }else{
                pieEntries.add(new PieEntry(dataMap.get(key).floatValue(),key));
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(pieEntries, "種類");
        if (pieEntries.isEmpty()) {
            dataSet.setDrawValues(false);
            pieEntries.add(new PieEntry(1, "無收入"));
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


    private void setListView() {
        List<PropertyFromVO> propertyFromVOS=propertyFromDB.findByPropertyId(propertyId);
    }

    private void setNowMoney() {
        total=0.0;
        propertyFromVOS=propertyFromDB.findByPropertyId(propertyId);
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
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());
    }

    private void findViewById() {
        chartPositive=view.findViewById(R.id.chart_positive);
        chartNegative=view.findViewById(R.id.chart_negative);
        namePositive=view.findViewById(R.id.name_positive);
        nameNagative=view.findViewById(R.id.name_negative);
        name=view.findViewById(R.id.name);
        name.setText(propertyVO.getName());
        currency=view.findViewById(R.id.currency);
        fabBGLayout=view.findViewById(R.id.fabBGLayout);
        insertConsume=view.findViewById(R.id.insertConsume);
        insertMoney= view.findViewById(R.id.insertMoney);
        returnMain= view.findViewById(R.id.returnMain);
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
        insertMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDB bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
                PropertyFromDB propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());

                Double remainMoney=(bankDB.getAllTotal()-propertyFromDB.getTotalAll());
                if(remainMoney<=0)
                {
                    Common.showToast(activity,"沒有資金，請增加收入!");
                    closeFABMenu();
                    return;
                }
                Fragment fragment=new PropertyInsertMoney();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Common.propertyID,propertyId);
                fragment.setArguments(bundle);
                Common.switchFragment(fragment,PropertyMoneyList,getFragmentManager());
            }
        });

        //返回
        returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.switchConfirmFragment(new PropertyMain(),getFragmentManager());
            }
        });

    }


    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getBytimeAndType(start.getTimeInMillis(),end.getTimeInMillis(),nowCurrency);
                    currency.setText(CurrencyResult(total,currencyVO));
                case 8:
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


    //Adapter
    private class ListAdapter extends BaseAdapter {
        private Context context;
        private List<PropertyFromVO> propertyFromVOS;

        public ListAdapter(Context context, List<PropertyFromVO> propertyFromVOS) {
            this.context = context;
            this.propertyFromVOS = propertyFromVOS;
        }

        @Override
        public int getCount() {
            return propertyFromVOS.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.property_list_from_detail, parent, false);
            }
            PropertyFromVO propertyFromVO=propertyFromVOS.get(position);
            TextView listTitle=itemView.findViewById(R.id.listTitle);
            TextView listDetail=itemView.findViewById(R.id.listDetail);
            StringBuilder title=new StringBuilder();
            if(StringUtil.isBlank(propertyFromVO.getSourceSecondType()))
            {
                title.append(propertyFromVO.getSourceMainType());
            }else{
                title.append(propertyFromVO.getSourceSecondType());
            }
            title.append(" "+Common.getCurrency(propertyFromVO.getSourceCurrency()));
            title.append(" "+Common.doubleRemoveZero(Double.valueOf(propertyFromVO.getSourceMoney())));
            listTitle.setText(title.toString());
            StringBuilder detail=new StringBuilder();
            detail.append("1. 手續費 : ");
            detail.append(Common.getCurrency(propertyFromVO.getSourceCurrency())).append(propertyFromVO.getImportFee()+"\n");
            if(propertyFromVO.getFixImport())
            {
                detail.append("2. 定期匯入 : ").append(propertyFromVO.getFixDateCode().getDetail());
                if(propertyFromVO.getFixDateDetail()!=null)
                {
                    detail.append(" "+propertyFromVO.getFixDateDetail());
                }
            }
            listDetail.setText(detail.toString());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return propertyFromVOS.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
}
