package com.chargeapp.whc.chargeapp.Control.Property;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListener;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertIncome;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chargeapp.whc.chargeapp.Control.Common.Currency;
import static com.chargeapp.whc.chargeapp.Control.Common.propertyCurrency;

public class PropertyInsertMoney extends Fragment {

    private BootstrapDropDown choicePropertyFrom,choiceStatue,choiceDay;
    private BootstrapButton currency,importCalculate,importCurrency,feeCalculate,feeCurrency;
    private BootstrapEditText money,importMoney,feeMoney;
    private BankDB bankDB;
    private Activity activity;
    private View view;
    private String[] nameData;
    private List<BootstrapText> propertyTypes;
    private PropertyFromDB propertyFromDB;
    private SharedPreferences sharedPreferences;
    private CurrencyVO currencyVO;
    private CurrencyDB currencyDB;
    private GridView numberKeyBoard,numberKeyBoard1;
    private CheckBox fixDate;
    private List<BootstrapText> BsTextDay,BsTextWeek,BsTextMonth,BsTextStatue;
    private String resultStatue,resultDay;
    private LinearLayout showFixDate;
    private TextView fixDateT;
    private int statueNumber;
    private Double total;

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
        view = inflater.inflate(R.layout.property_insert_money, container, false);
        findViewById();
        setDataBase();
        setDropDown();
        return view;
    }

    private void setDropDown() {
        List<String> nameList=bankDB.getAllName();
        nameData=nameList.toArray(new String[nameList.size()]);
        choicePropertyFrom.setDropdownData(nameData);
        propertyTypes=Common.propertyInsertMoneyData(activity,nameData);
        choicePropertyFrom.setBootstrapText(propertyTypes.get(0));
        choicePropertyFrom.setOnDropDownItemClickListener(new choiceMoneyName());
        total=bankDB.getTotalMoneyByName(nameData[0])-propertyFromDB.findBySourceId(nameData[0]);
        sharedPreferences = activity.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        String nowCurrency = sharedPreferences.getString(propertyCurrency, "TWD");
        currencyVO=currencyDB.getOneByType(nowCurrency);

        String cResult=Common.Currency().get(currencyVO.getType());
        currency.setText(cResult);
        importCurrency.setText(cResult);
        feeCurrency.setText(cResult);


        money.setBackgroundColor(Color.parseColor("#DDDDDD"));
        money.setText(Common.doubleRemoveZero(total/Double.valueOf(currencyVO.getMoney())));
        money.setFocusable(false);
        money.setFocusableInTouchMode(false);

        BsTextDay=Common.DateChoiceSetBsTest(activity,Common.DaySetSpinnerBS());
        BsTextWeek=Common.DateChoiceSetBsTest(activity,Common.WeekSetSpinnerBS);
        BsTextMonth=Common.DateChoiceSetBsTest(activity,Common.MonthSetSpinnerBS());
        BsTextStatue=Common.DateChoiceSetBsTest(activity,Common.DateStatueSetSpinner);
    }

    private void setDataBase() {
        Common.setChargeDB(activity);
        bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB.getReadableDatabase());
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB.getReadableDatabase());
    }

    private void findViewById() {

        //fixDate
        fixDateT=view.findViewById(R.id.fixDateT);
        showFixDate=view.findViewById(R.id.showFixDate);
        fixDate=view.findViewById(R.id.fixDate);
        choiceStatue=view.findViewById(R.id.choiceStatue);
        choiceStatue.setVisibility(View.GONE);
        choiceDay=view.findViewById(R.id.choiceDay);
        choiceDay.setVisibility(View.GONE);
        fixDate.setOnCheckedChangeListener(new showFixDate());
        choiceStatue.setOnDropDownItemClickListener(new choiceStateItemBS());
        choiceDay.setOnDropDownItemClickListener(new choiceDayItemBS());

        choicePropertyFrom=view.findViewById(R.id.choicePropertyFrom);

        currency=view.findViewById(R.id.currency);
        money=view.findViewById(R.id.money);

        //numberKeyBoard
        importCurrency=view.findViewById(R.id.importCurrency);
        numberKeyBoard = view.findViewById(R.id.numberKeyBoard);
        importCalculate=view.findViewById(R.id.importCalculate);
        importMoney=view.findViewById(R.id.importMoney);
        importMoney.setShowSoftInputOnFocus(false);

        //feeCalculate
        numberKeyBoard1=view.findViewById(R.id.numberKeyBoard1);
        feeCalculate=view.findViewById(R.id.feeCalculate);
        feeCurrency=view.findViewById(R.id.feeCurrency);
        feeMoney=view.findViewById(R.id.feeMoney);
        feeMoney.setShowSoftInputOnFocus(false);

        ArrayList items = new ArrayList<Map<String, Object>>();
        Map<String, Object> hashMap;
        for (String s : Common.keyboardArray) {
            hashMap = new HashMap<>();
            hashMap.put("text", s);
            items.add(hashMap);
        }
        SimpleAdapter adapter = new SimpleAdapter(activity, items, R.layout.ele_hand_item, new String[]{"text"},
                new int[]{R.id.cardview});
        numberKeyBoard.setAdapter(adapter);
        numberKeyBoard.setNumColumns(5);
        numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(importCalculate,importMoney,activity,numberKeyBoard,new StringBuilder(),true));
        importMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.clossKeyword(activity);
                numberKeyBoard.setVisibility(View.VISIBLE);
                numberKeyBoard1.setVisibility(View.GONE);
            }
        });
        importMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    numberKeyBoard.setVisibility(View.GONE);
                }
            }
        });

        numberKeyBoard1.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListener(feeCalculate,feeMoney,activity,numberKeyBoard,new StringBuilder(),true));
        numberKeyBoard1.setAdapter(adapter);
        numberKeyBoard1.setNumColumns(5);
        feeMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.clossKeyword(activity);
                numberKeyBoard.setVisibility(View.GONE);
                numberKeyBoard1.setVisibility(View.VISIBLE);
            }
        });
        feeMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    numberKeyBoard1.setVisibility(View.GONE);
                }
            }
        });

    }


    private class choiceMoneyName implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            choicePropertyFrom.setBootstrapText(propertyTypes.get(id));
            total=bankDB.getTotalMoneyByName(nameData[id])-propertyFromDB.findBySourceId(nameData[id]);
            money.setText(Common.doubleRemoveZero(total/Double.valueOf(currencyVO.getMoney())));


        }
    }


    private class showFixDate implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
            {
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                resultStatue=BsTextStatue.get(0).toString();
                fixDate.setX(showFixDate.getWidth()/10);
                fixDateT.setX(showFixDate.getWidth()/10+fixDate.getWidth());
                choiceStatue.setX(showFixDate.getWidth()/2+showFixDate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
            }else{
                resultStatue="";
                resultDay="";
                choiceStatue.setVisibility(View.GONE);
                choiceDay.setVisibility(View.GONE);
                fixDate.setX(showFixDate.getWidth()/3);
                fixDateT.setX(showFixDate.getWidth()/3+fixDate.getWidth());
            }
        }
    }

    private class choiceStateItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            resultStatue=BsTextStatue.get(id).toString();
            choiceStatue.setBootstrapText(BsTextStatue.get(id));
            statueNumber=id;
            choiceDay.setExpandDirection(ExpandDirection.UP);
            if(id==0)
            {
                resultDay="";
                choiceDay.setVisibility(View.GONE);
                fixDate.setX(showFixDate.getWidth()/10);
                fixDateT.setX(showFixDate.getWidth()/10+fixDate.getWidth());
                choiceStatue.setX(showFixDate.getWidth()/2+showFixDate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if(id==1)
            {
                resultDay=BsTextWeek.get(0).toString();
                choiceDay.setBootstrapText(BsTextWeek.get(0));
                choiceDay.setDropdownData(Common.WeekSetSpinnerBS);
            }
            if(id==2)
            {
                resultDay=BsTextDay.get(0).toString();
                choiceDay.setBootstrapText(BsTextDay.get(0));
                choiceDay.setDropdownData(Common.DaySetSpinnerBS());
            }
            if(id==3)
            {
                resultDay=BsTextMonth.get(0).toString();
                choiceDay.setBootstrapText(BsTextMonth.get(0));
                choiceDay.setDropdownData(Common.MonthSetSpinnerBS());
            }
            choiceDay.setVisibility(View.VISIBLE);
            fixDate.setX(showFixDate.getWidth()/20);
            fixDateT.setX(showFixDate.getWidth()/20+fixDate.getWidth());
            choiceStatue.setX(showFixDate.getWidth()/3+showFixDate.getWidth()/10);
            choiceDay.setX((showFixDate.getWidth()*2/3)+showFixDate.getWidth()/20);
        }
    }
    private class choiceDayItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            switch (statueNumber)
            {
                case 1:
                    choiceDay.setBootstrapText(BsTextWeek.get(id));
                    resultDay=BsTextWeek.get(id).toString();
                    break;
                case 2:
                    choiceDay.setBootstrapText(BsTextDay.get(id));
                    resultDay=BsTextDay.get(id).toString();
                    break;
                case 3:
                    choiceDay.setBootstrapText(BsTextMonth.get(id));
                    resultDay=BsTextMonth.get(id).toString();
                    break;
            }
        }
    }


}
