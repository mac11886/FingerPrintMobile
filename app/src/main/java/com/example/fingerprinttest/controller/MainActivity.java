package com.example.fingerprinttest.controller;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Attendance;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.google.android.material.snackbar.Snackbar;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity {


    private Tracker mTracker;
    private static final int VID = 6997;
    private static final int PID = 288;
    private TextView textView = null;
    private ImageView imageView = null;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 1;
    Button debug, inBtn, outbtn;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];
    List<User> users;
    String userImage;
    ImageView imageUser;
    TextView nameUser;
    String name;
    //    TextView textStatus;
    TextView dateUser;
    TextView timeUser;
    String status = "เข้า";
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private FingerprintSensor fingerprintSensor = null;
    private int i = -1;

    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        LogHelper.i("have permission!");
                    } else {
                        LogHelper.e("not permission!");
                    }
                }
            }
        }
    };


    public void createPostDate(int id, String date1, String time, String status) {
        Attendance attendance = new Attendance(id, " " + date1, " " + time, "" + status);
        Call<Attendance> call = jsonPlaceHolderApi.createPostDate(attendance);
        call.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                Attendance attendance2 = response.body();
                //textLog.setText(""+response.message());
            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
            }
        });
    }


    //get API
    public void getPosts() {
        Call<List<User>> call = jsonPlaceHolderApi.getPost();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                users = response.body();
                for (User user : users) {
                    String content = "";
                    content += "ID: " + user.getId() + "\n";
                    content += "Name: " + user.getName() + "\n";

                    userImage = user.getImguser();

//                    textStatus.setText("users"+users);
                }
//                textView.setText(""+response.toString());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                textView.setText("เกิดข้อผิดพลาดเกี่ยวกับ Internet");
                textView.setTextSize(15);
                SweetAlertDialog loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                loading.setTitleText("เกิดข้อผิดพลาดเกี่ยวกับ Internet");
                loading.setContentText("กรุณาเปิดแอพใหม่อีกครั้ง");
                loading.getProgressHelper().setBarColor(MainActivity.this.getResources().getColor(R.color.greentea));
                loading.setOnShowListener((DialogInterface.OnShowListener) dialog -> {
                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                    Typeface face = ResourcesCompat.getFont(MainActivity.this, R.font.kanit_light);
                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                    TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                    textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    textCon.setTextColor(getResources().getColor(R.color.black));
                    textCon.setTypeface(face);
                    textCon.setGravity(Gravity.CENTER);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    text.setTextColor(getResources().getColor(R.color.red25));
                    text.setTypeface(face);
                    text.setGravity(Gravity.CENTER);

                });

                loading.show();
            }
        });

    }

    public User getUser(int id) {
        return users.get(id);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.statusText);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageUser = (ImageView) findViewById(R.id.imageUser);

        nameUser = (TextView) findViewById(R.id.nameUser);
        dateUser = (TextView) findViewById(R.id.DateUser);
        timeUser = (TextView) findViewById(R.id.timeUser);

        ImageView adminBtn = (ImageView) findViewById(R.id.adminBtn);
        inBtn = (Button) findViewById(R.id.inBtn);
        outbtn = (Button) findViewById(R.id.outBtn);

        TextView main = (TextView) findViewById(R.id.item);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   testError();
               }catch (Exception exception){
                   Toast.makeText(MainActivity.this,"DONT DO THIS ",Toast.LENGTH_SHORT).show();
               }

            }
        });

        adminBtn.setOnClickListener(v -> {
            Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Intent")
                    .setAction("click")
                    .setLabel("LoginPage")
                    .build());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
//       SHOW DATE AND TIME
        dateUser.setText(new SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(new Date()));
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeUser.setText(new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);
        InitDevice();
        startFingerprintSensor();

        getPosts();
        if (!checkConfiguration()) {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.bad_config, Snackbar.LENGTH_INDEFINITE).show();
        }
//
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        outbtn.setOnClickListener(v -> {

            try {
                Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("CheckIn-Out")
                        .setAction("click")
                        .setLabel("CheckOut")
                        .build());
                OnBnBegin();
                status = "ออก";
                outbtn.setBackgroundColor(Color.parseColor("#207720"));
                inBtn.setBackgroundColor(Color.parseColor("#00AF91"));
            } catch (FingerprintException e) {
                e.printStackTrace();
            }
        });
        inBtn.setOnClickListener(v -> {

            try {
                Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("CheckIn-Out")
                        .setAction("click")
                        .setLabel("checkIn")
                        .build());

                OnBnBegin();
                inBtn.setBackgroundColor(Color.parseColor("#207720"));
                outbtn.setBackgroundColor(Color.parseColor("#00AF91"));
            } catch (FingerprintException e) {
                textView.setText("SERVER ERROR" + e.getMessage());
            }
            status = "เข้า";


        });

    }


    public void testError()  {
        throw  new EmptyStackException();
    }

    private boolean checkConfiguration() {
        XmlResourceParser parser = getResources().getXml(R.xml.global_tracker);

        boolean foundTag = false;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    String nameAttr = parser.getAttributeValue(null, "name");

                    foundTag = "string".equals(tagName) && "ga_trackingId".equals(nameAttr);
                }

                if (parser.getEventType() == XmlResourceParser.TEXT) {
                    if (foundTag && parser.getText().contains("REPLACE_ME")) {
                        return false;
                    }
                }

                parser.next();
            }
        } catch (Exception e) {
            Log.w("okok", "checkConfiguration", e);
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }


    private void startFingerprintSensor() {
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        fingerprintSensor = FingprintFactory.createFingerprintSensor(this, TransportType.USB, fingerprintParams);
    }


    public void saveBitmap(Bitmap bm) {
        File f = new File("/sdcard/fingerprint", "test.bmp");
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitDevice() {
        UsbManager musbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        Context context = this.getApplicationContext();
        context.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                if (!musbManager.hasPermission(device)) {
                    Intent intent = new Intent(ACTION_USB_PERMISSION);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    musbManager.requestPermission(device, pendingIntent);
                }
            }
        }
    }

    public void OnBnBegin() throws FingerprintException {

        try {

            if (bstart) return;
            fingerprintSensor.open(0);
            int i = 0;
            for (User user : users) {
                byte[] byte2 = Base64.decode(user.getFingerprint(), Base64.NO_WRAP);
                ZKFingerService.save(byte2, "" + i);
                i++;
            }
            final FingerprintCaptureListener listener = new FingerprintCaptureListener() {
                @Override
                public void captureOK(final byte[] fpImage) {
                    final int width = fingerprintSensor.getImageWidth();
                    final int height = fingerprintSensor.getImageHeight();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != fpImage) {
                                ToolUtils.outputHexString(fpImage);
                                LogHelper.i("width=" + width + "\nHeight=" + height);
                                Bitmap bitmapFp = ToolUtils.renderCroppedGreyScaleBitmap(fpImage, width, height);
                                //saveBitmap(bitmapFp);
                                //imageView.setImageBitmap(bitmapFp);
                            }
                            //textView.setText("FakeStatus:" + fingerprintSensor.getFakeStatus());
                        }
                    });
                }

                @Override
                public void captureError(FingerprintException e) {
                    final FingerprintException exp = e;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogHelper.d("captureError  errno=" + exp.getErrorCode() +
                                    ",Internal error code: " + exp.getInternalErrorCode() + ",message=" + exp.getMessage());
                        }
                    });
                }

                @Override
                public void extractError(final int err) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("extract fail, errorcode:" + err);
                        }
                    });
                }

                @Override
                public void extractOK(final byte[] fpTemplate) {

                    final byte[] tmpBuffer = fpTemplate;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isRegister) {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                                if (ret > 0) {
                                    String strRes[] = new String(bufids).split("\t");
                                    // finger has registry
                                    textView.setText("the finger already enroll by " + strRes[0] + ",cancel enroll");
                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }

                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
                                    //เมื่อลงทะเบียน นิ้ว 1 ครั้งละเปลี่ยนนิ้ว จะเข้า if นี้
                                    textView.setText("please press the same finger 3 times for the enrollment");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
                                    byte[] regTemp = new byte[2048];
                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {
                                        // save id
                                        ZKFingerService.save(regTemp, "test  " + uid++);
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);
                                        //Base64 Template
                                        // register success
                                        String strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        textView.setText("enroll succ, uid:" + uid + "count:" + ZKFingerService.count());
                                    } else {
                                        textView.setText("enroll fail");
                                    }
                                    isRegister = false;
                                } else {
                                    //เมื่อสแกนครั้งแรกเส้ดจะขึ้นให้แสกนอีกกี่ครั้ง
                                    textView.setText("You need to press the " + (3 - enrollidx) + "time fingerprint");
                                }
                            } else {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                                // register success เมื่อสแกนอีกรอบจะขึ้นอันนี้
                                if (ret > 0) {
                                    String strRes[] = new String(bufids).split("\t");
//                                    textView.setText("identify succ, userid:" + strRes[0] + ", score:" + strRes[1]);
                                    textView.setText("สแกนเสร็จสิ้น,ความชัด " + strRes[1] + " %");
                                    try {
                                        getPosts();
                                    } catch (Exception e) {
                                        textView.setText("server Error" + e.getMessage());
                                    }

                                    userImage = getUser(Integer.parseInt(strRes[0])).getImguser();
                                    name = getUser(Integer.parseInt(strRes[0])).getName();
                                    nameUser.setText("" + name);
                                    byte[] decodedString = Base64.decode(userImage, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                                    imageUser.setRotation(-90);
                                    imageUser.setImageBitmap(decodedByte);


                                    // DATE
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                                    String formatdate = simpleDateFormat.format(c.getTime());
                                    String formattime = simpleTimeFormat.format(c.getTime());
                                    dateUser.setText(formatdate);
                                    timeUser.setText(formattime);

                                    if (status == "เข้า") {
                                        SweetAlertDialog loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                        loading.setTitleText("ยินดีต้อนรับ");
                                        loading.setContentText("สวัสดีจ้า");

                                        loading.getProgressHelper().setBarColor(MainActivity.this.getResources().getColor(R.color.greentea));
                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(MainActivity.this, R.font.kanit_light);
                                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                                textCon.setTextColor(getResources().getColor(R.color.blueButton));
                                                textCon.setTypeface(face);
                                                textCon.setGravity(Gravity.CENTER);
                                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                                                text.setTextColor(getResources().getColor(R.color.blueButton));
                                                text.setTypeface(face);
                                                text.setGravity(Gravity.CENTER);

                                            }
                                        });

                                        loading.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                try {
                                                    outbtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                                    inBtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                                    OnBnStop();
                                                    loading.dismissWithAnimation();
                                                } catch (FingerprintException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        loading.show();
                                        loading.setCancelable(false);

                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("Scanner")
                                                .setAction("Scan")
                                                .setLabel("CheckIn")
                                                .build());
                                        createPostDate(users.get(Integer.parseInt(strRes[0])).getId(), formatdate, formattime, " " + status);
                                        try {
                                            outbtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                            inBtn.setBackgroundColor(Color.parseColor("#00AF91"));

                                            OnBnStop();
                                        } catch (FingerprintException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (status == "ออก") {
                                        SweetAlertDialog loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                        loading.setTitleText("บ้ายบายยย");
                                        loading.setContentText("กลับบ้านดีๆ");
                                        loading.getProgressHelper().setBarColor(MainActivity.this.getResources().getColor(R.color.greentea));

                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(MainActivity.this, R.font.kanit_light);
                                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                                textCon.setTextColor(getResources().getColor(R.color.blueButton));
                                                textCon.setTypeface(face);

                                                textCon.setGravity(Gravity.CENTER);
                                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                                                text.setTextColor(getResources().getColor(R.color.blueButton));
                                                text.setTypeface(face);

                                                text.setGravity(Gravity.CENTER);

                                            }
                                        });
                                        loading.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                try {

                                                    outbtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                                    inBtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                                    OnBnStop();
                                                    loading.dismissWithAnimation();
                                                } catch (FingerprintException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        loading.show();

                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("Scanner")
                                                .setAction("Scan")
                                                .setLabel("CheckOut")
                                                .build());
                                        createPostDate(users.get(Integer.parseInt(strRes[0])).getId(), formatdate, formattime, " " + status);
                                        try {
                                            outbtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                            inBtn.setBackgroundColor(Color.parseColor("#00AF91"));
                                            OnBnStop();
                                        } catch (FingerprintException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else {
                                    //ยังไม่เคยสแกนลายนิ้วมือ
                                    textView.setText("กรุณาสแกนใหม่อีกครั้ง");
                                }
                                //Base64 Template
                                //String strBase64 = Base64.encodeToString(tmpBuffer, 0, fingerprintSensor.getLastTempLen(), Base64.NO_WRAP);
                            }
                        }
                    });
                }


            };
            fingerprintSensor.setFingerprintCaptureListener(0, listener);
            fingerprintSensor.startCapture(0);
            bstart = true;
            textView.setText("สแกนนิ้วได้เลย");
        } catch (FingerprintException e) {
            //textView.setText("begin capture fail.errorcode:" + e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
            textView.setText("กรุณาเชื่อมต่อกับเครื่องสแกน");
        }
    }

    public void OnBnStop() throws FingerprintException {
        try {
            if (bstart) {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
//                textView.setText("ปิดเครื่องเสร็จสิ้น");
            } else {
                textView.setText("กรุณาเลือกสถานะเข้าหรือออก");
            }
        } catch (FingerprintException e) {
            textView.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }

    public void OnBnEnroll(View view) {
        if (bstart) {
            isRegister = true;
            enrollidx = 0;
            textView.setText("You need to press the 3 time fingerprint");
        } else {
            textView.setText("please begin capture first");
        }
    }

    public void OnBnVerify(View view) {
        if (bstart) {
            isRegister = false;
            enrollidx = 0;
        } else {
            textView.setText("please begin capture first");
        }
    }
}