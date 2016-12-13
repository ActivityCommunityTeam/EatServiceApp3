package com.dijiaapp.eatserviceapp.order;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dijiaapp.eatserviceapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {


    @BindView(R.id.order_tablayout)
    TabLayout mOrderTablayout;
    @BindView(R.id.order_viewpager)
    ViewPager mOrderViewpager;
    Unbinder unbinder;


    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public static OrdersFragment newInstance() {
        OrdersFragment ordersFragment = new OrdersFragment();
        return ordersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        OrdersViewpagerAdapter adapter = new OrdersViewpagerAdapter(getChildFragmentManager());
        mOrderViewpager.setAdapter(adapter);
        mOrderTablayout.setupWithViewPager(mOrderViewpager);

        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
}
