package com.chargeapp.whc.chargeapp.Control.Property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListener;
import com.chargeapp.whc.chargeapp.Adapter.KeyBoardInputNumberOnItemClickListenerTwo;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.CurrencyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PropertyFromDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.CurrencyVO;
import com.chargeapp.whc.chargeapp.Model.PropertyFromVO;
import com.chargeapp.whc.chargeapp.Model.PropertyVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.TypeCode.FixDateCode;
import com.chargeapp.whc.chargeapp.TypeCode.PropertyType;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.chargeapp.whc.chargeapp.Control.Common.propertyCurrency;


public class PropertyInsertMoney extends Fragment {

    private BootstrapDropDown choicePropertyFrom,choiceStatue,choiceDay;
    private BootstrapButton currency,importCalculate,importCurrency,feeCalculate,feeCurrency,save;
    private BootstrapEditText money,importMoney,feeMoney,date;
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
    private PopupMenu popupMenu;
    private String nowCurrency,choiceSource;
    private DatePicker datePicker;
    private String choiceDate;
    private LinearLayout showDate;
    private PropertyVO propertyVO;
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
        view = inflater.inflate(R.layout.property_insert_money, container, false);
        bundle=getArguments();
        if(bundle==null)
        {
            Common.homePageFragment(getFragmentManager(),activity);
            return view;
        }
        String object= (String) getArguments().getSerializable(Common.propertyID);

        PropertyDB propertyDB=new PropertyDB(MainActivity.chargeAPPDB);
        propertyVO=propertyDB.findById(object);
        nowCurrency="TWD";
        findViewById();
        setDataBase();
        setDropDown();
        setPopupMenu();
        Double d=total/Double.valueOf(currencyVO.getMoney());
        numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListenerTwo(importCalculate,importMoney,activity,numberKeyBoard,new StringBuilder(),true,money,d));
        return view;
    }

    private void setDropDown() {
        List<String> nameList=bankDB.getAllName();
        nameData=nameList.toArray(new String[nameList.size()]);
        choicePropertyFrom.setDropdownData(nameData);
        propertyTypes= Common.propertyInsertMoneyData(activity,nameData);
        choicePropertyFrom.setBootstrapText(propertyTypes.get(0));
        choicePropertyFrom.setOnDropDownItemClickListener(new choiceMoneyName());
        total=bankDB.getTotalMoneyByName(nameData[0])-propertyFromDB.findBySourceMainType(nameData[0]);
        sharedPreferences = activity.getSharedPreferences("Charge_User", Context.MODE_PRIVATE);
        String nowCurrency = sharedPreferences.getString(propertyCurrency, "TWD");
        currencyVO=currencyDB.getOneByType(nowCurrency);

        String cResult= Common.Currency().get(currencyVO.getType());
        currency.setText(cResult);
        importCurrency.setText(cResult);
        feeCurrency.setText(cResult);


        money.setBackgroundColor(Color.parseColor("#DDDDDD"));
        money.setText(Common.doubleRemoveZero(total/Double.valueOf(currencyVO.getMoney())));
        money.setFocusable(false);
        money.setFocusableInTouchMode(false);

        BsTextDay= Common.DateChoiceSetBsTest(activity, Common.DaySetSpinnerBS());
        BsTextWeek= Common.DateChoiceSetBsTest(activity, Common.WeekSetSpinnerBS);
        BsTextMonth= Common.DateChoiceSetBsTest(activity, Common.MonthSetSpinnerBS());
        BsTextStatue= Common.DateChoiceSetBsTest(activity, Common.DateStatueSetSpinner);
        resultStatue= Common.DateStatueSetSpinner[0];
        choiceSource=nameData[0];
    }

    private void setDataBase() {
        Common.setChargeDB(activity);
        bankDB=new BankDB(MainActivity.chargeAPPDB);
        propertyFromDB=new PropertyFromDB(MainActivity.chargeAPPDB);
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
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
        //save
        save=view.findViewById(R.id.save);
        save.setOnClickListener(new savePropertyFrom());
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

        datePicker=view.findViewById(R.id.datePicker);
        date=view.findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate.setVisibility(View.VISIBLE);
                showDate.requestFocus();
            }
        });
        date.setShowSoftInputOnFocus(false);
        date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
        showDate=view.findViewById(R.id.showDate);
        showDate.setOnClickListener(new choiceDateClick());
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                resultStatue = "";
                resultDay = "";
                choiceStatue.setVisibility(View.GONE);
                choiceDay.setVisibility(View.GONE);
                fixDate.setX(showFixDate.getWidth() / 3);
                fixDateT.setX(showFixDate.getWidth() / 3 + fixDate.getWidth());

            }
        });
    }


    private class choiceMoneyName implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            choiceSource=nameData[id];
            choicePropertyFrom.setBootstrapText(propertyTypes.get(id));
            total=bankDB.getTotalMoneyByName(choiceSource)-propertyFromDB.findBySourceMainType(choiceSource);


            Double nowMoney;
            try {
                nowMoney= new Double(importMoney.getText().toString().trim());
            }catch (Exception e)
            {
                nowMoney=0.0;
            }



            Double nowTotal=(total/Double.valueOf(currencyVO.getMoney()))-nowMoney;
            money.setText(Common.doubleRemoveZero(nowTotal));
            money.setBackgroundColor(Color.parseColor("#DDDDDD"));
            Double d=total/Double.valueOf(currencyVO.getMoney());
            StringBuilder sb=new StringBuilder();
            sb.append(importMoney.getText().toString());
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListenerTwo(importCalculate,importMoney,activity,numberKeyBoard,sb,false,money,d));
        }
    }


    private class showFixDate implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
            {
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                resultStatue= Common.DateStatueSetSpinner[0];
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
            resultStatue= Common.DateStatueSetSpinner[id];
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
                resultDay= Common.WeekSetSpinnerBS[0];
                choiceDay.setBootstrapText(BsTextWeek.get(0));
                choiceDay.setDropdownData(Common.WeekSetSpinnerBS);
            }
            if(id==2)
            {
                resultDay= Common.DaySetSpinnerBS()[0];
                choiceDay.setBootstrapText(BsTextDay.get(0));
                choiceDay.setDropdownData(Common.DaySetSpinnerBS());
            }
            if(id==3)
            {
                resultDay= Common.MonthSetSpinnerBS()[0];
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
                    resultDay= Common.WeekSetSpinnerBS[id];
                    break;
                case 2:
                    choiceDay.setBootstrapText(BsTextDay.get(id));
                    resultDay= Common.DaySetSpinnerBS()[id];
                    break;
                case 3:
                    choiceDay.setBootstrapText(BsTextMonth.get(id));
                    resultDay= Common.MonthSetSpinnerBS()[id];
                    break;
            }
        }
    }


    private class savePropertyFrom implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(importMoney.getText()==null){
                importMoney.setError(getString(R.string.error_Zero));
                return;
            }
            String stringMoney=importMoney.getText().toString().trim();
            if(stringMoney.isEmpty())
            {
                importMoney.setError(getString(R.string.error_Zero));
                return;
            }
            Integer iMoney;
            try {
                  iMoney=Integer.valueOf(stringMoney);
                  if(iMoney<0)
                  {
                      importMoney.setError(getString(R.string.error_negative_Integer));
                  }
            }catch (Exception e)
            {
                importMoney.setError(getString(R.string.error_Integer));
                return;
            }
            Integer fee=0;
            if(feeMoney.getText()!=null)
            {

                try {
                    fee=Integer.valueOf(feeMoney.getText().toString().trim());
                    if(fee<0)
                    {
                        feeMoney.setError(getString(R.string.error_negative_Integer));
                    }
                }catch (Exception e)
                {
                    feeMoney.setError(getString(R.string.error_Integer));
                    return;
                }
            }

            Double actuallyTotal= null;
            try {
                actuallyTotal = Common.nf.parse(money.getText().toString()).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(actuallyTotal<0)
            {
                importMoney.setError(getString(R.string.error_overMoney));
                return;
            }



            PropertyFromVO propertyFromVO=new PropertyFromVO();
            propertyFromVO.setType(PropertyType.Positive);
            propertyFromVO.setSourceCurrency(nowCurrency);
            propertyFromVO.setSourceMoney(iMoney.toString());
            propertyFromVO.setSourceMainType(choiceSource);
            propertyFromVO.setSourceSecondType(null);

            String sourceDate=date.getText().toString();
            String[] dateArray=sourceDate.split("/");
            Calendar sourceTime=new GregorianCalendar(Integer.valueOf(dateArray[0]),Integer.valueOf(dateArray[1])-1,Integer.valueOf(dateArray[2]));
            propertyFromVO.setSourceTime(sourceTime.getTime());
            propertyFromVO.setImportFee(fee.toString());
            propertyFromVO.setPropertyId(propertyVO.getId());
            propertyFromVO.setFixImport(fixDate.isChecked());
            propertyFromVO.setFixDateCode(FixDateCode.detailToEnum(resultStatue.trim()));
            propertyFromVO.setFixDateDetail(resultDay);



            if(fee>0)
            {
                ConsumeVO consumeVO=new ConsumeVO();
                consumeVO.setMaintype("銀行");
                consumeVO.setSecondType("轉帳");
                consumeVO.setCurrency(nowCurrency);
                consumeVO.setRealMoney(fee.toString());
                consumeVO.setFixDate("false");
                consumeVO.setDetailname("轉入"+propertyVO.getName()+"的費用");
                consumeVO.setDate(new Date(System.currentTimeMillis()));
                ConsumeDB consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
                consumeVO.setFkKey(UUID.randomUUID().toString());
                consumeDB.insert(consumeVO);
                propertyFromVO.setImportFeeId(consumeVO.getFkKey());
            }

            propertyFromDB.insert(propertyFromVO);

            Fragment fragment=new PropertyMoneyList();
            bundle=new Bundle();
            bundle.putSerializable(Common.propertyID,propertyVO.getId());
            bundle.putSerializable(Common.propertyFragment,Common.propertyInsertMoneyString);
            bundle.putSerializable(Common.propertyMainType,propertyFromVO.getSourceMainType());
            fragment.setArguments(bundle);
            Common.switchConfirmFragment(fragment,getFragmentManager());
            Common.showToast(activity,getString(R.string.insert_success));
        }
    }


    private void  setPopupMenu()
    {
        popupMenu=new PopupMenu(activity,importCurrency);
        Common.createCurrencyPopMenu(popupMenu, activity);
        importCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        currencyDB=new CurrencyDB(MainActivity.chargeAPPDB);
        popupMenu.setOnMenuItemClickListener(new choiceCurrency());
    }

    private class choiceCurrency implements PopupMenu.OnMenuItemClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    nowCurrency = "TWD";
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=new CurrencyVO("TWD","1");
                case 8:
                    popupMenu.dismiss();
                    break;
                default:
                    nowCurrency = Common.code.get(menuItem.getItemId() - 2);
                    sharedPreferences.edit().putString(propertyCurrency, nowCurrency).apply();
                    currencyVO=currencyDB.getOneByType(nowCurrency);
                    break;
            }

            String cResult= Common.Currency().get(currencyVO.getType());
            currency.setText(cResult);
            importCurrency.setText(cResult);
            feeCurrency.setText(cResult);

            Double nowMoney;
            try {
                nowMoney= new Double(importMoney.getText().toString().trim());
            }catch (Exception e)
            {
                nowMoney=0.0;
            }


            Double nowTotal=(total/Double.valueOf(currencyVO.getMoney()))-nowMoney;
            money.setText(Common.doubleRemoveZero(nowTotal));
            Double d=total/Double.valueOf(currencyVO.getMoney());

            StringBuilder sb=new StringBuilder();
            sb.append(importMoney.getText().toString());
            numberKeyBoard.setOnItemClickListener(new KeyBoardInputNumberOnItemClickListenerTwo(importCalculate,importMoney,activity,numberKeyBoard,sb,false,money,d));
            return true;
        }
    }


    private class choiceDateClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            date.requestFocus();
            choiceDate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            date.setText(choiceDate);
            showDate.setVisibility(View.GONE);
            date.setSelection(choiceDate.length());


        }
    }

}
