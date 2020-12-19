package com.ddrum.superchatvippro.view.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.ddrum.superchatvippro.Adapter.ViewPagerAdapter;
import com.ddrum.superchatvippro.Animation.DepthPageTransformer;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.authentication.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private String userId;
    private User mUser;

    //Database
    private DatabaseReference reference;

    //View
    private AppCompatTextView txtName;
    private AppCompatTextView titleToolbar;
    private AppCompatButton btnLogout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private CircleImageView imgAvatar;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        initView();
        firebase();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getUser(reference, userId); //Ở đây chú setValue của currentUser từ main,
        viewModel.getAllUser(reference);
        viewModel.getListSender(reference, userId);
        viewModel.getListFriend(reference, userId);
        // nên tất cả các Fragment kia muốn lấy thằng currentUser hì đều phải requireActivity() tức là MainActivity (Vì các Fragment khác đều là con của MainActivity)


        mUser = new User();
        //Lắng nghe currentUser thay đổi
        viewModel.currentUser.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser.setUsername(user.getUsername());
                mUser.setEmail(user.getEmail());
                txtName.setText(user.getUsername());
                ;
            }
        });
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(imgAvatar);
        }

        //Bottm Navigation
        navigation();
        //Log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        avatarClick();



    }

//Method

    private void navigation() {
//        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        viewPager.setPageTransformer(new DepthPageTransformer());
        titleToolbar.setText(getString(R.string.chat));
        viewPager.setCurrentItem(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigation.getMenu().findItem(R.id.menu_chat).setChecked(true);
                        titleToolbar.setText(getString(R.string.chat));
                        break;
                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.menu_friend).setChecked(true);
                        titleToolbar.setText(getString(R.string.friends_list));
                        break;
                    case 2:
                        bottomNavigation.getMenu().findItem(R.id.menu_people).setChecked(true);
                        titleToolbar.setText(getString(R.string.people));
                        break;
                }
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_chat:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_friend:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_people:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
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
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void avatarClick() {//
         imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_user_name);
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                window.setAttributes(windowAttributes);

                AppCompatButton btnXong = dialog.findViewById(R.id.btn_xong);
                CircleImageView imgAvatar = dialog.findViewById(R.id.img_avatar);
                AppCompatTextView tvUsername = dialog.findViewById(R.id.tv_username);
                AppCompatButton btnEdit = dialog.findViewById(R.id.btn_edit);
                AppCompatButton btnSave = dialog.findViewById(R.id.btn_save);
                AppCompatTextView tvEmail = dialog.findViewById(R.id.tv_email);
                AppCompatEditText edtInput = dialog.findViewById(R.id.edt_input);

                tvUsername.setText(mUser.getUsername());
                tvEmail.setText(mUser.getEmail());

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnEdit.setVisibility(View.GONE);
                        btnSave.setVisibility(View.VISIBLE);
                        edtInput.setText(mUser.getUsername());
                        edtInput.requestFocus();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = edtInput.getText().toString().trim();
                        if (!TextUtils.isEmpty(newName)) {
                            reference.child(Constant.USER)
                                    .child(userId)
                                    .child("username")
                                    .setValue(newName);
                            tvUsername.setText(newName);
                        } else {
                            Toast.makeText(MainActivity.this, "Ten trong", Toast.LENGTH_SHORT).show();
                        }
                        edtInput.setText("");
                        btnEdit.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.GONE);
                    }
                });


                btnXong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }



    private void firebase() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
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
                .child(userId) //Trỏ tới UserId đang đăng nhập (3idbF74gbNZ0TU6anbBfX6UiguQ2)
                .child("online")//Trỏ tiếp tới "online"
                .setValue("true"); //Set cho "online" giá trị true, tức là đang online
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.child(Constant.USER)
                .child(userId)
                .child("online")
                .setValue(System.currentTimeMillis() + "");
    }
}