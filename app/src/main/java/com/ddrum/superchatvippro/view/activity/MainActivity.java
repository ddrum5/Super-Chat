package com.ddrum.superchatvippro.view.activity;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.adapter.ViewPagerAdapter;
import com.ddrum.superchatvippro.animation.DepthPageTransformer;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.Firebase;
import com.ddrum.superchatvippro.model.Message;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.authentication.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    //Authentication
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String currentId;

    //Database
    private DatabaseReference reference;

    //View
    private AppCompatTextView txtName;
    private AppCompatTextView titleToolbar;
    private AppCompatButton btnLogout;
    private ViewPager2 viewPager;
    private AHBottomNavigation bottomNavigation;
    private CircleImageView imgAvatar;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        initView();
        firebase();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModelGetData();

        //Set User name & Avatar
        viewModel.user.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                    txtName.setText(user.getUsername());
                    Glide.with(MainActivity.this).load(user.getPhotoUrl()).into(imgAvatar);
            }
        });


        //Bottom Navigation
        navigation();

        //Info User
        avatarClick();

        //Log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        reference.child(Constant.LAST_MESSAGE).child(currentId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                showNotification("HAY", map.get("text"));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //Method
    private void showNotification(String title, String body) {
        String channelId = "hay";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }





    private void avatarClick() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoUserActivity.class);
                ActivityOptionsCompat option = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(MainActivity.this, imgAvatar, ViewCompat.getTransitionName(imgAvatar));
                startActivity(intent, option.toBundle());
            }
        });
    }


    private void navigation() {
        viewPager.setAdapter(new ViewPagerAdapter(this));
        viewPager.setPageTransformer(new DepthPageTransformer());
        viewPager.setOffscreenPageLimit(2);
        titleToolbar.setText(getString(R.string.chat));
        viewPager.setCurrentItem(0);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.chat, R.drawable.ic_navigation_chat, R.color.primary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.friends_list, R.drawable.ic_navigation_friend, R.color.primary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.people, R.drawable.ic_navigation_search, R.color.primary);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setAccentColor(getColor(R.color.primary));
        bottomNavigation.setInactiveColor(getColor(R.color.gray_dark));
        bottomNavigation.setDefaultBackgroundColor(getColor(R.color.gray_spLite));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigation.setCurrentItem(0);
                        titleToolbar.setText(getString(R.string.chat));
                        break;
                    case 1:
                        bottomNavigation.setCurrentItem(1);
                        titleToolbar.setText(getString(R.string.friends_list));
                        break;
                    case 2:
                        bottomNavigation.setCurrentItem(2);
                        titleToolbar.setText(getString(R.string.people));
                        break;
                }
            }
        });

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });


        viewModel.listReceiver.observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                int count;
                if (hashMap != null) {
                    count = hashMap.size();
                    bottomNavigation.setNotification(count + " yêu cầu", 1);
                } else {
                    bottomNavigation.setNotification("", 1);
                }
            }
        });

        viewModel.listLastMessage.observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                int count = 0;
                if (hashMap != null) {
                    for (String key : hashMap.keySet()) {
                        HashMap<String, String> map = (HashMap<String, String>) hashMap.get(key);
                        if (map.get("seen").equals("false")) {
                            count++;
                        }
                    }
                    if(count > 0 ){
                        bottomNavigation.setNotification(count+"", 0);
                    } else {
                        bottomNavigation.setNotification("", 0);
                    }
                } else {
                    bottomNavigation.setNotification("", 0);
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Super chat");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setIcon(getDrawable(R.drawable.ic_logout));
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentId = currentUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference(); //Trỏ tới database
    }

    private void viewModelGetData() {
        viewModel.getUser(reference, currentId);
        viewModel.getAllUser(reference);
        viewModel.getListSender(reference, currentId);
        viewModel.getListReceiver(reference, currentId);
        viewModel.getListFriend(reference, currentId);
        viewModel.getListLastMessage(reference, currentId);
    }

    private void initView() {
        txtName = findViewById(R.id.txtName);
        titleToolbar = findViewById(R.id.titleToolbar);
        btnLogout = findViewById(R.id.btnLogout);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        imgAvatar = findViewById(R.id.imgAvatar);
    }

    @Override
    protected void onResume() {
        Firebase.setOnlineStatus("true");
        super.onResume();

    }

    @Override
    protected void onPause() {
        reference.child(Constant.USER)
                .child(currentId)
                .child("online")
                .setValue(System.currentTimeMillis()+"");
        super.onPause();
    }

}