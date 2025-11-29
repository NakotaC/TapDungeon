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

public class AllClansAdapter extends RecyclerView.Adapter<AllClansAdapter.ClanViewHolder> {

    public interface OnJoinClickListener {
        void onJoinClick(Clan clan, Button joinButton);
    }

    private final List<Clan> clanList;
    private OnJoinClickListener listener;

    public AllClansAdapter(List<Clan> clanList) {
        this.clanList = clanList;
    }
    public void setOnJoinClickListener(OnJoinClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ClanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clan, parent, false);
        return new ClanViewHolder(view);
    }

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
