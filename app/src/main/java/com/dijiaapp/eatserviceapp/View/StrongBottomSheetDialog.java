package com.dijiaapp.eatserviceapp.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.Impl.ShopCarDelectAllLinsener;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;

import java.util.List;

import io.realm.Realm;

/**
 * <p>更加强壮的 BottomSheetDialog </p>
 *
 * <ul>
 *     <li>增加了设置显示高度跟最大高度的方法</li>
 *     <li>修复了通过手势关闭后无法再显示的问题</li>
 * </ul>
 *
 * @author xiaoGuy
 */

public class StrongBottomSheetDialog extends BottomSheetDialog {

    private int mPeekHeight;
    private int mMaxHeight;
    private boolean mCreated;
    private Window mWindow;
    private BottomSheetBehavior mBottomSheetBehavior;
    Realm realm;
    public RelativeLayout bottomSheet;
    public RecyclerView mFoodCartRecyclerview;

    private ImageView food_cart_bt;
    TextView delectTxt;
    public TextView mFoodNum;
    public TextView food_money;
    private int seatId=-1;
    Context mContext;
    Button food_next;
    ShopCarDelectAllLinsener mShopCarDelectAllLinsener;

    public StrongBottomSheetDialog(@NonNull Context context) {
        super(context);
        mContext=context;
        mWindow = getWindow();
    }

    // 便捷的构造器
    public StrongBottomSheetDialog(@NonNull Context context, int peekHeight, int maxHeight) {
        this(context);
        mContext=context;
        mPeekHeight = peekHeight;
        mMaxHeight = maxHeight;
    }

    public StrongBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        mContext=context;
        mWindow = getWindow();
    }
    private double getMoney() {
        double money = 0;
        if(realm==null){
            realm=Realm.getDefaultInstance();
        }
        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();

        for (Cart c : carts) {
            money += c.getMoney();
        }
        return money;
    }
    public StrongBottomSheetDialog(@NonNull Context context, boolean cancelable,
                                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext=context;
    }
    int maxHeight;

    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreated = true;

        setPeekHeight();
        setMaxHeight();
        setBottomSheetCallback();
        bottomSheet=(RelativeLayout)this.findViewById(R.id.bottomSheet);
        mFoodCartRecyclerview=(RecyclerView)this.findViewById(R.id.food_cart_recyclerview);
        food_cart_bt=(ImageView)this.findViewById(R.id.food_cart_bt);
        delectTxt=(TextView)this.findViewById(R.id.delect_car_txt);
        food_money=(TextView)this.findViewById(R.id.food_money);
        mFoodNum=(TextView)this.findViewById(R.id.food_num);
        food_money.setText("￥" + getMoney());
        food_next=(Button)this.findViewById(R.id.food_next);
        food_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShopCarDelectAllLinsener!=null) {
                    mShopCarDelectAllLinsener.nextOrder();
                }
            }
        });
        food_cart_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShopCarDelectAllLinsener!=null) {
                    mShopCarDelectAllLinsener.dimess();
                }
            }
        });
        delectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(realm==null){
                    realm=Realm.getDefaultInstance();
                }
                if(seatId!=-1){
                    if(mShopCarDelectAllLinsener!=null) {
                        List<Cart> carts = realm.where(Cart.class).equalTo("seatId", seatId).findAll();
                        for (Cart cart : carts) {
                            realm.beginTransaction();
                            cart.deleteFromRealm();
                            realm.commitTransaction();
                        }
                        mShopCarDelectAllLinsener.delectAll();
                    }
                }
            }
        });


    }

    public void setPeekHeight(int peekHeight) {
        mPeekHeight = peekHeight;

        if (mCreated) {
            setPeekHeight();
        }
    }

    public ShopCarDelectAllLinsener getmShopCarDelectAllLinsener() {
        return mShopCarDelectAllLinsener;
    }

    public void setmShopCarDelectAllLinsener(ShopCarDelectAllLinsener mShopCarDelectAllLinsener) {
        this.mShopCarDelectAllLinsener = mShopCarDelectAllLinsener;
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;

        if (mCreated) {
            setMaxHeight();
        }
    }

    public void setBatterSwipeDismiss(boolean enabled) {
        if (enabled) {

        }
    }
    public void setFoodNum(int num){
        if(mFoodNum!=null) {
            if (num > 0) {
                mFoodNum.setVisibility(View.VISIBLE);
                mFoodNum.setText(num+"");
            } else {
                mFoodNum.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void setPeekHeight() {
        if (mPeekHeight <= 0) {
            return;
        }

        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setPeekHeight(mPeekHeight);
        }
    }

    private void setMaxHeight() {
        if (mMaxHeight <= 0) {
            return;
        }

        mWindow.setLayout(LayoutParams.MATCH_PARENT, mMaxHeight);
        mWindow.setGravity(Gravity.BOTTOM);
    }
    public void setRecyclerviewHeight(int height){
        android.view.ViewGroup.LayoutParams pp =this.mFoodCartRecyclerview.getLayoutParams();

        pp.height =height;
        this.mFoodCartRecyclerview.setLayoutParams(pp);
    }
    private BottomSheetBehavior getBottomSheetBehavior() {
        if (mBottomSheetBehavior != null) {
            return mBottomSheetBehavior;
        }

        View view = mWindow.findViewById(android.support.design.R.id.design_bottom_sheet);
        // setContentView() 没有调用
        if (view == null) {
            return null;
        }
        mBottomSheetBehavior = BottomSheetBehavior.from(view);
        return mBottomSheetBehavior;
    }

    private void setBottomSheetCallback() {
        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetCallback);
        }
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet,
                                   @BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
                BottomSheetBehavior.from(bottomSheet).setState(
                        BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }
}