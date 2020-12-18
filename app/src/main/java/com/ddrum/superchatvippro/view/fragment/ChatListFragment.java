package com.ddrum.superchatvippro.view.fragment;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.Adapter.ChatListAdapter;
import com.ddrum.superchatvippro.Adapter.OnlineAdapter;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatListFragment extends Fragment {

    private ChatListAdapter chatListAdapter;
    private OnlineAdapter onlineAdapter;

    private FirebaseUser currentUser;
    private DatabaseReference reference;

    private MainViewModel viewModel;

    private AppCompatEditText edtSearch;
    private RecyclerView rcvChatList;
    private LinearLayout btnCreateRoom;
    private RecyclerView rcvOnlineTop;
    private View stroke;


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
        onlineAdapter.setCallback(new OnlineAdapter.Callback() {
            @Override
            public void onClick(String otherUserId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("otherId", otherUserId);
                startActivity(intent);
            }
        });


//Chat List
        Query queryMessage = reference.child(Constant.LAST_MESSAGE).child(currentUser.getUid());
        chatListAdapter = new ChatListAdapter(viewModel, requireContext(), queryMessage);
        rcvChatList.setAdapter(chatListAdapter);
        rcvChatList.setLayoutManager(new LinearLayoutManager(requireContext()));


//        edtSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //Query trường tên (username)
//                Query query = reference.child(Constant.MESSAGE).child(currentUser.getUid()).orderByChild("username").startAt(s.toString()).endAt(s + "\uf8ff");
//                chatListAdapter = new ChatListAdapter(viewModel, requireContext(), query);
//                rcvChatList.setAdapter(chatListAdapter);
//                clickItemChat();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        clickItemChat();
    }



    private void clickItemChat() {
        chatListAdapter.setCallback(new ChatListAdapter.Callback() {
            @Override
            public void onClick(int position, String otherUserId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("otherId", otherUserId);
                startActivity(intent);
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
        edtSearch = view.findViewById(R.id.edtSearch);
        btnCreateRoom = view.findViewById(R.id.btn_create_room);
        rcvOnlineTop = view.findViewById(R.id.rcv_online_top);
        stroke = view.findViewById(R.id.stroke);
    }


}