package com.dijiaapp.eatserviceapp.kaizhuo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.blankj.utilcode.utils.KeyboardUtils;
import com.blankj.utilcode.utils.StringUtils;
import com.dijiaapp.eatserviceapp.EatServiceApplication;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.ResultInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.diancan.FoodActivity;
import com.dijiaapp.eatserviceapp.my.MyFragment;
import com.dijiaapp.eatserviceapp.network.Network;
import com.dijiaapp.eatserviceapp.order.OrderAddFoodEvent;
import com.dijiaapp.eatserviceapp.order.OrderDetailActivity;
import com.dijiaapp.eatserviceapp.order.OrderOverEvent;
import com.dijiaapp.eatserviceapp.order.OrdersFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.dijiaapp.eatserviceapp.R.id.toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String HOME_TAG = "home_flag";
    private static final String ORDERS_TAG = "orders_flag";
    private static final String MY_TAG = "my_flag";
    private static final String FOUND_TAG = "found_flag";
    private static final int CONTENT_FOUND = 3;

    private static final int CONTENT_ORDERS = 2;
    private static final int CONTENT_MY = 4;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    private static final int CONTENT_HOME = 1;
    private CompositeSubscription mcompositeSubscription;
    private Realm realm;
    private UserInfo mUser;
    /**
     * 设置toolbar
     */
    private void setToolbar(String str) {

        mToolbar.setTitle(str);
        setSupportActionBar(mToolbar);
        /*toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EatServiceApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        realm = Realm.getDefaultInstance();
        mUser = realm.where(UserInfo.class).findFirst();
        ButterKnife.bind(this);
        String _str = getResources().getString(R.string.firstPage);
        setToolbar(_str);

        mcompositeSubscription = new CompositeSubscription();


        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        setContent(CONTENT_HOME);
                        break;
                    case R.id.tab_order:
                        setContent(CONTENT_ORDERS);
                        break;

                    case R.id.tab_mine:
                        setContent(CONTENT_MY);
                        break;
                }
            }
        });
    }
    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出服务员app", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
//            MyConfig.clearSharePre(this, "users");
            EatServiceApplication.getInstance().exit();
        }
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void enterSeatNumberActivity(EnterActivityEvent enterActivityEvent) {

        if (enterActivityEvent.getSeat() != null) {

            Seat seat = enterActivityEvent.getSeat();
            if (seat.getUseStatus().equals("01")) {
                Intent intent = new Intent(this, enterActivityEvent.getGotoClass());
                intent.putExtra("Seat", seat);
                startActivity(intent);
            } else {
                isUsed(seat);
            }

        }
        if (!StringUtils.isEmpty(enterActivityEvent.getId())) {

            OrderDetailActivity.startDetailActivity(this, enterActivityEvent.getId());
        }

    }


    /**
     * 获取座位使用状态
     * @param seat
     */
    @DebugLog
    private void isUsed(final Seat seat) {
        getOrderInfo(seat);
        isOrder(seat);
    }

    private void isOrder(final Seat seat) {
        Network.getSeatService().isOrder(seat.getSeatId() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i("Daniel", "---aBoolean---" +aBoolean);
                        if (!aBoolean){
                            //座位正在使用，进入座位详情
                            Intent _intent = new Intent(MainActivity.this, UnAddOrderSeatActivity.class);
                            _intent.putExtra("seat", seat);
                            startActivity(_intent);
                        }
                    }
                });

    }

    private void getOrderInfo(final Seat seat) {
        Subscription isusedSup = Network.getSeatService().getOrderInfo(seat.getSeatId() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                    @DebugLog
                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        //如果座位空闲则进行开桌
                        Log.i("Daniel", "---orderInfo---" +orderInfo);
                        if (orderInfo != null && orderInfo.getOrderId() == 0) {
                            Intent intent = new Intent(MainActivity.this, SeatEatNumberActivity.class);
                            intent.putExtra("Seat", seat);
                            startActivity(intent);
                        } else {
                            //座位正在使用，进入座位详情
                            Intent _intent = new Intent(MainActivity.this, SeatActivity.class);
                            _intent.putExtra("orderInfo", orderInfo);
                            startActivity(_intent);
//                            Toast.makeText(LibMainActivity.this, "1111", Toast.LENGTH_SHORT).show();
                        }
//                        if (orderInfo==null){
//                            //座位正在使用，进入座位详情
//                            Intent _intent = new Intent(MainActivity.this, UnAddOrderSeatActivity.class);
//                            _intent.putExtra("seat", seat);
//                            startActivity(_intent);
//                        }
                    }
                });
        mcompositeSubscription.add(isusedSup);
    }


    /**
     * 翻桌事件接收
     * @param orderOverEvent
     */
    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderOverEvent(OrderOverEvent orderOverEvent) {
        OrderInfo orderInfo = orderOverEvent.getOrderInfo();
        String _status = orderInfo.getStatusId();
        Log.i("Daniel", "---_status---" + _status);
        if (_status.equals("01")) {
            Toast.makeText(this, "订单未确认，不可翻桌！", Toast.LENGTH_SHORT).show();
        } else if (_status.equals("02")) {
            String id = orderInfo.getSeatName();

            submitOrderOver(id);
        }
    }

    /**
     * 接收加菜事件
     * @param orderAddFoodEvent
     */
    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderAddFoodEvent(OrderAddFoodEvent orderAddFoodEvent) {
        OrderInfo orderInfo = orderAddFoodEvent.getOrderInfo();
        String _getWaiterNameFromOrderInfo = orderInfo.getWaiterName();
        String _getWaiterNameFromUser = mUser.getWaiterName();
        Log.i("Daniel","---_getWaiterNameFromOrderInfo----"+_getWaiterNameFromOrderInfo);
        Log.i("Daniel","---mUser.getWaiterName()----"+_getWaiterNameFromUser);
        if (_getWaiterNameFromOrderInfo.equals(_getWaiterNameFromUser)){
            FoodActivity.startFoodActivity(this, orderInfo);
        }else {
            Toast.makeText(this, "不能加餐！", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 更新座位状态
     * @param id
     */
    @DebugLog
    private void submitOrderOver(String id) {
        Subscription subscription = Network.getSeatService().updateStatus(id, "01")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultInfo>() {
                    @Override
                    public void onCompleted() {

                    }
                    @DebugLog
                    @Override
                    public void onError(Throwable e) {

                    }
                    @DebugLog
                    @Override
                    public void onNext(ResultInfo resultInfo) {
                        Toast.makeText(MainActivity.this, "正在翻桌！", Toast.LENGTH_SHORT).show();
                    }
                });

        mcompositeSubscription.add(subscription);

    }

    /**
     * 页面切换
     * @param contentHome
     */
    private void setContent(int contentHome) {
        switch (contentHome) {
            case CONTENT_HOME:
                String home_str = getResources().getString(R.string.firstPage);
                setToolbar(home_str);
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance("1", "2");
                }
                setFragment(homeFragment, HOME_TAG);
                break;
            case CONTENT_ORDERS:
                String orders_str = getResources().getString(R.string.foodOrder);
                setToolbar(orders_str);
                OrdersFragment orderFragment = (OrdersFragment) getSupportFragmentManager().findFragmentByTag(ORDERS_TAG);
                if (orderFragment == null) {
                    orderFragment = OrdersFragment.newInstance();
                }
                setFragment(orderFragment, ORDERS_TAG);
                break;
            case CONTENT_MY:
                String my_str = getResources().getString(R.string.my);
                setToolbar(my_str);
                MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(MY_TAG);
                if (myFragment == null) {
                    myFragment = MyFragment.newInstance();
                }
                setFragment(myFragment, MY_TAG);
                break;
        }

    }

    /**
     * 设置fragment
     *
     * @param fragment
     */
    @DebugLog
    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        KeyboardUtils.hideSoftInput(this);
        if (mcompositeSubscription != null && !mcompositeSubscription.isUnsubscribed()) {
            mcompositeSubscription.unsubscribe();
        }
    }




}
