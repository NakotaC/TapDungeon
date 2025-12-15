// src/main/java/com/example/tapdungeon/social/AllClansAdapter.java
package com.example.tapdungeon.social.clan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tapdungeon.R;
import java.util.List;

/**
 * Adapter for the all clans list in the social dialog.
 */
public class AllClansAdapter extends RecyclerView.Adapter<AllClansAdapter.ClanViewHolder> {

    /**
     * Interface for the join button click listener.
     */
    public interface OnJoinClickListener {
        void onJoinClick(Clan clan, Button joinButton);
    }

    private final List<Clan> clanList;
    private OnJoinClickListener listener;

    /**
     * Constructor for the all clans adapter.
     * @param clanList list of clans
     */
    public AllClansAdapter(List<Clan> clanList) {
        this.clanList = clanList;
    }

    /**
     * Sets the listener for the join button click.
     * @param listener the listener to set
     */
    public void setOnJoinClickListener(OnJoinClickListener listener) {
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder for the adapter.
     */
    @NonNull
    @Override
    public ClanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clan, parent, false);
        return new ClanViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    @Override
    public void onBindViewHolder(@NonNull ClanViewHolder holder, int position) {
        Clan clan = clanList.get(position);
        holder.clanName.setText(clan.getName());

        holder.joinButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJoinClick(clan, holder.joinButton);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return clanList.size();
    }

    static class ClanViewHolder extends RecyclerView.ViewHolder {
        TextView clanName;
        Button joinButton;

        ClanViewHolder(@NonNull View itemView) {
            super(itemView);
            clanName = itemView.findViewById(R.id.item_clan_name);
            joinButton = itemView.findViewById(R.id.join_clan_button);
        }
    }
}
