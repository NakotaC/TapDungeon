package com.example.tapdungeon.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapdungeon.data.model.InboxItemInterface;
import com.example.tapdungeon.R;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the events list in the inbox dialog.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<InboxItemInterface> events;
    private OnEventClickListener listener;

    /**
     * Interface for the event click listener.
     */
    public interface OnEventClickListener {
        void onEventClick(InboxItemInterface event);
    }

    /**
     * Constructor for the events adapter.
     * @param events list of events
     * @param listener listener for the event click
     */
    public EventsAdapter(List<InboxItemInterface> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder for the adapter.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        InboxItemInterface event = events.get(position);
        holder.bind(event, listener);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }


    // ----------------------------
    // ViewHolder
    // ----------------------------
    static class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtType, txtDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.event_title);
            txtType = itemView.findViewById(R.id.event_type);
            txtDate = itemView.findViewById(R.id.event_date);
        }

        void bind(InboxItemInterface event, OnEventClickListener listener) {

            //txtTitle.setText(event.getTitle());
            txtType.setText(event.getItemType().name());

            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            txtDate.setText(sdf.format(event.getTimestamp()));

            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
