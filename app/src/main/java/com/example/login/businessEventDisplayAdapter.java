package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class businessEventDisplayAdapter extends RecyclerView.Adapter<businessEventDisplayAdapter.BusinessEventViewHolder> {

    private View.OnClickListener onClickListener;
    public static class BusinessEventViewHolder extends RecyclerView.ViewHolder {
        public TextView lblEventName;
        public TextView lbldate;
        public ImageView EventImage;
        public Event event;

        public BusinessEventViewHolder(@NonNull View view) {
            super(view);
            lblEventName = view.findViewById(R.id.txtEventCard);
            EventImage = view.findViewById(R.id.imgEventCard);
            lbldate = view.findViewById(R.id.ReventsDate);

        }

        public void setEvents(Event event) {
            this.event = event;

            if (event.getImage1() != null ) {
                EventImage.setImageBitmap(event.getImage1());
            } else {
                EventImage.setImageResource(R.drawable.img);
            }
            lblEventName.setText(event.getName());
            lbldate.setText(event.getDate());


        }


    }
    private final List<Event> events;
    public businessEventDisplayAdapter(List<Event> events ){
        this.events = events;
    }
    @NonNull
    @Override
    public BusinessEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_businessevent_card, parent, false);
        return new BusinessEventViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull BusinessEventViewHolder holder, int position) {
        Event eventtemo = events.get(position);
        holder.setEvents(eventtemo);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

