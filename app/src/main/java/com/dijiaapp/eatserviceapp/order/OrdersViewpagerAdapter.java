package com.dijiaapp.eatserviceapp.order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dijiaapp.eatserviceapp.kaizhuo.SeatFragment;

/**
 * Created by wjy on 16/8/15.
 *
 */
public class OrdersViewpagerAdapter extends FragmentPagerAdapter {
    public OrdersViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return OrderItemFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "开始中";
            case 1:
                return "已完成";

        }
        return super.getPageTitle(position);
    }
}
