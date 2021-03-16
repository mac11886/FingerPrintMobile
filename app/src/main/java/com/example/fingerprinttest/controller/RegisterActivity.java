package com.example.fingerprinttest.controller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.GroupData;
import com.example.fingerprinttest.model.GroupDatum;
import com.example.fingerprinttest.model.JobDatum;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.Api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    String textGroup, textJob, date;
    EditText edittext;
    int job_position_num;
    private Api api;
    List<GroupData> groupData;
    int id;
    String name;
    List<GroupData> groupData2;
    int num_group;
    final Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = null;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        try {
            getGroup();
        } catch (Exception e){
            Log.e("ERROR","getGroup is null");
        }

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
                try {
                    hideSoftKeyboard(RegisterActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (detectValid() == 1) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Detect")
                            .setAction("error")
                            .setLabel("ValidName")
                            .build());
//                    nameText.setError("กรุณาใส่ชื่อ");
//                    nameText.requestFocus();
                    loadAlert("กรุณาใส่ชื่อ");
                } else if (detectValid() == 3) {
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Detect")
                            .setAction("error")
                            .setLabel("ValidImage")
                            .build());
                    loadAlert("กรุณาใส่รูปภาพ");


                } else if (detectValid() == 2) {
                    loadAlert("กรุณาเลือกวันเกิด");
                } else if (detectValid() == 4) {
                    loadAlert("กรุณาเลือกแผนก");
                } else if (detectValid() == 5) {
                    loadAlert("กรุณาเลือกตำแหน่ง");
                } else {
                    //sent data to post on API
//                    num_group += 1;
                    String id_group = String.valueOf(num_group);

                    String imageBase64 = encoded;
                    String name = nameText.getText().toString();
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                    intent.putExtra("nameUser", name);
                    intent.putExtra("birthday", date);
                    intent.putExtra("group", id_group);
                    intent.putExtra("job", String.valueOf(job_position_num));
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

    public void createSpinnerRoot(List<GroupDatum> groupDatumList, List<JobDatum> jobDatumList) {

        List<String> groups = new ArrayList<>();

        for (GroupDatum group : groupDatumList) {
            groups.add(group.getName());
        }


        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerGroup,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data

                new HintAdapter<String>(this, "เลือกแผนก", groups),
                new HintSpinner.Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                        textGroup = itemAtPosition;
                        num_group = groupDatumList.get(position).getId();
                        Log.e("---", "----------------------------------------");
                        System.out.println("position" + num_group);
                        List<JobDatum> jobDatas = new ArrayList<>();

                        for (JobDatum jobDatum : jobDatumList) {
                            if (jobDatum.getIdGroup() == groupDatumList.get(position).getId()) {
                                jobDatas.add(jobDatum);
                            }
                        }

                        createJobSpinner(jobDatas);
                        try {
                            hideSoftKeyboard(RegisterActivity.this);
                        } catch (Exception e) {
                            Log.e("asd", "catch");
                        }


                    }
                });
        hintSpinner.init();
    }

    public void createJobSpinner(List<JobDatum> jobDatumList) {

        List<String> jobs = new ArrayList<>();

        for (JobDatum jobDatum : jobDatumList) {
            jobs.add(jobDatum.getName());
        }

        HintSpinner<String> hintSpinnerJob = new HintSpinner<>(
                spinnerJobPosition,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data

                new HintAdapter<String>(this, "เลือกตำแหน่ง", jobs),
                new HintSpinner.Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                        job_position_num = jobDatumList.get(position).getId();

                        Log.e("Job", "" + job_position_num);

                    }
                });

        hintSpinnerJob.init();
    }

    public void chooseDatePicker() {

        edittext = (EditText) findViewById(R.id.editTextAge);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
                    new DatePickerDialog(RegisterActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

//                    new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Material_Light_Dialog,date, myCalendar
//                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    hideSoftKeyboard(RegisterActivity.this);
                } else {

                }
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,date,myCalendar.get(Calendar.DAY_OF_MONTH), myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH)
//                        ).show();
                hideSoftKeyboard(RegisterActivity.this);
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
        if (job_position_num == 0) {
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
                            takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

                    if (resultCode == RESULT_OK) {
                        galleryAddPic();
                        try {
                            setPic();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
//                            imageUserRegister.setRotation(90);

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

    private void setPic() throws IOException {
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
//        imageUserRegister.setImageBitmap(bitmap);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


//        imageUserRegister.setImageDrawable(roundedBitmapDrawable);
//        imageUserRegister.setRotation(90);


        //---------------------------------------------------------------------------------------------------
        ExifInterface ei = new ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmapTest = null;

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmapTest = rotateImage(bitmap, 90);
                roundBitmap(rotatedBitmapTest);


                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmapTest = rotateImage(bitmap, 180);
                roundBitmap(rotatedBitmapTest);

                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmapTest = rotateImage(bitmap, 270);
                roundBitmap(rotatedBitmapTest);

                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmapTest = bitmap;
                roundBitmap(rotatedBitmapTest);

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void roundBitmap(Bitmap rotatedBitmapTest) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), rotatedBitmapTest);
        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        rotatedBitmapTest.compress(Bitmap.CompressFormat.WEBP, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        imageUserRegister.setImageDrawable(roundedBitmapDrawable);
    }


    public void getGroup() {
        Call<GroupData> call = api.getGroupApi();
        call.enqueue(new Callback<GroupData>() {
            @Override
            public void onResponse(Call<GroupData> call, Response<GroupData> response) {

                GroupData groupData = response.body();
//                Log.e("test" ,groupData.getGroupData().get(0).getName());
                try {
                createSpinnerRoot(groupData.getGroupData(), groupData.getJobData());
                }catch (Exception e){
                    Log.e("GETGROUP","getGroup is null");
                }
            }

            @Override
            public void onFailure(Call<GroupData> call, Throwable t) {

            }
        });
    }

    public void loadAlert(String content){
        SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
        loading.setTitleText("แจ้งเตือน");
        loading.setContentText(content);
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
    }

}