package com.dijiaapp.eatserviceapp.order;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.network.Network;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderItemFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    @BindView(R.id.orderitem_recyclerview)
    RecyclerView mOrderitemRecyclerview;
    Unbinder unbinder;
    private int type;

    Realm realm;
    long hotelId;
    private Subscription subscription;
    private OrdersItemAdapter ordersItemAdapter;

    public OrderItemFragment() {
        // Required empty public constructor
    }

    public static OrderItemFragment newInstance(int type) {
        OrderItemFragment fragment = new OrderItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        hotelId = userInfo.getHotelId();

        getOrders();

    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    /**
     * 获取订单
     */
    @DebugLog
    private void getOrders() {
        subscription = Network.getOrderService().listOrder(hotelId).subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<OrderInfo>, Observable<OrderInfo>>() {
                    @Override
                    public Observable<OrderInfo> call(List<OrderInfo> orderInfos) {
                        return Observable.from(orderInfos);
                    }
                })
                .filter(new Func1<OrderInfo, Boolean>() {
                    @Override
                    public Boolean call(OrderInfo orderInfo) {
                        if (type == 0)
                            return orderInfo.getStatusId().equals("01") || orderInfo.getStatusId().equals("02");
                        else
                            return orderInfo.getStatusId().equals("03");
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OrderInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {

                    }

                    @DebugLog
                    @Override
                    public void onNext(List<OrderInfo> orderInfos) {
                        ordersItemAdapter.setOrderInfos(orderInfos);
                    }
                });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_item, container, false);
        unbinder = ButterKnife.bind(this, view);
        ordersItemAdapter = new OrdersItemAdapter();
        mOrderitemRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderitemRecyclerview.setAdapter(ordersItemAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
