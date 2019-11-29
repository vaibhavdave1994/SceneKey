package com.scenekey.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.helper.WebServices;


public class TnCAndPrivacyActivity extends AppCompatActivity {
    WebView webView;
    String baseWebViewUrl = "<iframe src='http://docs.google.com/gview?embedded=true&url=";
    String screenType = "";
    String webViewUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);

        webView =  findViewById(R.id.webview);
        ImageView iv_back = findViewById(R.id.img_f1_back);
        TextView action_bar = findViewById(R.id.action_bar);


        webViewUrl = getIntent().getStringExtra("url");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });


        if(webViewUrl.equalsIgnoreCase(WebServices.TNC_WEBURL)){
            webView.loadData( baseWebViewUrl+WebServices.TNC_WEBURL+"' width='100%' height='100%' style='border: none;'></iframe>" , "text/html",  "UTF-8");
        }
        else {
            webView.loadData( baseWebViewUrl+WebServices.PRIVACY_POLICY_WEBURL+"' width='100%' height='100%' style='border: none;'></iframe>" , "text/html",  "UTF-8");
        }


    }



}
