// com/example/tapdungeon/social/FriendsFragment.java
package com.example.tapdungeon.social;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapdungeon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private RecyclerView friendsRecyclerView;
    private EditText friendNameInput;
    private Button addFriendButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FriendAdapter adapter;
    private final List<Friend> friendList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);
        friendNameInput = view.findViewById(R.id.friend_name_input);
        addFriendButton = view.findViewById(R.id.add_friend_button);
        progressBar = view.findViewById(R.id.friends_progress_bar);

        setupRecyclerView();

        addFriendButton.setOnClickListener(v -> addFriendByUsername());

        loadFriendsList();
    }

    private void setupRecyclerView() {
        adapter = new FriendAdapter(friendList);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(adapter);
    }

    private void loadFriendsList() {
        if (currentUser == null) return;
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    Map<String, Object> friendsMap = (Map<String, Object>) documentSnapshot.get("friends");
                    if (friendsMap == null || friendsMap.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        friendList.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    List<String> friendUids = new ArrayList<>(friendsMap.keySet());
                    if (friendUids.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    db.collection("users").whereIn("uid", friendUids).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                friendList.clear();
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    Friend friend = doc.toObject(Friend.class);
                                    if (friend != null) {
                                        friendList.add(friend);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load friends.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading friends list", e);
                });
    }

    private void addFriendByUsername() {
        String username = friendNameInput.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            friendNameInput.setError("Username cannot be empty.");
            return;
        }
        if (username.equalsIgnoreCase(currentUser.getDisplayName())) {
            friendNameInput.setError("You cannot add yourself.");
            return;
        }

        addFriendButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").whereEqualTo("username", username).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "User '" + username + "' not found.", Toast.LENGTH_SHORT).show();
                        resetAddFriendButton();
                        return;
                    }

                    DocumentSnapshot friendDoc = task.getResult().getDocuments().get(0);
                    String friendUid = friendDoc.getId();
                    DocumentReference currentUserRef = db.collection("users").document(currentUser.getUid());
                    DocumentReference friendUserRef = db.collection("users").document(friendUid);

                    WriteBatch batch = db.batch();
                    batch.update(currentUserRef, "friends." + friendUid, true);
                    batch.update(friendUserRef, "friends." + currentUser.getUid(), true);

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Added " + username + " as a friend!", Toast.LENGTH_SHORT).show();
                        friendNameInput.setText("");
                        resetAddFriendButton();
                        loadFriendsList(); // Refresh the list
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to add friend.", Toast.LENGTH_SHORT).show();
                        resetAddFriendButton();
                        Log.e(TAG, "Failed to commit friend batch write", e);
                    });
                });
    }

    private void resetAddFriendButton() {
        addFriendButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}

