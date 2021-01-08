package com.example.fingerprinttest.services;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.controller.RegisterActivity3;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    String content = " ";


    String sentdata = "";

    public String getSentdata() {
        return sentdata;
    }

    DatatoActivity datatoActivity = new DatatoActivity() {
        @Override
        public void sendData(String string) {
            sentdata = string;
        }
    };

    public Adapter(DatatoActivity datatoActivity) {
        this.datatoActivity = datatoActivity;
    }

    public Adapter(Context ctx, List<String> titles, List<Integer> images) {
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));

//        holder.title.setTextColor(Color.parseColor("000000"));


    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView gridIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item);
            gridIcon = itemView.findViewById(R.id.imageItem);


            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    String item0 = "Photography";
                    String item1 = "Animals";
                    String item2 = "Camping";
                    String item3 = "Sport";
                    String item4 = "Game";
                    String item5 = "Car";
                    String item6 = "Science";
                    String item7 = "Cooking";


                    switch (getAdapterPosition()) {
                        case 0:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Photography,","");
                                Toast.makeText(v.getContext(), "ITEM0 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item0 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item0, Toast.LENGTH_LONG).show();
                            }

                            System.out.println(""+Color.RED);
                            System.out.println(""+title.getTextColors().getDefaultColor());
                            //itemView.setBackgroundResource(R.color.black);
                            break;
                        case 1:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Animals,","");
                                Toast.makeText(v.getContext(), "ITEM1 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item1 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item1, Toast.LENGTH_LONG).show();
                            }
                           break;
                        case 2:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Camping,","");
                                Toast.makeText(v.getContext(), "ITEM2 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item2 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item2, Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 3:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Sport,","");
                                Toast.makeText(v.getContext(), "ITEM3 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item3 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item3, Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 4:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Game,","");
                                Toast.makeText(v.getContext(), "ITEM4 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item4 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item4, Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 5:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Car,","");
                                Toast.makeText(v.getContext(), "ITEM5 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item5 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item5, Toast.LENGTH_LONG).show();
                            }

                            break;
                        case 6:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Science,","");
                                Toast.makeText(v.getContext(), "ITEM6 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item6 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item6, Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 7:
                            if (title.getTextColors().getDefaultColor() == Color.RED){
                                title.setTextColor(Color.BLACK);
                                content=  content.replaceAll("Cooking,","");
                                Toast.makeText(v.getContext(), "ITEM7 -->  RED " , Toast.LENGTH_LONG).show();

                            }else {
                                title.setTextColor(Color.RED);
                                content += item7 + ",";
                                Toast.makeText(v.getContext(), "Click --> " + item7, Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                    // จะส่งตัวนี้ ไปที่ activity
                    sentdata = content;

                    makeInterest(sentdata);
                    //datatoActivity.sendData(sentdata);
                    String content1 = content;
                    Log.e("error", sentdata);

//                    System.out.println(content1.substring(0,content1.length()-1));
                }
            });

        }

    }


    public void makeInterest(String interest) {
        Intent intent = new Intent();
        intent.putExtra("interest", interest);

    }
}
