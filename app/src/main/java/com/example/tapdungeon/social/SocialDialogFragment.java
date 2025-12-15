package com.example.tapdungeon.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tapdungeon.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Fragment for the social dialog.
 */
public class SocialDialogFragment extends DialogFragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    /**
     * Creates a new instance of the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_dialog, container, false);

        tabLayout = view.findViewById(R.id.social_tab_layout);
        viewPager = view.findViewById(R.id.social_view_pager);

        // Create an adapter that will return a fragment for each of the two primary sections.
        SocialPagerAdapter pagerAdapter = new SocialPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Link the TabLayout and the ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Clans");
                    } else {
                        tab.setText("Friends");
                    }
                }
        ).attach();

        return view;
    }

    /**
     * Sets the size of the dialog.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}