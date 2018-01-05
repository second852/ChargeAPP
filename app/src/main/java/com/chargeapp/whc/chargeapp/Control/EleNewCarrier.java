package com.chargeapp.whc.chargeapp.Control;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.chargeapp.whc.chargeapp.R;


/**
 * Created by 1709008NB01 on 2018/1/5.
 */

public class EleNewCarrier extends Fragment {

    private WebView webView;
    protected ProgressBar myProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_newcarrier, container, false);
        webView = view.findViewById(R.id.webView);
        myProgressBar=view.findViewById(R.id.myProgressBar);

        final String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/generalCarrierRegBlank?UUID=second&appID=EINV3201711184648";
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);//设置缩放按钮
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                // Do something on page loading started
                // Visible the progressbar
                myProgressBar.setVisibility(View.VISIBLE);
                Common.showLongToast(getActivity(),"正在連線");
            }

            @Override
            public void onPageFinished(WebView view, String url){
                // Do something when page loading finished
                myProgressBar.setVisibility(View.GONE);
            }
        });




        webView.loadUrl(url);
        return view;
    }
}

