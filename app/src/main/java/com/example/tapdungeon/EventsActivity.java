package com.example.tapdungeon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.yourapp.adapters.EventsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.tapdungeon.data.model.*;
//import com.example.tapdungeon.adapters.InboxAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private List<InboxItemInterface> inboxItems = new ArrayList<>();

    private FirebaseFirestore db;
    private CollectionReference inboxRef;

    private static final String TAG = "InboxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        db = FirebaseFirestore.getInstance();
        inboxRef = db.collection("inbox_items");

        recyclerView = findViewById(R.id.events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventsAdapter(inboxItems);
        recyclerView.setAdapter(adapter);

        loadInboxItems();
    }

    /**
     * Loads ALL types of inbox items from Firestore:
     * - Mail
     * - Random events
     * - Scheduled events
     * - Friend requests
     */
    private void loadInboxItems() {
        inboxRef.get()
                .addOnSuccessListener(query -> {
                    inboxItems.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        String type = doc.getString("type");

                        if (type == null) continue;

                        InboxItemInterface item = mapDocumentToItem(doc, type);
                        if (item != null) {
                            inboxItems.add(item);
                        }
                    }

                    sortItemsByTimestamp();
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading inbox", e));
    }

    /**
     * Converts a Firestore document to the correct model class
     */
    private InboxItemInterface mapDocumentToItem(QueryDocumentSnapshot doc, String type) {

        switch (InboxItemType.valueOf(type)) {

//            case MAIL:
//                return doc.toObject(MailItem.class);

            case RANDOM_EVENT:
                return doc.toObject(RandomEvent.class);

            case SCHEDULED_EVENT:
                return doc.toObject(ScheduledEvent.class);

//            case FRIEND_REQUEST:
//                return doc.toObject(FriendRequestItem.class);

            default:
                return null;
        }
    }

    /**
     * Sort items newest → oldest
     */
    private void sortItemsByTimestamp() {
        inboxItems.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
    }
}

