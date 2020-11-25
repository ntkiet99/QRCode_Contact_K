package com.example.contactkapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactkapp.Models.UserModel;
import com.example.contactkapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class Update_Item_Activity extends AppCompatActivity {

    EditText txt_item_phone, txt_item_gmail, txt_item_zalo, txt_item_facebook, txt_item_instagram, txt_item_tiktok, txt_item_twitter, txt_item_wechat, txt_item_github;
    Button btnUpdate;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__item_);
        getSupportActionBar().hide();
        findViewById(R.id.imageBack_update_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        txt_item_phone = findViewById(R.id.txt_item_phone);
        txt_item_gmail = findViewById(R.id.txt_item_gmail);
        txt_item_zalo = findViewById(R.id.txt_item_zalo);
        txt_item_facebook = findViewById(R.id.txt_item_facebook);
        txt_item_instagram = findViewById(R.id.txt_item_instagram);
        txt_item_tiktok = findViewById(R.id.txt_item_tiktok);
        txt_item_twitter = findViewById(R.id.txt_item_twitter);
        txt_item_wechat = findViewById(R.id.txt_item_wechat);
        txt_item_github = findViewById(R.id.txt_item_github);

        btnUpdate = findViewById(R.id.btn_update_item);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel(currentUser.getEmail(), txt_item_phone.getText().toString(), txt_item_gmail.getText().toString(), txt_item_zalo.getText().toString(), txt_item_facebook.getText().toString(), txt_item_instagram.getText().toString(), txt_item_tiktok.getText().toString(), txt_item_twitter.getText().toString(), txt_item_wechat.getText().toString(), txt_item_github.getText().toString());
                UpdateUserItem(user);
            }
        });
        LoadUserItem();
    }

    private void LoadUserItem() {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    txt_item_phone.setText(broccoli.child("phone").getValue(String.class));
                    txt_item_gmail.setText(broccoli.child("gmail").getValue(String.class));
                    txt_item_zalo.setText(broccoli.child("zalo").getValue(String.class));
                    txt_item_facebook.setText(broccoli.child("facebook").getValue(String.class));
                    txt_item_instagram.setText(broccoli.child("instagram").getValue(String.class));
                    txt_item_tiktok.setText(broccoli.child("tiktok").getValue(String.class));
                    txt_item_twitter.setText(broccoli.child("twitter").getValue(String.class));
                    txt_item_wechat.setText(broccoli.child("wechat").getValue(String.class));
                    txt_item_github.setText(broccoli.child("github").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateUserItem(UserModel userModel) {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    broccoli.getRef().child("phone").setValue(userModel.getPhone());
                    broccoli.getRef().child("gmail").setValue(userModel.getGmail());
                    broccoli.getRef().child("zalo").setValue(userModel.getZalo());
                    broccoli.getRef().child("facebook").setValue(userModel.getFacebook());
                    broccoli.getRef().child("instagram").setValue(userModel.getInstagram());
                    broccoli.getRef().child("tiktok").setValue(userModel.getTiktok());
                    broccoli.getRef().child("twitter").setValue(userModel.getTwitter());
                    broccoli.getRef().child("wechat").setValue(userModel.getWechat());
                    broccoli.getRef().child("github").setValue(userModel.getGithub());
                }
                Toast.makeText(Update_Item_Activity.this, "Cập nhật thành công!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}