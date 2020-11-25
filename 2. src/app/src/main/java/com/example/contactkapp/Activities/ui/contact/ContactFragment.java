package com.example.contactkapp.Activities.ui.contact;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.contactkapp.Activities.ui.contact.ContactViewModel;
import com.example.contactkapp.Adapters.UserAdapter;
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
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> mUsers;

    EditText search_bar;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = root.findViewById(R.id.contact_recycleview_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        search_bar = root.findViewById(R.id.search_bar);

        mUsers = new ArrayList<>();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchString(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }

    public void onStart() {
        super.onStart();
        ReadUsers();
    }

    private void searchString(String s) {
        ArrayList<String> listIDUsers = new ArrayList<String>();
        databaseReference = firebaseDatabase.getReference("Follow").child(currentUser.getUid()).child("Following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDUsers.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getValue(String.class);
                    listIDUsers.add(id);
                }

                Query query = firebaseDatabase.getReference("UserItem").orderByChild("displayName").startAt(s.toUpperCase()).endAt(s.toLowerCase() + "\uf8ff");
                query.keepSynced(false);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUsers.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            UserModel user = data.getValue(UserModel.class);
                            if (listIDUsers.contains(user.getUserKey())) {
                                mUsers.add(user);
                            }
                        }
                        userAdapter = new UserAdapter(getActivity(), mUsers);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ReadUsers() {
        ArrayList<String> listIDUsers = new ArrayList<String>();
        databaseReference = firebaseDatabase.getReference("Follow").child(currentUser.getUid()).child("Following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDUsers.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getValue(String.class);
                    listIDUsers.add(id);
                }

                databaseReference = firebaseDatabase.getReference("UserItem");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUsers.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            UserModel user = data.getValue(UserModel.class);
                            if (listIDUsers.contains(user.getUserKey())) {
                                mUsers.add(user);
                            }
                        }
                        userAdapter = new UserAdapter(getActivity(), mUsers);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}