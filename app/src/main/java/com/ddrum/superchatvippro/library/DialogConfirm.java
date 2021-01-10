package com.ddrum.superchatvippro.library;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.view.activity.InfoUserActivity;
import com.ddrum.superchatvippro.view.authentication.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DialogConfirm {


    private Context context;
    private Callback callback;


    private AppCompatEditText edtOldPassword;
    private AppCompatEditText edtNewPassword;
    private AppCompatEditText edtPasswordConfirm;
    private MaterialButton btnChangePassword;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public DialogConfirm(Context context) {
        this.context = context;
    }


    public void openSimpleDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openChangePasswordDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        edtOldPassword = view.findViewById(R.id.edt_old_password);
        edtNewPassword = view.findViewById(R.id.edt_password);
        edtPasswordConfirm = view.findViewById(R.id.edt_password_confirm);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(edtOldPassword, edtNewPassword, edtPasswordConfirm);
            }
        });
        edtOldPassword.addTextChangedListener(textWatcher);
        edtOldPassword.addTextChangedListener(textWatcher);
        edtOldPassword.addTextChangedListener(textWatcher);

    }

    private void changePassword(AppCompatEditText edtOld, AppCompatEditText edtNewPass, AppCompatEditText edtNewPassConfirm  ) {
        String oldPass  = edtOldPassword.getText().toString().trim();
        String newPass  = edtNewPassword.getText().toString().trim();
        String newPassConfirm  = edtPasswordConfirm.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(newPass.equals(newPassConfirm)) {
                            if(newPass.length() < 6) {
                                Toast.makeText(context, "Mật khẩu phải có 6 kí tự trở lên", Toast.LENGTH_SHORT).show();
                            } else {
                                user.updatePassword(newPassConfirm)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                edtOld.setText("");
                                                edtNewPass.setText("");
                                                edtNewPassConfirm.setText("");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        } else {
                            edtNewPassConfirm.setError("Nhập lại mật khẩu không đúng");
                            edtNewPassConfirm.requestFocus();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        edtOld.setError("Mật khẩu cũ không đúng");
                        edtOld.requestFocus();
                    }
                });


    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String oldPass  = edtOldPassword.getText().toString().trim();
            String newPass  = edtNewPassword.getText().toString().trim();
            String newPassConfirm = edtPasswordConfirm.getText().toString().trim();
            boolean isEmpty = false;
            if (! (TextUtils.isEmpty(oldPass) && TextUtils.isEmpty(newPass) && TextUtils.isEmpty(newPassConfirm)) ){
                isEmpty = true;
            }
            btnChangePassword.setEnabled(isEmpty);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };






    public void openResetPasswordDiaLog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reset_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AppCompatEditText edtEmail = view.findViewById(R.id.edt_email);
        MaterialButton btnResetPassword = view.findViewById(R.id.btnResetPassword);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                resetPassword(email, view, edtEmail);
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isEmpty = TextUtils.isEmpty(edtEmail.getText().toString().trim());
                btnResetPassword.setEnabled(!isEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void resetPassword(String email, View view, AppCompatEditText editText) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (EmailValidator(email)) {
            auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (check) {
                                Snackbar.make(view, "Email Không tồn tại ", Snackbar.LENGTH_LONG).show();
                                return;
                            } else {
                                auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar.make(view, "Đã gửi đường dẫn đến Email của bạn", Snackbar.LENGTH_LONG).show();
                                                    editText.setText("");
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }

                        }
                    });
        } else {
            Snackbar.make(view, "Email không đúng định dạng", Snackbar.LENGTH_LONG).show();
        }
    }


    public interface Callback {
        void onClick();
    }

    private boolean EmailValidator(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";//
        Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(email);
        return matcher.matches();
    }

}
