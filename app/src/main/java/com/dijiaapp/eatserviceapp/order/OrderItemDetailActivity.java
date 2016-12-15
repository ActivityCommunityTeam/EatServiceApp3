package com.dijiaapp.eatserviceapp.order;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.Order;
import com.dijiaapp.eatserviceapp.data.OrderDishes;
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
    @BindView(R.id.order_detail_food_container)
    LinearLayout orderDetailFoodContainer;
    @BindView(R.id.order_detail_sum)
    TextView orderDetailSum;
    @BindView(R.id.order_detail_totalPrice)
    TextView orderDetailTotalPrice;
    @BindView(R.id.content_order_detail)
    LinearLayout contentOrderDetail;
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

        for (OrderDishes orderDishes : dishes) {
            LinearLayout foodItem = (LinearLayout) LayoutInflater.from(OrderItemDetailActivity.this).inflate(R.layout.food_listitem, orderDetailFoodContainer, false);
            TextView name = (TextView) foodItem.findViewById(R.id.foodName);
            TextView number = (TextView) foodItem.findViewById(R.id.number);
            TextView money = (TextView) foodItem.findViewById(R.id.moneyTv);
            name.setText(orderDishes.getDishesName());
            double _totalPrice = orderDishes.getTotalPrice();
            number.setText(orderDishes.getOrderNum() + orderDishes.getDishesUnit());
            money.setText("￥" + _totalPrice);
            mSumPrice +=_totalPrice;
            orderDetailFoodContainer.addView(foodItem);

        }
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
        toolbar.setNavigationIcon(R.drawable.back);
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
        orderDetailTime.setText("开台时间：" + order.getOrderTime());
        orderDetailOrderNumber.setText("就餐人数：" + order.getDinnerNum());
        orderDetailServer.setText("服务人员：" + order.getWaiterName());
        if (TextUtils.isEmpty(order.getRemark())) {
            orderDetailMarkTv.setText("备注：");
        } else {

            orderDetailMarkTv.setText("备注：" + order.getRemark());
        }
        Log.i("Daniel", "------");
        dishes = order.getDishes();
        Log.i("Daniel", "---dishes.size()---" + dishes.size());
        setFoodListView(dishes);
        orderDetailSum.setText("共"+dishes.size()+"份");
        orderDetailTotalPrice.setText("￥"+mSumPrice);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mCompositeSubscription.unsubscribe();
    }
}
