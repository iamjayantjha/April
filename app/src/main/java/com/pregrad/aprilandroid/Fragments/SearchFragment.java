package com.pregrad.aprilandroid.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pregrad.aprilandroid.Adapter.SearchAdapter;
import com.pregrad.aprilandroid.Model.Users;
import com.pregrad.aprilandroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    RecyclerView usersListRV;
    List<Users> usersList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        usersListRV = view.findViewById(R.id.usersList);
        getUsers();
        return view;
    }

    private void getUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (!users.getId().equals(user.getUid())) {
                        usersList.add(users);
                    }
                }
                SearchAdapter searchAdapter = new SearchAdapter(usersList, getParentFragmentManager());
                usersListRV.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}