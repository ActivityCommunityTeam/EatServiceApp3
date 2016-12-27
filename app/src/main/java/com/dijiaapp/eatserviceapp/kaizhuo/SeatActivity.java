package com.dijiaapp.eatserviceapp.kaizhuo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.EatServiceApplication;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 座位详情
 */
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
    @BindView(R.id.order_detail_seatNum_tv)
    TextView orderDetailSeatNumTv;
    @BindView(R.id.order_detail_dinnerNum_tv)
    TextView orderDetailDinnerNumTv;
    private Unbinder mUnbinder;
    private OrderInfo mOrderInfo;
    private Realm realm;
    private long hotelId;
    private CompositeSubscription mCompositeSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EatServiceApplication.getInstance().addActivity(this);//添加activity到集合
        setContentView(R.layout.activity_seat);
        mUnbinder = ButterKnife.bind(this);
        mCompositeSubscription = new CompositeSubscription();
        getData();
        setToolBar();
        getOrderDetail();

        //翻桌点击事件
        RxView.clicks(orderItemDone)
                //防止多点击
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //翻桌事件监听
                        EventBus.getDefault().post(new OrderOverEvent(mOrderInfo));

                    }
                });


    }

    /**
     * 订单详情请求
     */
    private void getOrderDetail() {
        Subscription subscription_orderDetail = Network.getOrderService().orderDetail(mOrderInfo.getOrderId())
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
        mCompositeSubscription.add(subscription_orderDetail);


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
        Subscription subscription_listOrder = Network.getOrderService().listOrder(hotelId)
                .subscribeOn(Schedulers.io())
                //遍历
                .flatMap(new Func1<List<OrderInfo>, Observable<OrderInfo>>() {
                    @Override
                    public Observable<OrderInfo> call(List<OrderInfo> orderInfos) {
                        return Observable.from(orderInfos);
                    }
                })
                //过滤
                .filter(new Func1<OrderInfo, Boolean>() {
                    @Override
                    public Boolean call(OrderInfo orderInfo) {
                        return orderInfo.getOrderId() == mOrderInfo.getOrderId();
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

                    }
                });
        mCompositeSubscription.add(subscription_listOrder);
    }

    @DebugLog
    private void setData(Order order) {
        Seat _seat = SeatFragment.getSeat_order(order.getSeatName());//通过桌位名获取Seat对象
        orderDetailSeatNameTv.setText(order.getSeatName());
        orderDetailDinnerNumTv.setText("就餐人数：" + order.getDinnerNum());
        orderDetailSeatNumTv.setText("" + _seat.getContainNum());
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
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        toolbar.setTitle("座位详情");
        setSupportActionBar(toolbar);
        //返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getData() {
        //接收包裹化对象
        mOrderInfo = getIntent().getParcelableExtra("orderInfo");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mCompositeSubscription.unsubscribe();
    }


    @OnClick({R.id.order_item_jiacan, R.id.order_item_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_item_jiacan:
                //加菜事件监听
                EventBus.getDefault().post(new OrderAddFoodEvent(mOrderInfo));
                break;
            case R.id.order_item_done:

                break;
        }
    }
}
