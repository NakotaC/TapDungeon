package com.example.tapdungeon.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tapdungeon.R;
import java.util.List;

/**
 * Adapter for the friends list in the social dialog.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private final List<Friend> friendList;

    /**
     *  Constructor for the friend adapter.
     * @param friendList list of friends
     */
    FriendAdapter(List<Friend> friendList) {
        this.friendList = friendList;
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
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.username.setText(friend.getUsername());
        holder.level.setText("Level: " + friend.getLevel());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    /**
     * ViewHolder for the friend adapter.
     */
    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView level;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.friend_username);
            level = itemView.findViewById(R.id.friend_level);
        }
    }
}
