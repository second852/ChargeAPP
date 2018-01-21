package com.chargeapp.whc.chargeapp.Control;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.ChargeDB.CarrierDB;
import com.chargeapp.whc.chargeapp.Model.CarrierVO;
import com.chargeapp.whc.chargeapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1709008NB01 on 2018/1/5.
 */

public class EleAddBank extends Fragment {

    private WebView webView;
    protected ProgressBar myProgressBar;
    private TextView showError;
    private TextView enter;
    private Spinner carrier;
    private CarrierDB carrierDB;
    private List<CarrierVO> carrierVOS;
    private CarrierVO carrierVO;
    public String url;
    private SwipeRefreshLayout reSw;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_add_carrier, container, false);
        findViewById(view);
        setSpinner();
        myProgressBar.setVisibility(View.GONE);
        enter.setOnClickListener(new CliientListener());
        reSw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/carrierBankAccBlank?UUID=second&appID=EINV3201711184648&CardCode=3J0002&";
                url=url+"CardNo="+carrierVO.getCarNul()+"&VerifyCode="+carrierVO.getPassword();
                webViewSetting();
                showError.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void setSpinner() {
        carrierVOS=carrierDB.getAll();
        if(carrierVOS==null||carrierVOS.size()<=0)
        {
            showError.setVisibility(View.VISIBLE);
            showError.setText("目前沒有載具，請新增載具!");
        }
        ArrayList<String> carrierNul=new ArrayList<>();
        for (CarrierVO c:carrierVOS)
        {
            carrierNul.add(c.getCarNul());
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,carrierNul);
        arrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        carrier.setAdapter(arrayAdapter);
        carrier.setOnItemSelectedListener(new choiceStateItem());
    }


    private void webViewSetting() {
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);//设置缩放按钮
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                webView.loadUrl(EleAddBank.this.url);
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
                    Common.showToast(getActivity(),"正在連線!");
                }else {
                    myProgressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(getActivity(),"正在更新!");
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
                    Common.showLongToast(getActivity(),"連線失敗!請確認網路狀態!");
                    return;
                }
                if(seconderror)
                {
                    showError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showLongToast(getActivity(),"更新失敗!請確認網路狀態!");
                    return;
                }
                if(first)
                {
                    myProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    Common.showLongToast(getActivity(),"連線成功!");
                    first=false;
                }else{
                    myProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    Common.showLongToast(getActivity(),"更新成功!");
                }

            }
        });
        webView.loadUrl(url);
    }

    private void findViewById(View view) {
        carrierDB=new CarrierDB(MainActivity.chargeAPPDB.getReadableDatabase());
        webView = view.findViewById(R.id.webView);
        myProgressBar=view.findViewById(R.id.myProgressBar);
        showError=view.findViewById(R.id.showError);
        carrier=view.findViewById(R.id.carrier);
        enter=view.findViewById(R.id.enter);
        reSw=view.findViewById(R.id.reSw);
    }

    private class CliientListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/carrierBankAccBlank?UUID=second&appID=EINV3201711184648&CardCode=3J0002&";
            url=url+"CardNo="+carrierVO.getCarNul()+"&VerifyCode="+carrierVO.getPassword();
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
}

