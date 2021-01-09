package com.ddrum.superchatvippro.view.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainViewModel extends ViewModel {

    public MutableLiveData<User> user = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Object>> listUser = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Object>> listSender = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Object>> listReceiver = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Object>> listFriends = new MutableLiveData<>();
    public MutableLiveData<HashMap<String, Object>> listLastMessage = new MutableLiveData<>();


    public void getUser(DatabaseReference reference, String userId) {
        reference.child(Constant.USER).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =  snapshot.getValue(User.class);
                        MainViewModel.this.user.setValue(user); //Chính nó
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

    }

    public void getAllUser(DatabaseReference reference) {
        reference.child(Constant.USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                listUser.setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getListSender(DatabaseReference reference, String userId) {
        reference.child(Constant.SENDER).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                listSender.setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getListReceiver(DatabaseReference reference, String userId) {
        reference.child(Constant.RECEIVER).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                listReceiver.setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getListFriend(DatabaseReference reference, String userId) {
        reference.child(Constant.FRIEND).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                listFriends.setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getListLastMessage(DatabaseReference reference, String userId) {
        reference.child(Constant.LAST_MESSAGE).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                listLastMessage.setValue(map);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



}
