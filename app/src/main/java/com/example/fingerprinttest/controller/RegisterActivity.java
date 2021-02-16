package com.example.fingerprinttest.controller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class
RegisterActivity extends AppCompatActivity {
    Tracker mTracker;
    ImageView imageUserRegister, nextBtn;
    Button takeOrChooseBtn;
    EditText nameText, ageText;
    String encoded;
    AlertDialog.Builder builder;
    String currentPhotoPath;
    Spinner spinnerGroup, spinnerJobPosition;
    String textGroup, textJob,date;
    EditText edittext;

    String[] dc_one = {"DC ONE"};
    String[] developer = {"Technical Leader", "Developer", "Supervisor Quality Assuarance", "Software Tester"};
    String[] accounting = {"accounting", "Customer Relationship"};
    String[] se = {"Software Engineering"};
    String[] coordinat = {"Project coordinat"};
    String[] deploy = {"Manager", "Barista"};
    String[] admin = {"Administrator", "IT Administrator"};
    String[] secretary = {"Secretary"};
    String[] boss = {"CEO"};
    String[] acc = {"CTO"};
    String[] ba = {"Business Analyst"};
    String[] graphic = {"Graphic Design"};
    String[] nj = {"NJ"};
    int num_group ;
    final Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = null;
    String[] job_position = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageUserRegister = (ImageView) findViewById(R.id.imageUserRegister);
        takeOrChooseBtn = (Button) findViewById(R.id.takeOrchooseBtn);
        nextBtn = (ImageView) findViewById(R.id.nextBtn);
        nameText = (EditText) findViewById(R.id.editTextName);
//        ageText = (EditText) findViewById(R.id.editTextAge);
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        spinnerJobPosition = findViewById(R.id.spinnerJobPosition);
        String token;
        token = getIntent().getStringExtra("token");
        builder = new AlertDialog.Builder(this);
        String[] group = {"Development", "Engineering", "Graphic Design", "Coordinat", "Deploy Space café", "Administrative","Accounting", "Secretary"
                , "Business Analyst", "CEO","CTO" ,"DC ONE","NJ"};

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerGroup,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter<String>(this, "เลือกตำแหน่ง", Arrays.asList(group)),
                new HintSpinner.Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                        textGroup = itemAtPosition;
                         num_group  = position;
                        Log.e("---","----------------------------------------");
                        System.out.println("position"+num_group);
                        switch (num_group) {
                            case 0:
                                job_position = developer;
                                break;

                            case 1:
                                job_position = se;
                                break;
                            case 2:
                                job_position = graphic;
                                break;
                            case 3:
                                job_position = coordinat;
                                break;
                            case 4:
                                job_position = deploy;
                                break;
                            case 5:
                                job_position = admin;
                                break;
                            case 6:
                                job_position = accounting;
                                break;
                            case 7:
                                job_position = secretary;
                                break;
                            case 8:
                                job_position = ba;
                                break;
                            case  9:
                                job_position =boss;
                                break;

                            case  10:
                                job_position =acc;
                                break;
                            case  11:
                                job_position =dc_one;
                                break;
                            case  12:
                                job_position =nj;
                                break;
                        }
                        createJobSpinner(job_position);
                    }
                });
        hintSpinner.init();
        createJobSpinner(job_position);

        chooseDatePicker();
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
            @RequiresApi(api = Build.VERSION_CODES.N)
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


                } else if (detectValid() == 2) {
                    SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("กรุณาเลือกวันเกิด");
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
                    SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("กรุณาเลือกแผนก");
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
                } else if (detectValid() == 5) {
                    SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("กรุณาเลือกตำแหน่ง");
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
                } else {
                    //sent data to post on API
                    num_group +=1;
                    String id_group = String.valueOf(num_group);

                    String imageBase64 = encoded;
                    String name = nameText.getText().toString();
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                    intent.putExtra("nameUser", name);
                    intent.putExtra("birthday", date);
                    intent.putExtra("group", id_group);
                    intent.putExtra("job", textJob);
                    intent.putExtra("imgUser", imageBase64);
                    intent.putExtra("token", token);
                    startActivity(intent);
//                    Toast.makeText(RegisterActivity.this,"date:"+date,Toast.LENGTH_SHORT).show();
                }
            }
        });
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RegisterActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }


    public void createJobSpinner(String[] job_position) {
        HintSpinner<String> hintSpinnerJob = new HintSpinner<>(
                spinnerJobPosition,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data

                new HintAdapter<String>(this, "เลือกตำแหน่ง", Arrays.asList(job_position)),
                new HintSpinner.Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                        textJob = job_position[position];

                    }
                });

        hintSpinnerJob.init();
    }


    public void chooseDatePicker() {

        edittext = (EditText) findViewById(R.id.editTextAge);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat, Locale.US);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         edittext.setText(sdf.format(myCalendar.getTime()));
            date = sdf.format(myCalendar.getTime());
        }
    }

    private int detectValid() {
        int min = 1;
        int max = 100;
        if (nameText.getText().toString().isEmpty()) {

            return 1;
        }
        if (sdf == null) {
            return 2;
        }
        if (imageUserRegister.getDrawable() == null) {
            return 3;
        }
        if (textGroup == null) {
            return 4;
        }
        if (textJob == null) {
            return 5;
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
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), rotatedBitmap);
                            roundedBitmapDrawable.setCornerRadius(50.0f);
                            roundedBitmapDrawable.setAntiAlias(true);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            imageUserRegister.setImageDrawable(roundedBitmapDrawable);
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

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), rotatedBitmap);
        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.WEBP, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        imageUserRegister.setImageBitmap(rotatedBitmap);
        imageUserRegister.setImageDrawable(roundedBitmapDrawable);

    }
}