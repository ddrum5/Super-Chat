package com.ddrum.superchatvippro.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.DialogConfirm;
import com.ddrum.superchatvippro.library.Firebase;
import com.ddrum.superchatvippro.library.Uti;
import com.ddrum.superchatvippro.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoUserActivity extends AppCompatActivity {
    private CircleImageView imgAvatar;
    private AppCompatEditText edtEditUserName;
    private AppCompatTextView tvUserName;
    private AppCompatTextView tvEmail;
    private AppCompatButton btnBack;
    private AppCompatButton btnSave;
    private Toolbar toolbar;
    LinearLayout linearLayout;

    private MainViewModel viewModel;

    private DatabaseReference reference;
    private FirebaseUser currentUer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        initView();


        reference = FirebaseDatabase.getInstance().getReference();
        currentUer = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getUser(reference, currentUer.getUid());

        viewModel.user.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                tvUserName.setText(user.getUsername());
                tvEmail.setText(user.getEmail());
                Glide.with(InfoUserActivity.this).load(user.getPhotoUrl()).into(imgAvatar);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarClick();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoUserActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Method
    private void avatarClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Chọn một ảnh"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            DialogConfirm dialogConfirm = new DialogConfirm(InfoUserActivity.this);
            dialogConfirm.openSimpleDialog("Bạn có muốn thay ảnh không?");
            dialogConfirm.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onClick() {
                    imgAvatar.setImageURI(uri);
                    Firebase.changeAvatar(linearLayout, currentUer.getUid(), uri);
                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_user_name:
                changeUserNameClick();
                return true;
            case R.id.menu_change_password:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeUserNameClick() {
        tvUserName.setVisibility(View.GONE);
        tvEmail.setVisibility(View.GONE);
        edtEditUserName.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        edtEditUserName.setText(tvUserName.getText());
        edtEditUserName.requestFocus();
        Uti.showKeyboard(this);

        edtEditUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = edtEditUserName.getText().toString().trim();
                btnSave.setEnabled(!TextUtils.isEmpty(text));
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtEditUserName.getText().toString().trim();

                if (newName.equals(tvUserName.getText())) {
                    updateUI();
                    Uti.hideKeyboard(InfoUserActivity.this);
                } else if (newName.length() > 20) {
                    edtEditUserName.setError("Tên bạn quá dài");
                } else {
                    DialogConfirm dialog = new DialogConfirm(InfoUserActivity.this);
                    dialog.openSimpleDialog("Bạn có chắc chắn muốn đổi tên?");
                    dialog.setCallback(new DialogConfirm.Callback() {
                        @Override
                        public void onClick() {
                            changeName(newName);
                            Uti.hideKeyboard(InfoUserActivity.this);
                            updateUI();
                            Snackbar.make(linearLayout, "Đã đổi tên", Snackbar.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    private void updateUI() {
        btnSave.setVisibility(View.GONE);
        edtEditUserName.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
        tvEmail.setVisibility(View.VISIBLE);
    }

    private void changeName(String newName) {
        reference.child(Constant.USER)
                .child(currentUer.getUid())
                .child("username")
                .setValue(newName);
    }


    private void initView() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtEditUserName = findViewById(R.id.edtEditUserName);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmailInfo);
        btnSave = findViewById(R.id.btnSave);
        linearLayout = findViewById(R.id.activity_info_user);
        btnBack = findViewById(R.id.btnBack);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    protected void onResume() {
        Firebase.setOnlineStatus("true");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Firebase.setOnlineStatus(System.currentTimeMillis() + "");
        super.onPause();
    }
}
