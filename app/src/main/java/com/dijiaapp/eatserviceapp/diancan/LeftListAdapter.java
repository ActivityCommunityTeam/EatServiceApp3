package com.dijiaapp.eatserviceapp.diancan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.FoodType;

import java.util.List;


/**
 * 基本功能：左侧Adapter
 * 创建：王杰
 * 创建时间：16/4/14
 * 邮箱：w489657152@gmail.com
 */
public class LeftListAdapter extends BaseAdapter {
   private List<FoodType> foodTypes;
    private Context context;
   private boolean[] flagArray;
    public LeftListAdapter(Context context, List<FoodType> foodTypes,boolean[] flagArray) {
        this.context = context;
        this.foodTypes = foodTypes;
        this.flagArray = flagArray;
    }

    @Override
    public int getCount() {
        return foodTypes.size();
    }

    @Override
    public Object getItem(int arg0) {
        return foodTypes.get(arg0).getDishesTypeDesc();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        Holder holder = null;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.left_list_item, arg2,false);
            holder.left_list_item = (TextView) arg1.findViewById(R.id.left_list_item);
            arg1.setTag(holder);
        } else {
            holder = (Holder) arg1.getTag();
        }
        holder.updataView(arg0);
        return arg1;
    }

    private class Holder {
        private TextView left_list_item;

        public void updataView(final int position) {
            left_list_item.setText(foodTypes.get(position).getDishesTypeDesc());
            if (flagArray[position]) {
                left_list_item.setBackgroundColor(Color.rgb(255, 255, 255));
            } else {
                left_list_item.setBackgroundColor(Color.TRANSPARENT);
            }
        }

    }
}
