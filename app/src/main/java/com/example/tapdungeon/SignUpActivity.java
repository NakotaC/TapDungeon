package com.example.tapdungeon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button loginButton, signUpButton; // Added signUpButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();


        emailEditText = findViewById(R.id.emailEnterTextSignup);
        passwordEditText = findViewById(R.id.passwordEnterTextSignup);
        loginButton = findViewById(R.id.loginBtn);
        signUpButton = findViewById(R.id.signUpBtn);
        usernameEditText = findViewById(R.id.usernameEnterTextSignup);


        loginButton.setOnClickListener(v -> {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        });

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (isValid(email, password)) {
                createAccount(email, username, password);
            }
        });
    }

    private boolean isValid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createAccount(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Auth", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        updateUserProfile(user, username);
                        sendToMain();
                    } else {
                        Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user, String username) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Auth", "User profile updated with username.");
                        // Step 3: Now that the user object has the username, save to Firestore
                        saveUserToFirestore(user);
                    } else {
                        Log.w("Auth", "Failed to update user profile.", task.getException());
                        // Even if profile update fails, try to save what we have and proceed
                        saveUserToFirestore(user);
                    }
                });
    }


    private void saveUserToFirestore(FirebaseUser user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user == null) return;

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("username", user.getDisplayName());
        userMap.put("uid", user.getUid());
        userMap.put("created", FieldValue.serverTimestamp());
        userMap.put("gold", 0);
        userMap.put("level", 1);
        userMap.put("clan", null);
        userMap.put("last_seen", FieldValue.serverTimestamp());
        userMap.put("friends", new HashMap<>());


        // Add a new document with the user's UID as the document ID
        db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User document successfully created!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error creating user document", e));


    }
    private void sendToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
           // sendToMain();
        }
    }
}
