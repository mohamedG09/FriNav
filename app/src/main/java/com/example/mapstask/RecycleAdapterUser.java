package com.example.mapstask;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleAdapterUser extends RecyclerView.Adapter<RecycleAdapterUser.viewHolder> {


    private Context context;
    private ArrayList<User> list;

    public RecycleAdapterUser(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerrow, parent, false);
        viewHolder holder = new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

        holder.text_lan.setText("Lan: "+list.get(position).getLan());
        holder.text_long.setText("Lon: "+list.get(position).getLon());
        holder.text_nickname.setText("Nickname "+list.get(position).getNickname());
        Glide
                .with(holder.person_image.getContext())
                .load(list.get(position).getPhotoUrl())
                .centerCrop()
                .placeholder(R.drawable.defaultprofimage)
                .into(holder.person_image);

        //On item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO opens the current location on google maps of the users
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public ArrayList<User> getList() {
        return list;
    }

    static
    class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_lan)
        TextView text_lan;
        @BindView(R.id.text_long)
        TextView text_long;
        @BindView(R.id.text_nickname)
        TextView text_nickname;
        @BindView(R.id.person_image)
        CircleImageView person_image;

        viewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
