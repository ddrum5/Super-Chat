package com.ddrum.superchatvippro.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.adapter.ChatAdapter;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.Firebase;
import com.ddrum.superchatvippro.library.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    //View
    private Toolbar toolbar;
    private RecyclerView rcvChatList;
    private AppCompatEditText edtInputMessage;
    private AppCompatButton btnSend;
    private CircleImageView imgAvatar;
    private AppCompatTextView tvUsername;
    private AppCompatTextView tvOnline;
    private AppCompatButton btnShowChooseImage;
    private AppCompatButton btnChooseImage;

    private String currentId;
    private String otherId;
    private MainViewModel viewModel;

    private ChatAdapter adapter;

    //Authentication
    private FirebaseAuth auth;
    private FirebaseUser user;


    //Database
    private DatabaseReference reference;
    private String time = "0";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        firebase();
        otherId = getIntent().getStringExtra("otherId");
//
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getListLastMessage(reference, otherId);
        viewModel.getAllUser(reference);
        viewModel.listUser.observe(this, new Observer<HashMap<String, Object>>() {
            @SuppressLint("ResourceType")
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    HashMap<String, String> map = (HashMap<String, String>) hashMap.get(otherId);
                    tvUsername.setText(map.get("username"));
                    tvOnline.setText(TimeAgo.getTimeAgo(map.get("online")));
                    Glide.with(ChatActivity.this).load(map.get("photoUrl")).into(imgAvatar);
                    setOnline();

                }
            }
        });
//
        Query query = reference.child(Constant.MESSAGE).child(currentId).child(otherId);
        adapter = new ChatAdapter(this,rcvChatList, viewModel, currentId, query);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcvChatList.setLayoutManager(linearLayoutManager);
        rcvChatList.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rcvChatList.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        });

        //Xét đã xem khi cuộn
        rcvChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Firebase.setSeenMessage(currentId,otherId);
            }
        });

//Lấy time stamp message trước đó
        reference.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((HashMap<String, String>) snapshot.getValue() != null) {
                    HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                    time = map.get("time");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String message = edtInputMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    Firebase.uploadTextMessage(currentId, otherId, message, time);
                    edtInputMessage.getText().clear();
                }
            }
        });
        edtInputMessage.addTextChangedListener(textWatcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition( R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });


        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });


    }

//Method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            Uri uri = data.getData();
            Firebase.uploadImageMessage(this, currentId, otherId, uri, time);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isEmpty = TextUtils.isEmpty(edtInputMessage.getText().toString());
            btnSend.setEnabled(isEmpty ? false : true);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void firebase() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference(); //Trỏ tới database
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        rcvChatList = findViewById(R.id.rcv_chat_list);
        edtInputMessage = findViewById(R.id.edt_input_message);
        btnSend = findViewById(R.id.btn_send);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tv_username);
        tvOnline = findViewById(R.id.tv_online);
        btnShowChooseImage = findViewById(R.id.btn_show_choose_image);
        btnChooseImage = findViewById(R.id.btn_choose_image);
    }

    private void setOnline() {
        reference.child(Constant.USER)
                .child(currentId)
                .child("online")
                .setValue("true");
        Firebase.setSeenMessage(currentId,otherId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnline();
    }


    @Override
    protected void onStop() {
        super.onStop();
        reference.child(Constant.USER)
                .child(currentId)
                .child("online")
                .setValue(System.currentTimeMillis() + "");

    }

}

