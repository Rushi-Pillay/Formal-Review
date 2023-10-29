package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RushenBusinessAdapter extends RecyclerView.Adapter<RushenBusinessAdapter.BusinessViewHolder> {

    private View.OnClickListener onClickListener;
    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName,location,Contactnum,email,Type;
        public ImageView icon;
        public Business business;


        public BusinessViewHolder(@NonNull View view) {
            super(view);
            Type = view.findViewById(R.id.RbusinessType);
            lblName = view.findViewById(R.id.RBusinessHeading);
            location = view.findViewById(R.id.RBusinessLocation);
            Contactnum = view.findViewById(R.id.RBusinessContact);
             email = view.findViewById(R.id.RBusinessEmail);
            icon = view.findViewById(R.id.RBusinessImageView);
            icon.setBackgroundResource(R.drawable.rounded_image_bg);

        }

        public void setBusiness(Business business) {
            this.business = business;

            if (business.getImage1() != null ) {
                icon.setImageBitmap(business.getImage1());
            } else {
                icon.setImageResource(R.drawable.img);
            }
            lblName.setText(business.getName());
            location.setText(business.getLocation());
            Contactnum.setText(business.getContactNumber());
            email.setText(business.getEmail());
            Type.setText(business.getType());
        }


    }
    private final List<Business> business;
    public RushenBusinessAdapter(List<Business> business ){
        this.business = business;
    }
    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rushen_browse_events_businesscard, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
               Business businesstemo = business.get(position);
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
