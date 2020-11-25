package com.example.contactkapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contactkapp.Activities.ContactDetailActivity;
import com.example.contactkapp.Models.UserModel;
import com.example.contactkapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context mContext;
    List<UserModel> mUsers;
    FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<UserModel> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final UserModel user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.displayName.setText(user.getDisplayName());
        holder.email.setText(user.getUserName());
        Glide.with(mContext).load(user.getAvatar()).into(holder.img_avatar);

        isFollowing(user.getUserKey(), holder.btn_follow);
        isFavorite(user.getUserKey(), holder.btn_contact_favorite);
        if (user.getUserName().equals(firebaseUser.getEmail())) {
            holder.user_item_container.setMaxHeight(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences;
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getUserKey());
                editor.apply();
            }
        });

        holder.btn_contact_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_contact_favorite.getTag().equals(R.drawable.ic_favorite_border)) {
                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                            .child("Favorites").child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
                    Toast.makeText(mContext, "Yêu thích!", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Bạn muốn bỏ yêu thích!").setCancelable(false)
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                                            .child("Favorites").child(firebaseUser.getUid()).removeValue();
                                    Toast.makeText(mContext, "Đã bỏ yêu thích!", Toast.LENGTH_SHORT).show();
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
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Theo dõi")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getUserKey()).setValue(user.getUserKey());
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserKey())
                            .child("Followers").child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Bạn muốn bỏ theo dõi!").setCancelable(false)
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                            .child("Following").child(user.getUserKey()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserKey())
                                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(user.getUserKey())
                                            .child("Favorites").child(firebaseUser.getUid()).removeValue();

                                    Toast.makeText(mContext, "Đã bỏ theo dõi!", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView displayName;
        public TextView email;
        public ImageView img_avatar;
        public ImageButton btn_contact_favorite;
        public Button btn_follow;
        public ConstraintLayout user_item_container;
        public CardView user_item_cardView;

        public UserViewHolder(View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.user_item_name);
            email = itemView.findViewById(R.id.user_item_email);
            img_avatar = itemView.findViewById(R.id.user_item_avatar);
            btn_follow = itemView.findViewById(R.id.user_item_follow);
            user_item_container = itemView.findViewById(R.id.user_item_container);
            user_item_cardView = itemView.findViewById(R.id.user_item_cardView);
            btn_contact_favorite = itemView.findViewById(R.id.btn_contact_favorite);

            user_item_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent userDetailActivity = new Intent(mContext, ContactDetailActivity.class);
                    int position = getAdapterPosition();
                    final UserModel user = mUsers.get(position);
                    userDetailActivity.putExtra("qrcode", user.getQRCode());
                    userDetailActivity.putExtra("userKey", user.getUserKey());
                    mContext.startActivity(userDetailActivity);
                }
            });
        }
    }

    private void isFollowing(final String userid, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {

                    button.setText("Đang theo dõi");
                } else {

                    button.setText("Theo dõi");
                }
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
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    button.setTag(R.drawable.ic_favorite);
                    button.setImageResource(R.drawable.ic_favorite);
                } else {
                    button.setTag(R.drawable.ic_favorite_border);
                    button.setImageResource(R.drawable.ic_favorite_border);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
