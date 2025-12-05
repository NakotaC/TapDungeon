package com.example.yourapp.adapters;

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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<InboxItemInterface> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(InboxItemInterface event);
    }

    public EventsAdapter(List<InboxItemInterface> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_recycler_view, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        InboxItemInterface event = events.get(position);
        holder.bind(event, listener);
    }

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
