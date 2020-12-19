package com.ddrum.superchatvippro.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ddrum.superchatvippro.view.fragment.ChatListFragment;
import com.ddrum.superchatvippro.view.fragment.FriendsListFragment;
import com.ddrum.superchatvippro.view.fragment.PeopleFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChatListFragment();
            case 1:
                return new FriendsListFragment();
            case 2:
                return new PeopleFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}