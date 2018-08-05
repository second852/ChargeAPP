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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.defaults.ExpandDirection;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.BankTybeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.Model.BankTypeVO;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
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


public class SearchByQrCode extends Fragment {
    private BootstrapEditText number, date, rdNumber;
    private AwesomeTextView flashL, flashR;
    private BootstrapButton search, back;
    private BootstrapLabel qrcodeP;
    private DatePicker datePicker;
    private Gson gson;
    private Activity context;
    private LinearLayout showdate;
    private AdView adView;
    private ConsumeVO consumeVO;
    private TextView datesave;
    private String choicedate;
    private RelativeLayout progressL;
    private SetupDateBase64 setupDateBase64;
    private String action;
    private ImageView rdNumberP;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        gson = new Gson();
        View view = inflater.inflate(R.layout.update_qrcode, container, false);
        findviewByid(view);
        consumeVO = (ConsumeVO) getArguments().getSerializable("consumeVO");
        action = (String) getArguments().getSerializable("action");
        setConsume();
        setSetOnClickView();
        view.setOnClickListener(new choicedateClick());
        return view;
    }

    private void setConsume() {
        number.setText(isNull(consumeVO.getNumber()));
        date.setText(Common.sTwo.format(consumeVO.getDate()));
        rdNumber.setText(isNull(consumeVO.getRdNumber()));
    }

    private String isNull(String s) {
        if (s == null) {
            s = "";
        }
        return s;
    }


    private void setSetOnClickView() {
        flashL.startFlashing(true, AwesomeTextView.AnimationSpeed.MEDIUM);
        flashR.startFlashing(true, AwesomeTextView.AnimationSpeed.MEDIUM);
        date.setOnClickListener(new dateClickListener());
        showdate.setOnClickListener(new choicedateClick());
        qrcodeP.setOnClickListener(new showImage());
        rdNumberP.setOnClickListener(new closeImage());
        search.setOnClickListener(new savecomsumer());
        back.setOnClickListener(new goback());
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


    public void findviewByid(View view) {
        number = view.findViewById(R.id.number);
        rdNumber = view.findViewById(R.id.rdNumber);
        date = view.findViewById(R.id.date);
        date.setFocusable(false);
        date.setFocusableInTouchMode(false);
        search = view.findViewById(R.id.search);
        back = view.findViewById(R.id.back);
        showdate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        adView = view.findViewById(R.id.adView);
        datesave = view.findViewById(R.id.datesave);
        progressL = view.findViewById(R.id.progressL);
        flashL = view.findViewById(R.id.flashL);
        flashR = view.findViewById(R.id.flashR);
        qrcodeP = view.findViewById(R.id.qrcodeP);
        rdNumberP = view.findViewById(R.id.rdNumberP);
        Common.setAdView(adView, context);
    }


    private class dateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Common.clossKeyword(context);
            showdate.setVisibility(View.VISIBLE);
        }
    }


    private class choicedateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choicedate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            date.setText(choicedate);
            showdate.setVisibility(View.GONE);
        }
    }


    private class savecomsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //date show not save
            if (showdate.getVisibility() == View.VISIBLE) {
                return;
            }

            if (rdNumberP.getVisibility() == View.VISIBLE) {
                return;
            }

            String CheckNul = number.getText().toString();
            if (CheckNul == null || CheckNul.trim().length() <= 0) {
                number.setError("不能空白");
            }

            if (CheckNul.length() != 10) {
                number.setError("統一發票中英文10個號碼");
                return;
            }
            try {
                new Integer(CheckNul.substring(2));
            } catch (NumberFormatException e) {
                number.setError("統一發票後8碼為數字");
                return;
            }
            int sN = (int) CheckNul.charAt(0);
            int eN = (int) CheckNul.charAt(1);
            if (sN < 65 || sN > 90 || eN < 65 || eN > 90) {
                number.setError("統一發票號前2碼為大寫英文字母");
                return;
            }


            if (rdNumber.getText() == null || rdNumber.getText().toString().trim().length() == 0) {
                rdNumber.setError("隨機碼不能空白");
                return;
            }
            try {
                new Integer(rdNumber.getText().toString().trim());
            } catch (Exception e) {
                rdNumber.setError("只能輸入數字");
                return;
            }

            if (date.getText() == null || date.getText().toString().trim().length() == 0) {
                date.setError(" ");
                Common.showToast(context, "日期不能空白");
                return;
            }
            consumeVO.setNumber(number.getText().toString().trim());
            consumeVO.setRdNumber(rdNumber.getText().toString().trim());
            String[] dates = date.getText().toString().split("/");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            Date d = new Date(c.getTimeInMillis());
            consumeVO.setDate(d);
            setupDateBase64 = new SetupDateBase64(SearchByQrCode.this);
            setupDateBase64.setConsumeVO(consumeVO);
            setupDateBase64.execute("getThisDetail");
            progressL.setVisibility(View.VISIBLE);
        }
    }

    public void resultD(String s) {
        progressL.setVisibility(View.GONE);
        if (s.indexOf("200") == -1) {
            Common.showToast(context, "參數有誤");
            return;
        }
    }

    private class goback implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }

    private class showImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            rdNumberP.setVisibility(View.VISIBLE);
        }
    }

    private class closeImage implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            rdNumberP.setVisibility(View.GONE);
        }
    }
}

