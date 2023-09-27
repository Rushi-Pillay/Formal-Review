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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private View.OnClickListener onClickListener;
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName2;
        public Button addbutton;
        public ImageView icon1;
        public Users User;

        public UserViewHolder(@NonNull View view) {
            super(view);
            lblName2 = view.findViewById(R.id.Username2);
            icon1 = view.findViewById(R.id.DisplayPicture3);
            addbutton = view.findViewById(R.id.AddRemoveBtn);
        }
        public void setUser(Users User) {
            this.User = User;

            if (User.getImage1() != null ) {
                icon1.setImageBitmap(User.getImage1());
            } else {
                icon1.setImageResource(R.drawable.img);
            }
            lblName2.setText(User.getUsername());
            addbutton.setText("Add");

        }
    }
    private final List<Users> Users;
    public UserAdapter(List<Users> Users ){
        this.Users = Users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users userItem = Users.get(position);
        holder.setUser(userItem);
        holder.addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, you trigger the method from FriendActivity to add a friend
                if(v.getContext() instanceof FriendActivity) {
                    ((FriendActivity) v.getContext()).addFriend(userItem);
                }
            }
        });
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return Users.size();
    }
}
