package com.chargeapp.whc.chargeapp.Control;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.chargeapp.whc.chargeapp.ChargeDB.GoalDB;
import com.chargeapp.whc.chargeapp.Model.GoalVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;

/**
 * Created by Wang on 2018/2/25.
 */

public class GoalSet extends Fragment {
    private EditText name,money;
    private Spinner spinnerT,choiceStatue,remindS,remindD;
    private CheckBox forever,remind,noWeekend;
    private LinearLayout limitL,showDate;
    private TextView limitP,save,clear;
    private DatePicker datePicker;
    private View mainView;
    private GoalDB goalDB;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_setgoal, container, false);
        goalDB=new GoalDB(MainActivity.chargeAPPDB.getReadableDatabase());
        mainView=view;
        findViewById(view);
        limitP.setOnClickListener(new showDate());
        showDate.setOnClickListener(new choicedateClick());
        forever.setOnCheckedChangeListener(new forverStatue());
        remind.setOnCheckedChangeListener(new dateStatue());
        remindS.setOnItemSelectedListener(new choiceDateStatue());
        clear.setOnClickListener(new clearOnClick());
        save.setOnClickListener(new saveOnClick());
        return view;
    }





    private void findViewById(View view) {
        spinnerT=view.findViewById(R.id.spinnerT);
        name=view.findViewById(R.id.name);
        money=view.findViewById(R.id.money);
        forever=view.findViewById(R.id.forever);
        limitL=view.findViewById(R.id.limitL);
        limitP=view.findViewById(R.id.limitP);
        remind=view.findViewById(R.id.remind);
        remindS=view.findViewById(R.id.remindS);
        remindD=view.findViewById(R.id.remindD);
        showDate=view.findViewById(R.id.showDate);
        datePicker=view.findViewById(R.id.datePicker);
        noWeekend=view.findViewById(R.id.noWeekend);
        clear=view.findViewById(R.id.clear);
        save=view.findViewById(R.id.save);
        choiceStatue=view.findViewById(R.id.choiceStatue);
    }

    private class showDate implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showDate.setVisibility(View.VISIBLE);
        }
    }

    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String choicedate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            limitP.setText(choicedate);
            showDate.setVisibility(View.GONE);
        }
    }

    private class forverStatue implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int width=mainView.getWidth();
            Log.d("TAG", String.valueOf(width));
            if(b)
            {
                forever.setX(width/2-forever.getWidth()/2);
                limitL.setVisibility(View.GONE);
            }else{
                forever.setX(width/2-forever.getWidth()-forever.getWidth()/3);
                limitL.setX(width/2+forever.getWidth()/3);
                limitL.setVisibility(View.VISIBLE);
                limitP.setHint(Common.sTwo.format(new Date(System.currentTimeMillis())));
            }
        }
    }


    private class dateStatue implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int width=mainView.getWidth();
            if(b)
            {
                remind.setX(width/20);
                remindS.setX(width/2-remind.getWidth());
                noWeekend.setX(width/2+remind.getWidth());
                remindD.setX(width/2+remind.getWidth());
                remindS.setVisibility(View.VISIBLE);
                noWeekend.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
            }else {
                remind.setX(width/2-remind.getWidth()/2);
                remindD.setVisibility(View.GONE);
                remindS.setVisibility(View.GONE);
                noWeekend.setVisibility(View.GONE);
            }
        }
    }


    private class choiceDateStatue implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            ArrayList<String> spinneritem=new ArrayList<>();
            if(position==0)
            {
                noWeekend.setVisibility(View.VISIBLE);
                remindD.setVisibility(View.GONE);
                return;
            }else if(position==1)
            {
                spinneritem.add("星期一");
                spinneritem.add("星期二");
                spinneritem.add("星期三");
                spinneritem.add("星期四");
                spinneritem.add("星期五");
                spinneritem.add("星期六");
                spinneritem.add("星期日");
            }else if(position==2)
            {
                for(int i=1;i<=31;i++) {
                    spinneritem.add("    "+String.valueOf(i)+"   ");
                }

            }else{
                for(int i=1;i<=12;i++) {
                    spinneritem.add(" "+String.valueOf(i)+"月");
                }
            }
            remindD.setVisibility(View.VISIBLE);
            noWeekend.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,spinneritem);
            arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            remindD.setAdapter(arrayAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class clearOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            name.setText("");
            money.setText("");
            forever.setChecked(true);
            remind.setChecked(false);
        }
    }

    private class saveOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String goalName = name.getText().toString().trim();
            String goalMoney = money.getText().toString().trim();
            if (goalName == null || goalName.length() <= 0)
            {
                name.setError("不能空白");
                return;
            }
            if(goalMoney==null||goalMoney.length()<=0)
            {
                money.setError("不能空白");
                return;
            }
            String reMa=(remindD.getSelectedItem()==null)?"":remindD.getSelectedItem().toString();
            GoalVO goalVO=new GoalVO();
            goalVO.setName(goalName);
            goalVO.setMoney(goalMoney);
            goalVO.setNoWeekend(noWeekend.isChecked());
            goalVO.setHavePeriod(forever.isChecked());
            goalVO.setNotify(remind.isChecked());
            goalVO.setNotifyDate(reMa);
            goalVO.setNotifyStatue(remindS.getSelectedItem().toString());
            String[] dates = limitP.getText().toString().trim().split("/");
            Date d;
            if(dates.length>1)
            {
                Calendar c = new GregorianCalendar(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
                d = new Date(c.getTimeInMillis());
            }else{
                Calendar c = new GregorianCalendar(0, 0, 0);
                d = new Date(c.getTimeInMillis());
            }
            goalVO.setPeriodTime(d);
            goalVO.setType(spinnerT.getSelectedItem().toString());
            goalVO.setTimeStatue(choiceStatue.getSelectedItem().toString());
            goalDB.insert(goalVO);
            Fragment fragment = new GoalActivity();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            for (Fragment fragment1 :  getFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment1);
            }
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            Common.showToast(getActivity(),"新增成功!");
        }
    }
}
