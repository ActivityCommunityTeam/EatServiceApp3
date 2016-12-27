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
    /**
     * 设置toolbar
     */
    private void setToolbar() {

        mToolbar.setTitle("服务员app");
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
        ButterKnife.bind(this);
        setToolbar();

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
    private void isUsed(final Seat seat) {
        Subscription isusedSup = Network.getSeatService().isOrder(seat.getSeatId() + "")
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
                        if (orderInfo != null && orderInfo.getOrderId() == 0) {
                            Intent intent = new Intent(MainActivity.this, SeatEatNumberActivity.class);
                            intent.putExtra("Seat", seat);
                            startActivity(intent);
                        } else {
                            //座位正在使用，进入座位详情
                            Intent _intent = new Intent(MainActivity.this, SeatActivity.class);
                            _intent.putExtra("orderInfo", orderInfo);
                            startActivity(_intent);
//                            Toast.makeText(MainActivity.this, "1111", Toast.LENGTH_SHORT).show();
                        }
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
        FoodActivity.startFoodActivity(this, orderInfo);
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
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance("1", "2");
                }
                setFragment(homeFragment, HOME_TAG);
                break;
            case CONTENT_ORDERS:
                OrdersFragment orderFragment = (OrdersFragment) getSupportFragmentManager().findFragmentByTag(ORDERS_TAG);
                if (orderFragment == null) {
                    orderFragment = OrdersFragment.newInstance();
                }
                setFragment(orderFragment, ORDERS_TAG);
                break;
            case CONTENT_MY:
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
