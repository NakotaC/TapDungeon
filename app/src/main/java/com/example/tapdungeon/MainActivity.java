package com.example.tapdungeon;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tapdungeon.social.SocialDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button logoutBtn, eventButton;
    ImageButton socialButton;
    private FirebaseFirestore db;
    private ProgressBar enemyHealthBar;
    private ImageView monsterImageView;
    private MonsterModel currentEnemy;
    private PlayerModel player;
    TextView welcomeText;
    private SoundPool soundPool;
    private int playerAttackSoundId;
    private int monsterDeathSoundId;
    private boolean soundsLoaded = false;






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
        eventButton = findViewById(R.id.eventButton);
        socialButton = findViewById(R.id.socialButton);
        enemyHealthBar = findViewById(R.id.enemyHealthBar);
        monsterImageView = findViewById(R.id.monsterImageView);
        welcomeText = findViewById(R.id.mainText);

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            sendToLogin();
        });

        eventButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        });

        socialButton.setOnClickListener(v -> {
            SocialDialogFragment socialDialog = new SocialDialogFragment();
            socialDialog.show(getSupportFragmentManager(), "SocialDialogFragment");
        });

        monsterImageView.setOnClickListener(v -> {
            dealDamageToEnemy(player.getDamagePerTap());
        });

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                soundsLoaded = true;
                Log.d("SoundPool", "Sounds loaded successfully.");
            } else {
                Log.e("SoundPool", "Error loading sound, status: " + status);
            }
        });

        playerAttackSoundId = soundPool.load(this, R.raw.player_attack, 1);
        monsterDeathSoundId = soundPool.load(this, R.raw.monster_death, 1);

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
                            Object skillsObject = documentSnapshot.get("skills");
                            Map<String, Object> skillsMap = Collections.emptyMap();
                            Long killsOnLevel = documentSnapshot.getLong("killsOnLevel");
                            com.google.firebase.Timestamp lastSeenTimestamp = documentSnapshot.getTimestamp("last_seen");


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

                            if (lastSeenTimestamp != null) {
                                com.google.firebase.Timestamp currentTime = new com.google.firebase.Timestamp(new java.util.Date());
                                long timeAwayInSeconds = currentTime.getSeconds() - lastSeenTimestamp.getSeconds();


                                final int GOLD_PER_MINUTE = 1;
                                if (timeAwayInSeconds > 60) {
                                    long minutesAway = timeAwayInSeconds / 60;
                                    long goldEarned = minutesAway * GOLD_PER_MINUTE;

                                    if (goldEarned > 0) {
                                        player.addGold(goldEarned);
                                        String toastMessage = "You were gone " + minutesAway + " minutes. You earned " + goldEarned + " gold.";
                                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                                    }
                                }

                                welcomeText.setText(player.getDisplayName() + "\nKills: " + player.getKillsOnLevel() + " Level: " + player.getLevel() + " Gold: " + player.getGold());
                                Log.d("Firestore", "User data loaded successfully.");
                                Toast lastSeenToast = new Toast(this);
                                lastSeenToast.setText("Last seen: " + player.getLastSeenDiff());
                                spawnNewEnemy();
                            } else {
                                Log.w("Firestore", "User document does not exist for UID: " + currentUser.getUid());
                                welcomeText.setText("Welcome! (Could not load user data)");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error getting user document", e);
                        welcomeText.setText("Welcome! (Error loading data)");
                    });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null || player == null) {
                Log.w("Firestore", "Cannot save data. User or player data is null.");
                return;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("gold", player.getGold());
            userData.put("level", player.getLevel());
            userData.put("killsOnLevel", player.getKillsOnLevel());
            userData.put("upgrades", player.getUpgrades());
            userData.put("skills", player.getSkills());
            userData.put("friends", player.getFriends());
        userData.put("last_seen", new java.util.Date());
        userData.put("clan", player.getClan());


            db.collection("users").document(currentUser.getUid())
                    .update(userData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Player data successfully saved!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error saving player data", e));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the SoundPool resources
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
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

        if (soundsLoaded) {
            soundPool.play(playerAttackSoundId, 1.0f, 1.0f, 0, 0, 1.25f);
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
        if (soundsLoaded) {
            soundPool.play(monsterDeathSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
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