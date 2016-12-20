package com.dijiaapp.eatserviceapp.kaizhuo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Order;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.network.Network;
import com.dijiaapp.eatserviceapp.order.OrderAddFoodEvent;
import com.dijiaapp.eatserviceapp.order.OrderOverEvent;
import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SeatActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.order_detail_seatName_tv)
    TextView orderDetailSeatNameTv;
    @BindView(R.id.seat_stause)
    TextView seatStause;
    @BindView(R.id.seat_time)
    TextView seatTime;
    @BindView(R.id.seat_orderNumber)
    TextView seatOrderNumber;
    @BindView(R.id.seat_waiter)
    TextView seatWaiter;
    @BindView(R.id.order_item_jiacan)
    Button orderItemJiacan;
    @BindView(R.id.order_item_done)
    Button orderItemDone;
    @BindView(R.id.seatName)
    LinearLayout seatName;
    private Unbinder mUnbinder;
    private OrderInfo mOrderInfo;
    private Realm realm;
    private long hotelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        mUnbinder = ButterKnife.bind(this);
        getData();
        setToolBar();
        Network.getOrderService().orderDetail(mOrderInfo.getOrderId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Order>() {
                    @Override
                    public void onCompleted() {

                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {

                    }

                    @DebugLog
                    @Override
                    public void onNext(Order order) {
                        setData(order);
                    }
                });


        RxView.clicks(orderItemDone)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        EventBus.getDefault().post(new OrderOverEvent(mOrderInfo));

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        hotelId = userInfo.getHotelId();
        getOrders();
    }

    private void getOrders() {
        Network.getOrderService().listOrder(hotelId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<OrderInfo>, Observable<OrderInfo>>() {
                    @Override
                    public Observable<OrderInfo> call(List<OrderInfo> orderInfos) {
                        return Observable.from(orderInfos);
                    }
                })
                .filter(new Func1<OrderInfo, Boolean>() {
                    @Override
                    public Boolean call(OrderInfo orderInfo) {
                            return orderInfo.getOrderId()==mOrderInfo.getOrderId();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        mOrderInfo = orderInfo;
                        Log.i("Daniel", "---" + mOrderInfo.getSeatName());
                        Log.i("Daniel", "---" + mOrderInfo.getOrderId());
                        Log.i("Daniel", "---" + mOrderInfo.getWaiterName());
                        Log.i("Daniel", "---" + mOrderInfo.getStatusId());
                        Log.i("Daniel", "---" + mOrderInfo.getOrderHeaderNo());


                    }
                });



    }

    @DebugLog
    private void setData(Order order) {
        Seat _seat = SeatFragment.getSeat_order(order.getSeatName());
        orderDetailSeatNameTv.setText(order.getSeatName());
        seatOrderNumber.setText("订单号：" + order.getOrderHeaderNo());
        seatWaiter.setText("操作员：" + order.getWaiterName());
        seatTime.setText("开台时间：" + order.getOrderTime());
        if (_seat.getUseStatus().equals("02")) {
            seatStause.setText("状态：使用中");
        } else {
            seatStause.setText("状态：可用");
        }
    }

    private void setToolBar() {
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("座位详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getData() {
        mOrderInfo = getIntent().getParcelableExtra("orderInfo");
        Log.i("Daniel", "---" + mOrderInfo.getSeatName());
        Log.i("Daniel", "---" + mOrderInfo.getOrderId());
        Log.i("Daniel", "---" + mOrderInfo.getWaiterName());
        Log.i("Daniel", "---" + mOrderInfo.getStatusId());
        Log.i("Daniel", "---" + mOrderInfo.getOrderHeaderNo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }


    @OnClick({R.id.order_item_jiacan, R.id.order_item_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_item_jiacan:
                EventBus.getDefault().post(new OrderAddFoodEvent(mOrderInfo));
                break;
            case R.id.order_item_done:

                break;
        }
    }
}
