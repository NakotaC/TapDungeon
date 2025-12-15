package com.example.tapdungeon.social;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tapdungeon.social.clan.ClanFragment;

/**
 * Adapter for the social dialog.
 */
public class SocialPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the social pager adapter.
     * @param fragment fragment that contains the adapter
     */
    public SocialPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Creates a new fragment based on the position.
     * @param position position of the fragment
     * @return new fragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ClanFragment();
        } else {
            return new FriendsFragment();
        }
    }

    /**
     * Returns the number of tabs.
     * @return number of tabs
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}