package com.andrewlaurien.chatApp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andrewlaurien.chatApp.fragments.EventFragment;

/**
 * Created by andrewlaurienrsocia on 12/05/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numberTabs;

    public PagerAdapter(FragmentManager fm, int numberTabs) {
        super(fm);
        this.numberTabs = numberTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EventFragment tab1 = new EventFragment();
                return tab1;
            case 1:
                EventFragment tab2 = new EventFragment();
                return tab2;
            case 2:
                EventFragment tab3 = new EventFragment();
                return tab3;
            case 3:
                EventFragment tab4 = new EventFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberTabs;
    }
}

