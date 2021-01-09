package com.ddrum.superchatvippro.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Firebase {

    private static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    public static void changeAvatar(View view, String currentId, Uri imageUri) {
        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.show();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageRef = storageRef.child(Constant.AVATAR_FOLDER).child(currentId);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dbRef.child(Constant.USER).child(currentId).child("photoUrl").setValue(String.valueOf(uri));
                                Snackbar.make(view, "Đã thay đổi avatar", Snackbar.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Thay avatar thất bại", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        pd.setMessage("Đang thay avatar");
                    }
                });

    }

    public static void uploadImageMessage(Context context, String currentId, String otherId, Uri imageUri, String time) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.show();

        String ramdomKey= UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(Constant.IMAGE_MESSAGE_FOLDER).child(currentId).child(otherId).child(ramdomKey);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("text", url);
                                map.put("type", "image");
                                map.put("sender", currentId);
                                map.put("time", System.currentTimeMillis() + "");
                                map.put("preTime", time);
                                map.put("receiver", otherId);


                                map.put("seen", "true");
                                map.put("receiver", otherId);
                                dbRef.child(Constant.MESSAGE).child(currentId).child(otherId).push().setValue(map);
                                dbRef.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).setValue(map);

                                map.put("seen", "false");
                                map.put("receiver", currentId);
                                dbRef.child(Constant.MESSAGE).child(otherId).child(currentId).push().setValue(map);
                                dbRef.child(Constant.LAST_MESSAGE).child(otherId).child(currentId).setValue(map);

                                Toast.makeText(context, "Đã gửi ảnh", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        pd.setMessage("Đang gửi");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Gửi ảnh thất bại", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                });

    }

    public static void uploadTextMessage(String currentName, String otherName, String currentId, String otherId, String message, String time) {
        HashMap<String, String> map = new HashMap<>();
        map.put("text", message);
        map.put("type", "text");
        map.put("sender", currentId);
        map.put("time", System.currentTimeMillis() + "");
        map.put("preTime", time);






        map.put("seen", "true");
        map.put("receiver", otherId);
        map.put("name", otherName);
        dbRef.child(Constant.MESSAGE).child(currentId).child(otherId).push().setValue(map);
        dbRef.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).setValue(map);

        map.put("seen", "false");
        map.put("receiver", currentId);
        map.put("name", currentName);
        dbRef.child(Constant.MESSAGE).child(otherId).child(currentId).push().setValue(map);
        dbRef.child(Constant.LAST_MESSAGE).child(otherId).child(currentId).setValue(map);
    }

    public static void setSeenMessage(String currentId, String otherId) {
        dbRef.child(Constant.LAST_MESSAGE)
                .child(currentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(otherId)) {
                            snapshot.getRef()
                                    .child(otherId)
                                    .child("seen")
                                    .setValue("true");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    public static void setOnlineStatus(String status) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef.child(Constant.USER)
                .child(currentUser.getUid())
                .child("online")
                .setValue(status);
    }

    public static void deleteChat(String currentId, String otherId) {
        dbRef.child(Constant.MESSAGE).child(currentId).child(otherId).removeValue();
        dbRef.child(Constant.LAST_MESSAGE).child(currentId).child(otherId).removeValue();

    }



}
