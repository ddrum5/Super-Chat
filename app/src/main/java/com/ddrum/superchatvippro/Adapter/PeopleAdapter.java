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

public class PeopleAdapter extends FirebaseRecyclerAdapter<User, PeopleAdapter.ViewHolder> {


    private Callback callback;

    private MainViewModel mainViewModel;
    private Context context;

    private String currentId;

    public void setOnAddFriendClick(Callback callback) {
        this.callback = callback;
    }

    public PeopleAdapter(Context context, MainViewModel viewModel, Query ref) {
        super(User.class, R.layout.item_people, ViewHolder.class, ref);
        this.context = context;
        this.mainViewModel = viewModel;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, User user, int position) {

        viewHolder.txtName.setText(user.getUsername());
        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onClick(position, user, viewHolder.btnAdd.getText().equals("Kết bạn"));
            }
        });


        mainViewModel.currentUser.observe((MainActivity) context, new Observer<User>() {
            @Override
            public void onChanged(User currentUser) {
                if (currentUser.getId().equals(user.getId())) {
                    currentId = currentUser.getId();
                    viewHolder.btnAdd.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.btnAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        mainViewModel.listSender.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                //:)))
                if (hashMap != null) {
                    if (hashMap.get(user.getId()) != null) {
                        viewHolder.btnAdd.setText("Huỷ lời mời"); //Lát nữa vứt vào string nhé
                    } else {
                        viewHolder.btnAdd.setText("Kết bạn");
                    }
                } else {
                    viewHolder.btnAdd.setText("Kết bạn");
                }
            }
        });

        mainViewModel.listFriends.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) hashMap.get(user.getId());
                    if (map != null && (map.get("id").equals(user.getId()) || user.getId().equals(currentId))) {
                        viewHolder.btnAdd.setVisibility(View.INVISIBLE);
                        viewHolder.imgOnline.setVisibility(user.getOnline().equals("true") ? View.VISIBLE : View.INVISIBLE);
                    } else {
                        viewHolder.btnAdd.setVisibility(user.getId().equals(currentId) ? View.INVISIBLE : View.VISIBLE);
                        viewHolder.imgOnline.setVisibility(View.INVISIBLE);
                    }
                } else {
                    viewHolder.imgOnline.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    public interface Callback {
        void onClick(int position, User user, boolean isReceived);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView txtName;
        AppCompatImageView imgOnline;
        CircleImageView imgAvatar;
        AppCompatButton btnAdd;
        View stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOnline = itemView.findViewById(R.id.imgOnline);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            stroke = itemView.findViewById(R.id.stroke);
        }
    }


}
