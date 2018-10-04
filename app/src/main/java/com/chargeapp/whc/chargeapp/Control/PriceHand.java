package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;


import java.util.ArrayList;
import java.util.Calendar;


import java.util.HashMap;
import java.util.List;


import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_EXCLAMATION;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_EXCLAMATION_CIRCLE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR_O;


/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceHand extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private TextView priceTitle, PIdateTittle, showRemain;
    private TextView inputNul;
    private RecyclerView donateRL;
    private PriceDB priceDB;
    private Calendar now;
    private int month, year;
    private PriceVO priceVO, oldPriceVO,grandPriceVO;
    private String message = "";
    private List<PriceVO> priceVOS;
    private HashMap<String, String> levelPrice;
    private RelativeLayout showMi, modelR;
    private BootstrapDropDown choiceModel;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private RelativeLayout PIdateL;
    private Activity context;
    private AwesomeTextView awardTitle, awardRemain;
    private int position;
    private List<BootstrapText> bootstrapTexts;
    private float remainTextSize,mutilTextSize;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (dpWidth > 650) {
            remainTextSize=40f;
            mutilTextSize=35f;
        } else if (dpWidth > 470) {
            remainTextSize=30f;
            mutilTextSize=25f;
        } else {
            remainTextSize=25f;
            mutilTextSize=18f;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_hand, container, false);
        Common.setChargeDB(context);
        priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
        now = Calendar.getInstance();
        findViewById(view);
        String period = priceDB.findMaxPeriod();
        if (period == null) {
//            cardview.setVisibility(View.GONE);
            priceTitle.setVisibility(View.GONE);
            showRemain.setVisibility(View.VISIBLE);
            modelR.setVisibility(View.GONE);
            PIdateL.setVisibility(View.GONE);
            showRemain.setText("財政部網路忙線中!\n請稍後使用!");
            return view;
        }
        this.month = Integer.valueOf(period.substring(period.length() - 2));
        this.year = Integer.valueOf(period.substring(0, period.length() - 2));
        setMonText("in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        donateRL.setLayoutManager(new GridLayoutManager(context, 3));
        List<String> number = getInputN();
        donateRL.setAdapter(new InputAdapter(context, number));
        showMi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceModel.setBootstrapText(bootstrapTexts.get(0));
                choiceModel.setShowOutline(false);
                setOneActon();
            }
        });
        return view;
    }



    private List<String> getInputN() {
        levelPrice = new HashMap<>();
        List<String> number = new ArrayList<>();
        number.add("7");
        number.add("8");
        number.add("9");
        number.add("4");
        number.add("5");
        number.add("6");
        number.add("1");
        number.add("2");
        number.add("3");
        number.add("C");
        number.add("0");
        number.add("Del");
        levelPrice.put("first", "20萬");
        levelPrice.put("second", "4萬");
        levelPrice.put("third", "1萬");
        levelPrice.put("fourth", "4000");
        levelPrice.put("fifth", "1000");
        levelPrice.put("sixth", "200");
        levelPrice.put("02", "01-02月");
        levelPrice.put("04", "03-04月");
        levelPrice.put("06", "05-06月");
        levelPrice.put("08", "07-08月");
        levelPrice.put("10", "09-10月");
        levelPrice.put("12", "11-12月");
        return number;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6){
            choiceModel.setBootstrapText(bootstrapTexts.get(0));
            choiceModel.setShowOutline(false);
            setOneActon();
            return;
        }
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (result != PackageManager.PERMISSION_GRANTED) {
            Common.showToast(context,"沒有麥克風權限，無法使聲音對獎!");
            choiceModel.setBootstrapText(bootstrapTexts.get(0));
            choiceModel.setShowOutline(false);
            setOneActon();
        }else {
            choiceModel.setBootstrapText(bootstrapTexts.get(1));
            choiceModel.setShowOutline(false);
            setTwoActon();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            startListening();
        } else {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   if(ActivityCompat.shouldShowRequestPermissionRationale(context,Manifest.permission.RECORD_AUDIO))
                   {
                       Common.askPermissions(Manifest.permission.RECORD_AUDIO, context,0);
                   }else {
                       Intent intent = new Intent();
                       intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                       Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                       intent.setData(uri);
                       context.startActivityForResult(intent,12);
                   }
                }
            };
            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    choiceModel.setBootstrapText(bootstrapTexts.get(0));
                    choiceModel.setShowOutline(false);
                    setOneActon();
                }
            };
            String remain;
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.RECORD_AUDIO)) {
                remain="沒有麥克風權限!\n請按\"YES\"並允許此權限。\n不使用此功能請按\"NO\"。";
            } else {
                remain="沒有麥克風權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開麥克風權限!\n不使用此功能請按\"NO\"。";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("無法使用聲音對獎!")
                    .setMessage(remain)
                    .setPositiveButton("YES", listener)
                    .setNegativeButton("NO", nolistener)
                    .setIcon(R.drawable.warning)
                    .show();
        }
    }

    private void autoSetInWin(String gnul) {
        String messageO, messageT;
        HashMap<Integer, String> messageHMO = new HashMap<>();
        HashMap<Integer, String> messageHMT = new HashMap<>();
        int i = 0,periodWin=0;
        for (PriceVO priceVO : priceVOS) {
            String nul = gnul;
            messageO = null;
            messageT = null;
            if (priceVO != null) {
                if (nul.equals(priceVO.getSuperPrizeNo().substring(5))) {
                    messageO = "特別獎";
                    messageT = "對獎號碼 : " + priceVO.getSuperPrizeNo();
                }
                if (nul.equals(priceVO.getSpcPrizeNo().substring(5))) {
                    messageO = "特獎";
                    messageT = "對獎號碼 : " + priceVO.getSpcPrizeNo();
                }
                if (nul.equals(priceVO.getFirstPrizeNo1().substring(5))) {
                    messageO = "頭獎";
                    messageT = "對獎號碼 : " + priceVO.getFirstPrizeNo1();
                }
                if (nul.equals(priceVO.getFirstPrizeNo2().substring(5))) {
                    messageO = "頭獎";
                    messageT = "對獎號碼 : " + priceVO.getFirstPrizeNo2();
                }
                if (nul.equals(priceVO.getFirstPrizeNo3().substring(5))) {
                    messageO = "頭獎";
                    messageT = "對獎號碼 : " + priceVO.getFirstPrizeNo3();
                }
                if (nul.equals(priceVO.getSixthPrizeNo1())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo1();
                }
                if (nul.equals(priceVO.getSixthPrizeNo2())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo2();
                }
                if (nul.equals(priceVO.getSixthPrizeNo3())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo3();
                }
                if (nul.equals(priceVO.getSixthPrizeNo4())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo4();
                }
                if (nul.equals(priceVO.getSixthPrizeNo5())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo5();
                }
                if (nul.equals(priceVO.getSixthPrizeNo6())) {
                    messageO = "六獎";
                    messageT = "對獎號碼 : " + priceVO.getSixthPrizeNo6();
                }
            }
            messageHMO.put(i, messageO);
            messageHMT.put(i, messageT);
            if(messageT!=null)
            {
                periodWin++;
            }

            i++;
        }
        String year, month;
        int redF;
        //兩期以上號碼都中 處理
        if (periodWin>=2) {
            awardTitle.setText(null);
            StringBuilder sb = new StringBuilder();
            for (int key : messageHMT.keySet()) {
                //null filter
                if (messageHMT.get(key) == null) {
                    continue;
                }
                switch (key) {
                    case 0:
                        month = priceVO.getInvoYm().substring(priceVO.getInvoYm().length() - 2);
                        year = priceVO.getInvoYm().substring(0, priceVO.getInvoYm().length() - 2);
                        sb.append(year + "年" + levelPrice.get(month));
                        break;
                    case 1:
                        month = oldPriceVO.getInvoYm().substring(oldPriceVO.getInvoYm().length() - 2);
                        year = priceVO.getInvoYm().substring(0, oldPriceVO.getInvoYm().length() - 2);
                        sb.append(year + "年" + levelPrice.get(month));
                        break;
                    case 2:
                        month = grandPriceVO.getInvoYm().substring(grandPriceVO.getInvoYm().length() - 2);
                        year = grandPriceVO.getInvoYm().substring(0, grandPriceVO.getInvoYm().length() - 2);
                        sb.append(year + "年" + levelPrice.get(month));
                        break;
                }
                sb.append(" " + messageHMT.get(key) + "\n");
            }
            priceTitle.setText("");
            //換顯示寬框
            Drawable drawable = getResources().getDrawable(R.drawable.price_result);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.RED);

            //提示
            Spannable content = new SpannableString(sb.toString());
            String[] prices=sb.toString().split(gnul);
            int totalBlack=0;
            for (int j=0;j<prices.length-2;j++)
            {
                totalBlack=totalBlack+prices[j].length()+3*j;
                content.setSpan(new ForegroundColorSpan(Color.RED), totalBlack,  totalBlack+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            int totalLength=sb.toString().length()-1;
            content.setSpan(new ForegroundColorSpan(Color.RED), totalLength-3,  totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            awardRemain.setTextSize(mutilTextSize);
            awardRemain.setText(content);
            return;
        }

        StringBuilder answerText;
        if (messageHMO.get(0) != null) {
            //獎項
            answerText=new StringBuilder();
            month = priceVO.getInvoYm().substring(priceVO.getInvoYm().length() - 2);
            answerText.append(levelPrice.get(month)).append(messageHMO.get(0));
            BootstrapText text = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_STAR)
                    .addText(" " + answerText.toString() + " ")
                    .addFontAwesomeIcon(FA_STAR)
                    .build();
            awardTitle.setText(text);
            awardTitle.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            //號碼
            redF = messageHMT.get(0).length();
            Spannable content = new SpannableString(messageHMT.get(0));
            content.setSpan(new ForegroundColorSpan(Color.RED), redF - 3, redF, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTitle.setText(content);

            //換顯示寬框
            Drawable drawable = getResources().getDrawable(R.drawable.price_result);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.RED);

            //提示不顯示
            awardRemain.setText("");
            Vibrator myVibrator = (Vibrator) this.context.getSystemService(Service.VIBRATOR_SERVICE);
            myVibrator.vibrate(500);
            return;
        }
        if (messageHMO.get(1) != null) {
            //獎項
            answerText=new StringBuilder();
            month = oldPriceVO.getInvoYm().substring(oldPriceVO.getInvoYm().length() - 2);
            answerText.append(levelPrice.get(month)).append( messageHMO.get(1));
            BootstrapText text = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_STAR_O)
                    .addText(" " + answerText.toString() + " ")
                    .addFontAwesomeIcon(FA_STAR_O)
                    .build();
            awardTitle.setText(text);
            awardTitle.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            //號碼
            redF = messageHMT.get(1).length();
            Spannable content = new SpannableString(messageHMT.get(1));
            content.setSpan(new ForegroundColorSpan(Color.parseColor("#00AA00")), redF - 3, redF, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTitle.setText(content);

            //換顯示寬框
            Drawable drawable = getResources().getDrawable(R.drawable.price_last);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.parseColor("#00AA00"));

            //提示不顯示
            awardRemain.setText("");
            Vibrator myVibrator = (Vibrator) this.context.getSystemService(Service.VIBRATOR_SERVICE);
            myVibrator.vibrate(500);
            return;
        }

        //上上期
        if (messageHMO.get(2) != null) {
            //獎項
            answerText=new StringBuilder();
            month = grandPriceVO.getInvoYm().substring(grandPriceVO.getInvoYm().length() - 2);
            answerText.append(levelPrice.get(month)).append( messageHMO.get(2));
            BootstrapText text = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_STAR_O)
                    .addText(" " + answerText.toString() + " ")
                    .addFontAwesomeIcon(FA_STAR_O)
                    .build();
            awardTitle.setText(text);
            awardTitle.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            //號碼
            redF = messageHMT.get(2).length();
            Spannable content = new SpannableString(messageHMT.get(2));
            content.setSpan(new ForegroundColorSpan(Color.parseColor("#ffc107")), redF - 3, redF, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTitle.setText(content);

            //換顯示寬框
            Drawable drawable = getResources().getDrawable(R.drawable.price_grand);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.parseColor("#ffc107"));

            //提示不顯示
            awardRemain.setText("");
            Vibrator myVibrator = (Vibrator) this.context.getSystemService(Service.VIBRATOR_SERVICE);
            myVibrator.vibrate(500);
            return;
        }

        if (messageHMO.get(0) == null) {
            BootstrapText text = new BootstrapText.Builder(context)
                    .addText(" " + "沒有中獎" + " ")
                    .addFontAwesomeIcon(FA_EXCLAMATION)
                    .addText(" " + "再接再厲" + " ")
                    .addFontAwesomeIcon(FA_FLAG)
                    .build();
            awardRemain.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            awardRemain.setText(text);
            awardRemain.setTextSize(remainTextSize);
            awardTitle.setText(null);
            priceTitle.setText(null);
        }
    }


    private void setMonText(String action) {
        String showtime, searchTime, searchOldTime,searchGrandTime;
        if (month == 2) {
            showtime = year + "年1-2月";
            searchTime = year + "02";
            searchOldTime = (year - 1) + "12";
            searchGrandTime = (year - 1) + "10";
        } else if (month == 4) {
            showtime = year + "年3-4月";
            searchTime = year + "04";
            searchOldTime = year + "02";
            searchGrandTime = (year - 1) + "12";
        } else if (month == 6) {
            showtime = year + "年5-6月";
            searchTime = year + "06";
            searchOldTime = year + "04";
            searchGrandTime = year + "02";
        } else if (month == 8) {
            showtime = year + "年7-8月";
            searchTime = year + "08";
            searchOldTime = year + "06";
            searchGrandTime = year + "04";
        } else if (month == 10) {
            showtime = year + "年9-10月";
            searchTime = year + "10";
            searchOldTime = year + "08";
            searchGrandTime = year + "06";
        } else {
            showtime = year + "年11-12月";
            searchTime = year + "12";
            searchOldTime = year + "10";
            searchGrandTime = year + "08";
        }

        priceVO = priceDB.getPeriodAll(searchTime);
        oldPriceVO = priceDB.getPeriodAll(searchOldTime);
        grandPriceVO = priceDB.getPeriodAll(searchGrandTime);


        priceVOS = new ArrayList<>();
        priceVOS.add(priceVO);
        priceVOS.add(oldPriceVO);
        priceVOS.add(grandPriceVO);

        if (priceVO == null && action.equals("add")) {
            month = month - 2;
            if (month == 0) {
                month = 12;
                year = year - 1;
            }
            setMonText("add");
            Common.showToast(context, showtime + "尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            month = month + 2;
            if (month > 12) {
                month = 2;
                year = year + 1;
            }
            setMonText("cut");
            Common.showToast(context, "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            speech.destroy();
        }
    }

    private void findViewById(View view) {
        PIdateL = view.findViewById(R.id.PIdateL);
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);
        donateRL = view.findViewById(R.id.donateRL);
        priceTitle = view.findViewById(R.id.priceTitle);
        PIdateAdd = view.findViewById(R.id.PIdateAdd);
        PIdateCut = view.findViewById(R.id.PIdateCut);
        PIdateTittle = view.findViewById(R.id.PIdateTittle);
        donateRL = view.findViewById(R.id.donateRL);
        inputNul = view.findViewById(R.id.inputNul);
        showMi = view.findViewById(R.id.showMi);
        showRemain = view.findViewById(R.id.showRemain);
        choiceModel = view.findViewById(R.id.choiceModel);
        awardRemain = view.findViewById(R.id.awardRemain);
        modelR = view.findViewById(R.id.modelR);
        awardTitle = view.findViewById(R.id.awardTitle);
        String[] SpinnerItem = new String[3];
        SpinnerItem[0]="鍵盤";
        SpinnerItem[1]="聲音";
        SpinnerItem[2]="QRCode掃描";

        bootstrapTexts=new ArrayList<>();
        for(int i=0;i<SpinnerItem.length;i++)
        {
            bootstrapTexts.add(Common.setPriceHandSetBsTest(context, SpinnerItem[i]));
        }

        choiceModel.setDropdownData(SpinnerItem);
        choiceModel.setBootstrapText(bootstrapTexts.get(0));
        choiceModel.setShowOutline(false);
        setOneActon();
        choiceModel.setOnDropDownItemClickListener(new BootstrapDropDown.OnDropDownItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View v, int i) {
                position=i;
                choiceModel.setBootstrapText(bootstrapTexts.get(i));
                choiceModel.setShowOutline(false);
                if (i == 0) {

                    setOneActon();

                } else if (i == 1) {

                    setTwoActon();


                } else if (i == 2) {

                    setThreeAction();

                }
            }
        });
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem);
//        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
//        choiceModel.setAdapter(arrayAdapter);
//        choiceModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                position=i;
//                if (i == 0) {
//                    donateRL.setVisibility(View.VISIBLE);
//                    showMi.setVisibility(View.GONE);
//
//                    awardTitle.setText(null);
//                    priceTitle.setText(null);
//
//                    BootstrapText text = new BootstrapText.Builder(context)
//                            .addText("請輸入末三碼 ")
//                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
//                            .build();
//                    awardRemain.setText(text);
//                    awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
//
//                    Drawable drawable = getResources().getDrawable(R.drawable.price_button);
//                    inputNul.setBackground(drawable);
//                    inputNul.setTextColor(Color.parseColor("#888888"));
//                    inputNul.setText(null);
//
//                    if (speech != null) {
//                        speech.stopListening();
//                        speech.cancel();
//                        speech.destroy();
//                    }
//                } else if (i == 1) {
//                    awardTitle.setText(null);
//                    priceTitle.setText(null);
//                    BootstrapText text = new BootstrapText.Builder(context)
//                            .addText("請念末三碼 ")
//                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
//                            .build();
//                    awardRemain.setText(text);
//                    awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
//
//                    showMi.setVisibility(View.VISIBLE);
//                    donateRL.setVisibility(View.INVISIBLE);
//
//                    Drawable drawable = getResources().getDrawable(R.drawable.price_button);
//                    inputNul.setBackground(drawable);
//                    inputNul.setTextColor(Color.parseColor("#888888"));
//                    inputNul.setText(null);
//
//
//                    int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
//                    if (rc != PackageManager.PERMISSION_GRANTED) {
//                        Common.askPermissions(Manifest.permission.RECORD_AUDIO, context,0);
//                    }else {
//                        startListening();
//                    }
//
//                } else if (i == 2) {
//                    Common.showToast(context, "載入資料中");
//                    MultiTrackerActivity.refresh = false;
//                    Intent intent = new Intent(context, MultiTrackerActivity.class);
//                    intent.putExtra("action", "PriceHand");
//                    startActivityForResult(intent, 6);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void setThreeAction() {

        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            speech.destroy();
        }

        Common.showToast(context, "載入資料中");
        MultiTrackerActivity.refresh = false;
        Intent intent = new Intent(context, MultiTrackerActivity.class);
        intent.putExtra("action", "PriceHand");
        startActivityForResult(intent, 6);
    }

    private void setTwoActon() {
        awardTitle.setText(null);
        priceTitle.setText(null);
        BootstrapText text = new BootstrapText.Builder(context)
                .addText("請念末三碼 ")
                .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                .build();
        awardRemain.setText(text);
        awardRemain.setTextSize(remainTextSize);
        awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);

        showMi.setVisibility(View.VISIBLE);
        donateRL.setVisibility(View.INVISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.price_button);
        inputNul.setBackground(drawable);
        inputNul.setTextColor(Color.parseColor("#888888"));
        inputNul.setText(null);

        int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            Common.askPermissions(Manifest.permission.RECORD_AUDIO, context,0);
        }else {
            startListening();
        }
    }

    private void setOneActon() {
        donateRL.setVisibility(View.VISIBLE);
        showMi.setVisibility(View.GONE);

        awardTitle.setText(null);
        priceTitle.setText(null);

        BootstrapText text = new BootstrapText.Builder(context)
                .addText("請輸入末三碼 ")
                .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                .build();
        awardRemain.setText(text);
        awardRemain.setTextSize(remainTextSize);
        awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);

        Drawable drawable = getResources().getDrawable(R.drawable.price_button);
        inputNul.setBackground(drawable);
        inputNul.setTextColor(Color.parseColor("#888888"));
        inputNul.setText(null);

        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            speech.destroy();
        }
    }


    private class addMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month += 2;
            if (month > 12) {
                month = 2;
                year++;
            }
            setMonText("add");
            Drawable drawable = getResources().getDrawable(R.drawable.price_button);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.parseColor("#888888"));
            awardTitle.setText(null);
            priceTitle.setText(null);
            inputNul.setText(null);
            message="";
            //設定提醒
            awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            BootstrapText text;
            switch (position)
            {
                case 0:
                   text = new BootstrapText.Builder(context)
                            .addText("請輸入末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setTextSize(remainTextSize);
                    break;
                case 1:
                   text = new BootstrapText.Builder(context)
                            .addText("請念末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setTextSize(remainTextSize);
                    break;
            }

        }
    }

    private class cutMonth implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            month -= 2;
            if (month == 0) {
                month = 12;
                year--;
            }
            setMonText("cut");

            Drawable drawable = getResources().getDrawable(R.drawable.price_button);
            inputNul.setBackground(drawable);
            inputNul.setTextColor(Color.parseColor("#888888"));
            awardTitle.setText(null);
            priceTitle.setText(null);
            inputNul.setText(null);
            message="";
            //設定提醒
            awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            BootstrapText text;
            switch (position)
            {
                case 0:
                    text = new BootstrapText.Builder(context)
                            .addText("請輸入末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setTextSize(remainTextSize);
                    break;
                case 1:
                    text = new BootstrapText.Builder(context)
                            .addText("請念末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setTextSize(remainTextSize);
                    break;
            }
        }
    }

    private class InputAdapter extends
            RecyclerView.Adapter<PriceHand.InputAdapter.MyViewHolder> {
        private Context context;
        private List<String> numberList;

        InputAdapter(Context context, List<String> memberList) {
            this.context = context;
            this.numberList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            Button cardview;
            View itemView;


            MyViewHolder(View itemView) {
                super(itemView);
                cardview = itemView.findViewById(R.id.cardview);
                this.itemView = itemView;
            }
        }

        @Override
        public int getItemCount() {
            return numberList.size();
        }

        @Override
        public PriceHand.InputAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.ele_hand_item, viewGroup, false);
            return new PriceHand.InputAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final PriceHand.InputAdapter.MyViewHolder viewHolder, int position) {
            final String number = numberList.get(position);
            viewHolder.cardview.setText(number);
            switch (position) {
                case 9:
                    viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_clear_before));
                    viewHolder.cardview.setTextColor(Color.parseColor("#FF0000"));
                    viewHolder.cardview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN||motionEvent.getAction()==MotionEvent.ACTION_BUTTON_PRESS)
                            {
                                Drawable drawable = getResources().getDrawable(R.drawable.price_button);
                                inputNul.setBackground(drawable);
                                inputNul.setTextColor(Color.parseColor("#888888"));
                                BootstrapText textRemain = new BootstrapText.Builder(context)
                                        .addText("請輸入末三碼 ")
                                        .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                                        .build();
                                awardRemain.setText(textRemain);
                                awardRemain.setTextSize(remainTextSize);
                                awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);

                                awardTitle.setText(null);
                                priceTitle.setText(null);
                                inputNul.setText(null);
                                message="";
                                viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_clear_after));
                                viewHolder.cardview.setTextColor(Color.WHITE);
                            }else{
                                viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_clear_before));
                                viewHolder.cardview.setTextColor(Color.parseColor("#FF0000"));
                            }
                            return true;
                        }
                    });
                    break;
                case 11:
                    viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_delete_before));
                    viewHolder.cardview.setTextColor(Color.parseColor("#009FCC"));
                    viewHolder.cardview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN||motionEvent.getAction()==MotionEvent.ACTION_BUTTON_PRESS)
                            {
                                //中獎資訊reset
                                Drawable drawable = getResources().getDrawable(R.drawable.price_button);
                                inputNul.setBackground(drawable);
                                inputNul.setTextColor(Color.parseColor("#888888"));
                                awardTitle.setText(null);
                                priceTitle.setText(null);

                                //對獎提醒
                                BootstrapText textRemain = new BootstrapText.Builder(context)
                                        .addText("請輸入末三碼 ")
                                        .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                                        .build();
                                awardRemain.setText(textRemain);
                                awardRemain.setTextSize(remainTextSize);
                                awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);

                                //顯示底線
                                SpannableString content;
                                if (message.length() > 2) {
                                    message = message.substring(0, message.length() - 1);
                                    content = new SpannableString(message);
                                    content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                                    inputNul.setText(content);
                                } else if (message.length() > 1) {
                                    message = message.substring(0, 1);
                                    content = new SpannableString(message);
                                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                    inputNul.setText(content);
                                } else {
                                    message = "";
                                    inputNul.setText(message);
                                }
                                viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_delete_after));
                                viewHolder.cardview.setTextColor(Color.WHITE);
                            }else{
                                viewHolder.cardview.setBackground(context.getDrawable(R.drawable.price_delete_before));
                                viewHolder.cardview.setTextColor(Color.parseColor("#009FCC"));
                            }
                            return true;
                        }
                    });
                    break;
                    default:
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Drawable drawable = getResources().getDrawable(R.drawable.price_button);
                                inputNul.setBackground(drawable);
                                inputNul.setTextColor(Color.parseColor("#888888"));
                                awardTitle.setText(null);
                                priceTitle.setText(null);
                                BootstrapText textRemain = new BootstrapText.Builder(context)
                                        .addText("請輸入末三碼 ")
                                        .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                                        .build();
                                awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                                message = message + number;
                                if (message.length() == 3) {
                                    autoSetInWin(message);
                                }
                                if (message.length() > 3) {
                                    message = number;
                                    awardRemain.setText(textRemain);
                                    awardRemain.setTextSize(remainTextSize);
                                }
                                //底線
                                SpannableString content;
                                content = new SpannableString(message);
                                content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                                inputNul.setText(content);
                            }
                        });
                        break;
            }
        }
    }

    public void startListening() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            startSR();
        }
    }

    public void startSR() {
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(new listener());
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }


    private class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            Log.d("XXX", "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d("XXX", "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d("XXX", "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d("XXX", "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d("XXX", "onEndofSpeech");

        }

        public void onError(int error) {
            String e = getErrorText(error);
            Log.d("XXX", "error " + e);
        }

        public void onResults(Bundle results) {

            Log.d("XXX", "onResults " + results);
            // Fill the list view with the strings the recognizer thought it could have heard, there should be 5, based on the call
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //display results.
            int matchNul = 0, y = 0;
            for (int i = 0; i < matches.size(); i++) {
                String m = matches.get(i);
                int as, x = 0;
                for (int j = 0; j < m.length(); j++) {
                    as = (int) m.charAt(j);
                    if (as >= 48 && as <= 57) {
                        x++;
                    }
                }
                if (x > y) {
                    y = x;
                    matchNul = i;
                }
            }
            inputNul.setText(matches.get(matchNul));
            autoSetInWin(matches.get(matchNul));
            startListening();
        }


        public void onPartialResults(Bundle partialResults) {
            Log.d("XXX", "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d("XXX", "onEvent " + eventType);
        }

        public String getErrorText(int errorCode) {
            String message;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    startListening();
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    startListening();
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    startListening();
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    speech.startListening(recognizerIntent);
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    //startListening();
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    startListening();
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    startListening();
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    startListening();
                    break;
            }
            return message;
        }
    }
}
