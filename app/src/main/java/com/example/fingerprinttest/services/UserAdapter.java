package com.example.fingerprinttest.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.controller.AdminActivity;
import com.example.fingerprinttest.controller.MainActivity;
import com.example.fingerprinttest.model.Payment;
import com.example.fingerprinttest.model.User;

import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    Context context;
    List<User> userList;

    Button user_action_profile,user_action_edit,user_action_delete;
    private Object AdminActivity;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userList != null && userList.size() > 0) {
            User user = userList.get(position);
            //holder.user_id.setText(user.getId());
            holder.user_name.setText(user.getName());
//            holder.user_action.setText(user.getPayment());
            user_action_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user1 = userList.get(position);
                    //context.startActivities(new Intent[]{new Intent(context, MainActivity.class)});
                    String url = "http://www.google.com";
                    Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);

//               Intent intent = Intent.makeMainActivity()
//                    intent.
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id, user_name, user_action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            user_id = itemView.findViewById(R.id.user_id);
            user_name = itemView.findViewById(R.id.user_name);
            user_action_profile = itemView.findViewById(R.id.user_action_profile);
            user_action_edit = itemView.findViewById(R.id.user_action_edit);
            user_action_delete = itemView.findViewById(R.id.user_action_delete);

        }
    }
}
