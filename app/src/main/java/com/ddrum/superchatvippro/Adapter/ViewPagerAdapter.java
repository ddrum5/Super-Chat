package com.ddrum.superchatvippro.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ddrum.superchatvippro.view.fragment.ChatListFragment;
import com.ddrum.superchatvippro.view.fragment.FriendsListFragment;
import com.ddrum.superchatvippro.view.fragment.PeopleFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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
    public int getCount() {
        return 3;
    }
}