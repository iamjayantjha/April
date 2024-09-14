package com.pregrad.aprilandroid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pregrad.aprilandroid.Model.Users;
import com.pregrad.aprilandroid.R;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText fullName = findViewById(R.id.fullName);
        EditText username = findViewById(R.id.userName);
        MaterialCardView signUp = findViewById(R.id.signUp);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        TextView signUpText = findViewById(R.id.signUpText);
        TextView logIn = findViewById(R.id.logIn);
        signUp.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || fullName.getText().toString().isEmpty() || username.getText().toString().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                signUp.setEnabled(false);
                loadingProgressBar.setVisibility(View.VISIBLE);
                signUpText.setVisibility(View.GONE);
                signUpUser(email.getText().toString(), password.getText().toString(), fullName.getText().toString(), username.getText().toString(), signUp, loadingProgressBar, signUpText);
            }
        });
        logIn.setOnClickListener(v -> finish());
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    email.setError("Email is required");
                } else {
                    checkEmail(s, email, password, signUp, loadingProgressBar, signUpText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    password.setError("Password is required");
                } else {
                    checkPassWord(email, password, signUp, loadingProgressBar, signUpText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    username.setError("Username is required");
                } else {
                    checkUserName(username, signUp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkUserName(EditText username, MaterialCardView signUp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean doesExist = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.getUsername().equalsIgnoreCase(username.getText().toString().trim())) {
                        doesExist = true;
                        break;
                    }
                }
                if (doesExist) {
                    username.setError("Username already exists");
                    signUp.setEnabled(false);
                    signUp.setCardBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primaryBtnDisabled));
                } else {
                    signUp.setEnabled(true);
                    signUp.setCardBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primaryBtn));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkEmail(CharSequence s, EditText email, EditText password, MaterialCardView signUp, ProgressBar loadingProgressBar, TextView signUpText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches()) {
            email.setError("Invalid Email");
        } else {
            checkPassWord(email, password, signUp, loadingProgressBar, signUpText);
        }
    }

    private void checkPassWord(EditText email, EditText password, MaterialCardView signUp, ProgressBar loadingProgressBar, TextView signUpText) {
        if (password.getText().toString().length() < 6) {
            password.setError("Password must be at least 6 characters");
            signUp.setEnabled(false);
            loadingProgressBar.setVisibility(View.GONE);
            signUpText.setVisibility(View.VISIBLE);
        } else if (email.getText().length()>4){
            signUp.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void signUpUser(String email, String password, String fullName, String userName, MaterialCardView signUp, ProgressBar loadingProgressBar, TextView signUpText) {
        auth.createUserWithEmailAndPassword(email.trim(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
                HashMap<String, String> map = new HashMap<>();
                map.put("id", auth.getCurrentUser().getUid());
                map.put("username", userName.toLowerCase());
                map.put("fullName", fullName);
                map.put("bio", "");
                map.put("imageUrl", "default");
                map.put("email", email);
                map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                            signUp.setEnabled(true);
                        }
                    }
                });
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to create user "+task.getException(), Toast.LENGTH_SHORT).show();
                signUp.setEnabled(true);
                loadingProgressBar.setVisibility(View.GONE);
                signUpText.setVisibility(View.VISIBLE);
                signUp.setEnabled(true);
            }
        });
    }
}