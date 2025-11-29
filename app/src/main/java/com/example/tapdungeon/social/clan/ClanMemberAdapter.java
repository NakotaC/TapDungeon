package com.example.tapdungeon.social.clan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tapdungeon.R;
import java.util.List;

public class ClanMemberAdapter extends RecyclerView.Adapter<ClanMemberAdapter.MemberViewHolder> {

    private final List<ClanMember> memberList;

    public ClanMemberAdapter(List<ClanMember> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clan_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ClanMember member = memberList.get(position);
        holder.username.setText(member.getUsername());
        holder.level.setText("Level: " + member.getLevel());
    }

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
