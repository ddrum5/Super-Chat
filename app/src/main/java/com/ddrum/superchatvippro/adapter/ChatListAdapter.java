package com.ddrum.superchatvippro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.library.TimeAgo;
import com.ddrum.superchatvippro.model.Message;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends FirebaseRecyclerAdapter<Message, ChatListAdapter.viewHolder> {

    private MainViewModel viewModel;
    private Context context;
    private Callback callback;
    private String currentId;

    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public ChatListAdapter(Context context, String currentId, MainViewModel viewModel, Query ref) {
        super(Message.class, R.layout.item_chat, viewHolder.class, ref);
        this.viewModel = viewModel;
        this.context = context;
        this.currentId = currentId;
    }

    @Override
    protected void populateViewHolder(ChatListAdapter.viewHolder viewHolder, Message message, int position) {
        String isMe = "";
        if (message.getSender().equals(currentId)) {
            isMe = "Bạn:  ";
        } else {
            isMe = "";
        }
        String lastMassage = message.getText();

        String messageText = isMe + lastMassage + " ∙ ";
        String messageImage = isMe + "Hình ảnh" + " ∙ ";
        if (message.getType().equals("text")) {
            viewHolder.txtLastMessage.setText(messageText);
        } else {
            viewHolder.txtLastMessage.setText(messageImage);
        }


        String time = TimeAgo.getTime(Long.parseLong(message.getTime()));
        viewHolder.txtLastTime.setText(time);


        if (message.getSeen().equals("false")) {
            viewHolder.txtLastMessage.setTextColor(context.getColor(R.color.black));
            viewHolder.txtLastMessage.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.txtName.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.dotSeen.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtLastMessage.setTextColor(context.getColor(R.color.gray));
            viewHolder.txtLastMessage.setTypeface(Typeface.DEFAULT);
            viewHolder.txtName.setTypeface(Typeface.DEFAULT);
            viewHolder.dotSeen.setVisibility(View.GONE);
        }

        viewModel.listUser.observe((MainActivity) context, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null && hashMap.get(message.getReceiver()) != null) {
                    HashMap<String, String> map = (HashMap<String, String>) hashMap.get(message.getReceiver());
                    viewHolder.txtName.setText(map.get("username"));
                    viewHolder.imgOnline.setVisibility(map.get("online").equals("true") ? View.VISIBLE : View.INVISIBLE);
                    String url = map.get("photoUrl");
                    Glide.with(context).load(url).into(viewHolder.imgAvatar);
                }

            }
        });

        viewHolder.itemMainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(message.getReceiver());
            }
        });

        viewBinderHelper.bind(viewHolder.swipeRevealLayout, message.getReceiver());

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDeleteClick(message.getReceiver());
            }
        });



    }


    public interface Callback {
        void onClick(String otherUserId);
        void onDeleteClick(String otherUserId);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar, imgOnline;
        AppCompatTextView txtName, txtLastMessage, txtLastTime, btnDelete;
        View stroke, dotSeen;
        SwipeRevealLayout swipeRevealLayout;
        LinearLayout itemMainContent;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgOnline = itemView.findViewById(R.id.imgOnline);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            stroke = itemView.findViewById(R.id.stroke);
            dotSeen = itemView.findViewById(R.id.dotSeen);
            txtLastTime = itemView.findViewById(R.id.txtLastTime);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            itemMainContent = itemView.findViewById(R.id.item_main_content);

        }
    }

}
