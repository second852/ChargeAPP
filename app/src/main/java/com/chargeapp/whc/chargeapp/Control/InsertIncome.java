package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beardedhen.androidbootstrap.*;


public class InsertIncome extends Fragment {
    private BootstrapEditText money,detailname,name,date;
    private CheckBox fixdate;
    private TextView fixDateT;
    private BootstrapButton save, clear,standard;
    private LinearLayout showdate,showfixdate;
    private DatePicker datePicker;
    private String choicedate;
    private BootstrapDropDown choiceStatue,choiceday;
    private Gson gson;
    private BankTybeDB bankTybeDB;
    private BankDB bankDB;
    private LinearLayout firstL;
    private GridView firstG;
    private int updateChoice;
    private Handler handler,secondHander;
    public static BankVO bankVO;
    public static boolean needSet;
    private Activity context;
    private AdView adView;
    private String resultStatue,resultDay;
    private List<BootstrapText> BsTextDay,BsTextWeek,BsTextMonth,BsTextStatue;
    private int statueNumber;

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




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View view = inflater.inflate(R.layout.insert_income, container, false);
        findviewByid(view);
        Common.setChargeDB(context);
        if(bankVO==null)
        {
            bankVO=new BankVO();
        }
        BsTextDay=Common.DateChoiceSetBsTest(context,Common.DaySetSpinnerBS());
        BsTextWeek=Common.DateChoiceSetBsTest(context,Common.WeekSetSpinnerBS);
        BsTextMonth=Common.DateChoiceSetBsTest(context,Common.MonthSetSpinnerBS());
        BsTextStatue=Common.DateChoiceSetBsTest(context,Common.DateStatueSetSpinner);
        handler=new Handler();
        secondHander=new Handler();
        handler.post(runnable);
        secondHander.post(setOnClick);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        secondHander.removeCallbacks(setOnClick);
    }

    private void setSpinner() {
        choiceStatue.setDropdownData(Common.DateStatueSetSpinner);
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            bankTybeDB=new BankTybeDB(MainActivity.chargeAPPDB.getReadableDatabase());
            setFirstGrid();
            if (Common.showfirstgrid) {
                firstL.setVisibility(View.VISIBLE);
                Common.showfirstgrid = false;
            }
        }
    };

    private Runnable setOnClick=new Runnable() {
        @Override
        public void run() {
            gson=new Gson();
            bankDB=new BankDB(MainActivity.chargeAPPDB.getReadableDatabase());
            date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
            setSpinner();
            setSetOnClickView();
            if(needSet)
            {
                setIncome();
            }
        }
    };

    private void setSetOnClickView()
    {
        date.setOnClickListener(new dateClickListener());
        name.setOnClickListener(new showFirstG());
        showdate.setOnClickListener(new choicedateClick());
        choiceStatue.setOnDropDownItemClickListener(new choiceStateItemBS());
        choiceday.setOnDropDownItemClickListener(new choicedayItemBS());
//        choiceStatue.setOnItemSelectedListener(new choiceStateItem());
        clear.setOnClickListener(new clearAllInput());
        save.setOnClickListener(new savecomsumer());
        fixdate.setOnCheckedChangeListener(new showfixdateClick());
        firstG.setOnItemClickListener(new firstGridOnClick());
    }



    private void setFirstGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        List<BankTypeVO> bankTypeVOS=bankTybeDB.getAll();
        for (BankTypeVO t : bankTypeVOS) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
            item.put("text", t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增");
        items.add(item);
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.cancel);
        item.put("text", "取消");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(context,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        firstG.setAdapter(adapter);
        firstG.setNumColumns(4);
    }

    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("新增")) {
                setBankVO();
                Common.showfirstgrid = true;
                Fragment fragment=new InsertIncomeType();
                Bundle bundle=new Bundle();
                bundle.putSerializable("bankVO",bankVO);
                bundle.putSerializable("action","InsertIncome");
                needSet=true;
                fragment.setArguments(bundle);
                switchFragment(fragment);
                return;
            }
            if (type.equals("取消")) {
                firstL.setVisibility(View.GONE);
                Common.showfirstgrid=false;
                return;
            }
            name.setText(type);
            firstL.setVisibility(View.GONE);
            Common.showfirstgrid = false;
        }
    }

    private void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add("InsertIncome");
        MainActivity.bundles.add(fragment.getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private class showFirstG implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showdate.setVisibility(View.GONE);
            firstL.setVisibility(View.VISIBLE);
        }
    }
    private void setIncome() {
        name.setText(bankVO.getMaintype());
        money.setText(String.valueOf(bankVO.getMoney()));
        date.setText(Common.sTwo.format(bankVO.getDate()));
        detailname.setText(bankVO.getDetailname());
        fixdate.setChecked(Boolean.valueOf(bankVO.getFixDate()));
        if(bankVO.getFixDate().equals("true"))
        {

            JsonObject js = gson.fromJson(bankVO.getFixDateDetail(),JsonObject.class);
            String choicestatue= js.get("choicestatue").getAsString().trim();
            String choicedate=js.get("choicedate").getAsString().trim();
            if(choicestatue.trim().equals("每天"))
            {
                statueNumber=0;
                resultStatue=BsTextStatue.get(0).toString();
                resultDay="";
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                choiceday.setVisibility(View.GONE);
                choiceday.setExpandDirection(ExpandDirection.DOWN);
                return;
            }else if(choicestatue.trim().equals("每周")){
                statueNumber=1;
                resultStatue=BsTextStatue.get(1).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(1));
                choiceday.setDropdownData(Common.WeekSetSpinnerBS);
                if(choicedate.equals("星期一"))
                {
                    updateChoice=0;
                }else if(choicedate.equals("星期二"))
                {
                    updateChoice=1;
                }else if(choicedate.equals("星期三"))
                {
                    updateChoice=2;
                }else if(choicedate.equals("星期四"))
                {
                    updateChoice=3;
                }else if(choicedate.equals("星期五"))
                {
                    updateChoice=4;
                }else if(choicedate.equals("星期六"))
                {
                    updateChoice=5;
                }else{
                    updateChoice=6;
                }
                choiceday.setBootstrapText(BsTextWeek.get(updateChoice));
                resultDay=BsTextWeek.get(updateChoice).toString();
                choiceday.setExpandDirection(ExpandDirection.DOWN);
            }else if(choicestatue.trim().equals("每月")){
                statueNumber=2;
                resultStatue=BsTextStatue.get(2).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(2));
                choicedate=choicedate.substring(0,choicedate.indexOf("日"));
                updateChoice= Integer.valueOf(choicedate)-1;
                resultDay=BsTextDay.get(updateChoice).toString();
                choiceday.setBootstrapText(BsTextDay.get(updateChoice));
                choiceday.setDropdownData(Common.DaySetSpinnerBS());
                choiceday.setExpandDirection(ExpandDirection.DOWN);
            }else{
                statueNumber=3;
                resultStatue=BsTextStatue.get(3).toString();
                choiceStatue.setBootstrapText(BsTextStatue.get(3));
                updateChoice=Integer.valueOf(choicedate.substring(0,choicedate.indexOf("月")))-1;
                resultDay=BsTextMonth.get(updateChoice).toString();
                choiceday.setBootstrapText(BsTextMonth.get(updateChoice));
                choiceday.setDropdownData(Common.MonthSetSpinnerBS());
                choiceday.setExpandDirection(ExpandDirection.DOWN);
            }
            fixdate.setX(showfixdate.getWidth()/20);
            fixDateT.setX(showfixdate.getWidth()/20+fixdate.getWidth());
            choiceStatue.setX(showfixdate.getWidth()/3+showfixdate.getWidth()/10);
            choiceday.setX((showfixdate.getWidth()*2/3)+showfixdate.getWidth()/20);
        }

    }


    public void findviewByid(View view) {
        name = view.findViewById(R.id.name);
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
        money = view.findViewById(R.id.money);
        date = view.findViewById(R.id.date);
        date.setFocusable(false);
        date.setFocusableInTouchMode(false);
        fixdate = view.findViewById(R.id.fixdate);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        showdate=view.findViewById(R.id.showdate);
        datePicker=view.findViewById(R.id.datePicker);
        showfixdate=view.findViewById(R.id.showfixdate);
        choiceStatue=view.findViewById(R.id.choiceStatue);
        choiceStatue.setVisibility(View.GONE);
        choiceday=view.findViewById(R.id.choiceday);
        choiceday.setVisibility(View.GONE);
        detailname=view.findViewById(R.id.detailname);
        fixDateT=view.findViewById(R.id.fixDateT);
        firstG = view.findViewById(R.id.firstG);
        firstL = view.findViewById(R.id.firstL);
        adView = view.findViewById(R.id.adView);
        Common.setAdView(adView,context);
        standard=view.findViewById(R.id.standard);
        standard.setVisibility(View.INVISIBLE);
    }






    private class dateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            firstL.setVisibility(View.GONE);
            showdate.setVisibility(View.VISIBLE);
        }
    }


    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choicedate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }


    private class showfixdateClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
            {
                choiceStatue.setBootstrapText(BsTextStatue.get(0));
                resultStatue=BsTextStatue.get(0).toString();
                fixdate.setX(showfixdate.getWidth()/10);
                fixDateT.setX(showfixdate.getWidth()/10+fixdate.getWidth());
                choiceStatue.setX(showfixdate.getWidth()/2+showfixdate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
            }else{
                resultStatue="";
                resultDay="";
                choiceStatue.setVisibility(View.GONE);
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth()/3);
                fixDateT.setX(showfixdate.getWidth()/3+fixdate.getWidth());
            }
        }
    }

//    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//            ArrayList<String> spinneritem=new ArrayList<>();
//            if(position==0)
//            {
//                choiceday.setVisibility(View.GONE);
//                fixdate.setX(showfixdate.getWidth()/10);
//                fixDateT.setX(showfixdate.getWidth()/10+fixdate.getWidth());
//                choiceStatue.setX(showfixdate.getWidth()/2+showfixdate.getWidth()/10);
//                choiceStatue.setVisibility(View.VISIBLE);
//                return;
//            }
//            if(position==1)
//            {
//                spinneritem=Common.WeekSetSpinner();
//            }
//            if(position==2)
//            {
//                spinneritem=Common.DaySetSpinner();
//            }
//            if(position==3)
//            {
//                spinneritem=Common.MonthSetSpinner();
//            }
//            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,R.layout.spinneritem,spinneritem);
//            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//            choiceday.setAdapter(arrayAdapter);
//            choiceday.setVisibility(View.VISIBLE);
//            fixdate.setX(showfixdate.getWidth()/20);
//            fixDateT.setX(showfixdate.getWidth()/20+fixdate.getWidth());
//            choiceStatue.setX(showfixdate.getWidth()/3+showfixdate.getWidth()/10);
//            choiceday.setX((showfixdate.getWidth()*2/3)+showfixdate.getWidth()/20);
//            if (first) {
//                choiceday.setSelection(updateChoice);
//                first = false;
//            }
//        }
//
//
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    }


    private class clearAllInput implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //設定種類時 不能儲存
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }

            //date show not save
            if(showdate.getVisibility()==View.VISIBLE)
            {
                return;
            }
            name.setText("");
            money.setText("");
            fixdate.setChecked(false);
            detailname.setText("");
            choiceStatue.setBootstrapText(BsTextStatue.get(0));
            choiceday.setBootstrapText(BsTextDay.get(0));
            resultDay="";
            resultStatue="";
        }
    }

    private String isnull(String text)
    {
        if(text==null||text.toString().length()<=0)
        {
            return " ";
        }
        text=text.substring(0,text.lastIndexOf(" "));
        return text.toString();
    }

    private void setBankVO() {
        Map<String, String> g = new HashMap<>();
        g.put("choicestatue", isnull(resultStatue));
        g.put("choicedate", isnull(resultDay));
        String fixdatedetail = gson.toJson(g);
        String[] dates = date.getText().toString().split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
        Date d = new Date(c.getTimeInMillis());
        bankVO.setMaintype(name.getText().toString().trim());
        try {
            bankVO.setMoney(Integer.valueOf(money.getText().toString().trim()));
        }catch (NumberFormatException e)
        {
            bankVO.setMoney(0);
        }
        bankVO.setDate(d);
        bankVO.setFixDate(String.valueOf(fixdate.isChecked()));
        bankVO.setFixDateDetail(fixdatedetail);
        bankVO.setDetailname(detailname.getText().toString().trim());
        bankVO.setAuto(false);
        bankVO.setAutoId(-1);
        needSet=false;
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //設定種類時 不能儲存
            if(firstL.getVisibility()==View.VISIBLE)
            {
                return;
            }
            //date show not save
            if(showdate.getVisibility()==View.VISIBLE)
            {
                return;
            }


            if(name.getText()==null||name.getText().toString().trim().length()==0)
            {
                name.setError(" ");
                Common.showToast(context,"主項目不能空白");
                return;
            }
            if(money.getText()==null||money.getText().toString().trim().length()==0)
            {
                money.setError("金額不能空白");
                return;
            }


            try {
                if (Integer.valueOf(money.getText().toString().trim()) == 0) {
                    money.setError("金額不能為0");
                    return;
                }
            } catch (Exception e) {
                money.setError("只能輸入數字");
                return;
            }

            if(date.getText()==null||date.getText().toString().trim().length()==0)
            {
                date.setError(" ");
                Common.showToast(context,"日期不能空白");
                return;
            }
            setBankVO();
            bankDB.insert(bankVO);
            Common.showToast(context,"新增成功");
            bankVO=new BankVO();
            name.setText("");
            money.setText("");
            fixdate.setChecked(false);
            detailname.setText("");
//            choiceStatue.setSelection(0);
//            choiceday.setSelection(0);
        }
    }

    private class choiceStateItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            resultStatue=BsTextStatue.get(id).toString();
            choiceStatue.setBootstrapText(BsTextStatue.get(id));
            statueNumber=id;
            choiceday.setExpandDirection(ExpandDirection.DOWN);
            if(id==0)
            {
                resultDay="";
                choiceday.setVisibility(View.GONE);
                fixdate.setX(showfixdate.getWidth()/10);
                fixDateT.setX(showfixdate.getWidth()/10+fixdate.getWidth());
                choiceStatue.setX(showfixdate.getWidth()/2+showfixdate.getWidth()/10);
                choiceStatue.setVisibility(View.VISIBLE);
                return;
            }
            if(id==1)
            {
                resultDay=BsTextWeek.get(0).toString();
                choiceday.setBootstrapText(BsTextWeek.get(0));
                choiceday.setDropdownData(Common.WeekSetSpinnerBS);
            }
            if(id==2)
            {
                resultDay=BsTextDay.get(0).toString();
                choiceday.setBootstrapText(BsTextDay.get(0));
                choiceday.setDropdownData(Common.DaySetSpinnerBS());
            }
            if(id==3)
            {
                resultDay=BsTextMonth.get(0).toString();
                choiceday.setBootstrapText(BsTextMonth.get(0));
                choiceday.setDropdownData(Common.MonthSetSpinnerBS());
            }
            choiceday.setVisibility(View.VISIBLE);
            fixdate.setX(showfixdate.getWidth()/20);
            fixDateT.setX(showfixdate.getWidth()/20+fixdate.getWidth());
            choiceStatue.setX(showfixdate.getWidth()/3+showfixdate.getWidth()/10);
            choiceday.setX((showfixdate.getWidth()*2/3)+showfixdate.getWidth()/20);
        }
    }
    private class choicedayItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            switch (statueNumber)
            {
                case 1:
                    choiceday.setBootstrapText(BsTextWeek.get(id));
                    resultDay=BsTextWeek.get(id).toString();
                    break;
                case 2:
                    choiceday.setBootstrapText(BsTextDay.get(id));
                    resultDay=BsTextDay.get(id).toString();
                    break;
                case 3:
                    choiceday.setBootstrapText(BsTextMonth.get(id));
                    resultDay=BsTextMonth.get(id).toString();
                    break;
            }
        }
    }
}

