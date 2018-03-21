package com.chargeapp.whc.chargeapp.Control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


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

/**
 * Created by 1709008NB01 on 2017/12/22.
 */

public class PriceHand extends Fragment {
    private ImageView PIdateAdd, PIdateCut;
    private TextView priceTitle, PIdateTittle, inputNul, showRemain;
    private RecyclerView donateRL;
    private PriceDB priceDB = new PriceDB(MainActivity.chargeAPPDB.getReadableDatabase());
    private Calendar now = Calendar.getInstance();
    private int month, year;
    private PriceVO priceVO, oldPriceVO;
    private String message = "";
    private List<PriceVO> priceVOS;
    private HashMap<String, String> levelPrice;
    private RelativeLayout showMi,modelR;
    private Spinner choiceModel;
    private CardView cardview;
    private static SpeechRecognizer speech = null;
    private static Intent recognizerIntent;
    private List<String> mResults;
    private boolean isRecognitionServiceAvailable = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.price_hand, container, false);
        findViewById(view);
        String period = priceDB.findMaxPeriod();
        if (period == null) {
            cardview.setVisibility(View.GONE);
            priceTitle.setVisibility(View.GONE);
            showRemain.setVisibility(View.VISIBLE);
            modelR.setVisibility(View.GONE);
            showRemain.setText("財政部網路忙線中~\n請稍後使用~");
            return view;
        }
        this.month = Integer.valueOf(period.substring(period.length() - 2));
        this.year = Integer.valueOf(period.substring(0, period.length() - 2));
        setMonText("in");
        PIdateAdd.setOnClickListener(new addMonth());
        PIdateCut.setOnClickListener(new cutMonth());
        donateRL.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        List<String> number = getInputN();
        donateRL.setAdapter(new InputAdapter(getActivity(), number));
        showMi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateRL.setVisibility(View.VISIBLE);
                showMi.setVisibility(View.GONE);
                speech.stopListening();
                speech.cancel();
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
        String message = null;
        HashMap<Integer, String> allMessage = new HashMap<>();
        int i = 0;
        for (PriceVO priceVO : priceVOS) {
            String nul = gnul;
            message = null;
            if (priceVO != null) {
                if (nul.equals(priceVO.getSuperPrizeNo().substring(5))) {
                    message = "特別獎?" + priceVO.getSuperPrizeNo() + "\n獎金一千萬";
                }
                if (nul.equals(priceVO.getSpcPrizeNo().substring(5))) {
                    message = "特獎?" + priceVO.getSpcPrizeNo() + "\n獎金兩百萬";
                }
                if (nul.equals(priceVO.getFirstPrizeNo1().substring(5))) {
                    message = "頭獎?" + priceVO.getFirstPrizeNo1() + "\n獎金20萬";
                }
                if (nul.equals(priceVO.getFirstPrizeNo2().substring(5))) {
                    message = "頭獎?" + priceVO.getFirstPrizeNo2() + "\n獎金20萬";
                    ;
                }
                if (nul.equals(priceVO.getFirstPrizeNo3().substring(5))) {
                    message = "頭獎?" + priceVO.getFirstPrizeNo3() + "\n獎金20萬";
                }
                if (nul.equals(priceVO.getSixthPrizeNo1())) {
                    message = "六獎" + priceVO.getSixthPrizeNo1() + "\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo2())) {
                    message = "六獎" + priceVO.getSixthPrizeNo2() + "\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo3())) {
                    message = "六獎" + priceVO.getSixthPrizeNo3() + "\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo4())) {
                    message = "六獎" + priceVO.getSixthPrizeNo4() + "\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo5())) {
                    message = "六獎" + priceVO.getSixthPrizeNo5() + "\n獎金200";
                }
                if (nul.equals(priceVO.getSixthPrizeNo6())) {
                    message = "六獎" + priceVO.getSixthPrizeNo6() + "\n獎金200";
                }
            }
            allMessage.put(i, message);
            i++;
        }

        String totalmessage = null;
        int redF = 0, redE = 0, printF = 0, printE = 0;
        String year, month;
        if (allMessage.get(0) != null) {
            totalmessage = allMessage.get(0);
            redF = totalmessage.lastIndexOf("獎");
            redE = redF - 4;
        }
        if (allMessage.get(1) != null) {
            int length = (totalmessage == null ? 0 : totalmessage.length());
            totalmessage = (totalmessage == null ? "" : totalmessage);
            String old;
            year = oldPriceVO.getInvoYm().substring(0, oldPriceVO.getInvoYm().length() - 2);
            month = oldPriceVO.getInvoYm().substring(oldPriceVO.getInvoYm().length() - 2);
            old = "上一期" + year + "年" + levelPrice.get(month) + allMessage.get(1);
            printE = old.lastIndexOf("獎") + length;
            printF = printE - 4;
            totalmessage = totalmessage + old;
        }
        if (totalmessage != null) {
            Spannable content = new SpannableString(totalmessage);
            content.setSpan(new ForegroundColorSpan(Color.RED), redE, redF, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new ForegroundColorSpan(Color.MAGENTA), printF, printE, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTitle.setText(content);
            return;
        } else {
            priceTitle.setText("沒有中獎!再接再厲!");
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
            Common.showToast(getActivity(), showtime + "尚未開獎");
            return;
        }
        if (priceVO == null && action.equals("cut")) {
            month = month + 2;
            if (month > 12) {
                month = 2;
                year = year + 1;
            }
            Common.showToast(getActivity(), "沒有資料");
            return;
        }
        PIdateTittle.setText(showtime);
    }


    private void findViewById(View view) {
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
        cardview = view.findViewById(R.id.cardview);
        choiceModel=view.findViewById(R.id.choiceModel);
        modelR=view.findViewById(R.id.modelR);
        ArrayList<String> SpinnerItem = new ArrayList<>();
        SpinnerItem.add("鍵盤");
        SpinnerItem.add("聲音");
        SpinnerItem.add("QRCode掃描");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, SpinnerItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        choiceModel.setAdapter(arrayAdapter);
        choiceModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    donateRL.setVisibility(View.VISIBLE);
                    showMi.setVisibility(View.GONE);
                    speech.stopListening();
                    speech.cancel();
                }else if(i==1)
                {
                    priceTitle.setText("請念後三碼");
                    showMi.setVisibility(View.VISIBLE);
                    donateRL.setVisibility(View.GONE);
                    startListening();
                }else if(i==2)
                {
                    MultiTrackerActivity.refresh = false;
                    Intent intent = new Intent(getActivity(), MultiTrackerActivity.class);
                    startActivity(intent);
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
            TextView day;
            CardView cardview;


            MyViewHolder(View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.QrCodeA);
                cardview = itemView.findViewById(R.id.cardview);
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
        public void onBindViewHolder(PriceHand.InputAdapter.MyViewHolder viewHolder, int position) {
            final String number = numberList.get(position);
            viewHolder.day.setText(number);
            viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString content;
                    if (number.equals("Del")) {

                        if (message.length() > 2) {
                            message = message.substring(0, message.length() - 1);
                            content = new SpannableString(message);
                            content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                            inputNul.setText(content);
                            return;
                        } else if (message.length() > 1) {
                            message = message.substring(0, 1);
                            content = new SpannableString(message);
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            inputNul.setText(content);
                            return;
                        } else {
                            message = "";
                        }
                        inputNul.setText(message);
                        return;
                    }
                    if (number.equals("C")) {
                        message = "";
                        inputNul.setText(message);
                        return;
                    }
                    message = message + number;
                    if (message.length() == 3) {
                        autoSetInWin(message);
                    }
                    if (message.length() > 3) {
                        message = number;
                        priceTitle.setText("請輸入末三碼");
                    }
                    content = new SpannableString(message);
                    content.setSpan(new UnderlineSpan(), message.length() - 1, content.length(), 0);
                    inputNul.setText(content);
                }
            });
        }
    }

    public void startListening() {
        if (SpeechRecognizer.isRecognitionAvailable(getActivity())) {
            if (isRecognitionServiceAvailable) {//(speech!=null){
                speech.startListening(recognizerIntent);
                mResults = new ArrayList<String>();
            } else
                startSR();
        }
    }

    public void startSR() {
        isRecognitionServiceAvailable = true;
        speech = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speech.setRecognitionListener(new listener());
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getActivity().getPackageName());
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
            int matchNul = 0,y = 0;
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
