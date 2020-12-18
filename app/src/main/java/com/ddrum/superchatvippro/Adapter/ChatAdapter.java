package com.ddrum.superchatvippro.Adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.TimeAgo;
import com.ddrum.superchatvippro.model.Message;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChatAdapter extends FirebaseRecyclerAdapter<Message, ChatAdapter.ViewHolder> {

    private String currentId;

    private View oldTime;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public ChatAdapter(String currentId, Query ref) {
        super(Message.class, R.layout.item_message, ViewHolder.class, ref);
        this.currentId = currentId;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Message message, int position) {
        viewHolder.tvSendMessageRight.setVisibility(View.GONE);
        viewHolder.tvSendMessageLeft.setVisibility(View.GONE);
        if (message.getSender().equals(currentId)) {
            viewHolder.tvSendMessageRight.setVisibility(View.VISIBLE);
            viewHolder.tvSendMessageRight.setText(message.getText());
        } else {
            viewHolder.tvSendMessageLeft.setVisibility(View.VISIBLE);
            viewHolder.tvSendMessageLeft.setText(message.getText());
        }

        //Set time
        long preLastTime = Long.parseLong(message.getPreTime());
        long lastTime = Long.parseLong(message.getTime());

        if (lastTime - preLastTime > 60000 * 3) {
            String time = TimeAgo.getTime(lastTime);
            viewHolder.tvTime.setVisibility(View.VISIBLE);
            viewHolder.tvTime.setText(time);
        } else {
            viewHolder.tvTime.setVisibility(View.GONE);
        }
        //Callback
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callback.onClick(); //cai nay la cai gi day viet chơi :))
                if (oldTime != null) oldTime.setVisibility(View.GONE);
                if ((oldTime == null || !oldTime.equals(viewHolder.tvTime)) && lastTime - preLastTime < 60000 * 3) {
                    viewHolder.tvTime.setVisibility(View.VISIBLE);
                    oldTime = viewHolder.tvTime;
                }

            }
        });
    }


    public interface Callback {
        void onClick();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tvSendMessageLeft;
        private AppCompatTextView tvSendMessageRight;
        private AppCompatTextView tvTime, txtSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSendMessageLeft = itemView.findViewById(R.id.tv_send_message_left);
            tvSendMessageRight = itemView.findViewById(R.id.tv_send_message_right);
            tvTime = itemView.findViewById(R.id.tv_time);
            txtSeen = itemView.findViewById(R.id.txtSeen);
        }
    }


}
