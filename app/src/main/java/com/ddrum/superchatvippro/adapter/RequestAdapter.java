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
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends FirebaseRecyclerAdapter<String, RequestAdapter.ViewHolder> {
    private Callback callback;
    private MainViewModel viewModel;
    private Context context;

    public void setOnCLick(Callback callback) {
        this.callback = callback;
    }

    public RequestAdapter(Context context, MainViewModel viewModel, Query ref) {
        super(String.class, R.layout.item_request, ViewHolder.class, ref);
        this.context = context;
        this.viewModel = viewModel;
    }


    @Override
    protected void populateViewHolder(ViewHolder viewHolder, String otherUserId, int position) {

        viewModel.listUser.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                HashMap<String, String> map = (HashMap<String, String>) hashMap.get(otherUserId);
                viewHolder.txtName.setText(map.get("username"));
                String url = map.get("photoUrl");
                Glide.with(context).load(url).into(viewHolder.imgAvatar);
            }
        });


        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onClickAccept(position, viewHolder.txtName.getText().toString(), otherUserId);
            }
        });
        viewHolder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickDeny(position, otherUserId);
            }
        });


    }


    public interface Callback {
        void onClickAccept(int position, String otherUserName, String otherUserId);

        void onClickDeny(int position, String otherUserId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView txtName;
        CircleImageView imgAvatar;
        MaterialButton btnAccept;
        MaterialButton btnDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDeny = itemView.findViewById(R.id.btnDeny);
        }
    }
}
