package com.example.tapdungeon.social.clan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tapdungeon.R;
import java.util.List;

/**
 * Adapter for the clan members list in the social dialog.
 */
public class ClanMemberAdapter extends RecyclerView.Adapter<ClanMemberAdapter.MemberViewHolder> {

    private final List<ClanMember> memberList;

    /**
     * Constructor for the clan member adapter.
     * @param memberList
     */
    public ClanMemberAdapter(List<ClanMember> memberList) {
        this.memberList = memberList;
    }

    /**
     * Creates a new ViewHolder for the adapter.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clan_member, parent, false);
        return new MemberViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ClanMember member = memberList.get(position);
        holder.username.setText(member.getUsername());
        holder.level.setText("Level: " + member.getLevel());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView level;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.member_username);
            level = itemView.findViewById(R.id.member_level);
        }
    }
}
