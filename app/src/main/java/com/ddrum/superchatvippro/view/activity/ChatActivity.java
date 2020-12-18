package com.ddrum.superchatvippro.view.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.ddrum.superchatvippro.Adapter.ChatAdapter;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
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

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rcvChatList;
    private AppCompatEditText edtInputMessage;
    private AppCompatButton btnSend;

    private String otherId;
    private MainViewModel viewModel;

    private ChatAdapter adapter;

    //Authentication
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String currentId;

    //Database
    private DatabaseReference reference;
    private String preTime="0";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        firebase();
        otherId = getIntent().getStringExtra("otherId");
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
//
        viewModel.getAllUser(reference);
        viewModel.listUser.observe(this, new Observer<HashMap<String, Object>>() {
            @SuppressLint("ResourceType")
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) hashMap.get(otherId);
                    toolbar.setTitle((String) map.get("username"));
                    toolbar.setSubtitle(TimeAgo.getTimeAgo((String) map.get("online")));

                }
            }
        });
//
        Query query = reference.child(Constant.MESSAGE).child(currentId).child(otherId);
        adapter = new ChatAdapter(currentId, query);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcvChatList.setLayoutManager(linearLayoutManager);
        rcvChatList.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rcvChatList.smoothScrollToPosition(adapter.getItemCount());
            }
        });
//
        reference.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((HashMap<String, String>) snapshot.getValue() != null) {
                    HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                    preTime = map.get("time");
                } else {
                    preTime = "0";
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
                String message = edtInputMessage.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("text", message);
                    map.put("sender", currentId);
                    map.put("type", "text");
                    map.put("time", System.currentTimeMillis() + "");
                    map.put("preTime", preTime == null ? "0" : preTime);

                    reference.child(Constant.MESSAGE).child(currentId).child(otherId).push().setValue(map);
                    reference.child(Constant.MESSAGE).child(otherId).child(currentId).push().setValue(map);

                    map.put("receiver", otherId);
                    map.put("seen", "true");
                    reference.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).setValue(map);
                    map.put("receiver", currentId);
                    map.put("seen", "false"); 
                    reference.child(Constant.LAST_MESSAGE).child(otherId).child(currentId).setValue(map);
                    edtInputMessage.getText().clear();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

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

    }



    @Override
    protected void onResume() {
        super.onResume();
        reference.child(Constant.USER)
                .child(currentId)
                .child("online")
                .setValue("true");

        reference.child(Constant.LAST_MESSAGE)
                .child(currentId).child(otherId)
                .child("seen")
                .setValue("true");


    }
//
    @Override
    protected void onStop() {
        super.onStop();
        reference.child(Constant.USER)
                .child(currentId)
                .child("online")
                .setValue(System.currentTimeMillis() +"");
    }
}

