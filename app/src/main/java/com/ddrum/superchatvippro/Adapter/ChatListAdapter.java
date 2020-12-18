package com.ddrum.superchatvippro.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.library.TimeAgo;
import com.ddrum.superchatvippro.model.Message;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends FirebaseRecyclerAdapter<Message, ChatListAdapter.viewHolder> {

    private MainViewModel viewModel;
    private Context context;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }




    public ChatListAdapter(MainViewModel viewModel, Context context, Query ref) {
        super(Message.class, R.layout.item_chat, viewHolder.class, ref);
        this.viewModel = viewModel;
        this.context = context;
    }

    @Override
    protected void populateViewHolder(ChatListAdapter.viewHolder viewHolder, Message message, int position) {

        String isMe = "";

        if(message.getSender().equals(message.getReceiver())){
            isMe ="";
        } else {
            isMe = "Bạn:  ";
        }
        String name = message.getText();
        String lastMassage = isMe + name + " ∙ ";
        String time = TimeAgo.getTime(Long.parseLong(message.getTime()));

        viewHolder.txtLastMessage.setText(lastMassage);
        viewHolder.txtlastTime.setText(time);



        if(message.getSeen().equals("false")) {
            viewHolder.txtLastMessage.setTextColor(context.getColor(R.color.black));
            viewHolder.txtLastMessage.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.txtName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {

            viewHolder.txtLastMessage.setTextColor(context.getColor(R.color.gray));
            viewHolder.txtLastMessage.setTypeface(Typeface.DEFAULT);
            viewHolder.txtName.setTypeface(Typeface.DEFAULT);
        }

        viewModel.listUser.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null && hashMap.get(message.getReceiver()) != null) {
                    HashMap<String, String> map = (HashMap<String, String>) hashMap.get(message.getReceiver());
                    viewHolder.txtName.setText(map.get("username"));
                    viewHolder.imgOnline.setVisibility(map.get("online").equals("true") ? View.VISIBLE : View.INVISIBLE);
                }

            }
        });


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position, message.getReceiver());
            }
        });
    }


    public interface Callback {
        void onClick(int position, String otherUserId);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        AppCompatImageView imgOnline;
        AppCompatTextView txtName, txtLastMessage, txtlastTime;
        View stroke;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgOnline = itemView.findViewById(R.id.imgOnline);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            stroke = itemView.findViewById(R.id.stroke);
            txtlastTime = itemView.findViewById(R.id.txtLastTime);
        }
    }
}
