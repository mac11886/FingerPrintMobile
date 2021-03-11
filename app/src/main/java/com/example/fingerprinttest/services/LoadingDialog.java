package com.example.fingerprinttest.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.fingerprinttest.R;

public class LoadingDialog {


    Activity activity;
    AlertDialog dialog;

    public LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_dialog_view, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void  cancelDialog(){
        dialog.setCancelable(false);
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
