package com.example.fingerprinttest.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class
RegisterActivity extends AppCompatActivity {
    Tracker mTracker;
    ImageView imageUserRegister, nextBtn;
    Button takeOrChooseBtn;
    EditText nameText, ageText;
    String encoded;
    AlertDialog.Builder builder;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageUserRegister = (ImageView) findViewById(R.id.imageUserRegister);
        takeOrChooseBtn = (Button) findViewById(R.id.takeOrchooseBtn);
        nextBtn = (ImageView) findViewById(R.id.nextBtn);
        nameText = (EditText) findViewById(R.id.editTextName);
        ageText = (EditText) findViewById(R.id.editTextAge);


        String token;
        token = getIntent().getStringExtra("token");
        Toast.makeText(this, "token:" + token, Toast.LENGTH_SHORT).show();
        builder = new AlertDialog.Builder(this);
        //take or choose image function
        takeOrChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUserRegister.setTag(true);
                selectImage(RegisterActivity.this);
            }
        });

        //go to the next page
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (detectValid() == 1) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Detect")
                            .setAction("error")
                            .setLabel("ValidName")
                            .build());
                    nameText.setError("กรุณาใส่ชื่อ");
                    nameText.requestFocus();
                } else if (detectValid() == 2) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Detect")
                            .setAction("error")
                            .setLabel("ValidAge")
                            .build());
                    ageText.setError("กรุณาใส่อายุ");
                    ageText.requestFocus();
                } else if (detectValid() == 3) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Detect")
                            .setAction("error")
                            .setLabel("ValidImage")
                            .build());
                    SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("กรุณาใส่รูปภาพ");
                    loading.getProgressHelper().setBarColor(RegisterActivity.this.getResources().getColor(R.color.greentea));
                    loading.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                            Typeface face = ResourcesCompat.getFont(RegisterActivity.this, R.font.kanit_light);
                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                            TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                            textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            textCon.setTextColor(getResources().getColor(R.color.black));
                            textCon.setTypeface(face);
//                                              title
                            textCon.setGravity(Gravity.CENTER);
                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                            text.setTextColor(getResources().getColor(R.color.red25));
                            text.setTypeface(face);

                            text.setGravity(Gravity.CENTER);

                        }
                    });

                    loading.show();


                } else if (detectValid() == 4) {
                    ageText.setError("จำกัดอายุแค่ 1-100 ");
                    ageText.requestFocus();
                } else {
                    //sent data to post on API
                    String imageBase64 = encoded;
                    String name = nameText.getText().toString();
                    String age = ageText.getText().toString();
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                    intent.putExtra("nameUser", name);
                    intent.putExtra("ageUser", age);
                    intent.putExtra("imgUser", imageBase64);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
            }
        });
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RegisterActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }


    private int detectValid() {
        int min = 1;
        int max = 100;
        if (nameText.getText().toString().isEmpty()) {

            return 1;
        }
        if (ageText.getText().toString().isEmpty()) {
            return 2;
        }
        int getAgeInt = Integer.parseInt(ageText.getText().toString());
        if (getAgeInt < min || getAgeInt > max) {
            return 4;
        }
        if (imageUserRegister.getDrawable() == null) {

            return 3;
        }
        return 0;


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Uri photoURI;

    private void selectImage(Context context) {
        final CharSequence[] options = {"ถ่ายรูป", "เลือกจาก Gallery", "ยกเลิก"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("เลือกรูปภาพของคุณ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("ถ่ายรูป")) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Image")
                            .setAction("take")
                            .setLabel("takeImage")
                            .build());
//                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            photoURI = FileProvider.getUriForFile(RegisterActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 0);
                        }
                    }
                } else if (options[item].equals("เลือกจาก Gallery")) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Image")
                            .setAction("choose")
                            .setLabel("chooseImage")
                            .build());
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);

                } else if (options[item].equals("ยกเลิก")) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Image")
                            .setAction("cancel")
                            .setLabel("cancelImage")
                            .build());
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //NEW
    //GET IMAGE TO IMAGEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    Log.e("CHECKER", "OUTSIDE");



                    if (resultCode == RESULT_OK) {
                        Log.e("CHECKER", "ERRORRRRRRRRRRRRRRRRINSIDE");
                        galleryAddPic();
                        setPic();
//                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

//                        imageUserRegister.setImageBitmap();


                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri uri = data.getData();
                        try {
                            Bundle extras = data.getExtras();
                            //Bitmap imageBitmap = (Bitmap) extras.get("data");
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 40, byteArrayOutputStream);
//                            bitmap.createScaledBitmap(bitmap,120,140,true);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(0);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                            imageUserRegister.setImageBitmap(bitmap);

                            imageUserRegister.setRotation(90);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                    break;
            }
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageUserRegister.getWidth();
        int targetH = imageUserRegister.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.WEBP, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        imageUserRegister.setImageBitmap(rotatedBitmap);

    }
}