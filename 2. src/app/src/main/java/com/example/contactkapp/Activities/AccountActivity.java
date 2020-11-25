package com.example.contactkapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactkapp.Models.UserModel;
import com.example.contactkapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class AccountActivity extends AppCompatActivity {

    static int PREQCODE = 1;
    static int REQUESCODE = 1;

    ImageView imgAvatar;
    EditText txtName, txtPassword, txtConfirmPassword, txtOldPassword;
    Button btnUpdateInfo;
    Uri pickedImgUri;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().hide();
        findViewById(R.id.imageBack_info_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        imgAvatar = findViewById(R.id.img_info_profile);
        txtName = findViewById(R.id.txt_info_profile_name);
        txtOldPassword = findViewById(R.id.txt_info_profile_oldpassword);
        txtPassword = findViewById(R.id.txt_info_profile_newpassword);
        txtConfirmPassword = findViewById(R.id.txt_info_profile_confirm_passowrd);
        btnUpdateInfo = findViewById(R.id.btn_infor_profile_update);

        txtName.setText(currentUser.getDisplayName());
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(AccountActivity.this).load(currentUser.getPhotoUrl()).into(imgAvatar);
        }

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strOldPassword = txtOldPassword.getText().toString().trim();
                String strNewPassword = txtPassword.getText().toString().trim();
                String strConfirmPassword = txtConfirmPassword.getText().toString().trim();
                if (currentUser.getDisplayName().equals(txtName.getText().toString()) && strOldPassword.isEmpty() && pickedImgUri == null) {
                    ShowMessage("Không có gì thay đổi!");
                    return;
                }
                if (!currentUser.getDisplayName().equals(txtName.getText().toString())) {
                    UpdateInforProfile(txtName.getText().toString());
                }

                if (!strOldPassword.isEmpty()) {
                    if (strNewPassword.isEmpty() || strConfirmPassword.isEmpty()) {
                        ShowMessage("Mật khẩu mới rỗng!");
                    } else {
                        if (!strConfirmPassword.equals(strNewPassword)) {
                            ShowMessage("Mật khấu mới không trùng nhau!");
                        } else {
                            UpdatePassword(strOldPassword, strNewPassword);
                        }
                    }
                }
                if (pickedImgUri != null) {
                    updateImageUser(pickedImgUri, mAuth.getCurrentUser());
                }
            }
        });

    }

    private void UpdatePassword(String oldPassword, String newPassword) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
        currentUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        currentUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ShowMessage("Cập nhật mật khẩu thành công!");
                                        mAuth.signOut();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ShowMessage("Lỗi! Kiểm tra lại!");
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ShowMessage("Lỗi! Mật khẩu không đúng!" + e.getMessage());
            }
        });
        ;
    }

    private void UpdateInforProfile(String name) {
        if (!name.isEmpty() && name.length() >= 5) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_LONG).show();
                            UserModel userModel = new UserModel();
                            userModel.setDisplayName(currentUser.getDisplayName());

                            UpdateUserDisplayName(userModel);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Lỗi! Kiểm tra lại thông tin!", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            ShowMessage("Lỗi! Tên người dùng không đủ 5 ký tự!");
        }
    }

    private void ShowMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void updateImageUser(Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStoage = FirebaseStorage.getInstance().getReference().child("users_photos");
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        StorageReference imageFilePath = mStoage.child("image-" + currentUser.getEmail() + "-" + ts + ".jpg");
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ShowMessage("Tải hình lên thành công!");
                                    UpdateAvatar();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void UpdateAvatar() {
        DatabaseReference userItem = firebaseDatabase.getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    broccoli.getRef().child("avatar").setValue(currentUser.getPhotoUrl().toString());
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(AccountActivity.this, "Hãy cho phép truy cập bộ nhớ!", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PREQCODE);
            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            imgAvatar.setImageURI(pickedImgUri);

        }
    }

    private void UpdateUserDisplayName(UserModel userModel) {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    broccoli.getRef().child("displayName").setValue(userModel.getDisplayName());
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}