package com.example.fingerprinttest.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.controller.EditFingerprintActivity;
import com.example.fingerprinttest.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<User> users;

    Context context;
    public  UserAdapter(Context ctx, List<User> users){
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

        holder.userImageView.setImageBitmap(decodedByte);
        holder.finger1.setImageResource(R.drawable.ic_fingerprint_1);
        holder.finger2.setImageResource(R.drawable.ic_fingerprint_1);
        holder.finger1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditFingerprintActivity.class);
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
        ImageView userImageView, finger1, finger2;
        public ViewHolder(@NonNull View view){
            super(view);
            nameText = view.findViewById(R.id.nameUser);
            userImageView = view.findViewById(R.id.userImage);
            finger1 = view.findViewById(R.id.finger1);
            finger2 = view.findViewById(R.id.finger2);
        }
    }
}
