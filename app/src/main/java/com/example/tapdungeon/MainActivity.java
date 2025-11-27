package com.example.tapdungeon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tapdungeon.data.model.LoggedInUser;
import com.example.tapdungeon.social.SocialDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.tapdungeon.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button logoutBtn;
    ImageButton socialButton;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        logoutBtn = findViewById(R.id.logoutBtn);
        socialButton = findViewById(R.id.socialButton);


        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            sendToLogin();
        });

        socialButton.setOnClickListener(v -> {
            SocialDialogFragment socialDialog = new SocialDialogFragment();
            socialDialog.show(getSupportFragmentManager(), "SocialDialogFragment");
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            TextView welcomeText = findViewById(R.id.mainText);
            welcomeText.setText("Welcome " + currentUser.getDisplayName());

            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            Long gold = documentSnapshot.getLong("gold"); // Use getLong for numbers
                            Long level = documentSnapshot.getLong("level");
                            String clan = documentSnapshot.getString("clan");
                            Object friendsObject = documentSnapshot.get("friends");
                            Map<String, Object> friendsMap = Collections.emptyMap();

                            if (friendsObject instanceof Map) {
                                friendsMap = (Map<String, Object>) friendsObject;
                                Log.d("Firestore", "Friends map loaded: " + friendsMap.toString());
                            }

                            LoggedInUser user = new LoggedInUser(currentUser.getUid(), username, clan, level, gold, friendsMap);

                            welcomeText.setText("Welcome, " + user.getDisplayName() + "! Level: " + user.getLevel() + " Gold: " + user.getGold());
                            Log.d("Firestore", "User data loaded successfully.");

                        } else {
                            Log.w("Firestore", "User document does not exist for UID: " + currentUser.getUid());
                            welcomeText.setText("Welcome! (Could not load user data)");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error getting user document", e);
                        welcomeText.setText("Welcome! (Error loading data)");
                    });
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // Finish MainActivity so the user can't go back to it without being logged in
        finish();
    }


}