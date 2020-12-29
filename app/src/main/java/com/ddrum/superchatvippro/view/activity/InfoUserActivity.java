package com.ddrum.superchatvippro.view.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.library.DialogConfirm;
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
    private MaterialButton btnChange;
    private AppCompatButton btnBack;
    LinearLayout linearLayout;

    private MainViewModel viewModel;

    private DatabaseReference reference;
    private FirebaseUser currentUer;
    private AppCompatButton btnDone;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        initView();

        reference = FirebaseDatabase.getInstance().getReference();
        currentUer = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getUser(reference, currentUer.getUid());

        viewModel.currentUser.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                tvUserName.setText(user.getUsername());
                tvEmail.setText(user.getEmail());
                Glide.with(InfoUserActivity.this).load(user.getPhotoUrl()).into(imgAvatar);
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changUserName();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });







    }

    private void changUserName() {
        btnChange.setVisibility(View.GONE);
        tvUserName.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
        edtEditUserName.setVisibility(View.VISIBLE);
        edtEditUserName.setText(tvUserName.getText());
        edtEditUserName.requestFocus();
        showKeyboard();

        edtEditUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = edtEditUserName.getText().toString().trim();
                btnDone.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtEditUserName.getText().toString().trim();

                if (newName.equals(tvUserName.getText())) {
                    updateUI();
                } else if (newName.length() > 20) {
                    edtEditUserName.setError("Tên bạn quá dài");
                    ;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoUserActivity.this);
                    builder.setMessage("Bạn có muốn đổi tên tên không?");
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setName(newName);
                            closeKeyboard();
                            updateUI();
                            Snackbar.make(linearLayout, "Đã đổi tên", Snackbar.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });
    }

    private void hideSoftKeybroad() {
        View view = InfoUserActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(InfoUserActivity.this.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void updateUI() {
        btnDone.setVisibility(View.GONE);
        edtEditUserName.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
        btnChange.setVisibility(View.VISIBLE);
    }

    private void setName(String newName) {
        reference.child(Constant.USER)
                .child(currentUer.getUid())
                .child("username")
                .setValue(newName);
    }


    private void initView() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtEditUserName = findViewById(R.id.edtEditUserName);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        btnChange = findViewById(R.id.btn_change);
        btnDone = findViewById(R.id.btnDone);
        linearLayout = findViewById(R.id.activity_info_user);
        btnBack = findViewById(R.id.btnBack);
    }
}
