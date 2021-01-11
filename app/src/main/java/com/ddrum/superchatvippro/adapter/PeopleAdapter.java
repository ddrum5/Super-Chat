package com.ddrum.superchatvippro.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
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

public class PeopleAdapter extends FirebaseRecyclerAdapter<User, PeopleAdapter.ViewHolder> {


    private Callback callback;
    private MainViewModel mainViewModel;
    private Context context;

    public void setOnAddFriendClick(Callback callback) {
        this.callback = callback;
    }

    //hàm khoi tạo
    public PeopleAdapter(Context context, MainViewModel viewModel, Query ref) {
        super(User.class, R.layout.item_people, ViewHolder.class, ref);
        this.context = context;
        this.mainViewModel = viewModel;
    }

    //Đổ dữ liệu vào item
    @Override
    protected void populateViewHolder(ViewHolder viewHolder, User user, int position) {

        viewHolder.imgOnline.setVisibility(View.GONE);
        Glide.with(context).load(user.getPhotoUrl()).into(viewHolder.imgAvatar);
        mainViewModel.user.observe((MainActivity) context, new Observer<User>() {
            @Override
            public void onChanged(User currentUser) {
                String name = "";
                if (currentUser.getId() != null)
                    if (currentUser.getId().equals(user.getId())) {
                        viewHolder.btnAdd.setVisibility(View.INVISIBLE);
                        name = user.getUsername() + " (Bạn)";
                    } else {
                        viewHolder.btnAdd.setVisibility(View.VISIBLE);
                        name = user.getUsername();
                    }
                viewHolder.txtName.setText(name);
            }
        });

        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onClick(position, user, viewHolder.btnAdd.getText().equals("Kết bạn"));
            }
        });


        //truy vấn đến bản friend
        mainViewModel.listFriends.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null && hashMap.get(user.getId()) != null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) hashMap.get(user.getId());
                    viewHolder.imgOnline.setVisibility(user.getOnline().equals("true") ? View.VISIBLE : View.INVISIBLE);
                    viewHolder.btnAdd.setEnabled(false);
                    viewHolder.btnAdd.setText("Bạn bè");
                    viewHolder.btnAdd.setIconResource(R.drawable.ic_navigation_friend);
                    viewHolder.btnAdd.setBackgroundColor(context.getColor(R.color.gray_spLite));
                    viewHolder.btnAdd.setTextColor(context.getColor(R.color.gray_dark));
                    viewHolder.btnAdd.setIconTint(context.getColorStateList(R.color.gray_dark));
                } else {
                    viewHolder.imgOnline.setVisibility(View.INVISIBLE);
                    viewHolder.btnAdd.setEnabled(true);
                    mainViewModel.listSender.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
                        @Override
                        public void onChanged(HashMap<String, Object> hashMap) {
                            if (hashMap != null) {
                                if (hashMap.get(user.getId()) != null) {
                                    showCancelRequestButton(viewHolder);
                                } else {
                                    showRequestButton(viewHolder);
                                }
                            } else {
                                showRequestButton(viewHolder);
                            }
                        }
                    });
                }
            }
        });
    }


    private void showRequestButton(ViewHolder viewHolder) {
        viewHolder.btnAdd.setText("Kết bạn");
        viewHolder.btnAdd.setIconResource(R.drawable.ic_friend_request);
        viewHolder.btnAdd.setBackgroundColor(context.getColor(R.color.primary));
        viewHolder.btnAdd.setTextColor(context.getColor(R.color.white));
        viewHolder.btnAdd.setIconTint(context.getColorStateList(R.color.white));
    }

    private void showCancelRequestButton(ViewHolder viewHolder) {
        viewHolder.btnAdd.setText("Huỷ lời mời");
        viewHolder.btnAdd.setIconResource(R.drawable.ic_unfriend);
        viewHolder.btnAdd.setBackgroundColor(context.getColor(R.color.gray_spLite));
        viewHolder.btnAdd.setTextColor(context.getColor(R.color.gray_dark));
        viewHolder.btnAdd.setIconTint(context.getColorStateList(R.color.gray));
    }




    public interface Callback {
        void onClick(int position, User user, boolean isReceived);
    }

    //anh xa view
    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView txtName;
        CircleImageView imgAvatar, imgOnline;
        MaterialButton btnAdd;
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
