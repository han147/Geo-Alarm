package com.hanjeong.android.geo_alarm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hanjeong on 2017. 2. 8..
 */

public class PagerAdapter extends FragmentStatePagerAdapter{
    int numberOfTabs;
    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numberOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AlarmListFragment tab1 = new AlarmListFragment();
                return tab1;
            case 1:
                ToDoListFragment tab2 = new ToDoListFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
