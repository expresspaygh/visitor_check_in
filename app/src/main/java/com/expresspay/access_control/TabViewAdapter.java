package com.expresspay.access_control;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabViewAdapter extends FragmentPagerAdapter {
    int totalTabs;
    Context context;

    public TabViewAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                TotalCheckedIn totalCheckedIn = new TotalCheckedIn();
                return totalCheckedIn;

            case 1:
                TotalCheckedOut totalCheckedOut = new TotalCheckedOut();
                return totalCheckedOut;

                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}


