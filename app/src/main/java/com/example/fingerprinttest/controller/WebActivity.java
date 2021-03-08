package com.example.fingerprinttest.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

public class WebActivity extends AppCompatActivity {
    Tracker mTracker;
    String token;
    WebView myWebView;
    private Uri[] results;
    private FilePickerDialog dialog;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> afterLolipop;
    private ValueCallback<Uri[]> mUploadMessage;
    private String LOG_TAG = "DREG";
    //check webeee

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        googleAnalytic();
        token = getIntent().getStringExtra("token");

//        myWebView.setWebViewClient(new WebViewClient());
//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        WebView myWebView;
//        myWebView.loadUrl("https://timepicker.co/");
        myWebView = findViewById(R.id.myWebView);
        myWebView = new WebView(getApplicationContext());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
//        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.setWebViewClient(new PQClient());
        myWebView.setWebChromeClient(new PQChromeClient());
        myWebView.loadUrl("https://ta.kisrateam.com/login?token=" + token);
        setContentView(myWebView);
        System.out.println("token :" + token);


    }


    private  void openFileSelectionDialog(){
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }

        //Create a DialogProperties object.
        final DialogProperties properties = new DialogProperties();

        //Instantiate FilePickerDialog with Context and DialogProperties.
        dialog = new FilePickerDialog(WebActivity.this, properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("Select");
        dialog.setNegativeBtnName("Cancel");
        properties.selection_mode = DialogConfigs.MULTI_MODE; // for multiple files
//        properties.selection_mode = DialogConfigs.SINGLE_MODE; // for single file
        properties.selection_type = DialogConfigs.FILE_SELECT;

        //Method handle selected files.
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                results = new Uri[files.length];
                for (int i = 0; i < files.length; i++) {
                    String filePath = new File(files[i]).getAbsolutePath();
                    if (!filePath.startsWith("file://scdcard/DCIM/camera")) {
                        filePath = "file://" + filePath;
                    }
                    results[i] = Uri.parse(filePath);
                    Log.e(LOG_TAG, "file path: " + filePath);
                    Log.e(LOG_TAG, "file uri: " + String.valueOf(results[i]));
                }
                mUploadMessage.onReceiveValue(results);
                mUploadMessage = null;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (null != mUploadMessage) {
                    if (null != results && results.length >= 1) {
                        mUploadMessage.onReceiveValue(results);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mUploadMessage = null;
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (null != mUploadMessage) {
                    if (null != results && results.length >= 1) {
                        mUploadMessage.onReceiveValue(results);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mUploadMessage = null;
            }
        });

        dialog.show();
    }
    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {
                        openFileSelectionDialog();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(WebActivity.this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class PQChromeClient extends WebChromeClient {

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePathCallback;

            openFileSelectionDialog();

            return true;
        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)

        return super.onKeyDown(keyCode, event);
    }
    public class PQClient extends WebViewClient {
        ProgressBar progressDialog;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {

                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                // Here we can open new activity

                return true;

            } else {
                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        // Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
//             in standard case YourActivity.this
            if (progressDialog == null) {
//                progressDialog = findViewById(R.id.progressBar);
//                progressDialog.setVisibility(View.VISIBLE);
            }
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            myWebView.loadUrl("javascript:(function(){ " +
                    "document.getElementById('android-app').style.display='none';})()");

            try {
                // Close progressDialog
                progressDialog.setVisibility(View.GONE);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
        public void googleAnalytic() {
        //GG
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("WebActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}





