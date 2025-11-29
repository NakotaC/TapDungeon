package com.example.tapdungeon.social;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tapdungeon.social.clan.ClanFragment;

public class SocialPagerAdapter extends FragmentStateAdapter {

    public SocialPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        if (position == 0) {
            return new ClanFragment();
        } else {
            return new FriendsFragment();
        }
    }

    @Override
    public int getItemCount() {
        // We have two tabs
        return 2;
    }
}