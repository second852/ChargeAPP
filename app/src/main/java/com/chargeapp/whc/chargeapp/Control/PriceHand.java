package com.chargeapp.whc.chargeapp.Control;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.chargeapp.whc.chargeapp.ChargeDB.PriceDB;
import com.chargeapp.whc.chargeapp.Model.PriceVO;
import com.chargeapp.whc.chargeapp.R;
import com.chargeapp.whc.chargeapp.ui.MultiTrackerActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BAN;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BUG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CC_JCB;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_COLUMNS;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_EXCLAMATION;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_EXCLAMATION_CIRCLE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_HEADPHONES;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_HEART;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_HEARTBEAT;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_QUESTION;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR_O;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_TWITTER;

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
    private PriceVO priceVO, oldPriceVO;
    private String message = "";
    private List<PriceVO> priceVOS;
    private HashMap<String, String> levelPrice;
    private RelativeLayout showMi, modelR;
    private Spinner choiceModel;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private RelativeLayout PIdateL;
    private Activity context;
    private AwesomeTextView awardTitle, awardRemain;

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
                choiceModel.setSelection(0);
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

    private void autoSetInWin(String gnul) {
        String messageO, messageT;
        HashMap<Integer, String> messageHMO = new HashMap<>();
        HashMap<Integer, String> messageHMT = new HashMap<>();
        int i = 0;
        for (PriceVO priceVO : priceVOS) {
            String nul = gnul;
            messageO = null;
            messageT = null;
            if (priceVO != null) {
                if (nul.equals(priceVO.getSuperPrizeNo().substring(5))) {
                    messageO = "特別獎";
                    messageT = "兌獎號碼 : " + priceVO.getSuperPrizeNo();
                }
                if (nul.equals(priceVO.getSpcPrizeNo().substring(5))) {
                    messageO = "特獎";
                    messageT = "兌獎號碼 : " + priceVO.getSpcPrizeNo();
                }
                if (nul.equals(priceVO.getFirstPrizeNo1().substring(5))) {
                    messageO = "頭獎";
                    messageT = "兌獎號碼 : " + priceVO.getFirstPrizeNo1();
                }
                if (nul.equals(priceVO.getFirstPrizeNo2().substring(5))) {
                    messageO = "頭獎";
                    messageT = "兌獎號碼 : " + priceVO.getFirstPrizeNo2();
                }
                if (nul.equals(priceVO.getFirstPrizeNo3().substring(5))) {
                    messageO = "頭獎";
                    messageT = "兌獎號碼 : " + priceVO.getFirstPrizeNo3();
                }
                if (nul.equals(priceVO.getSixthPrizeNo1())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo1();
                }
                if (nul.equals(priceVO.getSixthPrizeNo2())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo2();
                }
                if (nul.equals(priceVO.getSixthPrizeNo3())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo3();
                }
                if (nul.equals(priceVO.getSixthPrizeNo4())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo4();
                }
                if (nul.equals(priceVO.getSixthPrizeNo5())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo5();
                }
                if (nul.equals(priceVO.getSixthPrizeNo6())) {
                    messageO = "六獎";
                    messageT = "兌獎號碼 : " + priceVO.getSixthPrizeNo6();
                }
            }
            messageHMO.put(i, messageO);
            messageHMT.put(i, messageT);
            i++;
        }
        String year, month;
        int redF;
        if (messageHMO.get(0) != null) {
            //獎項
            BootstrapText text = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_STAR)
                    .addText(" " + messageHMO.get(0) + " ")
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
            return;
        }
        if (messageHMO.get(1) != null) {
            //獎項
            String old;
            month = oldPriceVO.getInvoYm().substring(oldPriceVO.getInvoYm().length() - 2);
            old = levelPrice.get(month) + messageHMO.get(1);
            BootstrapText text = new BootstrapText.Builder(context)
                    .addFontAwesomeIcon(FA_STAR_O)
                    .addText(" " + old + " ")
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
            awardTitle.setText(null);
            priceTitle.setText(null);
        }
    }


    private void setMonText(String action) {
        String showtime, searchtime, searcholdtime;
        if (month == 2) {
            showtime = year + "年1-2月";
            searchtime = year + "02";
            searcholdtime = (year - 1) + "12";
        } else if (month == 4) {
            showtime = year + "年3-4月";
            searchtime = year + "04";
            searcholdtime = year + "02";
        } else if (month == 6) {
            showtime = year + "年5-6月";
            searchtime = year + "06";
            searcholdtime = year + "04";
        } else if (month == 8) {
            showtime = year + "年7-8月";
            searchtime = year + "08";
            searcholdtime = year + "06";
        } else if (month == 10) {
            showtime = year + "年9-10月";
            searchtime = year + "10";
            searcholdtime = year + "08";
        } else {
            showtime = year + "年11-12月";
            searchtime = year + "12";
            searcholdtime = year + "10";
        }
        priceVO = priceDB.getPeriodAll(searchtime);
        oldPriceVO = priceDB.getPeriodAll(searcholdtime);
        priceVOS = new ArrayList<>();
        priceVOS.add(priceVO);
        priceVOS.add(oldPriceVO);
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
        ArrayList<String> SpinnerItem = new ArrayList<>();
        SpinnerItem.add("鍵盤");
        SpinnerItem.add("聲音");
        SpinnerItem.add("QRCode掃描");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinneritem, SpinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceModel.setAdapter(arrayAdapter);
        choiceModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    donateRL.setVisibility(View.VISIBLE);
                    showMi.setVisibility(View.GONE);
                    awardTitle.setText(null);
                    priceTitle.setText(null);
                    BootstrapText text = new BootstrapText.Builder(context)
                            .addText("請輸入末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                    if (speech != null) {
                        speech.stopListening();
                        speech.cancel();
                        speech.destroy();
                    }
                } else if (i == 1) {
                    awardTitle.setText(null);
                    priceTitle.setText(null);
                    BootstrapText text = new BootstrapText.Builder(context)
                            .addText("請念末三碼 ")
                            .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                            .build();
                    awardRemain.setText(text);
                    awardRemain.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                    showMi.setVisibility(View.VISIBLE);
                    donateRL.setVisibility(View.INVISIBLE);
                    int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
                    if (rc != PackageManager.PERMISSION_GRANTED) {
                        Common.askPermissions(Manifest.permission.RECORD_AUDIO, context);
                    }
                    startListening();

                } else if (i == 2) {
                    Common.showToast(context, "載入資料中");
                    MultiTrackerActivity.refresh = false;
                    Intent intent = new Intent(context, MultiTrackerActivity.class);
                    intent.putExtra("action", "PriceHand");
                    startActivityForResult(intent, 6);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
            inputNul.setText("");
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
            inputNul.setText("");
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
                                awardTitle.setText(null);
                                priceTitle.setText(null);
                                inputNul.setText(null);
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

                                //兌獎提醒
                                BootstrapText textRemain = new BootstrapText.Builder(context)
                                        .addText("請輸入末三碼 ")
                                        .addFontAwesomeIcon(FA_EXCLAMATION_CIRCLE)
                                        .build();
                                awardRemain.setText(textRemain);
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
