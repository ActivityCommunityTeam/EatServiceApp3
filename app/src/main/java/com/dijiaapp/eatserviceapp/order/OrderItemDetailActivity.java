package com.dijiaapp.eatserviceapp.order;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.Order;
import com.dijiaapp.eatserviceapp.data.OrderDishes;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.kaizhuo.SeatFragment;
import com.dijiaapp.eatserviceapp.network.Network;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OrderItemDetailActivity extends AppCompatActivity {

    @BindView(R.id.order_detail_time)
    TextView orderDetailTime;
    @BindView(R.id.order_detail_order_number)
    TextView orderDetailOrderNumber;
    @BindView(R.id.order_detail_server)
    TextView orderDetailServer;
    @BindView(R.id.order_detail_mark_tv)
    TextView orderDetailMarkTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.order_detail_food_container)
//    LinearLayout orderDetailFoodContainer;
    //    @BindView(R.id.order_detail_sum)
//    TextView orderDetailSum;
//    @BindView(R.id.order_detail_totalPrice)
//    TextView orderDetailTotalPrice;
    @BindView(R.id.content_order_detail)
    LinearLayout contentOrderDetail;
    @BindView(R.id.order_detail_seatName_tv)
    TextView orderDetailSeatNameTv;
    @BindView(R.id.order_detail_dinnerNum_tv)
    TextView orderDetailDinnerNumTv;
    @BindView(R.id.order_detail_seatNum_tv)
    TextView orderDetailSeatNumTv;
    @BindView(R.id.order_detail_food_list)
    RecyclerView orderDetailFoodList;
    private List<OrderDishes> dishes;
    private Subscription subscription;
    private Unbinder mUnbinder;
    private RealmResults<Cart> carts;
    private Realm realm;
    private double mSumPrice;
    private int mSum;
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderitem_detail);
        mUnbinder = ButterKnife.bind(this);
        mCompositeSubscription = new CompositeSubscription();
        setToolbar();

        long _orderId = getIntent().getLongExtra("_orderId", -1);
        int _seatId = getIntent().getIntExtra("_seatId", -1);
        Log.i("Daniel", "---_orderId---" + _orderId);
        if (_orderId != -1) {
            getOrderDetail(_orderId);
        } else {
            Toast.makeText(this, "_orderId为-1", Toast.LENGTH_SHORT).show();
        }
//        realm = Realm.getDefaultInstance();
//        Log.i("Daniel","---_seatId---"+_seatId);
//        carts = realm.where(Cart.class).equalTo("seatId", _seatId).findAll();
//        Log.i("Daniel","---carts.size()---"+carts.size());

    }

    private void setFoodListView(List<OrderDishes> dishes) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderDetailFoodList.setLayoutManager(linearLayoutManager);
        OrderItemDetailAdapter cartListAdapter = new OrderItemDetailAdapter(this,dishes);
        orderDetailFoodList.setAdapter(cartListAdapter);



//        for (OrderDishes orderDishes : dishes) {
//            orderDishes orderDishes1
//            LinearLayout foodItem = (LinearLayout) LayoutInflater.from(OrderItemDetailActivity.this)
//                    .inflate(R.layout.food_listitem, orderDetailFoodContainer, false);
//            TextView name = (TextView) foodItem.findViewById(R.id.foodName);
//            TextView number = (TextView) foodItem.findViewById(R.id.number);
//            TextView money = (TextView) foodItem.findViewById(R.id.moneyTv);
//            name.setText(orderDishes.getDishesName());
//            double _totalPrice = orderDishes.getTotalPrice();
//            number.setText("" + orderDishes.getOrderNum());
//            money.setText("￥" + _totalPrice);
//            mSumPrice += _totalPrice;
//            orderDetailFoodContainer.addView(foodItem);
//
//        }
//        if (!isAddFood) {
//            order.setOrdreTotal(orderMoney);
//            order.setDishes(dishesList);
//        }
    }

    /**
     * 设置toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("订单详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getOrderDetail(long _orderId) {
        subscription = Network.getOrderService().orderDetail(_orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Order>() {
                    @Override
                    public void onCompleted() {

                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {
                        Log.i("Daniel", "获取订单详情失败！");

                    }

                    @DebugLog
                    @Override
                    public void onNext(Order order) {
                        setOrderDetail(order);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    /**
     * 订单信息
     */
    private void setOrderDetail(Order order) {
        orderDetailSeatNameTv.setText(order.getSeatName());
        orderDetailTime.setText("开台时间：" + order.getOrderTime());
        orderDetailOrderNumber.setText("订单号：" + order.getOrderHeaderNo());
        orderDetailServer.setText("服务人员：" + order.getWaiterName());
        orderDetailDinnerNumTv.setText("就餐人数：" + order.getDinnerNum());
        //通过座位名找到Seat对应的model
        Seat _seat = SeatFragment.getSeat_order(order.getSeatName());
        orderDetailSeatNumTv.setText("" + _seat.getContainNum());
        orderDetailMarkTv.setText(order.getRemark());
        Log.i("Daniel", "------");
        dishes = order.getDishes();
        Log.i("Daniel", "---dishes.size()---" + dishes.size());
        setFoodListView(dishes);
//        orderDetailSum.setText("共" + dishes.size() + "份");
//        orderDetailTotalPrice.setText("￥" + mSumPrice);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mCompositeSubscription.unsubscribe();
    }
}
