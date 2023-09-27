package com.example.login;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder> {

    private View.OnClickListener onClickListener;
    public static class ImageAdapterViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img1;
        private BusinessImage images;

        public ImageAdapterViewHolder(@NonNull View view) {
            super(view);
            img1 = view.findViewById(R.id.imgSpecialsCard);

        }

        public void setImage(BusinessImage images) {
            this.images = images;
            img1.setImageBitmap(images.getImage());



        }


    }
    private final List<BusinessImage> businessImages;
    public ImageAdapter(List<BusinessImage>businessImages ){
        this.businessImages= businessImages;
    }
    @NonNull
    @Override
    public ImageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_card, parent, false);
        return new ImageAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ImageAdapterViewHolder holder, int position) {
        BusinessImage busImTemp =businessImages.get(position);
        holder.setImage(busImTemp);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return businessImages.size();
    }
}