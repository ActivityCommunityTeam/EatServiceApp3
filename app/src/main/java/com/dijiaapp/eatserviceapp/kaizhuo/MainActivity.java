package com.dijiaapp.eatserviceapp.kaizhuo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.utilcode.utils.KeyboardUtils;
import com.blankj.utilcode.utils.StringUtils;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.ResultInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.diancan.FoodActivity;
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
import io.realm.Case;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.R.attr.id;
import static android.R.attr.track;
import static com.dijiaapp.eatserviceapp.network.Network.getSeatService;

public class MainActivity extends AppCompatActivity {

    private static final String HOME_TAG = "home_flag";
    private static final String ORDERS_TAG = "orders_flag";
    private static final int CONTENT_ORDERS = 2;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    private static final int CONTENT_HOME = 1;
    private CompositeSubscription mcompositeSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mcompositeSubscription = new CompositeSubscription();
        setContent(CONTENT_HOME);

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
                }
            }
        });
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

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        if (orderInfo != null && orderInfo.getOrderId() == 0) {
                            Intent intent = new Intent(MainActivity.this, SeatEatNumberActivity.class);
                            intent.putExtra("Seat", seat);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, orderInfo.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mcompositeSubscription.add(isusedSup);
    }


    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderOverEvent(OrderOverEvent orderOverEvent) {
        OrderInfo orderInfo = orderOverEvent.getOrderInfo();
        String id = orderInfo.getSeatName();
        submitOrderOver(id);
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderAddFoodEvent(OrderAddFoodEvent orderAddFoodEvent) {
        OrderInfo orderInfo = orderAddFoodEvent.getOrderInfo();
        FoodActivity.startFoodActivity(this, orderInfo);
    }

    @DebugLog
    private void submitOrderOver(String id) {
       Subscription  subscription = Network.getSeatService().updateStatus(id, "01")
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
                        Toast.makeText(MainActivity.this, resultInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });

        mcompositeSubscription.add(subscription);
    }

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
        }

    }

    /**
     * 设置fragment
     *
     * @param fragment
     */

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
