package com.pregrad.aprilandroid.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pregrad.aprilandroid.Application.Utils;
import com.pregrad.aprilandroid.Fragments.HomeFragment;
import com.pregrad.aprilandroid.Fragments.ProfileFragment;
import com.pregrad.aprilandroid.Fragments.ReelsFragment;
import com.pregrad.aprilandroid.Fragments.SearchFragment;
import com.pregrad.aprilandroid.Model.Users;
import com.pregrad.aprilandroid.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    Fragment selectedFragment = null;
    MaterialCheckBox home, search, reels;
    ImageView add;
    MaterialCardView profile;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    CircleImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.statusBar(this, R.color.white);
        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        reels = findViewById(R.id.reels);
        add = findViewById(R.id.add);
        profile = findViewById(R.id.profile);
        profile_image = findViewById(R.id.profile_image);
        search.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                selectedFragment = new SearchFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                home.setChecked(false);
                reels.setChecked(false);
                home.setEnabled(true);
                reels.setEnabled(true);
                search.setEnabled(false);
                search.setChecked(true);
                profile.setStrokeWidth(0);
            }else {
                search.setEnabled(true);
            }
        });
        home.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                selectedFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                search.setChecked(false);
                reels.setChecked(false);
                search.setEnabled(true);
                reels.setEnabled(true);
                home.setEnabled(false);
                home.setChecked(true);
                profile.setStrokeWidth(0);
            }else {
                home.setEnabled(true);
            }
        });
        reels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                selectedFragment = new ReelsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                search.setChecked(false);
                home.setChecked(false);
                search.setEnabled(true);
                home.setEnabled(true);
                reels.setEnabled(false);
                reels.setChecked(true);
                profile.setStrokeWidth(0);
            }else {
                reels.setEnabled(true);
            }
        });
        profile.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.apply();
            selectedFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            search.setChecked(false);
            home.setChecked(false);
            reels.setChecked(false);
            search.setEnabled(true);
            home.setEnabled(true);
            reels.setEnabled(true);
            profile.setStrokeWidth(5);
            profile.setStrokeColor(ContextCompat.getColor(this, R.color.black));
        });
        if (selectedFragment == null){
            selectedFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else {
            getCurrentUserData();
        }
    }

    private void getCurrentUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(MainActivity.this).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}