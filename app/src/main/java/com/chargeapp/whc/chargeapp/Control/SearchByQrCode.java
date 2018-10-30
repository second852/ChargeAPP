package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.BarcodeGraphic;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;



public class SearchByQrCode extends Fragment {
    private BootstrapEditText number, date, rdNumber;
    private AwesomeTextView flashL, flashR;
    private BootstrapButton search, back;
    private BootstrapLabel qrcodeP;
    private DatePicker datePicker;
    private Gson gson;
    private Activity context;
    private LinearLayout showdate;
    private ConsumeVO consumeVO;
    private TextView datesave;
    private String choicedate;
    private RelativeLayout progressL;
    private SetupDateBase64 setupDateBase64;
    private String action;
    private ImageView rdNumberP;
    private View view;
    private Bundle bundle;
    private String scan;


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
        context.setTitle("QRCode線上查詢");
        view = inflater.inflate(R.layout.update_qrcode, container, false);
        Common.setChargeDB(context);

        findviewByid(view);
        action = (String) getArguments().getSerializable("action");

        try {
            scan=(String) getArguments().getSerializable("scan");
        }catch (Exception e)
        {
            scan=null;
        }
        if (action.equals("InsertSpend")) {
            InsertSpend.needSet=true;
            consumeVO = InsertSpend.consumeVO;
        } else if (action.equals("setConsume")) {
            InsertSpend.needSet=true;
            consumeVO = InsertSpend.consumeVO;
            setQRcodCon();
        } else if (action.equals("UpdateSpend")) {
            bundle=getArguments().getBundle("bundle");
            consumeVO= (ConsumeVO) bundle.getSerializable("consumeVO");
            setQRcodCon();
        }else {
            consumeVO = (ConsumeVO) getArguments().getSerializable("consumeVO");
        }
        setConsume();
        setSetOnClickView();
        view.setOnClickListener(new closeImage());
        return view;
    }

    private void setQRcodCon() {
        if (BarcodeGraphic.hashMap.get(1) != null&&scan!=null) {
            String[] EleNulAll = BarcodeGraphic.hashMap.get(1).split(":");
            String EleNul = EleNulAll[0].substring(0, 10);
            String day = EleNulAll[0].substring(10, 17);
            String m = EleNulAll[0].substring(29, 37);
            String rdNumber = EleNulAll[0].substring(17, 21);
            Calendar calendar = new GregorianCalendar((Integer.valueOf(day.substring(0, 3)) + 1911), (Integer.valueOf(day.substring(3, 5)) - 1), Integer.valueOf(day.substring(5)), 12, 0, 0);
            consumeVO.setMoney(Integer.parseInt(m, 16));
            consumeVO.setNumber(EleNul);
            consumeVO.setDate(new Date(calendar.getTimeInMillis()));
            consumeVO.setRdNumber(rdNumber);
        }
//        if (BarcodeGraphic.hashMap.get(2) != null) {
//            String s = BarcodeGraphic.hashMap.get(2);
//            String result = "";
//            if (s.indexOf(":") == -1) {
//                try {
//                    byte[] bytes = Base64.decode(s, Base64.DEFAULT);
//                    result = new String(bytes, "UTF-8");
//                } catch (Exception e) {
//                    result = "";
//                }
//            } else {
//                try {
//                    int codeNumber=Common.identify(s.getBytes("ISO-8859-1"));
//                    switch (codeNumber){
//                        case 1:
//                            result= new String(s.getBytes("ISO-8859-1"), "Big5");
//                            break;
//                        case 2:
//                            result=s;
//                            break;
//                    }
//                } catch (Exception e1) {
//                    result = "";
//                }
//            }
//            StringBuffer sb = new StringBuffer();
//            if (result.trim().length() > 0) {
//                String[] ddd = result.trim().split(":");
//                ArrayList<String> answer = new ArrayList<>();
//                Double total, price, amount;
//                for (String string : ddd) {
//                    answer.add(string.replaceAll("\\s+", ""));
//                    if (answer.size() == 3) {
//                        price = Double.valueOf(Common.onlyNumber(answer.get(2)));
//                        amount = Double.valueOf(Common.onlyNumber(answer.get(1)));
//                        total = price * amount;
//                        sb.append(answer.get(0) + " :\n").append(answer.get(2) + " X ").append(answer.get(1) + " = ").append(Common.DoubleToInt(total) + "\n");
//                        answer.clear();
//                    }
//                }
//                consumeVO.setDetailname(sb.toString());
//            }
//        }
        if(consumeVO!=null)
        {
            MainActivity.bundles.getLast().putSerializable("consumeVO",consumeVO);
        }
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
        datesave = view.findViewById(R.id.datesave);
        progressL = view.findViewById(R.id.progressL);
        flashL = view.findViewById(R.id.flashL);
        flashR = view.findViewById(R.id.flashR);
        qrcodeP = view.findViewById(R.id.qrcodeP);
        rdNumberP = view.findViewById(R.id.rdNumberP);
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
        if (s.equals("500") || s.equals("502")) {
            Common.showToast(context, "網路忙線中，請稍後再試!");
            progressL.setVisibility(View.GONE);
            return;

        }
        if (s.indexOf("200") == -1) {
            Common.showToast(context, "參數有誤");
            progressL.setVisibility(View.GONE);
            return;
        }
        if (s.indexOf("該筆發票並無開立") != -1) {
            Common.showToast(context, "1.檢查日期、號碼和隨機碼是否正確\n2.今天發票，請隔兩天查詢");
            progressL.setVisibility(View.GONE);
            return;
        }
        if (s.indexOf("detail") == -1) {
            rdNumber.setError("隨機碼錯誤");
            progressL.setVisibility(View.GONE);
            return;
        }

        JsonObject js = gson.fromJson(s, JsonObject.class);
        Type cdType = new TypeToken<List<JsonObject>>() {
        }.getType();
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
        consumeVO.setMoney(Common.DoubleToInt(total));
        consumeVO.setDetailname(sb.toString());
        consumeVO = getType(consumeVO);
        Common.showToast(context, "查詢成功!");
        setBundle();
    }

    public void setBundle() {
        if (action.equals("InsertSpend")||action.equals("setConsume")) {
            InsertSpend.needSet=true;
            Fragment fragment = new InsertActivity();
            fragment.setArguments(getArguments());
            switchFramgent(fragment);
        }  else if (action.equals("UpdateSpend")) {
            Fragment fragment = new UpdateSpend();
            bundle.putSerializable("consumeVO",consumeVO);
            fragment.setArguments(bundle);
            switchFramgent(fragment);
        }else{
            Fragment fragment = new UpdateSpend();
            getArguments().putSerializable("consumeVO",consumeVO);
            fragment.setArguments(getArguments());
            switchFramgent(fragment);
        }
    }


    public void switchFramgent(Fragment fragment) {
        MainActivity.oldFramgent.remove(MainActivity.oldFramgent.size() - 1);
        MainActivity.bundles.remove(MainActivity.bundles.size() - 1);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fragment1 : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(fragment1);
        }
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }


    private class goback implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            consumeVO.setNumber(number.getText().toString().trim());
            consumeVO.setRdNumber(rdNumber.getText().toString().trim());
            String[] dates = date.getText().toString().split("/");
            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(dates[0]), (Integer.valueOf(dates[1]) - 1), Integer.valueOf(dates[2]), 12, 0, 0);
            Date d = new Date(c.getTimeInMillis());
            consumeVO.setDate(d);
            setBundle();
        }
    }

    private ConsumeVO getType(ConsumeVO consumeVO) {

        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB.getReadableDatabase());
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
}

