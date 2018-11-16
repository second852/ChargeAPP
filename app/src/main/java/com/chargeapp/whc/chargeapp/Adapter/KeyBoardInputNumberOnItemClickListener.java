package com.chargeapp.whc.chargeapp.Adapter;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chargeapp.whc.chargeapp.Control.Common;

import static com.chargeapp.whc.chargeapp.Control.Common.doubleRemoveZero;
import static com.chargeapp.whc.chargeapp.Control.Common.onlyNumberToDouble;

public class KeyBoardInputNumberOnItemClickListener implements AdapterView.OnItemClickListener{

    private StringBuilder showSb;
    private boolean clearToZero, needInit,firstCalculate;
    private double oldNumber;
    private BootstrapButton  calculate;
    private EditText money;
    private Activity context;
    private GridView numberKeyBoard;


    public KeyBoardInputNumberOnItemClickListener(BootstrapButton calculate, EditText money, Activity context, GridView numberKeyBoard,
                                                  StringBuilder showSb,Boolean clearToZero) {
        this.calculate = calculate;
        this.money = money;
        this.context = context;
        this.numberKeyBoard = numberKeyBoard;
        needInit=false;
        this.clearToZero = clearToZero;
        firstCalculate=true;
        this.showSb=showSb;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String word = Common.keyboardArray[i];
        String symbol = calculate.getText().toString().trim();
        if (symbol == null || symbol.isEmpty()&&!word.equals("倒退")&&!word.equals("確定")&&!word.equals("返回")&&!word.equals("歸零")) {
            symbol = new String(word);
        }
        if (clearToZero) {
            clearToZero = false;
            if(word.equals("+")||word.equals("-")||word.equals("x")||word.equals("÷"))
            {
                clearToZero=true;
                Common.showToast(context,"沒有數字無法計算");
                return;
            }
            if(word.equals("倒退")||word.equals("歸零")||word.equals("確定")||word.equals("返回")||word.equals("="))
            {
                clearToZero=true;
                return;
            }
            if(word.equals("."))
            {
                showSb.append("0");
            }
            showSb.append(word);
            money.setText(showSb.toString());
            money.setSelection(showSb.length());
            return;
        }
        switch (word) {
            case "倒退":
                if(needInit){
                    Common.showToast(context,"計算中的數值，不能倒退");
                    break;
                }
                if(showSb.length()<=1)
                {
                    showSb=new StringBuilder();
                    showSb.append("0");
                }else {
                    showSb.delete(showSb.length()-1,showSb.length());
                }
                money.setText(showSb.toString());
                money.setSelection(showSb.length());
                break;
            case "歸零":
                clearToZero=false;
                firstCalculate=true;
                needInit=true;
                oldNumber=0.0;
                showSb = new StringBuilder();
                showSb.append("0");
                money.setText(showSb.toString());
                money.setSelection(showSb.length());
                calculate.setText(null);
                break;
            case "確定":
                numberKeyBoard.setVisibility(View.GONE);
                calculate.setText(null);
                oldNumber = 0.0;
                needInit=false;
                firstCalculate=true;
                money.setFocusableInTouchMode(true);
                money.setSelection(showSb.length());
                break;
            case "返回":
                numberKeyBoard.setVisibility(View.GONE);
                calculate.setText(null);
                oldNumber = 0.0;
                needInit=false;
                firstCalculate=true;
                break;
            case "x":
                if (needInit) {
                    calculate.setText("x");
                    break;
                }
                if(firstCalculate)
                {
                    firstCalculate=false;
                    calculate.setText("x");
                    needInit = true;
                    break;
                }
                needInit = true;
                resultCalculate(symbol);
                calculate.setText("x");
                oldNumber = 0.0;
                break;
            case "÷":
                if (needInit) {
                    calculate.setText("÷");
                    break;
                }
                if(firstCalculate)
                {
                    firstCalculate=false;
                    calculate.setText("÷");
                    needInit = true;
                    break;
                }
                needInit = true;
                resultCalculate(symbol);
                calculate.setText("÷");
                oldNumber = 0.0;
                break;
            case "+":
                if (needInit) {
                    calculate.setText("+");
                    break;
                }
                if(firstCalculate)
                {
                    oldNumber=0;
                    firstCalculate=false;
                    needInit = true;
                }
                resultCalculate(symbol);
                needInit = true;
                calculate.setText("+");
                oldNumber = 0.0;
                break;
            case "-":
                if (needInit) {
                    calculate.setText("-");
                    break;
                }
                if(firstCalculate)
                {
                    oldNumber=0;
                    firstCalculate=false;
                    needInit = true;
                }
                resultCalculate(symbol);
                needInit = true;
                calculate.setText("-");
                oldNumber = 0.0;
                break;
            case ".":
                if(needInit){
                    Common.showToast(context,"計算中的數值，不能使用小數點");
                    break;
                }
                if(showSb.indexOf(".")!=-1)
                {
                    Common.showToast(context,"不能加小數點");
                    break;
                }
                if(showSb==null||showSb.length()<=0)
                {
                    showSb=new StringBuilder();
                    showSb.append("0.");
                    money.setSelection(showSb.length());
                    break;
                }
                showSb.append(word);
                money.setText(showSb.toString());
                money.setSelection(showSb.length());
                break;
            case "=":
                //no symbol, no active
                if (symbol == null || symbol.isEmpty()||symbol.equals("=")) {
                    break;
                } else {
                    //calculate
                    if(!needInit)
                    {
                        resultCalculate(symbol);
                    }
                    calculate.setText(null);
                    oldNumber = 0.0;
                    needInit = false;
                    firstCalculate=true;
                }
                break;
            default:
                //no calculate
                if (!needInit) {
                    //If clear to zero,No append
                    showSb.append(word);
                } else {
                    //calculate
                    oldNumber = onlyNumberToDouble(showSb.toString());
                    needInit = false;
                    showSb = new StringBuilder();
                    showSb.append(word);
                }
                money.setText(showSb.toString());
                money.setSelection(showSb.length());
                break;
        }
    }


    private Double resultCalculate(String symbol) {
        double answer = 0.0;
        double nowNumber=onlyNumberToDouble(showSb.toString());
        switch (symbol) {
            case "x":
                answer = oldNumber * nowNumber;
                break;
            case "÷":
                if(Double.valueOf(showSb.toString().trim())==0.0)
                {
                    answer=oldNumber;
                    Common.showToast(context,"除數不能為零");
                    break;
                }
                answer = oldNumber / nowNumber;
                break;
            case "+":
                answer = oldNumber + nowNumber;
                break;
            case "-":
                answer = oldNumber - nowNumber;
                break;
        }
        //init
        showSb = new StringBuilder();
        showSb.append(doubleRemoveZero(answer));
        money.setText(showSb.toString());
        money.setSelection(showSb.length());
        //clear
        return answer;
    }
}
