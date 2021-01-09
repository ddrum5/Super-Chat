package com.ddrum.superchatvippro.view.fragment;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.adapter.ChatListAdapter;
import com.ddrum.superchatvippro.adapter.OnlineAdapter;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.DialogConfirm;
import com.ddrum.superchatvippro.library.Firebase;
import com.ddrum.superchatvippro.service.Notification;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.ddrum.superchatvippro.view.activity.InfoUserActivity;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Random;

public class

ChatListFragment extends Fragment {

    private ChatListAdapter chatListAdapter;
    private OnlineAdapter onlineAdapter;

    private FirebaseUser currentUser;
    private DatabaseReference reference;

    private MainViewModel viewModel;

    private AppCompatEditText edtSearch;
    private RecyclerView rcvChatList;
    private RecyclerView rcvOnlineTop;


    public static ChatListFragment newInstance() {

        Bundle args = new Bundle();

        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        initView(view);
        initDatabase();


//Online top
        Query queryFriend = reference.child(Constant.FRIEND).child(currentUser.getUid());
        onlineAdapter = new OnlineAdapter(requireContext(), viewModel, queryFriend);
        rcvOnlineTop.setAdapter(onlineAdapter);



//Chat List
        Query queryMessage = reference.child(Constant.LAST_MESSAGE).child(currentUser.getUid());
        chatListAdapter = new ChatListAdapter(requireContext(), currentUser.getUid(), viewModel, queryMessage);
        rcvChatList.setAdapter(chatListAdapter);
        rcvChatList.setLayoutManager(new LinearLayoutManager(requireContext()));


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Query query = reference.child(Constant.LAST_MESSAGE)
                                        .child(currentUser.getUid())
                                        .orderByChild("name").startAt(s.toString()).endAt(s + "\uf8ff");
                chatListAdapter = new ChatListAdapter(requireContext(), currentUser.getUid(), viewModel,  query);
                rcvChatList.setAdapter(chatListAdapter);
                itemClick(view);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        itemClick(view);

    }

    private void itemClick(View view){
        onlineAdapter.setCallback(new OnlineAdapter.Callback() {
            @Override
            public void onClick(String otherUserId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("otherId", otherUserId);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


            }
        });
        chatListAdapter.setCallback(new ChatListAdapter.Callback() {
            @Override
            public void onClick( String otherUserId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("otherId", otherUserId);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onDeleteClick(String otherUserId) {
                DialogConfirm dialog = new DialogConfirm(requireContext());
                dialog.openSimpleDialog("Bạn có chắc chắn muốn xoá tin nhắn không?");
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onClick() {
                        Firebase.deleteChat(currentUser.getUid(), otherUserId);
                    }
                });
            }
        });
    }


    private void initDatabase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    private void initView(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        rcvChatList = view.findViewById(R.id.rcv_chat_list);
        rcvOnlineTop = view.findViewById(R.id.rcv_online_top);
    }





}