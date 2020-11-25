package com.example.contactkapp.Activities.ui.settings;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactkapp.Activities.AccountActivity;
import com.example.contactkapp.Activities.LoginActivity;
import com.example.contactkapp.Activities.MainActivity;
import com.example.contactkapp.Activities.RegisterActivity;
import com.example.contactkapp.Activities.Update_Item_Activity;
import com.example.contactkapp.Activities.ui.qrcode.QRCodeViewModel;
import com.example.contactkapp.Models.UserModel;
import com.example.contactkapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingsFragment extends Fragment {

    private static final int PREQCODE = 2;
    private static final int REQUESCODE = 2;

    private SettingsViewModel settingsViewModel;
    private CardView btn_account, btn_item, btn_logout;
    private Intent AccountActivity;
    private ImageView btn_setting_img_background;
    private ImageView img_setting_background;

    Uri pickedImgUri = null;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String imageBackground;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        btn_account = root.findViewById(R.id.card_profile_account);
        btn_item = root.findViewById(R.id.card_profile_item);
        btn_logout = root.findViewById(R.id.card_profile_logout);
        btn_setting_img_background = root.findViewById(R.id.btn_setting_img_background);
        img_setting_background = root.findViewById(R.id.img_setting_background);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountActivity = new Intent(getActivity(), AccountActivity.class);
                startActivity(AccountActivity);
            }
        });

        btn_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountActivity = new Intent(getActivity(), Update_Item_Activity.class);
                startActivity(AccountActivity);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Bạn muốn đăng xuất tài khoản?").setCancelable(false)
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogoutAccount();
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        setupPopupImageClick();

            LoadUserItem();



        return root;
    }


    private void LoadUserItem() {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {

                    imageBackground = broccoli.child("backgroundUser").getValue(String.class);

                    Glide.with(getActivity()).load(imageBackground).into(img_setting_background);

                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void LogoutAccount() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void UpdateUserBackground() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        StorageReference imageFilePath = storageReference.child("image-background-" + currentUser.getEmail() + ts + ".jpg");

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageDownloadLink = uri.toString();
                        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");

                        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
                        updateUserItemQuery.keepSynced(false);
                        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot broccoli : snapshot.getChildren()) {
                                    broccoli.getRef().child("backgroundUser").setValue(imageDownloadLink);
                                }
                                Toast.makeText(getContext(), "Cập nhật ảnh bìa thành công!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Lỗi! Cập nhật không thành công!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void setupPopupImageClick() {
        btn_setting_img_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PREQCODE);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            img_setting_background.setImageURI(pickedImgUri);
            UpdateUserBackground();
        }
    }
}