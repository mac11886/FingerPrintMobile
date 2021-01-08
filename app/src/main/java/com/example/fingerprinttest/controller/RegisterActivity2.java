package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.fingerprinttest.controller.RegisActivity.COLOR_PALEGOLDENROD;

public class RegisterActivity2 extends AppCompatActivity {

    ImageView firstImage, secondImage, thridImage;
    TextView scanText;
    Button scanBtn, nextBtn;
    List<User> users;
    public static final int COLOR_PALEGOLDENROD = 0xff000000;
    String name;
    String age;
    String imgUser;
    String finger;
    String interest;
    private static final int VID = 6997;
    private static final int PID = 288;
    int dataFinger;
    String strBase64;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 0;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];
    private FingerprintSensor fingerprintSensor = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";
    private JsonPlaceHolderApi jsonPlaceHolderApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        firstImage = (ImageView) findViewById(R.id.firstImage);
        secondImage = (ImageView) findViewById(R.id.secondImage);
        thridImage = (ImageView) findViewById(R.id.thridImage);
        scanText = (TextView) findViewById(R.id.scanText);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        getPosts();
        startFingerprintSensor();
        initDevice();
        //recieve from page 1
        name = getIntent().getStringExtra("nameUser");
        age = getIntent().getStringExtra("ageUser");
        imgUser = getIntent().getStringExtra("imgUser");


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                intent.putExtra("nameUser", name);
                intent.putExtra("ageUser", age);
                intent.putExtra("imgUser", imgUser);
                intent.putExtra("fingerprint", strBase64);
                startActivity(intent);
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
//                    textDropdown.setText("Code : " + response.code());
                    return;
                }
                users = response.body();
                for (User user : users) {
                    String content = "";
                    content += "ID: " + user.getId() + "\n";
                    content += "Name: " + user.getName() + "\n";
                    content += "Age: " + user.getAge() + "\n";
                    content += "Interest: " + user.getInterest() + "\n";
                    content += "ImageUser: " + user.getImguser() + "\n";
                    content += "Fingeprint: " + user.getFingerprint() + "\n";
                    content += "update_at: " + user.getUpdated_at() + "\n";
                    content += "Create_at: " + user.getCreated_at() + "\n";
                    //textDropdown.setText(content);

                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
//                textDropdown.setText(t.getMessage());
            }
        });

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
            getPosts();
            for (User user : users) {
                byte[] byte2 = Base64.decode(user.getFingerprint(), Base64.NO_WRAP);
                int ret = ZKFingerService.save(byte2, " " + user.getId());

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
                                    scanText.setText("ลายนิ้วมือนี้ได้ทำการลงทะเบียนแล้วโดย " + strRes[0] + ",ยกเลิกการลงทะเบียน");
                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }

                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
                                    //เมื่อลงทะเบียน นิ้ว 1 ครั้งละเปลี่ยนนิ้ว จะเข้า if นี้
                                    if (enrollidx == 1) {
                                        secondImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                    }
                                    if (enrollidx == 2) {
                                        thridImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                    }
                                    scanText.setText("กรุณาวางลายนิ้วมือให้เหมือนกัน 3 ครั้ง");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
//
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
                                        scanText.setText("ลงทะเบียนเสร็จสิ้น, name :" + "คนที่ " + ZKFingerService.count());


                                    } else {
                                        firstImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        secondImage.setImageResource(R.drawable.shape_rectangle_error_finger);
                                        thridImage.setImageResource(R.drawable.shape_rectangle_error_finger);
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
                                    if (enrollidx == 3) {

                                    }

                                    //เมื่อสแกนครั้งแรกเส้ดจะขึ้นให้แสกนอีกกี่ครั้ง
                                    scanText.setText("กรุณาวางนิ้วอีก  " + (3 - enrollidx) + "ครั้ง ");
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
            scanText.setText("กรุณาเชื่อมต่อกับที่สแกนนิ้ว");
//            statusText.setText("begin capture fail.errorcode:"+ e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
        }
    }

    public void OnBnEnroll(View view) throws FingerprintException {
        firstImage.setImageResource(R.drawable.shape_rectangle);
        secondImage.setImageResource(R.drawable.shape_rectangle);
        thridImage.setImageResource(R.drawable.shape_rectangle);
        OnBnBegin();
        if (bstart) {
            isRegister = true;
            enrollidx = 0;
            scanText.setText("กรุณาวางนิ้ว 3 ครั้งบนทีสแกน");
        } else {
            scanText.setText("กรุณาเชื่อมต่อกับเครื่อง");
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
    public void OnBnStop(View view) throws FingerprintException {
        try {
            if (bstart) {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
                scanText.setText("ปิดการสแกน");
            } else {
                scanText.setText("เครื่องยังไม่ถูกเปิดใช้งาน");
            }
        } catch (FingerprintException e) {
            scanText.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }
}