package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.EditFingerprint;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.Api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EditFingerprintActivity extends AppCompatActivity {
    Tracker mTracker;
    ImageView firstImage, secondImage, thridImage, scanbtn, userimg;
    TextView scanText, nametext, fingerText;
    String token;
    List<User> users;
    public static final int COLOR_PALEGOLDENROD = 0xff000000;
    String name;
    String imgUser;
    String birthday;
    String group, job;
    String user[];
    AlertDialog.Builder builder;
    private static final int VID = 6997;
    private static final int PID = 288;
    int dataFinger;
    String strBase64;
    String Secondfinger;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 0;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];

    private FingerprintSensor fingerprintSensor = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";
    private Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fingerprint);

        builder = new AlertDialog.Builder(this);
        firstImage = (ImageView) findViewById(R.id.firstImage);
        secondImage = (ImageView) findViewById(R.id.secondImage);
        thridImage = (ImageView) findViewById(R.id.thridImage);
        scanbtn = (ImageView) findViewById(R.id.scanbtn);
        scanText = findViewById(R.id.textView4);

        userimg = findViewById(R.id.imageUser);
        nametext = findViewById(R.id.name);
        fingerText = findViewById(R.id.finger);

//        String img = getIntent().getStringExtra("users");
//        String name = getIntent().getStringExtra("name");
//        String finger = getIntent().getStringExtra("finger");

//        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);



        Bundle bundle = this.getIntent().getExtras();
        user = bundle.getStringArray("user");
//        Log.e("P'best","P:" + user[0]);
        byte[] decodedString = Base64.decode(user[1], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodedByte);
        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        userimg.setImageDrawable(roundedBitmapDrawable);
        nametext.setText(user[0]);
        fingerText.setText(user[2]);
//        Toast.makeText(EditFingerprintActivity.this,""+,Toast.LENGTH_SHORT).show();

        // comment check branch
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
//        getPosts();
        startFingerprintSensor();
        initDevice();

        //recieve from page 1

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("EditFingerprintActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    OnBnEnroll();
                    scanbtn.setImageResource(R.drawable.ic_power_green);
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //get API
    public void getPosts() {
        try {


            Call<List<User>> call = api.getPost();
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
//                    content += "Age: " + user.getAge() + "\n";
                        content += "Interest: " + user.getInterest() + "\n";
                        content += "ImageUser: " + user.getImguser() + "\n";
                        content += "Fingeprint: " + user.getFingerprint() + "\n";
                        content += "update_at: " + user.getUpdated_at() + "\n";
                        content += "Create_at: " + user.getCreated_at() + "\n";

                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("SERVER ERROR");
                    loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                    loading.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                            Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                            TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                            textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            textCon.setTextColor(getResources().getColor(R.color.black));
                            textCon.setTypeface(face);
                            textCon.setGravity(Gravity.CENTER);
                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                            text.setTextColor(getResources().getColor(R.color.red25));
                            text.setTypeface(face);
                            text.setGravity(Gravity.CENTER);

                        }
                    });

                    loading.show();
                }
            });
        } catch (Exception e) {
            com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("EditFingerprintActivity", "getPost", "can't get data from api");
            Call<com.example.fingerprinttest.model.Log> call = api.createLog(log);
            call.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                @Override
                public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                }

                @Override
                public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                }
            });
        }
    }

    //save API
    public void editFingerprintApi(int id, String finger, String typeOfFingerprint) {
        try {

            EditFingerprint editFingerprint = new EditFingerprint(id, finger, typeOfFingerprint);
            Call<EditFingerprint> call = api.editFingerprintApi(editFingerprint);
            call.enqueue(new Callback<EditFingerprint>() {
                @Override
                public void onResponse(Call<EditFingerprint> call, Response<EditFingerprint> response) {
                    if (!response.isSuccessful()) {
//                    textLog.setText("Code ERROR : " + response.code());
                        return;
                    }
                    //
                    EditFingerprint editFingerprint1 = response.body();

                }

                @Override
                public void onFailure(Call<EditFingerprint> call, Throwable t) {
                    com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("RegisterActivity3", "createPost", "can't save to API");
                    Call<com.example.fingerprinttest.model.Log> call1 = api.createLog(log);
                    call1.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                        @Override
                        public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                        }

                        @Override
                        public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                        }
                    });
                }


            });
        } catch (Exception e) {
            com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("RegisterActivity3", "createPost", "can't save to API");
            Call<com.example.fingerprinttest.model.Log> call = api.createLog(log);
            call.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                @Override
                public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                }

                @Override
                public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                }
            });
        }
    }

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

    private void initDevice() {
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
                                //imageFinger.setImageBitmap(bitmapFp);
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
                            scanText.setText("extract fail, errorcode:" + err);
                        }
                    });
                }

                @Override
                public void extractOK(final byte[] fpTemplate) {
//                    Toast.makeText(EditFingerprintActivity.this, "1", Toast.LENGTH_SHORT);
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
                                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                    t.send(new HitBuilders.EventBuilder()
                                            .setCategory("EnrollNotFinish")
                                            .setAction("error")
                                            .setLabel("alreadyFinger")
                                            .build());

                                    firstImage.setImageResource(R.drawable.shape_rectangle);
                                    secondImage.setImageResource(R.drawable.shape_rectangle);
                                    thridImage.setImageResource(R.drawable.shape_rectangle);
                                    scanbtn.setImageResource(R.drawable.ic_power__red);
                                    SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    loading.setTitleText("แจ้งเตือน");
                                    loading.setContentText("ลายนิ้วมือนี้ได้ทำการลงทะเบียนแล้ว กดลงทะเบียนใหม่");
                                    scanbtn.setImageResource(R.drawable.ic_power__red);
                                    loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                                    loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                            Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
                                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                            TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                            textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                            textCon.setTextColor(getResources().getColor(R.color.black));
                                            textCon.setTypeface(face);
                                            textCon.setGravity(Gravity.CENTER);
                                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
                                            text.setTextColor(getResources().getColor(R.color.red25));
                                            text.setTypeface(face);
                                            text.setGravity(Gravity.CENTER);

                                        }
                                    });

                                    loading.show();

                                    scanText.setText("ลายนิ้วมือนี้ได้ทำการลงทะเบียนแล้ว กดลงทะเบียนใหม่");
                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }

                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
                                    //เมื่อลงทะเบียน นิ้ว 1 ครั้งละเปลี่ยนนิ้ว จะเข้า if นี้
                                    if (enrollidx == 1) {
                                        secondImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("EnrollNotFinish")
                                                .setAction("error")
                                                .setLabel("differentFinger")
                                                .build());
                                        SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        loading.setTitleText("กรุณาวางลายนิ้วมือให้เหมือนกัน 3 ครั้ง");
                                        loading.setContentText("กรุณาวางนิ้วใหม่อีกครั้ง");
                                        loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
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

                                            }
                                        });

                                        loading.show();

                                    }
                                    if (enrollidx == 2) {
                                        thridImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("EnrollNotFinish")
                                                .setAction("error")
                                                .setLabel("differentFinger")
                                                .build());
                                        loading.setTitleText("กรุณาวางลายนิ้วมือให้เหมือนกัน 3 ครั้ง");
                                        loading.setContentText("กรุณาวางนิ้วใหม่อีกครั้ง");
                                        loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
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

                                            }
                                        });

                                        loading.show();

                                    }
                                    scanText.setText("กรุณาวางลายนิ้วมือให้เหมือนกัน 3 ครั้ง");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
//
//                                        Toast.makeText(EditFingerprintActivity.this, "1", Toast.LENGTH_SHORT);
                                    thridImage.setImageResource(R.drawable.shape_rectanglge_finger);


                                    byte[] regTemp = new byte[2048];
                                    //merge check finger 3 time and check newest
                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {
                                        // save id
                                        ZKFingerService.save(regTemp, "test" + uid++);
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);
                                        //Base64 Template
                                        // register success
                                        strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("EnrollFinish")
                                                .setAction("finish")
                                                .setLabel("enrollFinish")
                                                .build());
                                        SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.WARNING_TYPE);
                                        loading.setTitleText("ตืนยันการแก้ไข");
//                                            loading.setContentText("ข้อมูลได้ถูกบันทึกแล้ว");
                                        loading.setConfirmText("ตกลง");
                                        loading.setCancelText("ยกเลิก");
                                        loading.showCancelButton(true);
                                        loading.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        });
                                        loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
                                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                                textCon.setTextColor(getResources().getColor(R.color.black));
                                                textCon.setTypeface(face);
//
                                                textCon.setGravity(Gravity.CENTER);
                                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                                                text.setTextColor(getResources().getColor(R.color.greentea));
                                                text.setTypeface(face);
//
                                                text.setGravity(Gravity.CENTER);

                                            }
                                        });
                                        loading.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                if (user[2].equals("นิ้วชี้ขวา")) {

                                                    editFingerprintApi(Integer.parseInt(user[3]), strBase64, "foreFingerprint");
                                                    Log.e("TEST", "+++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                                    Intent intent = new Intent(EditFingerprintActivity.this, ListUserActivity.class);
                                                    startActivity(intent);
                                                    try {
                                                        OnBnStop();
                                                    } catch (FingerprintException e) {
                                                        e.printStackTrace();
                                                        Log.e("TEST", "---------------------------------------------------------------------------------------------");
                                                    }
                                                } else {
                                                    editFingerprintApi(Integer.parseInt(user[3]), strBase64, "thumbFingerprint");
                                                    Intent intent = new Intent(EditFingerprintActivity.this, ListUserActivity.class);
                                                    startActivity(intent);
                                                    try {
                                                        OnBnStop();
                                                    } catch (FingerprintException e) {
                                                        e.printStackTrace();
                                                    }
                                                }


                                            }
                                        });
//                                        loading.setCancelable(false);
                                        loading.show();

                                        scanText.setText(" ");

                                    } else {
                                        firstImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        secondImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        thridImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                                        t.send(new HitBuilders.EventBuilder()
                                                .setCategory("EnrollNotFinish")
                                                .setAction("error")
                                                .setLabel("enrollNotFinish")
                                                .build());
                                        scanbtn.setImageResource(R.drawable.ic_power__red);
                                        SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        loading.setTitleText("ลงทะเบียนไม่สำเร็จ");
                                        loading.setContentText("กดลงทะเบียนใหม่อีกครั้ง");
                                        loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                                        loading.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                                Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
                                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                                textCon.setTextColor(getResources().getColor(R.color.black));
                                                textCon.setTypeface(face);
                                                textCon.setGravity(Gravity.CENTER);
                                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                                                text.setTextColor(getResources().getColor(R.color.red25));
                                                text.setTypeface(face);
//
                                                text.setGravity(Gravity.CENTER);

                                            }
                                        });

                                        loading.show();

                                        scanText.setText("ลงทะเบียนไม่สำเร็จ");
                                    }
                                    isRegister = false;
                                } else {
                                    if (enrollidx == 1) {
                                        firstImage.setImageResource(R.drawable.shape_rectanglge_finger);
                                    }
                                    if (enrollidx == 2) {
//
                                        secondImage.setImageResource(R.drawable.shape_rectanglge_finger);
                                    }

                                    //เมื่อสแกนครั้งแรกเส้ดจะขึ้นให้แสกนอีกกี่ครั้ง
                                    scanText.setText("กรุณาวางนิ้วอีก  " + (3 - enrollidx) + " ครั้ง ");
                                }
                            } else {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                                // register success เมื่อสแกนอีกรอบจะขึ้นอันนี้
                                if (ret > 0) {
                                    String strRes[] = new String(bufids).split("\t");
                                    //statusText.setText("ยืนยันตัวตนสำเร็จ, userid:" + strRes[0] + ", score:" + strRes[1]);
                                } else {
                                    //ยังไม่เคยสแกนลายนิ้วมือ
                                    //statusText.setText("ยืนยันตัวตนไม่สำเร็จ");
                                }
                                //Base64 Template
                                //String strBase64 = Base64.encodeToString(tmpBuffer, 0, fingerprintSensor.getLastTempLen(), Base64.NO_WRAP);+
                            }
                        }
                    });
                }


            };
            fingerprintSensor.setFingerprintCaptureListener(0, listener);
            fingerprintSensor.startCapture(0);
            bstart = true;
            scanText.setText("วางนิ้วมือลงบนที่สแกนได้");
        } catch (FingerprintException e) {
            Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Device")
                    .setAction("click")
                    .setLabel("notDetectDevice")
                    .build());
            com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("EditFingerprintActivity", "OnBegin", "not connect device");
            Call<com.example.fingerprinttest.model.Log> call = api.createLog(log);
            call.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                @Override
                public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                }

                @Override
                public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                }
            });
            scanText.setText("กรุณาเชื่อมต่อกับที่สแกนนิ้ว");
        }
    }

    public void OnBnEnroll() throws FingerprintException {
        try {
            firstImage.setImageResource(R.drawable.shape_rectangle);
            secondImage.setImageResource(R.drawable.shape_rectangle);
            thridImage.setImageResource(R.drawable.shape_rectangle);

            scanText.setText("กรุณาวางนิ้ว 3 ครั้งบนทีสแกน");

            Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Enroll")
                    .setAction("click")
                    .setLabel("EnrollScan")
                    .build());

            OnBnBegin();

            if (bstart) {
                isRegister = true;
                enrollidx = 0;
                scanText.setText("กรุณาวางนิ้วชี้ข้างขวา 3 ครั้งบนทีสแกน");
            } else {

                SweetAlertDialog loading = new SweetAlertDialog(EditFingerprintActivity.this, SweetAlertDialog.WARNING_TYPE);
                loading.setTitleText("แจ้งเตือน");
                loading.setContentText("" +
                        "กรุณาเชื่อมต่อกับเครื่องสแกนนิ้ว");
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Device")
                        .setAction("click")
                        .setLabel("notDetectDevice")
                        .build());
                loading.getProgressHelper().setBarColor(EditFingerprintActivity.this.getResources().getColor(R.color.greentea));
                loading.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                        Typeface face = ResourcesCompat.getFont(EditFingerprintActivity.this, R.font.kanit_light);
                        TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                        TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                        textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textCon.setTextColor(getResources().getColor(R.color.black));
                        textCon.setTypeface(face);
                        textCon.setGravity(Gravity.CENTER);
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        text.setTextColor(getResources().getColor(R.color.red25));
                        text.setTypeface(face);
                        text.setGravity(Gravity.CENTER);

                    }
                });

                loading.show();
                scanText.setText("กรุณาเชื่อมต่อกับเครื่องสแกนนิ้ว");
            }

        } catch (Exception e) {
            com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("EditFingerprintActivity", "OnEnroll", "not connect device");
            Call<com.example.fingerprinttest.model.Log> call = api.createLog(log);
            call.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                @Override
                public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                }

                @Override
                public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                }
            });
        }
    }

    public void OnBnVerify(View view) {
        if (bstart) {
            isRegister = false;
            enrollidx = 0;
        } else {
            scanText.setText("กรุณากด แสกนลายนิ้วมือ");
        }
    }

    //close finger
    public void OnBnStop() throws FingerprintException {
        try {
            if (bstart) {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
            } else {
                scanText.setText("เครื่องยังไม่ถูกเปิดใช้งาน");
            }
        } catch (FingerprintException e) {
            scanText.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }
}