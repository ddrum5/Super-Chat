package com.ddrum.superchatvippro.view.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.authentication.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    //Authentication
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String currentId;
    private User mUser;

    //Database
    private DatabaseReference reference;

    //View
    private AppCompatTextView txtName;
    private AppCompatTextView titleToolbar;
    private AppCompatButton btnLogout;
    private ViewPager2 viewPager;
    private AHBottomNavigation bottomNavigation;
    private CircleImageView imgAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        //
        initView();
        firebase();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getUser(reference, currentId); //Ở đây chú setValue của currentUser từ main,
        viewModel.getAllUser(reference);
        viewModel.getListSender(reference, currentId);
        viewModel.getListReceiver(reference, currentId);
        viewModel.getListFriend(reference, currentId);
        // nên tất cả các Fragment kia muốn lấy thằng currentUser hì đều phải requireActivity() tức là MainActivity (Vì các Fragment khác đều là con của MainActivity)


        mUser = new User();
        //Set User name & Avatar
        viewModel.currentUser.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser.setUsername(user.getUsername());
                mUser.setEmail(user.getEmail());
                if (user.getUsername() == null) {
                    User nUser = new User();
                    nUser.setId(currentId);
                    nUser.setUsername(currentUser.getDisplayName());
                    nUser.setEmail(currentUser.getEmail());
                    nUser.setOnline("true");
                    nUser.setPhotoUrl(currentUser.getPhotoUrl() + "");
                    nUser.setPassword("");
                    reference.child(Constant.USER).child(currentId).setValue(nUser);
                }
                txtName.setText(user.getUsername());
                Glide.with(MainActivity.this).load(user.getPhotoUrl()).into(imgAvatar);

            }
        });


        //Bottm Navigation
        navigation();
        //Log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

//        imgAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent.createChooser(intent, "Chọn một ảnh"), 1);
//            }
//        });
        avatarClick();

    }

    //Method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imgAvatar.setImageURI(uri);
            Firebase.setPhotoUrlForUser(this,currentId, uri);
        }
    }

    private void avatarClick() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoUserActivity.class);
                startActivity(intent);
                overridePendingTransition( R.anim.zoom_in, R.anim.static_animation);
            }
        });
    }

    //Method
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

//        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                return  true;
//            }
//        });

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
        super.onResume();
        reference.child(Constant.USER)
                .child(currentId) //Trỏ tới UserId đang đăng nhập (3idbF74gbNZ0TU6anbBfX6UiguQ2)
                .child("online")//Trỏ tiếp tới "online"
                .setValue("true"); //Set cho "online" giá trị true, tức là đang online
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