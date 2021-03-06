package com.dijiaapp.eatserviceapp.diancan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dijiaapp.eatserviceapp.EatServiceApplication;
import com.dijiaapp.eatserviceapp.Impl.ListItemSizeChangeLinsener;
import com.dijiaapp.eatserviceapp.Impl.RecyclerViewItemHeightlinsener;
import com.dijiaapp.eatserviceapp.Impl.ShopCarDelectAllLinsener;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.View.PinnedHeaderListView;
import com.dijiaapp.eatserviceapp.View.StrongBottomSheetDialog;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.DishesListBean;
import com.dijiaapp.eatserviceapp.data.FoodType;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.ResultInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.network.Network;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.dijiaapp.eatserviceapp.R.id.pinnedListView;
import static com.dijiaapp.eatserviceapp.network.Network.getSeatService;

public class FoodActivity extends AppCompatActivity {
    int eatNumber;
    long hotelId;//酒店id
    //@BindView(R.id.food_cart_recyclerview)
    RecyclerView mFoodCartRecyclerview;
    private boolean[] flagArray;
    private boolean isScroll = true;
    CompositeSubscription compositeSubscription;
    private int seatId;//桌位id
    private OrderInfo orderInfo;
    private boolean isAddFood = false;//是不是添加订单

    public static void startFoodActivity(Context context, String eatNumber, Seat seat) {
        Intent intent = new Intent(context, FoodActivity.class);
        intent.putExtra("number", eatNumber);
        intent.putExtra("seat", seat);
        context.startActivity(intent);

    }

    public static void startFoodActivity(Context context, OrderInfo orderInfo) {

        Intent intent = new Intent(context, FoodActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        intent.putExtra("addFood", true);
        context.startActivity(intent);

    }

    private Seat seat;
    Realm realm;
    List<FoodType> allfoodTypes;

    Observer<List<FoodType>> observerFoodFromNet = new Observer<List<FoodType>>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @DebugLog
        @Override
        public void onNext(List<FoodType> foodTypes) {
            flagArray = new boolean[foodTypes.size()];
            flagArray[0] = true;
            for (int i = 1; i < foodTypes.size(); i++) {
                flagArray[i] = false;
            }
            saveFood(foodTypes);
            setListViews(foodTypes);

        }
    };

    Observer<List<FoodType>> observerFoodFromLocal = new Observer<List<FoodType>>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @DebugLog
        @Override
        public void onNext(List<FoodType> foodTypes) {
            flagArray = new boolean[foodTypes.size()];
            flagArray[0] = true;
            for (int i = 1; i < foodTypes.size(); i++) {
                flagArray[i] = false;
            }
            setListViews(foodTypes);
        }
    };

    @DebugLog
    private void saveFood(final List<FoodType> foodTypes) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(foodTypes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @DebugLog
            @Override
            public void onSuccess() {
                // Transaction was a success.
                System.out.println("chenggong");
            }
        }, new Realm.Transaction.OnError() {
            @DebugLog
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.

            }
        });
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.left_listview)
    ListView mLeftListview;
    @BindView(pinnedListView)
    PinnedHeaderListView mPinnedListView;
    @BindView(R.id.food_cart_bt)
    ImageView mFoodCartBt;
    @BindView(R.id.food_money)
    TextView mFoodMoney;
    @BindView(R.id.food_next)
    Button mFoodNext;
    @BindView(R.id.food_num)
    TextView mFoodNum;

    @BindView(R.id.food_code_edi)
    EditText food_code_edi;

    private LeftListAdapter leftListAdapter;

    public void initFoodCodeEdi(){
        food_code_edi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(mainSectionedAdapter!=null){
                    //根据输入助记码匹配菜品形成新的数据源更新列表
                    List<FoodType> foodTypes=new ArrayList<FoodType>();
                    for(int i=0;i<allfoodTypes.size();i++){
                        FoodType foodType=new FoodType();
                        foodType.setDishesTypeDesc(allfoodTypes.get(i).getDishesTypeDesc());
                        foodType.setId(allfoodTypes.get(i).getId());
                        foodType.setShowSort(allfoodTypes.get(i).getShowSort());
                        RealmList<DishesListBean> dishesListBeen=new RealmList<DishesListBean>();
                        for(int j=0;j<allfoodTypes.get(i).getDishesList().size();j++){
                            DishesListBean d= allfoodTypes.get(i).getDishesList().get(j);
                            if(d.getMemoryCode()!=null) {
                                if (d.getMemoryCode().contains(s)) {
                                    dishesListBeen.add(d);
                                }else{
                                    //foodTypes.get(i).getDishesList().remove(j);
                                }
                            }else{
                               // foodTypes.get(i).getDishesList().remove(j);
                            }
                        }
                        foodType.setDishesList(dishesListBeen);
                        foodTypes.add(foodType);
                    }
                    mainSectionedAdapter.setFoodCode(s.toString(),foodTypes);
                    Log.i("gqf","Editable"+s.toString());
                }
            }
        });
    }

     public int getFoodNum(){
         //获得点菜数量
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        int num=0;
        for(Cart cart:carts){
            num=num+cart.getAmount();
        }
        return  num;
    }
    MainSectionedAdapter mainSectionedAdapter;
    StrongBottomSheetDialog mBottomSheetDialog;
    CartRecyclerViewAdapter mCartRecyclerViewAdapter;
    private void setListViews(final List<FoodType> foodTypes) {
        //初始化购物车
        mBottomSheetDialog = new StrongBottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.content_bottomsheet, null, false);
        mFoodCartRecyclerview= (RecyclerView) view.findViewById(R.id.food_cart_recyclerview);
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        //购物车数据源
        OrderedRealmCollection<Cart> carts1 = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        //初始化购物车列表adapter
        mCartRecyclerViewAdapter = new CartRecyclerViewAdapter(this, carts1);
        mCartRecyclerViewAdapter.setListItemSizeChangeLinsener(new ListItemSizeChangeLinsener() {
            @Override
            public void getListItemSize(int size) {
                //删除菜品时如果购物车中没有菜品则滑出
                if(realm.where(Cart.class).equalTo("seatId", seatId).findAll().size()==0){
                    if(mBottomSheetDialog!=null) {
                        if (mBottomSheetDialog.isShowing()) {
                            mBottomSheetDialog.dismiss();
                        }
                    }
                }
            }
        });
        mFoodCartRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        //加载购物车列表
        mFoodCartRecyclerview.setAdapter(mCartRecyclerViewAdapter);
        //购物车视图加载
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setSeatId(seatId);
        mBottomSheetDialog.setmShopCarDelectAllLinsener(new ShopCarDelectAllLinsener() {
            @Override
            public void delectAll() {
                //购物车删除全部菜品
                mainSectionedAdapter.update();
                mainSectionedAdapter.setFoodNum(getFoodNum());
                mBottomSheetDialog.dismiss();
                mFoodMoney.setText("￥" + 0);
            }

            @Override
            public void dimess() {
                mBottomSheetDialog.dismiss();
            }
            @Override
            public void nextOrder(){
                mBottomSheetDialog.dismiss();
                next();
            }
        });
        //setBehaviorCallback();
        //初始化底部栏数据显示
        setCartMoney();
        //初始化菜单adapter（菜品，购物车）
        mainSectionedAdapter = new MainSectionedAdapter(this, foodTypes, carts);

        mPinnedListView.setAdapter(mainSectionedAdapter);
        //控制底部状态栏中菜品数量显示
        mainSectionedAdapter.setMfoodNum(mFoodNum);
        mainSectionedAdapter.setFoodNum(getFoodNum());
        //获取菜品数据源
        allfoodTypes=mainSectionedAdapter.getFoodTypes();
        //初始化左侧菜品类别列表
        leftListAdapter = new LeftListAdapter(this, foodTypes, flagArray);
        mLeftListview.setAdapter(leftListAdapter);
        //菜品类别点击选择监听
        mLeftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (int i = 0; i < foodTypes.size(); i++) {
                    if (i == position) {
                        flagArray[i] = true;
                    } else {
                        flagArray[i] = false;
                    }
                }
                leftListAdapter.notifyDataSetChanged();
                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += mainSectionedAdapter.getCountForSection(i) + 1;
                }
                mPinnedListView.setSelection(rightSection);
            }
        });
        //菜品列表滑动联动
        mPinnedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (mPinnedListView.getLastVisiblePosition() == (mPinnedListView.getCount() - 1)) {
                            mLeftListview.setSelection(ListView.FOCUS_DOWN);
                        }
                        // 判断滚动到顶部
                        if (mPinnedListView.getFirstVisiblePosition() == 0) {
                            mLeftListview.setSelection(0);
                        }

                        break;
                }
            }
            int y = 0;
            int x = 0;
            int z = 0;
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < foodTypes.size(); i++) {
                        if (i == mainSectionedAdapter.getSectionForPosition(mPinnedListView.getFirstVisiblePosition())) {
                            flagArray[i] = true;
                            x = i;
                        } else {
                            flagArray[i] = false;
                        }
                    }
                    if (x != y) {
                        leftListAdapter.notifyDataSetChanged();
                        y = x;
                        if (y == mLeftListview.getLastVisiblePosition()) {
                            //                            z = z + 3;
                            mLeftListview.setSelection(z);
                        }
                        if (x == mLeftListview.getFirstVisiblePosition()) {
                            //                            z = z - 1;
                            mLeftListview.setSelection(z);
                        }
                        if (firstVisibleItem + visibleItemCount == totalItemCount - 1) {
                            mLeftListview.setSelection(ListView.FOCUS_DOWN);
                        }
                    }
                } else {
                    isScroll = true;
                }
            }
        });
    }

    @DebugLog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cartEvent(CartEvent event) {
        if(event.getFlag()==0||event.getFlag()==1) {
            refreshCart(event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainSectionedAdapter.updateByDate(realm.where(Cart.class).equalTo("seatId", seatId).findAll());
        setCartMoney();
        mainSectionedAdapter.setFoodNum(getFoodNum());

    }

    //刷新购物车
    @DebugLog
    private void refreshCart(CartEvent event) {
        int id = event.getDisesBeanId();
        Cart cart = realm.where(Cart.class).equalTo("seatId", seatId).equalTo("dishesListBean.id", id).findFirst();
        //flag 0代表减 1 加。
        if (cart != null) {
            int amount = cart.getAmount();
            if (event.getFlag() == 0) {
                //减分两种情况 还剩一件的时候 直接删除 菜品，否则减一
                if (amount == 1) {
                    realm.beginTransaction();
                    cart.deleteFromRealm();
                    realm.commitTransaction();
                    if(mBottomSheetDialog.isShowing()) {
                        if (mCartRecyclerViewAdapter.getItemCount() <= 3) {
                            if (mCartRecyclerViewAdapter.itemHeight != -1) {
                                //根据购物车菜品数量动态修改购物车列表高度
                                mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight * (mCartRecyclerViewAdapter.getItemCount() - 1));
                            }
                        }
                    }
                } else {
                    amount--;
                    realm.beginTransaction();
                    cart.setMoney(cart.getDishesListBean().getOnSalePrice() * amount);
                    cart.setAmount(amount);
                    realm.commitTransaction();
                }

            } else {
                amount++;
                realm.beginTransaction();
                cart.setMoney(amount * cart.getDishesListBean().getOnSalePrice());
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
                cartNew.setMoney(disesBean.getOnSalePrice());
                cartNew.setTime(Calendar.getInstance().getTime().getTime());
                cartNew.setSeatId(seatId);
                realm.commitTransaction();
            }
        }
        //更新菜品列表
        mainSectionedAdapter.update();
        //更新底部购物车中菜品数量显示
        mainSectionedAdapter.setFoodNum(getFoodNum());
        //更新底部状态栏
        setCartMoney();
    }

    private void setCartMoney() {
        double money = getMoney();
        BigDecimal bmoney=new BigDecimal(money).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        mFoodMoney.setText("￥" + bmoney);

        if(mBottomSheetDialog.food_money!=null){
            //更新购物车中状态
            mBottomSheetDialog.food_money.setText("￥" + bmoney);
            int num=0;
            for(Cart c:realm.where(Cart.class).equalTo("seatId", seatId).findAll()){
                num=num+c.getAmount();
            }
            mBottomSheetDialog.setFoodNum(num);
        }
    }
    /**
     * 设置toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("点菜");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    //获得所有购物车中菜品价格总和
    private double getMoney() {
        double money = 0;
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();

        for (Cart c : carts) {
            money += c.getMoney();
        }
        return money;
    }

    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EatServiceApplication)getApplication()).addActivity(this);
        setContentView(R.layout.activity_food);
        ButterKnife.bind(this);
        mFoodNum.setVisibility(View.INVISIBLE);
        setToolbar();
        EventBus.getDefault().register(this);
        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
        //获取酒店id
        //hotelId = realm.where(UserInfo.class).findFirst().getHotelId();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        hotelId = userInfo.getHotelId();
        //判断点餐还是加菜
        Intent intent = getIntent();
        isAddFood = intent.getBooleanExtra("addFood", false);

        initData(intent);
        //mFoodCartRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        //获取酒店菜品
        getFood();
        //初始化助记码功能
        initFoodCodeEdi();

    }
    private void updateSeat(String seatname) {

        Subscription subscription = getSeatService().updateStatus(seatname, "02")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        compositeSubscription.add(subscription);
    }
    @DebugLog
    private void initData(Intent intent) {
        if (isAddFood) {
            orderInfo = intent.getParcelableExtra("orderInfo");
            seatId = Integer.parseInt(orderInfo.getSeatName());
        } else {
            eatNumber = Integer.parseInt(intent.getStringExtra("number"));
            seat = intent.getParcelableExtra("seat");
            seatId = seat.getSeatId();
            updateSeat(seat.getSeatId()+"");

        }
    }

    @DebugLog
    private void getFood() {
        //获取酒店菜品
        Observable<List<FoodType>> observable = getFoodFromLocal();
//        if(SettingsUtils.isAutoLogin(getApplicationContext())){
//            observable = getFoodFromLocal();
//        }

        if (observable == null) {
            getFoodFromNet();
        } else {
            Subscription subscription = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerFoodFromLocal);

            compositeSubscription.add(subscription);
        }

    }

    @DebugLog
    private Observable<List<FoodType>> getFoodFromLocal() {
        //本地数据库获得酒店菜品
        List<FoodType> foodTypes = realm.where(FoodType.class).findAll();
        if (foodTypes.size() > 0) {
            return Observable.just(foodTypes);
        } else {
            return null;
        }
    }

    @DebugLog
    private void getFoodFromNet() {
        //获得酒店菜品从网路
        Subscription subscription = Network.getFoodService().listFoods(hotelId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerFoodFromNet);
        compositeSubscription.add(subscription);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    Observer<ResultInfo> observer = new Observer<ResultInfo>() {
        @Override
        public void onCompleted() {
            Log.i("gqf1","onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Log.i("gqf1","onError"+e.getMessage());
        }

        @Override
        public void onNext(ResultInfo resultInfo) {
            Log.i("gqf1","resultInfo"+resultInfo.getMsg());
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();

        EventBus.getDefault().unregister(this);
        compositeSubscription.unsubscribe();
    }


    @OnClick({R.id.food_cart_bt, R.id.food_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.food_cart_bt:
                //显示购物车
                if(mCartRecyclerViewAdapter.getItemCount()==0){
                    Toast.makeText(FoodActivity.this,"您的购物车中没有商品，请添加",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mBottomSheetDialog.isShowing()) {
                        mBottomSheetDialog.dismiss();
                    } else {
                        mBottomSheetDialog.show();
                        if(mBottomSheetDialog.getmRecyclerViewItemHeightlinsener()==null){
                            mBottomSheetDialog.setmRecyclerViewItemHeightlinsener(new RecyclerViewItemHeightlinsener() {
                                @Override
                                public void getItemHeight(int height) {
                                    mCartRecyclerViewAdapter.setItemHeight(height/mCartRecyclerViewAdapter.getItemCount());
                                    //动态赋予购物车高度
                                    if(mCartRecyclerViewAdapter.getItemCount()>=3){
                                        mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.getItemHeight()*3);
                                    }else{
                                        mBottomSheetDialog.setRecyclerviewHeight(mCartRecyclerViewAdapter.itemHeight*mCartRecyclerViewAdapter.getItemCount());
                                    }
                                }
                            });
                        }
                        mBottomSheetDialog.showAndSet(mCartRecyclerViewAdapter);
                        mBottomSheetDialog.setFoodNum(getFoodNum());
                    }
                }
                break;
            case R.id.food_next:
                next();
                break;
        }
    }
    public void next(){
        //下一步
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
        if (carts.size() > 0) {
            Intent intent = new Intent(this, OrderActivity.class);
            if (isAddFood) {
                intent.putExtra("addFood",true);
                intent.putExtra("orderInfo",orderInfo);
            } else {
                intent.putExtra("seat", seat);
                intent.putExtra("number", eatNumber);
            }
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先选菜品", Toast.LENGTH_SHORT).show();
        }
    }
}
