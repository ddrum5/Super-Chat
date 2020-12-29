package com.ddrum.superchatvippro.library;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class DialogConfirm extends AlertDialog {

    AlertDialog dialog;
    Callback callback;
    Context context;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public DialogConfirm(@NonNull Context context) {
        super(context);
        this.context = context;


    }

    public void build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Có", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick();
            }
        });

        builder.setNegativeButton("Không", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick();
            }
        });
        dialog = builder.create();
    }

    public interface Callback {
        void onClick();
    }

}
