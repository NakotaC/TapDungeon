package com.example.tapdungeon.social.clan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapdungeon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanFragment extends Fragment implements AllClansAdapter.OnJoinClickListener{

    private static final String TAG = "ClanFragment";

    private ProgressBar progressBar;
    private LinearLayout clanDetailsView;
    private LinearLayout clanListView;

    private TextView clanNameText;
    private RecyclerView clanMembersRecyclerView;
    private Button leaveClanButton;

    private RecyclerView allClansRecyclerView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = view.findViewById(R.id.clan_progress_bar);
        clanDetailsView = view.findViewById(R.id.clan_details_view);
        clanListView = view.findViewById(R.id.clan_list_view);

        clanNameText = view.findViewById(R.id.clan_name_text);
        clanMembersRecyclerView = view.findViewById(R.id.clan_members_recycler_view);
        leaveClanButton = view.findViewById(R.id.leave_clan_button);

        allClansRecyclerView = view.findViewById(R.id.all_clans_recycler_view);

        Button createClanButton = view.findViewById(R.id.createClanButton);

        createClanButton.setOnClickListener(v -> {
            CreateClanDialogFragment dialog = new CreateClanDialogFragment();
            dialog.show(getChildFragmentManager(), "CreateClanDialog");
        });

        checkUserClanStatus();
    }

    private void checkUserClanStatus() {
        if (currentUser == null) {
            Log.w(TAG, "Current user is null, cannot check clan status.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        clanDetailsView.setVisibility(View.GONE);
        clanListView.setVisibility(View.GONE);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String clanId = documentSnapshot.getString("clan");

                        if (clanId != null && !clanId.isEmpty()) {
                            Log.d(TAG, "User is in clan: " + clanId);
                            showClanDetails(clanId);
                        } else {
                            Log.d(TAG, "User is not in a clan.");
                            showAllClansList();
                        }
                    } else {
                        Log.w(TAG, "User document does not exist.");
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load clan data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showClanDetails(String clanId) {
        progressBar.setVisibility(View.VISIBLE);
        clanDetailsView.setVisibility(View.GONE);
        clanListView.setVisibility(View.GONE);

        db.collection("clans").document(clanId).get()
                .addOnSuccessListener(clanDocument -> {
                    if (!clanDocument.exists()) {
                        Log.w(TAG, "Clan document " + clanId + " does not exist. Forcing user out.");
                        showAllClansList();
                        return;
                    }

                    String clanName = clanDocument.getString("name");
                    clanNameText.setText(clanName);

                    Map<String, Boolean> memberMap = (Map<String, Boolean>) clanDocument.get("members");
                    if (memberMap == null || memberMap.isEmpty()) {

                        progressBar.setVisibility(View.GONE);
                        clanDetailsView.setVisibility(View.VISIBLE);
                        return;
                    }

                    List<ClanMember> memberDetailsList = new ArrayList<>();
                    List<String> memberIds = new ArrayList<>(memberMap.keySet());

                    db.collection("users").whereIn("uid", memberIds).get()
                            .addOnSuccessListener(userSnapshots -> {
                                for(DocumentSnapshot userDoc : userSnapshots) {
                                    ClanMember member = userDoc.toObject(ClanMember.class);
                                    if (member != null) {
                                        memberDetailsList.add(member);
                                    }
                                }

                                clanMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                ClanMemberAdapter adapter = new ClanMemberAdapter(memberDetailsList);
                                clanMembersRecyclerView.setAdapter(adapter);

                                progressBar.setVisibility(View.GONE);
                                clanDetailsView.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to fetch member details", e);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to load clan members.", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch clan details", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading your clan.", Toast.LENGTH_SHORT).show();
                });

        leaveClanButton.setOnClickListener(v -> {
            leaveClanButton.setEnabled(false);
            Toast.makeText(getContext(), "Leaving clan...", Toast.LENGTH_SHORT).show();

            Map<String, Object> clanUpdates = new HashMap<>();
            clanUpdates.put("members." + currentUser.getUid(), FieldValue.delete());

            db.collection("clans").document(clanId)
                    .update(clanUpdates)
                    .addOnSuccessListener(aVoid_clan -> {
                        Log.d(TAG, "Successfully removed user from clan's member list.");

                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("clan", null);

                        db.collection("users").document(currentUser.getUid())
                                .update(userUpdates)
                                .addOnSuccessListener(aVoid_user -> {
                                    Log.d(TAG, "Successfully removed clan field from user's document.");
                                    Toast.makeText(getContext(), "You have left the clan.", Toast.LENGTH_SHORT).show();

                                    checkUserClanStatus();
                                })
                                .addOnFailureListener(e_user -> {
                                    Log.e(TAG, "Error removing clan from user profile", e_user);
                                    Toast.makeText(getContext(), "Error updating your profile. Please try again.", Toast.LENGTH_LONG).show();
                                    leaveClanButton.setEnabled(true);
                                });
                    })
                    .addOnFailureListener(e_clan -> {
                        Log.e(TAG, "Error updating clan document", e_clan);
                        Toast.makeText(getContext(), "Failed to leave clan. Please try again.", Toast.LENGTH_LONG).show();
                        // Re-enable button on failure
                        leaveClanButton.setEnabled(true);
                    });
        });
    }

    private void showAllClansList() {
        progressBar.setVisibility(View.VISIBLE);
        clanDetailsView.setVisibility(View.GONE);
        clanListView.setVisibility(View.GONE);

        db.collection("clans").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Clan> clanList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Clan clan = document.toObject(Clan.class);
                        if (clan != null) {
                            clan.id = document.getId();
                            clanList.add(clan);
                        }
                    }

                    allClansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    AllClansAdapter adapter = new AllClansAdapter(clanList);
                    adapter.setOnJoinClickListener(this);
                    allClansRecyclerView.setAdapter(adapter);


                    progressBar.setVisibility(View.GONE);
                    clanListView.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching clans", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load clan list.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onJoinClick(Clan clan, Button joinButton) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to join a clan.", Toast.LENGTH_SHORT).show();
            return;
        }

        joinButton.setEnabled(false);
        Toast.makeText(getContext(), "Joining " + clan.getName() + "...", Toast.LENGTH_SHORT).show();

        Map<String, Object> clanUpdates = new HashMap<>();
        clanUpdates.put("members." + currentUser.getUid(), true);

        db.collection("clans").document(clan.getId())
                .update(clanUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully added user to clan's member list.");

                    db.collection("users").document(currentUser.getUid())
                            .update("clan", clan.getId())
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Successfully updated user's clan field.");
                                Toast.makeText(getContext(), "Joined " + clan.getName() + "!", Toast.LENGTH_SHORT).show();

                                refreshClanData();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update user's clan field.", e);
                                Toast.makeText(getContext(), "Error: Failed to update your profile.", Toast.LENGTH_LONG).show();
                                joinButton.setEnabled(true); // Re-enable button on failure
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to join clan.", e);
                    Toast.makeText(getContext(), "Error: Could not join clan.", Toast.LENGTH_LONG).show();
                    joinButton.setEnabled(true); // Re-enable button on failure
                });
    }

    public void refreshClanData() {
        Log.d(TAG, "Refreshing clan data...");
        checkUserClanStatus();
    }
}

