    package com.example.fingerprinttest.controller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerprinttest.services.JsonPlaceHolderApi;
import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.User;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.ZKIDFprService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisActivity extends AppCompatActivity {
    private static final int VID = 6997;
    private static final int PID = 288;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private TextView statusText = null;
    private ImageView imageFinger = null;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    List<User> users ;

    ImageView imageUser;
    EditText nameText;
    EditText ageText;
    TextView textDropdown;
    Spinner spinner;
    User user;
    Button saveBtn;
    String interestText;
    String text = "";
    String saveRegTem ;
    String saveRegTem1;
    String saveRegTem2;
    int dataFinger;
    String strBase64;
    String[] interest = {"football", "basketball", "book"};
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 0;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];
    private FingerprintSensor fingerprintSensor = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";
    private JsonPlaceHolderApi jsonPlaceHolderApi;




    //get API
    public void getPosts() {
        Call<List<User>> call = jsonPlaceHolderApi.getPost();
//        textDropdown.setText(call.request().toString());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    textDropdown.setText("Code : " + response.code());
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
                textDropdown.setText(t.getMessage());
            }
        });

    }

    //save API
    public void createPost() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//         user = new User(""+nameText.getText(),Integer.parseInt(ageText.getText().toString()), ""+interest,""+imageUser.toString(),"", timestamp ,timestamp);

        int ageEdit = Integer.parseInt(ageText.getText().toString());


        saveRegTem1 =saveRegTem.replace("\\","\\ ");

        user = new User(" "+nameText.getText(),ageEdit,""+interestText.substring(0,interestText.length()-1),"11111",
                ""+strBase64);
        Call<User> call = jsonPlaceHolderApi.createPost(user);
//        Call<User> call = jsonPlaceHolderApi.createPost("" + nameText.getText(), Integer.parseInt(ageText.getText().toString()),
//                "" + interestText[0], "" + imageUser.toString(), "", timestamp, timestamp);
//        textDropdown.setText(call.request().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    textDropdown.setText("Code ERROR : " +response.code());
                    return;
                }
                    textDropdown.setText("success");
                //
                User userPost = response.body();
                String content = "";
                content += "ID: " + userPost.getId() + "\n";
                content += "Name: " + userPost.getName() + "\n";
                content += "Age: " + userPost.getAge() + "\n";
                content += "Interest: " + userPost.getInterest() + "\n";
                content += "ImageUser: " + userPost.getImguser() + "\n";
                content += "Fingeprint: " + userPost.getFingerprint() + "\n";
                content += "update_at: " + userPost.getUpdated_at() + "\n";
                content += "Create_at: " + userPost.getCreated_at() + "\n";
               // textDropdown.setText(response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }


        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        imageUser = (ImageView) findViewById(R.id.imageUser);
        statusText = (TextView) findViewById(R.id.statusText);
        imageFinger = (ImageView) findViewById(R.id.imageFinger);
        nameText = (EditText) findViewById(R.id.nameEditText);
        ageText = (EditText) findViewById(R.id.ageEditText);
        imageUser = (ImageView) findViewById(R.id.imageUser);
        textDropdown = (TextView) findViewById(R.id.textDropdown);
        spinner = (Spinner) findViewById(R.id.spinnerInterest);
        saveBtn = (Button) findViewById(R.id.saveDataBtn);

        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinner,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter<String>(this, "เลือกสิ่งที่น่าสนใจ", Arrays.asList(interest)),
                new HintSpinner.Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)

                        text += interest[position];
                        text += ",";
                        textDropdown.setText("" + text);
                        interestText = text;
                    }
                });
        hintSpinner.init();



        takeImage();

        //fingerprint
        initDevice();
        startFingerprintSensor();
        //connectAPI
         getPosts();
       // createPost();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
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


    public void takeImage() {
        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "NOT TAKE A PHOTO ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


    }


    //get Image to ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageUser.setImageBitmap(imageBitmap);
        }
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

    public void OnBnBegin(View view) throws FingerprintException {
        try {
            if (bstart) return;
            fingerprintSensor.open(0);
//            getPosts();
            for (User user:users) {
                byte[] byte2 = Base64.decode(user.getFingerprint(),Base64.NO_WRAP);
                int ret = ZKFingerService.save(byte2,""+user.getId());

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
                                imageFinger.setImageBitmap(bitmapFp);
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
                            statusText.setText("extract fail, errorcode:" + err);
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

                                    statusText.setText("ลายนิ้วมือนี้ได้ทำการลงทะเบียนแล้วโดย " + strRes[0] + ",ยกเลิกการลงทะเบียน");
                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }

                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
                                    //เมื่อลงทะเบียน นิ้ว 1 ครั้งละเปลี่ยนนิ้ว จะเข้า if นี้
                                    statusText.setText("กรุณาวางลายนิ้วมือให้เหมือนกัน 3 ครั้ง");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
                                    byte[] regTemp = new byte[2048];
                                    //merge check finger 3 time and check newest
                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {
                                        // save id
                                        String name = nameText.getText().toString();
                                        saveRegTem =new String (regTemp);
                                        ZKFingerService.save(regTemp, "test"  + uid++);
                                        Log.e(String.valueOf(REQUEST_IMAGE_CAPTURE), "run: " + regTemp.toString());
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);
                                        //Base64 Template
                                        // register success
                                         strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        statusText.setText("ลงทะเบียนเสร็จสิ้น, name :" + nameText.getText().toString() + " คนที่" + ZKFingerService.count());
                                         dataFinger = ZKFingerService.get(tmpBuffer,"test"+(uid-1));


                                    } else {
                                        statusText.setText("ลงทะเบียนไม่สำเร็จ");
                                    }
                                    isRegister = false;
                                } else {
                                    //เมื่อสแกนครั้งแรกเส้ดจะขึ้นให้แสกนอีกกี่ครั้ง
                                    statusText.setText("กรุณาวางนิ้วอีก  " + (3 - enrollidx) + "ครั้ง ");
                                }
                            } else {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                                // register success เมื่อสแกนอีกรอบจะขึ้นอันนี้
                                if (ret > 0) {
                                    String strRes[] = new String(bufids).split("\t");
                                    statusText.setText("ยืนยันตัวตนสำเร็จ, userid:" + strRes[0] + ", score:" + strRes[1]);
                                } else {
                                    //ยังไม่เคยสแกนลายนิ้วมือ
                                    statusText.setText("ยืนยันตัวตนไม่สำเร็จ");
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
            statusText.setText("วางนิ้วมือลงบนที่สแกนได้");
        } catch (FingerprintException e) {
            statusText.setText("กรุณาเชื่อมต่อกับที่สแกนนิ้ว");
//            statusText.setText("begin capture fail.errorcode:"+ e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
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
                statusText.setText("ปิดการสแกน");
            } else {
                statusText.setText("เครื่องยังไม่ถูกเปิดใช้งาน");
            }
        } catch (FingerprintException e) {
            statusText.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }

    public void OnBnEnroll(View view) {
        if (bstart) {
            isRegister = true;
            enrollidx = 0;
            statusText.setText("กรุณาวางนิ้ว 3 ครั้งบนทีสแกน");
        } else {
            statusText.setText("กรุณากด แสกนลายนิ้วมือ");
        }
    }

    public void OnBnVerify(View view) {
        if (bstart) {
            isRegister = false;
            enrollidx = 0;
        } else {
            statusText.setText("กรุณากด แสกนลายนิ้วมือ");
        }
    }


//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        textDropdown.setText(" "+ interest[position]);
//      //  Toast.makeText(getApplicationContext(),"Select Interest: "+interest[position],Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}