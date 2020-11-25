package com.example.contactkapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactkapp.Models.UserModel;
import com.example.contactkapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String strAvatar;
    private String qrCode = "", userKey = null;
    private Button btn_detail_following;
    private ImageButton btn_contact_detail_favorite;
    private ImageView img_Avatar;
    private TextView detail_name, detail_email, content_phone, content_gmail, content_zalo, content_facebook, content_instagram, content_tiktok, content_twitter, content_wechat, content_github;
    private UserModel user;
    private CardView contact_detail_viewcard_phone, contact_detail_viewcard_gmail, contact_detail_viewcard_zalo, contact_detail_viewcard_facebook, contact_detail_viewcard_instagram, contact_detail_viewcard_tiktok, contact_detail_viewcard_twitter, contact_detail_viewcard_wechat, contact_detail_viewcard_github;
    private TextView contact_detail_followers;
    private TextView contact_detail_favorite;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private int counterFollowers = 0;
    private int counterFavourites = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        getSupportActionBar().hide();
        findViewById(R.id.imageBack_qrcodescan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        contact_detail_followers = findViewById(R.id.contact_detail_followers);
        contact_detail_favorite = findViewById(R.id.contact_detail_favorite);
        btn_contact_detail_favorite = findViewById(R.id.btn_contact_detail_favorite);
        myClipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        contact_detail_viewcard_phone = findViewById(R.id.contact_detail_viewcard_phone);
        contact_detail_viewcard_zalo = findViewById(R.id.contact_detail_viewcard_zalo);
        contact_detail_viewcard_gmail = findViewById(R.id.contact_detail_viewcard_email);
        contact_detail_viewcard_facebook = findViewById(R.id.contact_detail_viewcard_facebook);
        contact_detail_viewcard_instagram = findViewById(R.id.contact_detail_viewcard_instagram);
        contact_detail_viewcard_tiktok = findViewById(R.id.contact_detail_viewcard_tiktok);
        contact_detail_viewcard_twitter = findViewById(R.id.contact_detail_viewcard_twitter);
        contact_detail_viewcard_wechat = findViewById(R.id.contact_detail_viewcard_wechat);
        contact_detail_viewcard_github = findViewById(R.id.contact_detail_viewcard_github);

        contact_detail_viewcard_phone.setOnClickListener(this);
        contact_detail_viewcard_zalo.setOnClickListener(this);
        contact_detail_viewcard_gmail.setOnClickListener(this);
        contact_detail_viewcard_facebook.setOnClickListener(this);
        contact_detail_viewcard_instagram.setOnClickListener(this);
        contact_detail_viewcard_tiktok.setOnClickListener(this);
        contact_detail_viewcard_twitter.setOnClickListener(this);
        contact_detail_viewcard_wechat.setOnClickListener(this);
        contact_detail_viewcard_github.setOnClickListener(this);

        img_Avatar = findViewById(R.id.detail_avatar);
        detail_name = findViewById(R.id.detail_name);
        detail_email = findViewById(R.id.detail_email);
        content_phone = findViewById(R.id.info_content_phone);
        content_gmail = findViewById(R.id.info_content_gmail);
        content_zalo = findViewById(R.id.info_content_zalo);
        content_facebook = findViewById(R.id.info_content_facebook);
        content_instagram = findViewById(R.id.info_content_instagram);
        content_tiktok = findViewById(R.id.info_content_tiktok);
        content_twitter = findViewById(R.id.info_content_twitter);
        content_wechat = findViewById(R.id.info_content_wechat);
        content_github = findViewById(R.id.info_content_github);
        btn_detail_following = findViewById(R.id.btn_detail_following);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userKey = getIntent().getExtras().getString("userKey");
        if (userKey != null) {
            String qrcode = getIntent().getExtras().getString("qrcode");
            LoadUserItem(qrcode);
            userKey = null;
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                qrCode = extras.getString("data");
                if (!qrCode.isEmpty()) {
                    LoadUserItem(qrCode);
                }
            }
        }
        btn_contact_detail_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_contact_detail_favorite.getTag().equals(R.drawable.ic_favorite_border)) {
                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                            .child("Favorites").child(currentUser.getUid()).setValue(currentUser.getUid());
                    Toast.makeText(ContactDetailActivity.this, "Yêu thích!", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                            .child("Favorites").child(currentUser.getUid()).removeValue();
                    Toast.makeText(ContactDetailActivity.this, "Đã bỏ yêu thích!", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btn_detail_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_detail_following.getText().toString().equals("Theo dõi")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
                            .child("Following").child(user.getUserKey()).setValue(user.getUserKey());
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserKey())
                            .child("Followers").child(currentUser.getUid()).setValue(currentUser.getUid());
                    Toast.makeText(ContactDetailActivity.this, "Đã theo dõi!", Toast.LENGTH_SHORT).show();

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
                            .child("Following").child(user.getUserKey()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserKey())
                            .child("Followers").child(currentUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                            .child("Favorites").child(currentUser.getUid()).removeValue();
                    Toast.makeText(ContactDetailActivity.this, "Bỏ theo dõi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void LoadUserItem(String qrcodeResult) {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("qrcode").equalTo(qrcodeResult);
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    user = broccoli.getValue(UserModel.class);
                    LoadCountFollowers(user.getUserKey());
                    CounterFavorite(user.getUserKey());
                    isFollowing(user.getUserKey(), btn_detail_following);
                    isFavorite(user.getUserKey(), btn_contact_detail_favorite);
                    detail_name.setText(broccoli.child("displayName").getValue(String.class));
                    detail_email.setText(broccoli.child("userName").getValue(String.class));
                    content_phone.setText(broccoli.child("phone").getValue(String.class));
                    content_gmail.setText(broccoli.child("gmail").getValue(String.class));
                    content_zalo.setText(broccoli.child("zalo").getValue(String.class));
                    content_facebook.setText(broccoli.child("facebook").getValue(String.class));
                    content_instagram.setText(broccoli.child("instagram").getValue(String.class));
                    content_tiktok.setText(broccoli.child("tiktok").getValue(String.class));
                    content_twitter.setText(broccoli.child("twitter").getValue(String.class));
                    content_wechat.setText(broccoli.child("wechat").getValue(String.class));
                    content_github.setText(broccoli.child("github").getValue(String.class));
                    strAvatar = broccoli.child("avatar").getValue(String.class);

                    Glide.with(getApplicationContext()).load(strAvatar).into(img_Avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isFollowing(final String userid, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(currentUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    button.setText("Đang theo dõi");
                } else {
                    button.setText("Theo dõi");
                }
                LoadCountFollowers(userid);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void isFavorite(final String userid, final ImageButton button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Favorite").child(userid).child("Favorites");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(currentUser.getUid()).exists()) {
                    button.setTag(R.drawable.ic_favorite);
                    button.setImageResource(R.drawable.ic_favorite);
                } else {
                    button.setTag(R.drawable.ic_favorite_border);
                    button.setImageResource(R.drawable.ic_favorite_border);
                }
                CounterFavorite(userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        String text = "";
        switch (v.getId()) {
            case R.id.contact_detail_viewcard_phone:
                text = content_phone.getText().toString();
                break;
            case R.id.contact_detail_viewcard_email:
                text = content_gmail.getText().toString();
                break;
            case R.id.contact_detail_viewcard_zalo:
                text = content_zalo.getText().toString();
                break;
            case R.id.contact_detail_viewcard_facebook:
                text = content_facebook.getText().toString();
                break;
            case R.id.contact_detail_viewcard_instagram:
                text = content_instagram.getText().toString();
                break;
            case R.id.contact_detail_viewcard_tiktok:
                text = content_tiktok.getText().toString();
                break;
            case R.id.contact_detail_viewcard_twitter:
                text = content_twitter.getText().toString();
                break;
            case R.id.contact_detail_viewcard_wechat:
                text = content_wechat.getText().toString();
                break;
            case R.id.contact_detail_viewcard_github:
                text = content_github.getText().toString();
                break;
            default:
                break;
        }
        if (!text.isEmpty()) {
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(getApplicationContext(), "Copy text " + text, Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadCountFollowers(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(id).child("Followers");

        reference.keepSynced(false);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counterFollowers = 0;
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    counterFollowers++;
                }
                contact_detail_followers.setText("Theo dõi: " + String.valueOf(counterFollowers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void CounterFavorite(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Favorite").child(id).child("Favorites");

        reference.keepSynced(false);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counterFavourites = 0;
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    counterFavourites++;
                }
                contact_detail_favorite.setText("Yêu thích: " + String.valueOf(counterFavourites));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}