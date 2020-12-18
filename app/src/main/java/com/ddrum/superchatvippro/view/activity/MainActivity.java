package com.ddrum.superchatvippro.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.Adapter.ViewPagerAdapter;
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
    private ViewPagerAdapter viewPagerAdapter;


    //Authentication
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userId;

    //Database
    private DatabaseReference reference;

    //View
    private AppCompatTextView txtName;
    private AppCompatTextView titleToolbar;
    private AppCompatButton btnLogout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;
    private CircleImageView imgAvatar;


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

        //Lắng nghe currentUser thay đổi
        viewModel.currentUser.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                txtName.setText(user.getUsername());
            }
        });
        if(currentUser.getPhotoUrl() != null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(imgAvatar);
        }







        //Bottm Navigation
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        titleToolbar.setText(getString(R.string.chat));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
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

            @Override
            public void onPageScrollStateChanged(int state) {
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


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
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