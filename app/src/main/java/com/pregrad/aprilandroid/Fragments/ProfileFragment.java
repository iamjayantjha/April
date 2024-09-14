package com.pregrad.aprilandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pregrad.aprilandroid.Activities.EditProfileActivity;
import com.pregrad.aprilandroid.Activities.LoginActivity;
import com.pregrad.aprilandroid.Model.Users;
import com.pregrad.aprilandroid.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    TextView userName, fullName, bio, posts, followers, following;
    String userId ="";
    CircleImageView userProfilePicture;
    MaterialButton edit_follow_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logOut = view.findViewById(R.id.logOut);
        userName = view.findViewById(R.id.username);
        fullName = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        userProfilePicture = view.findViewById(R.id.user_profile_picture);
        edit_follow_btn = view.findViewById(R.id.edit_follow_btn);
        SharedPreferences preferences = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userId = preferences.getString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            edit_follow_btn.setText("Edit Profile");
            edit_follow_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
            edit_follow_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
        else {
            checkFollow();
        }
        edit_follow_btn.setOnClickListener(v -> {
            String btnText = edit_follow_btn.getText().toString();
            switch (btnText) {
                case "Edit Profile":
                    editProfile();
                    break;
                case "Follow":
                    followUser();
                    break;
                case "Following":
                    unfollowUser();
                    break;
            }
        });
        logOut.setOnClickListener(this::logOutUser);
        getUserData();
        getFollowers();
        getPosts();
        getFollowing();
        return view;
    }

    private void unfollowUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("follower");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.child(userId).removeValue();
        edit_follow_btn.setText("Follow");
        edit_follow_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryBtn));
        edit_follow_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private void followUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("follower");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.child(userId).setValue(true);
        edit_follow_btn.setText("Following");
        edit_follow_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
        edit_follow_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
    }

    private void editProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists()) {
                    edit_follow_btn.setText("Following");
                    edit_follow_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
                    edit_follow_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
                else {
                    edit_follow_btn.setText("Follow");
                    edit_follow_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryBtn));
                    edit_follow_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot.child("publisherId").getValue()).toString().equals(userId)) {
                            counter++;
                        }
                    }
                }
                posts.setText(counter+"\nPosts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(snapshot.getChildrenCount()+"\nFollowing");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("follower");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"\nFollower");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                userName.setText(user.getUsername());
                fullName.setText(user.getFullName());
                bio.setText(user.getBio());
                if (user.getBio().equals("")) {
                    bio.setVisibility(View.GONE);
                }
                else {
                    bio.setVisibility(View.VISIBLE);
                }
                if (user.getImageUrl().equals("default")) {
                    userProfilePicture.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(requireActivity()).load(user.getImageUrl()).into(userProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logOutUser(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}