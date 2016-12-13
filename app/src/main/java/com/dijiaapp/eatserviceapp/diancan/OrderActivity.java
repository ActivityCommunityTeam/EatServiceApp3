package com.dijiaapp.eatserviceapp.diancan;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.TimeUtils;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.DishesListBean;
import com.dijiaapp.eatserviceapp.data.Order;
import com.dijiaapp.eatserviceapp.data.OrderDishes;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.ResultInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.kaizhuo.MainActivity;
import com.dijiaapp.eatserviceapp.network.Network;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.dijiaapp.eatserviceapp.network.Network.getOrderService;

public class OrderActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.order_name)
    TextView mOrderName;
    @BindView(R.id.order_time)
    TextView mOrderTime;
    @BindView(R.id.order_number)
    TextView mOrderNumber;
    @BindView(R.id.order_server_name)
    TextView mOrderServerName;
    @BindView(R.id.food_container)
    LinearLayout mFoodContainer;
    @BindView(R.id.order_mark)
    RelativeLayout mOrderMark;
    @BindView(R.id.food_cart_bt)
    ImageView mFoodCartBt;
    @BindView(R.id.food_money)
    TextView mFoodMoney;
    @BindView(R.id.food_next)
    Button mFoodNext;
    private Realm realm;
    private double orderMoney;
    private Order order;
    private List<OrderDishes> dishesList = new ArrayList<>();
    private RealmResults<Cart> carts;
    private int seatId;
    private boolean isAddFood;
    private int eatNubmer;
    private OrderInfo orderInfo;
    private CompositeSubscription compositeSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        UserInfo user = realm.where(UserInfo.class).findFirst();
        isAddFood = intent.getBooleanExtra("addFood", false);
        if (isAddFood) {
            orderInfo = intent.getParcelableExtra("orderInfo");
            seatId = Integer.parseInt(orderInfo.getSeatName());

            dishesList = new ArrayList<>();
        } else {
            eatNubmer = intent.getIntExtra("number", 0);
            Seat seat = intent.getParcelableExtra("seat");
            seatId = seat.getSeatId();
            order = new Order();
            order.setDinnerNum(eatNubmer);
            order.setHotelId(user.getHotelId());
            order.setUserId(user.getWaiterId());
            order.setSeatName(seatId + "");
        }
        carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        mOrderTime.setText(TimeUtils.getCurTimeString());
        mOrderName.setText(user.getWaiterName());
        mOrderNumber.setText(eatNubmer + "人");

        setFoodListView(carts);


    }

    private void setFoodListView(List<Cart> carts) {

        for (Cart cart : carts) {
            DishesListBean dishesListBean = cart.getDishesListBean();
            ConstraintLayout foodItem = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.food_listitem, mFoodContainer, false);
            TextView name = (TextView) foodItem.findViewById(R.id.foodName);
            TextView number = (TextView) foodItem.findViewById(R.id.number);
            TextView money = (TextView) foodItem.findViewById(R.id.moneyTv);
            name.setText(cart.getDishesListBean().getDishesName());
            number.setText(cart.getAmount() + dishesListBean.getDishesUnit());
            money.setText("￥" + cart.getMoney());


            OrderDishes orderDishes = new OrderDishes();
            orderDishes.setDishesId(dishesListBean.getId());
            orderDishes.setDishesName(dishesListBean.getDishesName());
            orderDishes.setDishesPrice(dishesListBean.getDishesPrice());
            orderDishes.setDishesUnit(dishesListBean.getDishesUnit() != null ? dishesListBean.getDishesUnit() : "");
            orderDishes.setOrderNum(cart.getAmount());
            orderDishes.setTotalPrice(cart.getMoney());

            dishesList.add(orderDishes);
            orderMoney += cart.getMoney();
            mFoodContainer.addView(foodItem);

        }
        if (!isAddFood) {
            order.setOrdreTotal(orderMoney);
            order.setDishes(dishesList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    @OnClick(R.id.food_next)
    public void onClick() {
        if (isAddFood) {
            addFoodOrder();
        } else
            saveOrder();
    }

    private void addFoodOrder() {
       Subscription addFoodSubscription =  Network.getOrderService().addDishes(orderInfo.getOrderId(), orderMoney, new Gson().toJson(dishesList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @DebugLog
                    @Override
                    public void onNext(ResultInfo resultInfo) {
                       finishOrder(resultInfo);
                    }
                });
        compositeSubscription.add(addFoodSubscription);
    }

    private void saveOrder() {
       Subscription subscription = getOrderService().saveOrder(order.getHotelId(), order.getUserId(), order.getOrdreTotal(), order.getDinnerNum(), order.getSeatName(), new Gson().toJson(order.getDishes()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        compositeSubscription.add(subscription);
    }

    Observer<ResultInfo> observer = new Observer<ResultInfo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @DebugLog
        @Override
        public void onNext(ResultInfo resultInfo) {
            if (resultInfo.getCode() == 1001) {
                finishOrder(resultInfo);

            }
        }
    };

    /**
     * 结束本次订单
     * @param resultInfo
     */
    private void finishOrder(ResultInfo resultInfo) {
        Toast.makeText(OrderActivity.this, resultInfo.getMsg(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(OrderActivity.this, MainActivity.class));
        realm.beginTransaction();
        carts.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
