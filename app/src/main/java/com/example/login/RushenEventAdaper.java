package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RushenEventAdaper extends RecyclerView.Adapter<RushenEventAdaper.EventViewHolder> {

    private View.OnClickListener onClickListener;
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName1,txtdesrip,txtlocation,txtprice,txtTime,txtdate;
        public TextView lbldate;
        public ImageView icon1;
        public RatingBar pb ;
        public Event event;

        public EventViewHolder(@NonNull View view) {
            super(view);
            lblName1 = view.findViewById(R.id.ReventscardHeading);
            icon1 = view.findViewById(R.id.imageView4);
            txtlocation = view.findViewById(R.id.ReventsLocation);
            txtdesrip= view.findViewById(R.id.txtDescription);
            txtdate = view.findViewById(R.id.ReventsDate);
//            txtprice = view.findViewById(R.id.txtPrice);
            txtTime = view.findViewById(R.id.ReventTime);
        }

        public void setEvents(Event event) {
            this.event = event;

            if (event.getImage1() != null ) {
                icon1.setImageBitmap(event.getImage1());
            } else {
                icon1.setImageResource(R.drawable.img);
            }
            lblName1.setText(event.getName());
            txtdate.setText(event.getDate());
            txtTime.setText(event.getTime());
//            txtprice.setText("R"+event.getTicketprice());
            txtdesrip.setText(event.getDescription());
            txtlocation.setText(event.getDate());


        }


    }
    private final List<Event> events;
    public RushenEventAdaper(List<Event> events ){
        this.events = events;
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
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

