// src/main/java/com/example/tapdungeon/social/CreateClanDialogFragment.java
package com.example.tapdungeon.social.clan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tapdungeon.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateClanDialogFragment extends DialogFragment {

    private static final String TAG = "CreateClanDialog";

    private TextInputEditText clanNameInput;
    private Button createButton;
    private Button cancelButton;

    private FirebaseFirestore db;
    private FirebaseAuth mauth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_clan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();

        clanNameInput = view.findViewById(R.id.clan_name_input);
        createButton = view.findViewById(R.id.create_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v -> dismiss());

        createButton.setOnClickListener(v -> {
            String clanName = clanNameInput.getText().toString().trim();
            if (TextUtils.isEmpty(clanName)) {
                clanNameInput.setError("Clan name cannot be empty.");
                return;
            }
            createNewClan(clanName);
        });
    }

    private void createNewClan(String clanName) {
        createButton.setEnabled(false);
        Toast.makeText(getContext(), "Creating clan...", Toast.LENGTH_SHORT).show();

        Map<String, Object> clanData = new HashMap<>();
        clanData.put("name", clanName);
        clanData.put("leader_id", mauth.getCurrentUser().getUid());
        clanData.put("member_count", 1);

        Map<String, Object> membersMap = new HashMap<>();
        membersMap.put(mauth.getCurrentUser().getUid(), true);
        clanData.put("members", membersMap);

        clanData.put("created_at", System.currentTimeMillis());

        String newClanId = db.collection("clans").document().getId();

        db.collection("clans").document(newClanId).set(clanData)
                .addOnSuccessListener(aVoid_clan -> {
                    Log.d(TAG, "Clan document created successfully with ID: " + newClanId);

                    db.collection("users").document(mauth.getCurrentUser().getUid())
                            .update("clan", newClanId)
                            .addOnSuccessListener(aVoid_user -> {
                                Log.d(TAG, "User document updated with new clan ID.");

                                Toast.makeText(getContext(), "Clan created!", Toast.LENGTH_SHORT).show();

                                if (getParentFragment() instanceof ClanFragment) {
                                    ((ClanFragment) getParentFragment()).refreshClanData();
                                }

                                dismiss(); // Close the dialog
                            })
                            .addOnFailureListener(e_user -> {
                                // This is an important error to handle!
                                Log.e(TAG, "Clan was created, but failed to update user profile.", e_user);
                                Toast.makeText(getContext(), "Error: Could not join new clan.", Toast.LENGTH_LONG).show();
                                createButton.setEnabled(true);
                            });
                })
                .addOnFailureListener(e_clan -> {
                    Log.e(TAG, "Error creating clan document", e_clan);
                    Toast.makeText(getContext(), "Error creating clan. Please try again.", Toast.LENGTH_LONG).show();
                    createButton.setEnabled(true);
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
