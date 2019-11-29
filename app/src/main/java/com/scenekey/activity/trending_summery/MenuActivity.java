package com.scenekey.activity.trending_summery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.scenekey.R;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.model.Events;

public class MenuActivity extends BaseActivity implements View.OnClickListener {


    private WebView webView;
    private String pdf;
    private CustomProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        progressBar = new CustomProgressBar(this);
        setStatusBarColor();
        init();
    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public void init() {
        webView = findViewById(R.id.webview);
        ImageView img_back = findViewById(R.id.img_back);
        setClicks(img_back);
        getIntentData();

    }
    private void getIntentData() {

        pdf = getIntent().getStringExtra("pdf");
        showPdf();
    }

    private void setClicks(View... views) {
        for (View view : views) view.setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showPdf(){
//        pDialog.pdialog(TermActivity.this);
        progressBar.show();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
//                pDialog.hideDialog();
                progressBar.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }


}



