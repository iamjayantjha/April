package com.pregrad.aprilandroid.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.pregrad.aprilandroid.Model.Users;
import com.pregrad.aprilandroid.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    EditText username, fullname, bio, email;
    CircleImageView profileImage;
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ImageButton save;
    ImageView back;
    Uri imageUri;
    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        email = findViewById(R.id.email);
        profileImage = findViewById(R.id.profile_picture);
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        profileImage.setOnClickListener(v -> {
            chooseImage();
        });
        storageReference = FirebaseStorage.getInstance().getReference("profile_picture");
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForUsername(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        save.setOnClickListener(v -> {
            updateProfile();
        });
        getCurrentUserData();
    }

    private void updateProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        reference.child("username").setValue(username.getText().toString());
        reference.child("fullName").setValue(fullname.getText().toString());
        reference.child("bio").setValue(bio.getText().toString());
        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            profileImage.setImageURI(data.getData());
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference fileReference = storageReference.child(currentUserId + ".png");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = (Uri) task.getResult();
                    String mUri = downloadUri.toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
                    reference.child("imageUrl").setValue(mUri).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this,"Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkForUsername(CharSequence s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user.getUsername().equals(s.toString()) && !user.getId().equals(currentUserId)) {
                        username.setError("Username already exists");
                        save.setEnabled(false);
                        save.setColorFilter(ContextCompat.getColor(EditProfileActivity.this, R.color.grey));
                        break;
                    }else if (s.length() < 1){
                        username.setError("Username is required");
                        save.setEnabled(false);
                        save.setColorFilter(ContextCompat.getColor(EditProfileActivity.this, R.color.grey));
                    }else {
                        save.setEnabled(true);
                        save.setColorFilter(ContextCompat.getColor(EditProfileActivity.this, R.color.primaryBtn));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());
                fullname.setText(user.getFullName());
                bio.setText(user.getBio());
                email.setText(user.getEmail());
                if (user.getImageUrl().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(EditProfileActivity.this).load(user.getImageUrl()).into(profileImage);
                }
                email.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}