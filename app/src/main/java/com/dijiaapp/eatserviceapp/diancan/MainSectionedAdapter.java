package com.dijiaapp.eatserviceapp.diancan;


import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.View.SectionedBaseAdapter;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.DishesListBean;
import com.dijiaapp.eatserviceapp.data.FoodType;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 基本功能：右侧Adapter
 * 创建：王杰
 * 创建时间：16/4/14
 * 邮箱：w489657152@gmail.com
 */
public class MainSectionedAdapter extends SectionedBaseAdapter {

    private Context mContext;
    private List<FoodType> foodTypes;
    private List<Cart> carts;

    public List<FoodType> getFoodTypes() {
        return foodTypes;
    }

    public void setFoodTypes(List<FoodType> foodTypes) {
        this.foodTypes = foodTypes;
    }

    TextView mfoodNum;

    String foodCode="";

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode, List<FoodType> foodTypes) {
        this.foodCode = foodCode;
        this.foodTypes=foodTypes;
        this.notifyDataSetChanged();

    }

    public TextView getMfoodNum() {
        return mfoodNum;
    }

    public void setMfoodNum(TextView mfoodNum) {
        this.mfoodNum = mfoodNum;

    }

    public MainSectionedAdapter(Context context, List<FoodType> foodTypes, List<Cart> carts) {
        this.mContext = context;
        this.foodTypes = foodTypes;
        this.carts = carts;
    }

    @Override
    public Object getItem(int section, int position) {
        return foodTypes.get(section).getDishesList().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return foodTypes.size();
    }

    @Override
    public int getCountForSection(int section) {
        return foodTypes.get(section).getDishesList().size();
    }

    public void update(){
        this.notifyDataSetChanged();

    }
    public void updateByDate(List<Cart> carts){
        this.carts=carts;
        this.notifyDataSetChanged();

    }
    public void setFoodNum(int num){
        if(mfoodNum!=null) {
            if (num > 0) {
                mfoodNum.setVisibility(View.VISIBLE);
                mfoodNum.setText(num+"");
            } else {
                mfoodNum.setVisibility(View.INVISIBLE);
            }
        }
    }
    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = null;
        boolean isInCart = false;
        int amount = 0;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflator.inflate(R.layout.right_list_item, null);
        } else {
            layout = (RelativeLayout) convertView;
        }
        final DishesListBean dishesListBean = foodTypes.get(section).getDishesList().get(position);
        ((TextView) layout.findViewById(R.id.textItem)).setText(dishesListBean.getDishesName());
        TextView oldpriceItem=((TextView) layout.findViewById(R.id.oldpriceItem));
        oldpriceItem.setText("￥" +dishesListBean.getDishesPrice());
        oldpriceItem.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        ((TextView) layout.findViewById(R.id.priceItem)).setText("￥" + dishesListBean.getOnSalePrice());
        Log.i("gqf","dishesListBean"+dishesListBean.toString());
        final Button min = (Button) layout.findViewById(R.id.minBt);
        Button plus = (Button) layout.findViewById(R.id.plusBt);
        final TextView amountTv = (TextView) layout.findViewById(R.id.amountTv);

        //当前菜品是否在购物车中，获得数量
        for(Cart cart :carts){
            if(cart.getDishesListBean().getId() == dishesListBean.getId()){
                isInCart = true;
                amount = cart.getAmount();

            }

        }

        min.setVisibility(isInCart?View.VISIBLE:View.INVISIBLE);
        amountTv.setText(amount+"");
        min.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = Integer.parseInt(amountTv.getText().toString());
                if (amount != 0) {
                    amount--;
                    amountTv.setText(amount + "");
                    min.setVisibility(View.VISIBLE);

                }
                if (amount == 0)
                    min.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(new CartEvent(0,dishesListBean.getId()));

            }
        });

        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = Integer.parseInt(amountTv.getText().toString());
                min.setVisibility(View.VISIBLE);
                amount++;
                amountTv.setText(amount + "");
                EventBus.getDefault().post(new CartEvent(1,dishesListBean.getId()));
            }
        });


        return layout;
    }


    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        layout.setClickable(false);
        ((TextView) layout.findViewById(R.id.textItem)).setText(foodTypes.get(section).getDishesTypeDesc());

        return layout;
    }

}
