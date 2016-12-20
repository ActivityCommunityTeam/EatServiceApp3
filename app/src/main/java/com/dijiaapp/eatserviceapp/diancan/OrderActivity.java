package com.dijiaapp.eatserviceapp.diancan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.TimeUtils;
import com.dijiaapp.eatserviceapp.Impl.ListItemSizeChangeLinsener;
import com.dijiaapp.eatserviceapp.Impl.ShopCarDelectAllLinsener;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.View.StrongBottomSheetDialog;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.dijiaapp.eatserviceapp.network.Network.getOrderService;
import static com.dijiaapp.eatserviceapp.network.Network.getSeatService;

public class OrderActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    //    @BindView(R.id.order_name)
//    TextView mOrderName;
    @BindView(R.id.order_time)
    TextView mOrderTime;
    @BindView(R.id.order_number)
    TextView mOrderNumber;
    @BindView(R.id.order_server_name)
    TextView mOrderServerName;
//    @BindView(R.id.food_container)
//    LinearLayout mFoodContainer;
//    @BindView(R.id.order_mark)
//    RelativeLayout mOrderMark;
    @BindView(R.id.food_cart_bt)
    ImageView mFoodCartBt;
    @BindView(R.id.food_money)
    TextView mFoodMoney;
    @BindView(R.id.food_next)
    Button mFoodNext;
    @BindView(R.id.order_detail_seatName_tv)
    TextView orderDetailSeatNameTv;
    @BindView(R.id.order_detail_list)
    RecyclerView orderDetailList;


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
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        UserInfo user = realm.where(UserInfo.class).findFirst();
        isAddFood = intent.getBooleanExtra("addFood", false);
        orderInfo = intent.getParcelableExtra("orderInfo");
        if (isAddFood) {
            seatId = Integer.parseInt(orderInfo.getSeatName());
            dishesList = new ArrayList<>();
            mOrderNumber.setText(orderInfo.getDinnerNum() + "人");
        } else {
            eatNubmer = intent.getIntExtra("number", 0);
            Seat seat = intent.getParcelableExtra("seat");
            seatId = seat.getSeatId();
            order = new Order();
            order.setDinnerNum(eatNubmer);
            order.setHotelId(user.getHotelId());
            order.setUserId(user.getWaiterId());
            order.setSeatName(seatId + "");
            mOrderNumber.setText("就餐人数：" + eatNubmer);
            orderDetailSeatNameTv.setText(seat.getSeatName());
        }
        carts=realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        mOrderTime.setText("开台时间：" + TimeUtils.getCurTimeString());
        mOrderServerName.setText("服务人员：" + user.getWaiterName());
        setFoodListView(carts);


        initBottomSheetDialog();

    }

    StrongBottomSheetDialog mBottomSheetDialog;
    RecyclerView mFoodCartRecyclerview;
    CartRecyclerViewOnOrderAdapter mCartRecyclerViewAdapter;

    public void initBottomSheetDialog() {
        mBottomSheetDialog = new StrongBottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.content_bottomsheet, null, false);
        mFoodCartRecyclerview = (RecyclerView) view.findViewById(R.id.food_cart_recyclerview);


        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        //购物车数据源
        OrderedRealmCollection<Cart> carts2 = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        //初始化购物车列表adapter
        mCartRecyclerViewAdapter = new CartRecyclerViewOnOrderAdapter(this, carts2);

        mCartRecyclerViewAdapter.setListItemSizeChangeLinsener(new ListItemSizeChangeLinsener() {
            @Override
            public void getListItemSize(int size) {
                Log.i("gqf", "size" + size);
                if (size == 1) {
                    if (mBottomSheetDialog != null) {
                        if (mBottomSheetDialog.isShowing()) {
                            mBottomSheetDialog.dismiss();
                        }
                    }
                }

            }
        });
        mFoodCartRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mFoodCartRecyclerview.setAdapter(mCartRecyclerViewAdapter);


        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setSeatId(seatId);
        mBottomSheetDialog.setmShopCarDelectAllLinsener(new ShopCarDelectAllLinsener() {
            @Override
            public void delectAll() {
                mBottomSheetDialog.dismiss();
                mFoodMoney.setText("￥" + 0);
                finish();
            }

            @Override
            public void dimess() {
                mBottomSheetDialog.dismiss();
            }

            @Override
            public void nextOrder() {
                mBottomSheetDialog.dismiss();
                next();
            }
        });
    }

    private void setFoodListView(List<Cart> carts) {
//        MyLayoutManager linearLayoutManager = new MyLayoutManager(this);
//        linearLayoutManager.setAutoMeasureEnabled(true);
//        orderDetailList.setHasFixedSize(false);
        orderDetailList.setLayoutManager(new LinearLayoutManager(this));
        CartListAdapter cartListAdapter = new CartListAdapter(OrderActivity.this, carts);
        orderDetailList.setAdapter(cartListAdapter);


//        for (Cart cart : carts) {
//            DishesListBean dishesListBean = cart.getDishesListBean();
//            LinearLayout foodItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.food_listitem, mFoodContainer, false);
//            TextView name = (TextView) foodItem.findViewById(R.id.foodName);
//            TextView number = (TextView) foodItem.findViewById(R.id.number);
//            TextView money = (TextView) foodItem.findViewById(R.id.moneyTv);
//            name.setText(cart.getDishesListBean().getDishesName());
//            number.setText(cart.getAmount() + "");
//            money.setText("￥" + cart.getMoney());
//
//
//            OrderDishes orderDishes = new OrderDishes();
//            orderDishes.setDishesId(dishesListBean.getId());
//            orderDishes.setDishesName(dishesListBean.getDishesName());
//            orderDishes.setDishesPrice(dishesListBean.getDishesPrice());
//            orderDishes.setDishesUnit(dishesListBean.getDishesUnit() != null ? dishesListBean.getDishesUnit() : "");
//            orderDishes.setOrderNum(cart.getAmount());
//            orderDishes.setTotalPrice(cart.getMoney());
//
//            dishesList.add(orderDishes);
//            orderMoney += cart.getMoney();
//            mFoodContainer.addView(foodItem);
//
//        }
//        if (!isAddFood) {
//            order.setOrdreTotal(orderMoney);
//            order.setDishes(dishesList);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        EventBus.getDefault().unregister(this);
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    @OnClick({R.id.food_next, R.id.food_cart_bt})
    public void onClick(View view) {
        Log.i("Daniel", "---onClick--isAddFood: ----" + isAddFood);
        switch (view.getId()) {
            case R.id.food_cart_bt:

                if (mCartRecyclerViewAdapter.getItemCount() == 0) {

                    Toast.makeText(OrderActivity.this, "您的购物车中没有商品，请添加", Toast.LENGTH_SHORT).show();
                } else {
                    if (mBottomSheetDialog.isShowing()) {
                        mBottomSheetDialog.dismiss();
                    } else {

                        mBottomSheetDialog.show();
                        if (mCartRecyclerViewAdapter.itemHeight == -1) {
                            ViewTreeObserver vto = mFoodCartRecyclerview.getViewTreeObserver();
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    mFoodCartRecyclerview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    mCartRecyclerViewAdapter.itemHeight = mFoodCartRecyclerview.getHeight() / mCartRecyclerViewAdapter.getItemCount();

                                    if (mCartRecyclerViewAdapter.getItemCount() >= 3) {
                                        mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * 3);
                                    } else {
                                        mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * mCartRecyclerViewAdapter.getItemCount());
                                    }
                                }
                            });
                        } else {
                            if (mCartRecyclerViewAdapter.getItemCount() >= 3) {
                                mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * 3);
                            } else {
                                mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * mCartRecyclerViewAdapter.getItemCount());

                            }
                        }
                    }
                }


                break;
            case R.id.food_next:
                next();
                break;
        }

    }

    public void next() {
        if (isAddFood) {
            addFoodOrder();
        } else {
            saveOrder();
            updateSeat();
        }
    }

    private void addFoodOrder() {
        String _dishes = new Gson().toJson(dishesList);
        Log.i("Daniel", "-----orderInfo.getOrderId(): ----" + orderInfo.getOrderId());
        Log.i("Daniel", "-----orderMoney: ----" + orderMoney);
        Log.i("Daniel", "-----_dishes----" + _dishes);

        Subscription addFoodSubscription = Network.getOrderService()
                .addDishes(orderInfo.getOrderId(), orderMoney, _dishes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.i("gqf", "onCompleted");
                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {

                    }

                    @DebugLog
                    @Override
                    public void onNext(ResultInfo resultInfo) {
                        Log.i("gqf", "onNext" + resultInfo.toString());
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

    private void updateSeat() {

        Subscription subscription = getSeatService().updateStatus(order.getSeatName(), "02")
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
     *
     * @param resultInfo
     */
    private void finishOrder(ResultInfo resultInfo) {
        Toast.makeText(OrderActivity.this, resultInfo.getMsg(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(OrderActivity.this, MainActivity.class));
        realm.beginTransaction();
        carts.deleteAllFromRealm();
        realm.commitTransaction();
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cartEventOrder(CartEvent event) {
        Log.i("gqf", "cartEventOrder");
        if (event.getFlag() == 3 || event.getFlag() == 4) {
            refreshCart(event);
        }
    }

    //刷新购物车
    @DebugLog
    private void refreshCart(CartEvent event) {
        int id = event.getDisesBeanId();
        Cart cart = realm.where(Cart.class).equalTo("seatId", seatId).equalTo("dishesListBean.id", id).findFirst();
        //flag 0代表减 1 加。
        if (cart != null) {
            int amount = cart.getAmount();
            if (event.getFlag() == 3) {
                //减分两种情况 还剩一件的时候 直接删除 菜品，否则减一
                if (amount == 1) {
                    realm.beginTransaction();
                    cart.deleteFromRealm();
                    realm.commitTransaction();
                    if (mBottomSheetDialog.isShowing()) {
                        if (mCartRecyclerViewAdapter.getItemCount() <= 3) {
                            if (mCartRecyclerViewAdapter.itemHeight != -1) {
                                mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * (mCartRecyclerViewAdapter.getItemCount() - 1));
                            }
                        }
                    }
                } else {
                    amount--;
                    realm.beginTransaction();
                    cart.setMoney(cart.getDishesListBean().getDishesPrice() * amount);
                    cart.setAmount(amount);
                    realm.commitTransaction();
                }

            } else if (event.getFlag() == 4) {
                amount++;
                realm.beginTransaction();
                cart.setMoney(amount * cart.getDishesListBean().getDishesPrice());
                cart.setAmount(amount);
                realm.commitTransaction();
            }
        } else {

            if (event.getFlag() == 1) {
                realm.beginTransaction();
                DishesListBean disesBean = realm.where(DishesListBean.class).equalTo("id", id).findFirst();
                Cart cartNew = realm.createObject(Cart.class);
                cartNew.setAmount(1);
                cartNew.setDishesListBean(disesBean);
                cartNew.setMoney(disesBean.getDishesPrice());
                cartNew.setTime(Calendar.getInstance().getTime().getTime());
                cartNew.setSeatId(seatId);

                realm.commitTransaction();
            }
        }
        setFoodListView(realm.where(Cart.class).equalTo("seatId", seatId).findAll());
        setCartMoney();
    }

    private void setCartMoney() {
        double money = getMoney();

        mFoodMoney.setText("￥" + money);
        if (mBottomSheetDialog.food_money != null) {
            mBottomSheetDialog.food_money.setText("￥" + money);
        }
    }

    private double getMoney() {
        double money = 0;
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();

        for (Cart c : carts) {
            money += c.getMoney();
        }
        return money;
    }
}
