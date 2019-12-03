package com.chargeapp.whc.chargeapp.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.BankDB;
import com.chargeapp.whc.chargeapp.ChargeDB.ConsumeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.BankVO;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jsoup.internal.StringUtil;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class ScanByOnline extends Fragment {
    private BootstrapEditText number, date, rdNumber;
    private AwesomeTextView flashL, flashR;
    private BootstrapButton search,recordTwo,backP;
    private BootstrapLabel qrcodeP;
    private DatePicker datePicker;
    private Gson gson;
    private Activity context;
    private LinearLayout showDate;
    private ConsumeVO consumeVO;
    private String choiceDate;
    private RelativeLayout progressL;
    private SetupDateBase64 setupDateBase64;
    private ImageView rdNumberP;
    private View view;
    private ConsumeDB consumeDB;
    private PriceDB priceDB;
    private int max;




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
        consumeDB=new ConsumeDB(MainActivity.chargeAPPDB);
        priceDB=new PriceDB(MainActivity.chargeAPPDB);
        max= Integer.parseInt(priceDB.findMaxPeriod());
        context.setTitle("QR Code線上查詢");
        view = inflater.inflate(R.layout.scan_update_qrcode, container, false);
        Common.setChargeDB(context);
        findViewById(view);
        setSetOnClickView();
        view.setOnClickListener(new closeImage());
        return view;
    }


    private void setSetOnClickView() {
        flashL.startFlashing(true, AwesomeTextView.AnimationSpeed.MEDIUM);
        flashR.startFlashing(true, AwesomeTextView.AnimationSpeed.MEDIUM);
        date.setOnClickListener(new dateClickListener());
        showDate.setOnClickListener(new choiceDateClick());
        qrcodeP.setOnClickListener(new showImage());
        rdNumberP.setOnClickListener(new closeImage());
        search.setOnClickListener(new saveConsumer());
        recordTwo.setOnClickListener(new showRecord());
    }


    public void findViewById(View view) {
        number = view.findViewById(R.id.number);
        rdNumber = view.findViewById(R.id.rdNumber);
        date = view.findViewById(R.id.date);
        date.setFocusable(false);
        date.setFocusableInTouchMode(false);
        date.setText(Common.sTwo.format(new Date(System.currentTimeMillis())));
        search = view.findViewById(R.id.search);
        showDate = view.findViewById(R.id.showdate);
        datePicker = view.findViewById(R.id.datePicker);
        progressL = view.findViewById(R.id.progressL);
        flashL = view.findViewById(R.id.flashL);
        flashR = view.findViewById(R.id.flashR);
        qrcodeP = view.findViewById(R.id.qrcodeP);
        rdNumberP = view.findViewById(R.id.rdNumberP);
        recordTwo=view.findViewById(R.id.recordTwo);
        backP=view.findViewById(R.id.backP);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new ScanFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (Fragment fragment1 : getFragmentManager().getFragments()) {
                    fragmentTransaction.remove(fragment1);
                }
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
                MainActivity.oldFramgent.removeLast();
                MainActivity.bundles.removeLast();
            }
        });
    }


    private class dateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Common.clossKeyword(context);
            showDate.setVisibility(View.VISIBLE);
        }
    }


    private class choiceDateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceDate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            date.setText(choiceDate);
            showDate.setVisibility(View.GONE);
        }
    }


    private class saveConsumer implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //date show not save
            if (showDate.getVisibility() == View.VISIBLE) {
                return;
            }

            if (rdNumberP.getVisibility() == View.VISIBLE) {
                return;
            }

            String CheckNul = number.getText().toString();
            if (StringUtil.isBlank(CheckNul)) {
                number.setError("不能空白");
                return;
            }

            if (CheckNul.length() != 10) {
                number.setError("統一發票中英文10個號碼");
                return;
            }





            try {
                Integer.valueOf(CheckNul.substring(2));
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
                Integer.valueOf(rdNumber.getText().toString().trim());
            } catch (Exception e) {
                rdNumber.setError("只能輸入數字");
                return;
            }

            if (date.getText() == null || date.getText().toString().trim().length() == 0) {
                date.setError(" ");
                Common.showToast(context, "日期不能空白");
                return;
            }
            consumeVO=new ConsumeVO();
            consumeVO.setNumber(number.getText().toString().trim());
            consumeVO.setRdNumber(rdNumber.getText().toString().trim());
            String[] dates = date.getText().toString().split("/");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            Date d = new Date(c.getTimeInMillis());
            consumeVO.setDate(d);
            ConsumeVO old=consumeDB.findByNulAndAmountAndRd(consumeVO.getNumber(),consumeVO.getRdNumber(),consumeVO.getDate());
            if(old!=null)
            {
                Common.showToast(context, "手機資料庫以存檔!\n 請換下一張發票!");
                return;
            }
            setupDateBase64 = new SetupDateBase64(ScanByOnline.this);
            setupDateBase64.setConsumeVO(consumeVO);
            setupDateBase64.execute("getThisDetail");
            progressL.setVisibility(View.VISIBLE);
        }
    }

    public void resultD(String s) {
        if (s.equals("500") || s.equals("502")) {
            Common.showToast(context, "網路忙線中，請稍後再試!");
            progressL.setVisibility(View.GONE);
            return;

        }
        if (!s.contains("200")) {
            Common.showToast(context, "參數有誤");
            progressL.setVisibility(View.GONE);
            return;
        }
        if (s.contains("該筆發票並無開立") ) {
            Common.showToast(context, "1.檢查日期、號碼和隨機碼是否正確\n2.今天發票，請隔兩天查詢");
            progressL.setVisibility(View.GONE);
            return;
        }
        if (!s.contains("detail")) {
            rdNumber.setError("隨機碼錯誤");
            progressL.setVisibility(View.GONE);
            return;
        }

        JsonObject js = gson.fromJson(s, JsonObject.class);
        String currency;
        try {
            currency=js.get("currency").getAsString();
            if(currency==null||currency.trim().length()<=0)
            {
                currency="TWD";
            }
        }catch (Exception e)
        {
            currency="TWD";
        }
        consumeVO.setCurrency(currency);
        Type cdType = new TypeToken<List<JsonObject>>() {}.getType();
        String result = js.get("details").toString();
        List<JsonObject> b = gson.fromJson(result, cdType);
        double price, unit, unitTotal;
        double total = 0;
        StringBuilder sb = new StringBuilder();
        for (JsonObject jsonObject : b) {

            try {
                price = jsonObject.get("unitPrice").getAsDouble();
            }catch (Exception e)
            {
                price=0;
            }

            try {
                unit = jsonObject.get("quantity").getAsDouble();
            }catch (Exception e)
            {
                unit=0;
            }


            try {
                unitTotal = jsonObject.get("amount").getAsDouble();
            }catch (Exception e)
            {
                unitTotal=0;
            }



            try {
                sb.append(jsonObject.get("description").getAsString());
            } catch (Exception e) {
                sb.append(jsonObject.get("錯誤").getAsString());
            }
            sb.append(":\n").append(Common.doubleRemoveZero(price)).append("X").append(Common.doubleRemoveZero(unit)).append("=").append(Common.doubleRemoveZero(unitTotal) + "\n");

            try {
                total = Double.valueOf(unitTotal) + total;
            }catch (Exception e)
            {

            }

        }
        consumeVO.setRealMoney(String.valueOf(Common.DoubleToInt(total)));
        consumeVO.setDetailname(sb.toString());
        consumeVO = getType(consumeVO);


        Calendar calendar=Calendar.getInstance();
        calendar.setTime(consumeVO.getDate());
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);

        StringBuilder sPeriod = new StringBuilder();
        StringBuilder bPeriod = new StringBuilder();
        sPeriod.append((year - 1911));
        int realMonth = month + 1;
        switch (realMonth) {
            case 1:
            case 2:
                sPeriod.append("02");
                bPeriod.append("年01-02月");
                break;
            case 3:
            case 4:
                sPeriod.append("04");
                bPeriod.append("年03-04月");
                break;
            case 5:
            case 6:
                sPeriod.append("06");
                bPeriod.append("年05-06月");
                break;
            case 7:
            case 8:
                sPeriod.append("08");
                bPeriod.append("年07-08月");
                break;
            case 9:
            case 10:
                sPeriod.append("10");
                bPeriod.append("年09-10月");
                break;
            case 11:
            case 12:
                sPeriod.append("12");
                bPeriod.append("年11-12月");
                break;
        }

        if (Integer.valueOf(sPeriod.toString()) > max) {
            consumeVO.setIsWin("0");
            consumeVO.setIsWinNul("0");
        } else {

            PriceVO priceVO = priceDB.getPeriodAll(sPeriod.toString());
            if (priceVO == null) {
                consumeVO.setIsWin("0");
                consumeVO.setIsWinNul("0");
            } else {
                List<String> answer = Common.answer(consumeVO.getNumber().substring(2), priceVO);
                consumeVO.setIsWin(answer.get(0));
                consumeVO.setIsWinNul(answer.get(1));
            }
        }
        consumeVO.setFkKey(UUID.randomUUID().toString());
        consumeDB.insert(consumeVO);


        if(Common.getPrice().containsKey(consumeVO.getIsWin()))
        {
            BankDB bankDB=new BankDB(MainActivity.chargeAPPDB);
            BankVO bankVO = bankDB.getIsExist(bPeriod.toString(), consumeVO.getNumber());
            if (bankVO == null) {
                bankVO = new BankVO();
                bankVO.setFixDate("false");
                bankVO.setMoney(Common.getIntPrice().get(consumeVO.getIsWin()));
                bankVO.setDate(new Date(System.currentTimeMillis()));
                bankVO.setMaintype("中獎");
                bPeriod.append("\n" + Common.getPriceName().get(consumeVO.getIsWin()) + " : " + Common.getPrice().get(consumeVO.getIsWin()) + "\n中獎號碼 : " + consumeVO.getNumber());
                bankVO.setDetailname(bPeriod.toString());
                bankDB.insert(bankVO);
            }
        }

        ScanFragment.nulName.put(consumeVO.getNumber(),consumeVO.getRdNumber());
        Common.showToast(context, "新增成功!");
        progressL.setVisibility(View.GONE);
    }




    public void switchFragment(Fragment fragment) {
        MainActivity.oldFramgent.add(Common.scanByOnline);
        MainActivity.bundles.add(getArguments());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }



    private ConsumeVO getType(ConsumeVO consumeVO) {

        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.getTypdAll();
        String main = "O", second = "O";
        int x = 0, total = 0;
        for (TypeDetailVO t : typeDetailVOS) {
            x = 0;
            String[] key = t.getKeyword().split(" ");
            for (int i = 0; i < key.length; i++) {
                if (consumeVO.getDetailname().indexOf(key[i].trim()) != -1) {
                    x = x + key[i].length();
                }
            }
            if (x > total) {
                total = x;
                main = t.getGroupNumber();
                second = t.getName();
            }
        }
        if (second.indexOf("餐") != -1) {
            int hour = Integer.valueOf(Common.sHour.format(consumeVO.getDate()));
            if (hour > 0 && hour < 11) {
                second = "早餐";
            } else if (hour >= 11 && hour < 18) {
                second = "午餐";
            } else {
                second = "晚餐";
            }
        }
        consumeVO.setMaintype(main);
        consumeVO.setSecondType(second);
        return consumeVO;
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


    private class showRecord implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Fragment fragment=new ScanListFragment();
            fragment.setArguments(getArguments());
            switchFragment(fragment);
        }
    }
}

