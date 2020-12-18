package com.ddrum.superchatvippro.Adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.model.UserId;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineAdapter extends FirebaseRecyclerAdapter<UserId, OnlineAdapter.ViewHolder> {
    private Context context;
    private MainViewModel viewModel;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }



    public OnlineAdapter(Context context, MainViewModel viewModel, Query ref) {
        super(UserId.class, R.layout.item_online, ViewHolder.class, ref);
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    protected void populateViewHolder(OnlineAdapter.ViewHolder viewHolder, UserId userId, int i) {
        viewModel.listUser.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if(hashMap.get(userId.getId()) != null){
                    HashMap<String, String> map = (HashMap<String, String> ) hashMap.get(userId.getId());
                    viewHolder.txtName.setText(map.get("username"));
                    viewHolder.imgOnline.setVisibility(map.get("online").equals("true") ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(userId.getId());
            }
        });
    }


    public interface Callback{
        void onClick (String otherUserId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        AppCompatImageView imgOnline;
        AppCompatTextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgOnline = itemView.findViewById(R.id.imgOnline);
            txtName = itemView.findViewById(R.id.tv_name);
        }
    }
}
