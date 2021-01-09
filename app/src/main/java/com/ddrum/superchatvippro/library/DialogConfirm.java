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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DialogConfirm {


    private Context context;
    private Callback callback;


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
