package com.chargeapp.whc.chargeapp.Control.EleInvoice;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.R;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_ID_CARD_O;

/**
 * Created by 1709008NB01 on 2018/1/5.
 */

public class EleAddBank extends Fragment {

    private WebView webView;
    protected ProgressBar myProgressBar;
    private TextView showError;
    private BootstrapButton enter;
    private BootstrapDropDown carrier;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private CarrierVO carrierVO;
    private Activity context;
    private DrawerLayout drawerLayout;
    private List<BootstrapText> bootstrapTexts;


    @Override
    public void onAttach(android.content.Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context= (Activity) context;
        }else{
            this.context=getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle(R.string.text_EleBank);
        TypefaceProvider.registerDefaultIconSets();
        final View view = inflater.inflate(R.layout.ele_add_carrier, container, false);
        findViewById(view);
        setSpinner();
        myProgressBar.setVisibility(View.GONE);
        drawerLayout = this.context.findViewById(R.id.drawer_layout);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }

    private void setSpinner() {
        carrierVOS=carrierDB.getAll();
        if(carrierVOS==null||carrierVOS.size()<=0)
        {
            String[] carrierNul =new String[1];
            BootstrapText text = new BootstrapText.Builder(context)
                    .addText("無載具 ")
                    .addFontAwesomeIcon(FA_ID_CARD_O)
                    .build();
            carrier.setBootstrapText(text);
            carrierNul[0]="無載具";
            carrier.setDropdownData(carrierNul);
            webView.setVisibility(View.GONE);
            showError.setVisibility(View.VISIBLE);
            showError.setText("請新增載具!");
            return;
        }
        String[] carrierNul =new String[carrierVOS.size()];
        bootstrapTexts=new ArrayList<>();
        for (int i=0;i<carrierVOS.size();i++) {
            carrierNul[i]=carrierVOS.get(i).getCarNul();
            bootstrapTexts.add(Common.setCarrierSetBsTest(context,carrierVOS.get(i).getCarNul()));
        }
        carrierVO=carrierVOS.get(0);
        carrier.setDropdownData(carrierNul);
        carrier.setBootstrapText(bootstrapTexts.get(0));
        carrier.setOnDropDownItemClickListener(new choiceStateItemBS());
//        carrier.setOnItemSelectedListener(new choiceStateItem());
        enter.setOnClickListener(new CliientListener());
    }


    private void webViewSetting() {
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);//设置缩放按钮
        webView.setInitialScale(300);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                webView.loadUrl(EleAddBank.this.url);
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            boolean showerror= false;
            boolean first=true;
            boolean seconderror=false;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(first)
                {
                    myProgressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(context,"正在連線!");
                }else {
                    myProgressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(context,"正在更新!");
                }

            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(first)
                {
                    showerror=true;
                }else {
                    showerror=false;
                    seconderror=true;
                }


            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished
                myProgressBar.setVisibility(View.GONE);
                if(showerror)
                {
                    showError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(context,"連線失敗!請確認網路狀態!");
                    return;
                }
                if(seconderror)
                {
                    showError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(context,"更新失敗!請確認網路狀態!");
                    return;
                }
                if(first)
                {
                    myProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    Common.showToast(context,"連線成功!");
                    first=false;
                }else{
                    myProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    Common.showToast(context,"更新成功!");
                }

            }
        });
        try {

           String url= "<!DOCTYPE html>" +
                    "<html>" +
                    "<body onload='document.frm1.submit()'>" +
                    "<form action='https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/carrierBankAccBlank?' method='post' name='frm1' enctype='application/x-www-form-urlencoded'>" +
                    "  <input type='hidden' name='uuid' value='second'><br>" +
                    "  <input type='hidden' name='appID' value='EINV3201711184648'><br>" +
                    "  <input type='hidden' name='cardCode' value='3J0002'><br>" +
                    "  <input type='hidden' name='cardNo' value='"+carrierVO.getCarNul()+"'><br>" +
                    "  <input type='hidden' name='verifyCode' value='"+carrierVO.getPassword()+"'><br>" +
                    "</form>" +
                    "</body>" +
                    "</html>";
            webView.loadData(url, "text/html", "UTF-8");
        }catch (Exception e)
        {
            Common.showToast(context,"手機載具有問題!");
            Log.d("XXXXXXX",e.getMessage());
        }
    }

    private void findViewById(View view) {
        Common.setChargeDB(context);
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        webView = view.findViewById(R.id.webView);
        myProgressBar=view.findViewById(R.id.myProgressBar);
        showError=view.findViewById(R.id.showError);
        carrier=view.findViewById(R.id.carrier);
        enter=view.findViewById(R.id.enter);
    }


    private class CliientListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            webViewSetting();
            showError.setVisibility(View.GONE);
        }
    }
    private class choiceStateItem implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            carrierVO=carrierVOS.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class choiceStateItemBS implements BootstrapDropDown.OnDropDownItemClickListener {
        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            carrierVO = carrierVOS.get(id);
            carrier.setBootstrapText(bootstrapTexts.get(id));
        }
    }
}

