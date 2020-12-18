package com.ddrum.superchatvippro.Adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

                HashMap<String, Object> map = (HashMap<String, Object>) hashMap.get(user.getId());
                viewHolder.txtName.setText(map.get("username").toString());
                String online = map.get("online").toString();
                viewHolder.imOnline.setVisibility(online.equals("true") ? View.VISIBLE : View.INVISIBLE);

            }
        });
        viewHolder.btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onClickUnfriend(position, user.getId());
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
        void onClickUnfriend(int position, String userOtherId);
        void onItemClick(int position, String userOtherId);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtName;
        CircleImageView imgAvatar, imOnline;
        AppCompatButton btnUnfriend;
        View stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imOnline = itemView.findViewById(R.id.imgOnline);
            txtName = itemView.findViewById(R.id.txtName);
            btnUnfriend = itemView.findViewById(R.id.btn_unfriend);
            stroke = itemView.findViewById(R.id.stroke);
        }
    }


}
