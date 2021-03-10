package com.example.fingerprinttest.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fingerprinttest.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    String s[];
    Context context;
    public  UserAdapter(Context ctx, String s[]){
        this.context = ctx;
        this.s = s;
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
        holder.textView.setText(s[position]);
    }

    @Override
    public int getItemCount() {
        return s.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View view){
            super(view);
            textView = view.findViewById(R.id.text1);
        }
    }
}
