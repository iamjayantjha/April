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

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        MaterialCardView loginButton = findViewById(R.id.login);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        TextView signUp = findViewById(R.id.logIn);
        TextView loginText = findViewById(R.id.loginText);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        loginButton.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginButton.setEnabled(false);
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginText.setVisibility(View.GONE);
                loginUser(email.getText().toString(), password.getText().toString(), loginButton, loadingProgressBar, loginText);
            }
        });
        forgotPassword.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                email.setError("Email is required");
            } else {
               /* auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });*/
                checkEmailExists(email.getText().toString().trim(), email);
            }

        });
        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    email.setError("Email is required");
                } else {
                   checkEmail(s, email, password, loginButton, loadingProgressBar, loginText);
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
                    checkPassWord(email, password, loginButton, loadingProgressBar, loginText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkEmailExists(String email, EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://androidaprilbatch2024-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean doesExist = false;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.getEmail().equals(email)){
                       doesExist = true;
                    }
                }
                if (doesExist){
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Password Reset Email Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    editText.setError("There is no user with this email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkEmail(CharSequence s, EditText email, EditText password, MaterialCardView loginButton, ProgressBar loadingProgressBar, TextView loginText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches()) {
            email.setError("Invalid Email");
        } else {
            checkPassWord(email, password, loginButton, loadingProgressBar, loginText);
        }
    }

    private void checkPassWord(EditText email, EditText password, MaterialCardView loginButton, ProgressBar loadingProgressBar, TextView loginText) {
        if (password.getText().toString().length() < 6) {
            password.setError("Password must be at least 6 characters");
            loginButton.setEnabled(false);
            loadingProgressBar.setVisibility(View.GONE);
            loginText.setVisibility(View.VISIBLE);
        } else if (email.getText().length()>4){
            loginButton.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void loginUser(String email, String password, MaterialCardView loginButton, ProgressBar loadingProgressBar, TextView loginText ) {
        auth.signInWithEmailAndPassword(email.trim(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                loginButton.setEnabled(true);
                loadingProgressBar.setVisibility(View.GONE);
                loginText.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Login Failed "+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}