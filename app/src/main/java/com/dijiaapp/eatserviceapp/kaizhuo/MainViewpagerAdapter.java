package com.dijiaapp.eatserviceapp.kaizhuo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by wjy on 16/8/15.
 *
 */
public class MainViewpagerAdapter extends FragmentPagerAdapter {
    public MainViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return SeatFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "全部桌位";
            case 1:
                return "大厅";
            case 2:
                return "包间";
        }
        return super.getPageTitle(position);
    }
}
