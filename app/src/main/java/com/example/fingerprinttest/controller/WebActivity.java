package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.fingerprinttest.R;

public class WebActivity extends AppCompatActivity {

    String token ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        token = getIntent().getStringExtra("token");


//        WebView myWebView;
        WebView myWebView = new WebView(getApplicationContext());
//        myWebView = findViewById(R.id.myWeb);
        setContentView(myWebView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.setWebViewClient(new WebViewClient());
//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        // web test datePicker ja
//        myWebView.loadUrl("https://jqueryui.com/datepicker/");
        myWebView.loadUrl("https://ta.kisrateam.com/login?token="+token);
        System.out.println("token :"+token);



    }
}