package com.dijiaapp.eatserviceapp.kaizhuo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.blankj.utilcode.utils.KeyboardUtils;
import com.blankj.utilcode.utils.StringUtils;
import com.dijiaapp.eatserviceapp.EatServiceApplication;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.FoodType;
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
import com.dijiaapp.eatserviceapp.update.UpdateMsg;
import com.dijiaapp.eatserviceapp.update.UpdateService;

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

import static com.dijiaapp.eatserviceapp.EatServiceApplication.isUpdateForVersion;
import static com.dijiaapp.eatserviceapp.R.id.toolbar;
import static com.dijiaapp.eatserviceapp.network.Network.getUpdateService;

public class MainActivity extends AppCompatActivity {

    private static final String HOME_TAG = "home_flag";
    private static final String ORDERS_TAG = "orders_flag";
    private static final String MY_TAG = "my_flag";
    private static final String FOUND_TAG = "found_flag";
    private static final int CONTENT_FOUND = 3;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE=1234;

    private static final int CONTENT_ORDERS = 2;
    private static final int CONTENT_MY = 4;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottomBar)
    com.ashokvarma.bottomnavigation.BottomNavigationBar bottomBar;
    private static final int CONTENT_HOME = 1;
    private CompositeSubscription mcompositeSubscription;
    private UserInfo mUser;
    private UpdateMsg mUpdateMsg;
    /**
     * 设置toolbar
     */
    private void setToolbar(String toolstr) {

        mToolbar.setTitle(toolstr);
        setSupportActionBar(mToolbar);

    }
    Observer<UpdateMsg> observer=new Observer<UpdateMsg>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(UpdateMsg updateMsg) {
            //与本地版本号对比
            if(isUpdateForVersion(updateMsg.getClient_version(), EatServiceApplication.getCurrentVersion(getApplicationContext()))) {
                Log.i("gqf","updateMsg"+updateMsg.toString());
                mUpdateMsg=updateMsg;
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("软件升级")
                        .setMessage(updateMsg.getUpdate_log())
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //开启更新服务UpdateService
                                //这里为了把update更好模块化，可以传一些updateService依赖的值
                                //如布局ID，资源ID，动态获取的标题,这里以app_name为例
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    //申请WRITE_EXTERNAL_STORAGE权限
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                                }else{
                                    startUpdateService(mUpdateMsg);
                                }
                            }
                        })
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(mUpdateMsg.getUpdate_install().equals("1")){
                                    //强制安装，询问后不安装退出程序
//                                    ((EatServiceApplication)getApplication()).exit();
                                }
                            }
                        });
                alert.create().show();


            }
        }
    };

    public void startUpdateService(UpdateMsg updateMsg){
        Intent updateIntent =new Intent(MainActivity.this, UpdateService.class);
        updateIntent.putExtra("client_version",updateMsg.getClient_version());
        updateIntent.putExtra("download_url",updateMsg.getDownload_url());
        updateIntent.putExtra("update_log",updateMsg.getUpdate_log());
        updateIntent.putExtra("update_install",updateMsg.getUpdate_install());
        startService(updateIntent);
    }

    //检测更新
    public void updateApp(long hotelId){

        //判断本地数据库是否有版本号

        Subscription subscription = getUpdateService().getAppVersion(hotelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        mcompositeSubscription.add(subscription);

    }
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EatServiceApplication)getApplication()).addActivity(this);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        realm = Realm.getDefaultInstance();
        mUser = realm.where(UserInfo.class).findFirst();
        ButterKnife.bind(this);
        String _str = getResources().getString(R.string.firstPage);
        setToolbar(_str);
        setContent(CONTENT_HOME);
        mcompositeSubscription = new CompositeSubscription();
        updateApp(mUser.getHotelId());
        //设置底部栏
        initBottomBar();
    }

    private void initBottomBar() {
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomBar
                .setActiveColor(R.color.colorPrimary)
                .setInActiveColor(R.color.bottom_img)
                .setBarBackgroundColor(R.color.white);
        bottomBar.addItem(new BottomNavigationItem(R.drawable.ic_bottom_home, R.string.firstPage))
                .addItem(new BottomNavigationItem(R.drawable.ic_bottom_order,  R.string.footorder_bottom))
                .addItem(new BottomNavigationItem(R.drawable.ic_bottom_mine, R.string.mian))
                .initialise();

        bottomBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        setContent(CONTENT_HOME);
                        break;
                    case 1:
                        setContent(CONTENT_ORDERS);

                        break;
                    case 2:
                        setContent(CONTENT_MY);
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //获取权限后调用
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                startUpdateService(mUpdateMsg);
            } else {
                // Permission Denied
                if(mUpdateMsg.getUpdate_install().equals("1")){
                    ((EatServiceApplication)getApplication()).exit();
                }else{
                    Toast.makeText(getApplicationContext(),"无法更新请开起下载权限",Toast.LENGTH_SHORT).show();
                }

            }
        }
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
            if(realm.where(FoodType.class).findFirst()!=null) {
                realm.beginTransaction();
                realm.delete(FoodType.class);
                realm.commitTransaction();
            }
//            if(SettingsUtils.isAutoLogin(getApplicationContext())){
//                realm.beginTransaction();
//                UserInfo userInfo = realm.where(UserInfo.class).findFirst();
//                Log.i("gqf",userInfo.toString());
//                if (userInfo != null) {
//                    userInfo.deleteFromRealm();
//                }
//                realm.commitTransaction();
//            }
            Toast.makeText(MainActivity.this, "再按一次退出服务员app", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {

//            MyConfig.clearSharePre(this, "users");
            ((EatServiceApplication)getApplication()).exit();
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
        isOrderOver(orderInfo.getSeatName());
//        String _status = orderInfo.getStatusId();
//        Log.i("Daniel", "---_status---" + _status);

    }

    private void isOrderOver(final String seatId) {
        Network.getSeatService().isTurnTable(seatId)
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
                        if (aBoolean) {
                            submitOrderOver(seatId);
                            setContent(CONTENT_HOME);
                        } else {
                            Toast.makeText(MainActivity.this, "未结单，不可翻桌！", Toast.LENGTH_SHORT).show();
//                            String id = orderInfo.getSeatName();

                        }
                    }
                });

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

        realm.close();

        KeyboardUtils.hideSoftInput(this);
        if (mcompositeSubscription != null && !mcompositeSubscription.isUnsubscribed()) {
            mcompositeSubscription.unsubscribe();
        }
    }




}
