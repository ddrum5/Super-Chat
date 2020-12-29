package com.ddrum.superchatvippro.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends FirebaseRecyclerAdapter<User, FriendAdapter.ViewHolder> {
    private MainViewModel viewModel;
    private Context context;
    private Callback callback;

    public void setOnUnfriendClick(Callback callback) {
        this.callback = callback;
    }

    public FriendAdapter(Context context, MainViewModel viewModel, Query ref) {
        super(User.class, R.layout.item_friend, ViewHolder.class, ref);
        this.context = context;
        this.viewModel = viewModel;
    }


    @Override
    protected void populateViewHolder(ViewHolder viewHolder, User user, int position) {

        viewModel.listUser.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                HashMap<String, String> map = (HashMap<String, String>) hashMap.get(user.getId());
                viewHolder.txtName.setText(map.get("username"));
                viewHolder.imOnline.setVisibility(map.get("online").equals("true") ? View.VISIBLE : View.INVISIBLE);
                String url = map.get("photoUrl");
                Glide.with(context).load(url).into(viewHolder.imgAvatar);
            }
        });
        viewHolder.btnUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onClickUnFriend(position, user.getId());
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onItemClick(position, user.getId());
            }
        });

    }


    public interface Callback {
        void onClickUnFriend(int position, String userOtherId);
        void onItemClick(int position, String userOtherId);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtName;
        CircleImageView imgAvatar, imOnline;
        MaterialButton btnUnFriend;
        View stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imOnline = itemView.findViewById(R.id.imgOnline);
            txtName = itemView.findViewById(R.id.txtName);
            btnUnFriend = itemView.findViewById(R.id.btnUnFriend);
            stroke = itemView.findViewById(R.id.stroke);
        }
    }


}
