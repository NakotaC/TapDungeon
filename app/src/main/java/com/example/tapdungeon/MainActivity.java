package com.example.tapdungeon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ProgressBar enemyHealthBar;
    private ImageView monsterImageView;
    private MonsterModel currentEnemy;
    private PlayerModel player;
    TextView welcomeText;






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
        enemyHealthBar = findViewById(R.id.enemyHealthBar);
        monsterImageView = findViewById(R.id.monsterImageView);
        welcomeText = findViewById(R.id.mainText);

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            sendToLogin();
        });

        socialButton.setOnClickListener(v -> {
            SocialDialogFragment socialDialog = new SocialDialogFragment();
            socialDialog.show(getSupportFragmentManager(), "SocialDialogFragment");
        });

        monsterImageView.setOnClickListener(v -> {
            dealDamageToEnemy(player.getDamagePerTap());
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
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
                            Object upgradesObject = documentSnapshot.get("upgrades");
                            Map<String, Object> upgradesMap = Collections.emptyMap();
                            Object skillsObject = documentSnapshot.get("friends");
                            Map<String, Object> skillsMap = Collections.emptyMap();
                            Long killsOnLevel = documentSnapshot.getLong("killsOnLevel");

                            if (friendsObject instanceof Map) {
                                friendsMap = (Map<String, Object>) friendsObject;
                                Log.d("Firestore", "Friends map loaded: " + friendsMap.toString());
                            }

                            if (upgradesObject instanceof Map) {
                                upgradesMap = (Map<String, Object>) upgradesObject;
                                Log.d("Firestore", "Friends map loaded: " + friendsMap.toString());
                            }

                            if (skillsObject instanceof Map) {
                                skillsMap = (Map<String, Object>) skillsObject;
                                Log.d("Firestore", "Friends map loaded: " + friendsMap.toString());
                            }

                            player = new PlayerModel(currentUser.getUid(), username, clan, level, gold, friendsMap, upgradesMap, skillsMap, killsOnLevel);

                            welcomeText.setText(player.getDisplayName() + "\nKills: " + player.getKillsOnLevel() + " Level: " + player.getLevel() + " Gold: " + player.getGold());
                            Log.d("Firestore", "User data loaded successfully.");
                            spawnNewEnemy();
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
        finish();
    }

    private void spawnNewEnemy() {
        currentEnemy = new MonsterModel(player.getLevel());

        enemyHealthBar.setMax(currentEnemy.getHealth());

        updateHealthBar();
    }

    private void dealDamageToEnemy(int damage) {
        if (currentEnemy == null || currentEnemy.isDead()) {
            return;
        }

        currentEnemy.takeDamage(damage);

        updateHealthBar();

        if (currentEnemy.isDead()) {
//            Toast.makeText(this, "Enemy defeated!", Toast.LENGTH_SHORT).show();
            player.enemyKilled(currentEnemy);

            playEnemyDeathAnimation();
            updatePlayerInfo();
        }
    }

    private void updateHealthBar() {
        enemyHealthBar.setProgress(currentEnemy.getHealth());
    }

    private void updatePlayerInfo(){
        welcomeText.setText(player.getDisplayName() + "\nKills: " + player.getKillsOnLevel() + " Level: " + player.getLevel() + " Gold: " + player.getGold());
    }

    private void playEnemyDeathAnimation() {
        monsterImageView.animate()
                .rotation(90f)
                .translationY(200f)
                .alpha(0f)
                .setDuration(500)
                .withEndAction(() -> {
                    monsterImageView.setRotation(0f);
                    monsterImageView.setTranslationY(0f);
                    monsterImageView.setAlpha(1f);
                    spawnNewEnemy();
                })
                .start();
    }
}