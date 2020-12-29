package com.ddrum.superchatvippro.view.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private AppCompatEditText edtUsername;
    private AppCompatEditText edtEmail;
    private AppCompatEditText edtPassword;
    private AppCompatEditText edtPasswordConfirm;
    private MaterialButton btnRegister;

    //Authentication
    private FirebaseAuth auth;

    //Database
    private DatabaseReference reference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        firebase();

        btnRegisterIsEnable();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClick();
            }
        });


    }

// Method

    private void btnRegisterIsEnable() {
        edtUsername.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);
        edtPasswordConfirm.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String user = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString();
            String passConfirm = edtPasswordConfirm.getText().toString();
            btnRegister.setEnabled(
                    !TextUtils.isEmpty(user) &&
                            !TextUtils.isEmpty(email) &&
                            !TextUtils.isEmpty(pass) &&
                            !TextUtils.isEmpty(passConfirm));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void registerClick() {
        String user = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString();
        String passConfirm = edtPasswordConfirm.getText().toString();

        if (!pass.equals(passConfirm)) {
            Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            edtPasswordConfirm.requestFocus();
            return;
        } else {
            if (pass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có 6 kí tự trở lên", Toast.LENGTH_SHORT).show();
                edtPassword.requestFocus();
                return;
            }
        }
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check  = task.getResult().getSignInMethods().isEmpty();
                        if(!check) {
                            Toast.makeText(RegisterActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = task.getResult().getUser().getUid();
                                        FirebaseUser currentUser = auth.getCurrentUser();

                                        createUserAndLogin(userId, user, email, pass);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                                        edtEmail.requestFocus();
                                    }
                                }
                            });

                        }
                    }
                });
    }


    private void createUserAndLogin(String userId, String username, String email, String pass) {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(pass);
        user.setPhotoUrl(Constant.DEFAULT_AVATAR);
        user.setOnline("true");
        //Add data User vào database
        reference.child(Constant.USER).child(userId).setValue(user) //Set Value chỉ đến đây thôi
                //Dòng dưới này là để check xem api call có thành công hay không
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Something error!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance(); //cái này là khai báo fỉebaseAuth, muốn đăng ký đăng nhập hay lấy thông tin user (authen) thì phải có
        reference = FirebaseDatabase.getInstance().getReference(); //Để ntn sau này tiện child nhiều bảng khác hơn
    }

    private void initView() {
        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtPasswordConfirm = findViewById(R.id.edt_password_confirm);
        btnRegister = findViewById(R.id.btn_register);
    }

}
