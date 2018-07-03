package com.chargeapp.whc.chargeapp.Control;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.R;


/**
 * Created by 1709008NB01 on 2018/1/5.
 */

public class EleNewCarrier extends Fragment {

    private WebView webView;
    protected ProgressBar myProgressBar;
    private TextView showError;
    private String url;
    private Activity activity;
    private DrawerLayout drawerLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity= (Activity) context;
        }else {
            activity=getActivity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(activity.getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ele_newcarrier, container, false);
        webView = view.findViewById(R.id.webView);
        myProgressBar = view.findViewById(R.id.myProgressBar);
        showError = view.findViewById(R.id.showError);
        url="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/generalCarrierRegBlank?UUID=second&appID=EINV3201711184648";
        webViewSetting();
        drawerLayout = activity.findViewById(R.id.drawer_layout);

        if(activity.getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void webViewSetting() {
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);//设置缩放按钮
        webView.setInitialScale(400);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                if(message.indexOf("驗證碼")!=-1)
//                {
//                    Common.showToast(getActivity(),"請到信箱收信和開手機收簡訊!");
//                    webViewSetting();
//                }
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            boolean showerror= false;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    drawerLayout.closeDrawer(GravityCompat.START);
                    myProgressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(getActivity(),"正在連線!");
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                    showerror=true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished
                myProgressBar.setVisibility(View.GONE);
                if(showerror)
                {
                    showError.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Common.showToast(getActivity(),"連線失敗!請確認網路狀態!");
                    return;
                }
                    myProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    Common.showToast(getActivity(),"連線成功!");
            }
        });
        webView.loadUrl(url);
    }
}

