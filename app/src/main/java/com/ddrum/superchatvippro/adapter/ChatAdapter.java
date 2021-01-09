package com.ddrum.superchatvippro.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.library.TimeAgo;
import com.ddrum.superchatvippro.model.Message;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class ChatAdapter extends FirebaseRecyclerAdapter<Message, ChatAdapter.ViewHolder> {

    private String currentId;
    private Context context;
    private MainViewModel viewModel;

    private View oldTime;

    private Callback callback;



    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public ChatAdapter(Context context, MainViewModel viewModel, String currentId, Query ref) {
        super(Message.class, R.layout.item_message, ViewHolder.class, ref);
        this.currentId = currentId;
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Message message, int position) {
        viewHolder.tvSendMessageRight.setVisibility(View.GONE);
        viewHolder.tvSendMessageLeft.setVisibility(View.GONE);
        viewHolder.imgSendImageRight.setVisibility(View.GONE);
        viewHolder.imgSendImageLeft.setVisibility(View.GONE);
        viewHolder.tvSeen.setVisibility(View.VISIBLE);


        if (message.getType().equals("text")) {
            viewHolder.imgSendImageRight.setVisibility(View.GONE);
            viewHolder.imgSendImageLeft.setVisibility(View.GONE);
            if (message.getSender().equals(currentId)) {
                viewHolder.tvSendMessageRight.setVisibility(View.VISIBLE);
                viewHolder.tvSendMessageRight.setText(message.getText());

            } else {
                viewHolder.tvSendMessageLeft.setVisibility(View.VISIBLE);
                viewHolder.tvSendMessageLeft.setText(message.getText());
            }
        } else {
            viewHolder.tvSendMessageRight.setVisibility(View.GONE);
            viewHolder.tvSendMessageLeft.setVisibility(View.GONE);

            if (message.getSender().equals(currentId)) {
                viewHolder.imgSendImageRight.setVisibility(View.VISIBLE);
                Glide.with((ChatActivity) context).load(message.getText()).into(viewHolder.imgSendImageRight);

            } else {
                viewHolder.imgSendImageLeft.setVisibility(View.VISIBLE);
                Glide.with((ChatActivity) context).load(message.getText()).into(viewHolder.imgSendImageLeft);
            }

        }


        //seen
        viewModel.listLastMessage.observe((ChatActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    if (hashMap.get(currentId) != null) {
                        HashMap<String, String> map = (HashMap<String, String>) hashMap.get(currentId);
                        if (position == getItemCount() - 1) {
                            if (map.get("seen").equals("true")) {
                                if (message.getSender().equals(currentId)) {
                                    viewHolder.tvSeen.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.tvSeen.setVisibility(View.GONE);
                                }
                            } else {
                                viewHolder.tvSeen.setVisibility(View.GONE);
                            }
                        } else {
                            viewHolder.tvSeen.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        //Set time
        long preLastTime = Long.parseLong(message.getPreTime());
        long lastTime = Long.parseLong(message.getTime());

        String time = TimeAgo.getTime(lastTime);
        if (lastTime - preLastTime > 60000 * 10) {
            viewHolder.tvTime.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvTime.setVisibility(View.GONE);
        }
        viewHolder.tvTime.setText(time);

        //Xem thời gian tin nhắn khi click
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldTime != null) {
                    oldTime.setVisibility(View.GONE);
                    if (lastTime - preLastTime < 60000 * 10) {
                        viewHolder.tvTime.setVisibility(View.VISIBLE);
                        if (oldTime.equals(viewHolder.tvTime)) {
                            oldTime.setVisibility(View.GONE);
                        }
                        oldTime = viewHolder.tvTime;
                    }
                } else {
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
        private AppCompatTextView tvSendMessageLeft, tvSendMessageRight;
        private AppCompatTextView tvTime, tvSeen;
        private AppCompatImageView imgSendImageLeft, imgSendImageRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSendMessageLeft = itemView.findViewById(R.id.tv_send_message_left);
            tvSendMessageRight = itemView.findViewById(R.id.tv_send_message_right);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvSeen = itemView.findViewById(R.id.tv_seen);
            imgSendImageLeft = itemView.findViewById(R.id.img_send_image_left);
            imgSendImageRight = itemView.findViewById(R.id.img_send_image_right);
        }
    }

}
