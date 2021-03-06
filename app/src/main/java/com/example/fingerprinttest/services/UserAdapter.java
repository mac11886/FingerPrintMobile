package com.example.fingerprinttest.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.controller.EditFingerprintActivity;
import com.example.fingerprinttest.model.User;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<User> users;

    Context context;

    public UserAdapter(Context ctx, List<User> users) {
        this.context = ctx;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameText.setText(users.get(position).getName());
        byte[] decodedString = Base64.decode(users.get(position).getImguser(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), decodedByte);
        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        holder.userImageView.setImageDrawable(roundedBitmapDrawable);
        holder.finger1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditFingerprintActivity.class);
                User user = users.get(position);
//                    String finger = "นิ้วชี้ขวา";
//                    intent.putExtra("users",user.getImguser());
//                    intent.putExtra("name",user.getName());
//                    intent.putExtra("finger",finger);
                if (user.getImguser().length() > 200000) {
                    byte[] decodedString = Base64.decode(user.getImguser(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    decodedByte.compress(Bitmap.CompressFormat.WEBP, 50, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    user.setImguser(encode);
                }
                Bundle bundle = new Bundle();
                bundle.putStringArray("user", new String[]{user.getName(), user.getImguser(), "นิ้วชี้ขวา",String.valueOf(user.getId())});
//                    intent.putExtra("image", user.getImguser());
                intent.putExtras(bundle);
                context.startActivity(intent);
                Log.e("TEST", "Image:" + user.getName() + "\n" + user.getImguser().length());
            }
        });
        holder.finger2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditFingerprintActivity.class);
                User user = users.get(position);
//                String finger = "นิ้วโป้งขวา";
//                intent.putExtra("users",user.getImguser());
//                intent.putExtra("name",user.getName());
//                intent.putExtra("finger",finger);

                if (user.getImguser().length() > 200000) {
                    byte[] decodedString = Base64.decode(user.getImguser(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    decodedByte.compress(Bitmap.CompressFormat.WEBP, 50, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    user.setImguser(encode);
                }
                Bundle bundle = new Bundle();
                bundle.putStringArray("user", new String[]{user.getName(), user.getImguser(), "นิ้วโป้งขวา",String.valueOf(user.getId())});
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView userImageView;
        Button finger1, finger2;

        public ViewHolder(@NonNull View view) {
            super(view);
            nameText = view.findViewById(R.id.nameUser);
            userImageView = view.findViewById(R.id.userImage);
            finger1 = view.findViewById(R.id.finger1);
            finger2 = view.findViewById(R.id.finger2);
        }
    }
}
