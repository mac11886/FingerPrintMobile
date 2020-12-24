package com.example.fingerprinttest.controller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fingerprinttest.R;
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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DebugFinger extends AppCompatActivity {
    private static final int PID = 288;
    private static final int VID = 6997;
    private TextView textView = null;
    private ImageView imageView = null;
    TextView textLog;
    TextView textArray;
    TextView textEnroll;
    String text;
    int save;
    private boolean bstart = false;
    private boolean isRegister = false;
    public int uid = 1;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];
    Button saveBtn;


    private FingerprintSensor fingerprintSensor = null;

    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_finger);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        textLog = (TextView) findViewById(R.id.debugText);
        saveBtn = (Button) findViewById(R.id.saveDataBtn);
        textArray = (TextView) findViewById(R.id.textArray);
        textEnroll = (TextView) findViewById(R.id.textEnroll);
        LogHelper.d("Helper");

        // text mail pink right down

        // set id
        InitDevice();
        startFingerprintSensor();


//        int ra1 = Integer.parseInt("1cd2f33", 16);
//        int ra2 = Integer.parseInt("d30d2f0", 16);
//        int ra3 = Integer.parseInt("7a6faee", 16);
////
//        byte[] bt = (BigInteger.valueOf(ra1).toByteArray());
//        byte[] bt2 = (BigInteger.valueOf(ra2).toByteArray());
//        byte[] bt3 = (BigInteger.valueOf(ra3).toByteArray());
//        byte[] bufids = new byte[256];
//        textEnroll.setText("ret" + ZKFingerService.merge(bt, bt2, bt3, rt));
//        ZKFingerService.merge(bt, bt2, bt3, rt);
//        ZKFingerService.verifyId(rt, "test1");
//        textLog.setText("verify=" + ZKFingerService.identify(rt, bufids,55,1));
//        ZKFingerService.save(rt, "test1");


        //test open fingerScan
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fingerprintSensor.open(0);
                    textLog.setText("success open");
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //accept usb
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


    //save image??
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

    // เชื่อมต่อกับ แสกนนิ้ว
    private void InitDevice() {
        System.out.println("INITDEVICE CONNECT");
        UsbManager musbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        Context context = this.getApplicationContext();
        context.registerReceiver(mUsbReceiver, filter);
        System.out.println("connect finger");
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

    //start app
    public void OnBnBegin(View view) throws FingerprintException {
        try {

            if (bstart) return;
            fingerprintSensor.open(0);
            String reg = "TWNTUzIxAAAEICcECAUHCc7QAAAcIWkBAABkhM0k5iAZAJkOugDlAJQvtAAvAI0PRQ" +
                    "AxIIAPPgBIALcOPiBbAHAPkwCmAIIv+ABuAA4NlAB7IPIPiwCMALkPOSCeAGQPtgBkAI0vhwDFAP4PYQDNIHwPbQDTAKYO/yDUABQNlQA" +
                    "TAAEvwADfAIwPrgDmIF0OpwDtALwPjCDuAPkPWQA9ANgucwD9AFYPNAABIRwOnwAaAY8P5yAbASIN5gDtATUs8AAqAaMOPAAoISgOTQAyAYcPY" +
                    "yAyAT4P3QDwAaEsPAA8AUIPIwA7IUYN/QBEAfoOoyBPAaAPKV/MM1AsWByLZlsY1T0HCen3PmIPToMKFOD8M+D0NW/JPk8nMy/7Nfv0+/H1ZXUPElcCOo" +
                    "R/QSPwV08OEtk7LD1dRE4MT/zv70IOEqsFPtL5R+D9UPiEWSd1XRFEZBJeJ+SPLDaD/0CFG5d3Ksz2MNLzPWh/O42JJvO0Rd7URj4BUBLo6UgIEIsNPF2LPouEE4X" +
                    "4He2bHohEKOWCFWZ4Hol/4UAABB/xD2MHHc1lIx5rKCBeMbUyO+GoSLryVrFx0V6MNx8HHM8MHWWMGkASJI8PLXxCO+lYuXvv1Akg+QEGbR3LCwCHFdXAxOH8wlX/BgANFRPiO" +
                    "QMA6xYXOwgElhQew/9bwMIAZjgCNlkPAFfmAzngwV1DWQUAuTGCTwoAhDcGTJDAUScBfDiAwv4E/8cvAUJI+sDAOjhI4FkDADxMbToRBB5Z7SvA/UU6/1JiAgA4Xmv/zwCLQojCgMD" +
                    "DTNUAVlvsKT5EVcAEFQQBduf/RzP/hkrE38HAwAUATURpXDABVYH0wDGRwPtyTQoAh4mAVYz74QsAj40GQQRHxCoBh499kMGzwBAgIJDnVcD+Oz48eFQNALOdjEHD" +
                    "eOFZAwA5oWQEDQSSoonBwMTBvXPFJwG6oxBKRtYAIYXo/2j+/y+DU04sAYfB9/78gsH73WsKAKjKDIVKXSUBoMt6jAnFadVJgnrABACZFv0sNQAK1I//WqLA" +
                    "xuLDeMHAwcAHCwRL1meQwMLABMHELQGR13DDwaxxxOT/AwCZ2AY6BQTl4Bf//8EVxAfivnBWwoaMZqgIBIvvDDPATwjFo+tXwMTDwIMWxAnxgsBxwf/BwQTD" +
                    "jVHBwXwPAHU/3vve+v79wf3AOv/F3sEGAFf7UzrBhygRbQBcwsAHwm8oEXMBU8BsvgQU0gggTAQQm+VMx+YREOMkp3cHw8DjwsLAw8L/AAQUyionIwQQ5OktN" +
                    "C4R3DCti8UHxcHiwcOiBBD49S00IxFjNkPCA9VNM2PAAxBqN0AHBBTAOUD6/gkQAD2+48XIy8SxBNXYRH7A/Q0Qo1BJ+/0E/fv6/XIF1aRSIMKkUkIAC4YBBCAKRVIA";


            byte[] rt = new byte[2048];
            rt = reg.getBytes();
            byte[] byte2 = Base64.decode(reg,Base64.NO_WRAP);


            int ret = ZKFingerService.save(byte2,"test1");
            textLog.setText(""+reg.getBytes());
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
                                imageView.setImageBitmap(bitmapFp);
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
                                    textView.setText("the finger already enroll by " + strRes[0] + ",cancel enroll");

                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }
                                // tong scan same finger 3 time
                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {

                                    textView.setText("please press the same finger 3 times for the enrollment");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);

                                enrollidx++;
                                if (enrollidx == 3) {
                                    byte[] regTemp = new byte[2048];

                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {


                                        ZKFingerService.save(regTemp, "test" + uid++);
                                        int it = Integer.parseInt(regTemp.toString().substring(3), 16);
                                        BigInteger bigInteger = BigInteger.valueOf(it);
                                        byte[] bytes = (bigInteger.toByteArray());
                                        textArray.setText("regtemp=" + regTemp + "\n ret=" + ret + "\nsave=" + ZKFingerService.save(regTemp, "test" + uid)
                                                + "\ntmpbuffer=" + tmpBuffer);
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);

                                        String saveRegTem = new String(regTemp);
                                        String strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        byte[] byte2 = Base64.decode(strBase64,Base64.NO_WRAP);
                                        textEnroll.setText("arrayReg="+ byte2);

//                                        textEnroll.setText("\n tmpbuff=" + tmpBuffer.toString()
//                                                + "\nbufids=" + bufids.toString() +
//                                                "\n retemarrayEnroll=" + regtemparray.toString()
//                                                + "\n retMerge=" + ret + "\n lastregtemp=" + lastRegTemp
//                                                + "\nregtemp=" + regTemp + "\n regTemp[Array]=" + saveRegTem + "\nLastRegTem 0 =" + lastRegTemp[0]
//                                        );
                                        //Base64 Template

                                        textView.setText("enroll succ, uid:" + uid + "count:" + ZKFingerService.count());
//                                        textEnroll.setText(""+ZKFingerService.);
                                        textLog.setText("ret=" + ret + "\n" + "GET=" + ZKFingerService.get(tmpBuffer, "test" + (uid - 1)) + "\n tmpbufferGet=" + tmpBuffer);
                                    } else {
                                        textView.setText("enroll fail");
                                    }
                                    isRegister = false;
                                } else {
                                    textView.setText("You need to press the " + (3 - enrollidx) + " time fingerprint");
                                }
                            } else {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                                if (ret > 0) {
                                    String strRes[] = new String(bufids).split("\t");
                                    textView.setText("identify succ, userid:" + strRes[0] + ", score:" + strRes[1]);
                                    textEnroll.setText("tmpbuf=" + tmpBuffer + "\nbufid=" + bufids + "\nregtemar" + regtemparray
                                            + "\n retfromMerge=" + ret + "\n regtem=");
                                } else {
                                    textView.setText("identify fail");
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
            textView.setText("start capture succ");
        } catch (FingerprintException e) {
            textView.setText("begin capture fail.errorcode:" + e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
        }
    }

    public void OnBnStop(View view) throws FingerprintException {
        try {
            if (bstart) {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
                textView.setText("stop capture success");
            } else {
                textView.setText("already stop");
            }
        } catch (FingerprintException e) {
            textView.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }

    //register
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