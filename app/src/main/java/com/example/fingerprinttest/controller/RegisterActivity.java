package com.example.fingerprinttest.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fingerprinttest.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    ImageView imageUserRegister;
    Button takeOrchooseBtn, nextBtn;
    EditText nameText, ageText;
    String encoded;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageUserRegister = (ImageView) findViewById(R.id.imageUserRegister);
        takeOrchooseBtn = (Button) findViewById(R.id.takeOrchooseBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        nameText = (EditText) findViewById(R.id.editTextName);
        ageText = (EditText) findViewById(R.id.editTextAge);

        builder = new AlertDialog.Builder(this);
        //take or choose image function
        takeOrchooseBtn.setOnClickListener(new View.OnClickListener() {
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
                int a = 0;
                if (detectValid() == 1) {
                    nameText.setError("กรุณาใส่ชื่อ");
                    nameText.requestFocus();
                } else if (detectValid() == 2) {
                    ageText.setError("กรุณาใส่อายุ");
                    ageText.requestFocus();
                } else if (detectValid() == 3) {
                    builder.setMessage("กรุณาใส่รูป").setTitle("ใส่ข้อมูลให้ครบ");
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("แจ้งเตือน");
                    alert.show();
                } else {
                    //sent data to post on API
                    String imageBase64 = encoded;
                    String name = nameText.getText().toString();
                    String age = ageText.getText().toString();
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                    intent.putExtra("nameUser", name);
                    intent.putExtra("ageUser", age);
                    intent.putExtra("imgUser", imageBase64);
                    startActivity(intent);
                }
            }
        });


    }

    private int detectValid() {
        if (nameText.getText().toString().isEmpty()) {

            return 1;
        }
        if (ageText.getText().toString().isEmpty()) {
            return 2;
        }
        if (imageUserRegister.getDrawable() == null) {

            return 3;
        }
        return 0;
//        if ((Boolean) imageUserRegister.getTag()) {
//            takeOrchooseBtn.setEnabled(false);
//
//        } else {
//            takeOrchooseBtn.setEnabled(true);
//        }

        // if (imageUser.getDrawable().isVisible()) ;


    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"ถ่ายรูป", "เลือกจาก Gallery", "ยกเลิก"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("เลือกรูปภาพของคุณ");


        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("ถ่ายรูป")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (options[item].equals("เลือกจาก Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);

//                    Intent pickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPicture, 1);
                } else if (options[item].equals("ยกเลิก")) {
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
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, selectedImage.getWidth(), selectedImage.getHeight(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.WEBP, 40, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        imageUserRegister.setImageBitmap(selectedImage);
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


                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                textDropdown.setText("encode"+encoded.length()+"\n byte"+byteArray.length);
                            imageUserRegister.setRotation(90);
                            imageUserRegister.setImageBitmap(bitmap);

//                imageUser.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


//
                    }
                    break;
            }
        }

    }
}