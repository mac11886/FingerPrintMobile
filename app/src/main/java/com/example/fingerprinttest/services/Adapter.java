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


    int countInterest = 0;

    String sentdata = "";

    public int getCountInterest() {
        return countInterest;
    }

    public String getSentdata() {
        return sentdata;
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
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Photography,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item0 + ",";
                                countInterest += 1;

                            }

                            System.out.println("" + Color.RED);
                            System.out.println("" + title.getTextColors().getDefaultColor());
                            break;
                        case 1:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Animals,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item1 + ",";
                                countInterest += 1;

                            }
                            break;
                        case 2:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Camping,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item2 + ",";
                                countInterest += 1;
                            }
                            break;
                        case 3:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Sport,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item3 + ",";
                                countInterest += 1;
                            }
                            break;
                        case 4:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Game,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item4 + ",";
                                countInterest += 1;
                            }
                            break;
                        case 5:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Car,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item5 + ",";
                                countInterest += 1;
                            }

                            break;
                        case 6:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Science,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item6 + ",";
                                countInterest += 1;
                            }
                            break;
                        case 7:
                            if (title.getTextColors().getDefaultColor() == Color.RED) {
                                title.setTextColor(Color.BLACK);
                                content = content.replaceAll("Cooking,", "");
                                countInterest -= 1;
                            } else {
                                title.setTextColor(Color.RED);
                                content += item7 + ",";
                                countInterest += 1;
                            }
                            break;
                    }
                    // จะส่งตัวนี้ ไปที่ activity
                    sentdata = content;

                    makeInterest(sentdata);
                    String content1 = content;
                    Log.e("error", sentdata);

                }
            });

        }

    }


    public void makeInterest(String interest) {
        Intent intent = new Intent();
        intent.putExtra("interest", interest);

    }
}
