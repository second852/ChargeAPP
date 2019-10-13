package com.chargeapp.whc.chargeapp.Control.EleInvoice;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.R;

import static android.content.Context.WINDOW_SERVICE;


/**
 * Created by 1709008NB01 on 2018/1/5.
 */

public class EleNewCarrier extends Fragment {

    private WebView webView;
    protected ProgressBar myProgressBar;
    private TextView showError;
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
        if(activity.getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
        activity.setTitle(R.string.text_NewCarrier);
        final View view = inflater.inflate(R.layout.ele_newcarrier, container, false);
        webView = view.findViewById(R.id.webView);
        myProgressBar = view.findViewById(R.id.myProgressBar);
        showError = view.findViewById(R.id.showError);
        drawerLayout = activity.findViewById(R.id.drawer_layout);
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                drawerLayout.closeDrawer(GravityCompat.START);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        webViewSetting();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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


        try {

            String html = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body onload='document.frm1.submit()'>" +
                    "<form action='https://api.einvoice.nat.gov.tw/PB2CAPIVAN/APIService/generalCarrierRegBlank' method='post' name='frm1' enctype='application/x-www-form-urlencoded'>" +
                    "  <input type='hidden' name='uuid' value='second'><br>" +
                    "  <input type='hidden' name='appID' value='EINV3201711184648'><br>" +
                    "</form>" +
                    "</body>" +
                    "</html>";
            webView.loadData(html, "text/html", "UTF-8");
        }catch (Exception e)
        {
            Common.showToast(activity,"資料有誤");
            Log.d("XXXXXXX",e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adjustFontScale(getResources().getConfiguration());
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1) {
            configuration.fontScale = (float) 1;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }
}

