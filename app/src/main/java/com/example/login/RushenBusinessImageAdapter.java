package com.example.login;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RushenBusinessImageAdapter extends RecyclerView.Adapter<RushenBusinessImageAdapter.BusinessViewHolder> {

    private View.OnClickListener onClickListener;
    public static class BusinessViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public BusinessImages business;
        BusinessImages businessimage;
        FloatingActionButton btndelete;

        public BusinessViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.editBusinessImaeview);
            btndelete = view.findViewById(R.id.floatingActionButton4);

        }


        public void setBusiness(BusinessImages business) {
            this.business = business;
            if (business.getImage() != null ) {
                icon.setImageBitmap(business.getImage());
            } else {
                icon.setImageResource(R.drawable.img);
            }

        }


    }
    private final ArrayList<BusinessImages> business;
    public RushenBusinessImageAdapter(ArrayList<com.example.login.BusinessImages> business ){
        this.business = business;
    }
    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rushen_business_card, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        BusinessImages businesstemo = business.get(position);
        holder.setBusiness(businesstemo);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return business.size();
    }
}
