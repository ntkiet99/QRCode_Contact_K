package com.example.contactkapp.Activities.ui.profile;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactkapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView img_avatar, img_background;
    private TextView profile_displayname, content_phone, content_gmail, content_zalo, content_facebook, content_instagram, content_tiktok, content_twitter, content_wechat, content_github;
    private CardView viewcard_phone, viewcard_gmail, viewcard_zalo, viewcard_facebook, viewcard_instagram, viewcard_tiktok, viewcard_twitter, viewcard_wechat, viewcard_github;
    private TextView profile_counter_following;
    private TextView profile_counter_followers;
    private TextView profile_counter_favorite;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    String pathImageBackground;
    int counterFollowing = 0;
    int counterFollowers = 0;
    int counterFavourites = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        profile_counter_following = root.findViewById(R.id.profile_counter_following);
        profile_counter_following.setText(String.valueOf(counterFollowing));

        profile_counter_followers = root.findViewById(R.id.profile_counter_followers);
        profile_counter_followers.setText(String.valueOf(counterFollowers));

        profile_counter_favorite = root.findViewById(R.id.profile_counter_favorite);
        profile_counter_favorite.setText(String.valueOf(counterFavourites));

        img_avatar = root.findViewById(R.id.profile_avatar);
        img_background = root.findViewById(R.id.profile_background);
        content_phone = root.findViewById(R.id.profile_content_phone);
        content_gmail = root.findViewById(R.id.profile_content_gmail);
        content_zalo = root.findViewById(R.id.profile_content_zalo);
        content_facebook = root.findViewById(R.id.profile_content_facebook);
        content_instagram = root.findViewById(R.id.profile_content_instagram);
        content_tiktok = root.findViewById(R.id.profile_content_tiktok);
        content_twitter = root.findViewById(R.id.profile_content_twitter);
        content_wechat = root.findViewById(R.id.profile_content_wechat);
        content_github = root.findViewById(R.id.profile_content_github);
        profile_displayname = root.findViewById(R.id.profile_displayname);
        LoadUserItem();
        LoadCountFollowers();
        LoadCountFollowing();
        viewcard_phone = root.findViewById(R.id.viewcard_phone);
        viewcard_gmail = root.findViewById(R.id.viewcard_gmail);
        viewcard_zalo = root.findViewById(R.id.viewcard_zalo);
        viewcard_facebook = root.findViewById(R.id.viewcard_facebook);
        viewcard_instagram = root.findViewById(R.id.viewcard_instagram);
        viewcard_tiktok = root.findViewById(R.id.viewcard_tiktok);
        viewcard_twitter = root.findViewById(R.id.viewcard_twitter);
        viewcard_wechat = root.findViewById(R.id.viewcard_wechat);
        viewcard_github = root.findViewById(R.id.viewcard_github);
        viewcard_phone.setOnClickListener(this);
        viewcard_gmail.setOnClickListener(this);
        viewcard_zalo.setOnClickListener(this);
        viewcard_facebook.setOnClickListener(this);
        viewcard_instagram.setOnClickListener(this);
        viewcard_tiktok.setOnClickListener(this);
        viewcard_twitter.setOnClickListener(this);
        viewcard_wechat.setOnClickListener(this);
        viewcard_github.setOnClickListener(this);
        return root;
    }

    private void LoadUserItem() {
        DatabaseReference userItem = firebaseDatabase.getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(img_avatar);

        profile_displayname.setText(currentUser.getDisplayName());
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    Glide.with(getContext()).load(broccoli.child("backgroundUser").getValue(String.class)).into(img_background);
                    content_phone.setText(broccoli.child("phone").getValue(String.class));
                    content_gmail.setText(broccoli.child("gmail").getValue(String.class));
                    content_zalo.setText(broccoli.child("zalo").getValue(String.class));
                    content_facebook.setText(broccoli.child("facebook").getValue(String.class));
                    content_instagram.setText(broccoli.child("instagram").getValue(String.class));
                    content_tiktok.setText(broccoli.child("tiktok").getValue(String.class));
                    content_twitter.setText(broccoli.child("twitter").getValue(String.class));
                    content_wechat.setText(broccoli.child("wechat").getValue(String.class));
                    content_github.setText(broccoli.child("github").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void LoadCountFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(currentUser.getUid()).child("Followers");

        reference.keepSynced(false);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counterFollowers = 0;
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    counterFollowers++;
                }

                profile_counter_followers.setText(String.valueOf(counterFollowers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void LoadCountFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(currentUser.getUid()).child("Following");

        reference.keepSynced(false);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counterFollowing = 0;
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    counterFollowing++;
                }

                profile_counter_following.setText(String.valueOf(counterFollowing));
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
            case R.id.viewcard_phone:
                text = content_phone.getText().toString();
                break;
            case R.id.viewcard_gmail:
                text = content_gmail.getText().toString();
                break;
            case R.id.viewcard_zalo:
                text = content_zalo.getText().toString();
                break;
            case R.id.viewcard_facebook:
                text = content_facebook.getText().toString();
                break;
            case R.id.viewcard_instagram:
                text = content_instagram.getText().toString();
                break;
            case R.id.viewcard_tiktok:
                text = content_tiktok.getText().toString();
                break;
            case R.id.viewcard_twitter:
                text = content_twitter.getText().toString();
                break;
            case R.id.viewcard_wechat:
                text = content_wechat.getText().toString();
                break;
            case R.id.viewcard_github:
                text = content_github.getText().toString();
                break;
            default:
                break;
        }
        if (!text.isEmpty()) {
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(getContext(), "Copy text " + text, Toast.LENGTH_SHORT).show();
        }
    }

    private void CounterFavorite(String id) {
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

                profile_counter_favorite.setText(String.valueOf(counterFavourites));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}