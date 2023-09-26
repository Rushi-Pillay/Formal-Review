package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private View.OnClickListener onClickListener;
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName2;
        public Button addbutton;
        public ImageView icon1;
        public Users friend;

        public FriendViewHolder(@NonNull View view) {
            super(view);
            lblName2 = view.findViewById(R.id.Username2);
            icon1 = view.findViewById(R.id.DisplayPicture3);
            addbutton = view.findViewById(R.id.AddRemoveBtn);
        }
        public void setFriend(Users friend) {
            this.friend = friend;

            if (friend.getImage1() != null ) {
                icon1.setImageBitmap(friend.getImage1());
            } else {
                icon1.setImageResource(R.drawable.img);
            }
            lblName2.setText(friend.getUsername());
//            addbutton.setText("remove");

        }
    }
    private final List<Users> friends;
    public FriendAdapter(List<Users> friends ){
        this.friends = friends;
    }
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_card, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
       Users friendtemo = friends.get(position);
        holder.setFriend(friendtemo);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
