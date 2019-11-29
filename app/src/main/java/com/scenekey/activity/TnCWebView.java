package com.scenekey.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.WebServices;


public class TnCWebView extends BaseActivity {

    WebView webview;
    String webViewUrl="";
    TextView txt_f1_title;
    AppCompatImageView img_f1_back;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
        txt_f1_title = findViewById(R.id.txt_f1_title);
        img_f1_back = findViewById(R.id.img_f1_back);
        img_f1_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showProgDialog(false,"");
        webview =  findViewById(R.id.webview);

        webViewUrl = getIntent().getStringExtra("url");

        if(webViewUrl.equalsIgnoreCase(WebServices.TNC_WEBURL)){
            txt_f1_title.setText("Terms and Conditions");
        }
        else {
            txt_f1_title.setText("Privacy Policy");
        }

        webview.setWebViewClient(new WebViewClient());
        //startWebView(webViewUrl);

        boolean netStatus = isNetworkAvailable(TnCWebView.this);
        if(netStatus == true) {

            if(webViewUrl.equalsIgnoreCase(WebServices.TNC_WEBURL)){
                String url = Uri.encode(WebServices.TNC_WEBURL);
                String finalUrl = "http://docs.google.com/viewer?url=" + url + "&embedded=true";
                webview.loadUrl( finalUrl);        }
            else {
                String url = Uri.encode(WebServices.PRIVACY_POLICY_WEBURL);
                String finalUrl = "http://docs.google.com/viewer?url=" + url + "&embedded=true";
                webview.loadUrl( finalUrl);
            }
        }
        else{
            showAlertBoxForThrdparty("No Internet Connection Available");
        }

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);

//-------or recive error-------

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, final String failingUrl) {
                //control you layout, show something like a retry button, and
                //call view.loadUrl(failingUrl) to reload.

                if(errorCode == -8) {
                    boolean netStatus = isNetworkAvailable(TnCWebView.this);
                    if (netStatus == true) {
                        if(webViewUrl.equalsIgnoreCase(WebServices.TNC_WEBURL)){
                            String url = Uri.encode(WebServices.TNC_WEBURL);
                            String finalUrl = "http://docs.google.com/viewer?url=" + url + "&embedded=true";
                            webview.loadUrl( finalUrl);                        }
                        else {
                            String url = Uri.encode(WebServices.PRIVACY_POLICY_WEBURL);
                            String finalUrl = "http://docs.google.com/viewer?url=" + url + "&embedded=true";
                            webview.loadUrl( finalUrl);                        }
                    } else {
                        showAlertBoxForThrdparty("No Internet Connection Available");
                    }
                }
                else{
                    showAlertBoxForThrdparty("Something went wrong...");
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
               // pd.dismiss();
                dismissProgDialog();
            }
        });

    }


    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
            super.onBackPressed();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        double backId = 16908332;
        if (id == backId) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //-----dialog for error page------
    public void showAlertBoxForThrdparty(final String msg) {

        new AlertDialog.Builder(TnCWebView.this)
                .setTitle("Retry ?")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent getIntnt = getIntent();
                        // getIntnt.putExtra("NET_BANKING_URL",webViewUrl);
                        finish();
                        startActivity(getIntnt);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setCancelable(false)

        .show();
    }
}
