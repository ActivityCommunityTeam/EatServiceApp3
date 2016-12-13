package com.dijiaapp.eatserviceapp.diancan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;
import com.dijiaapp.eatserviceapp.data.FoodType;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by wjy on 16/10/8.
 */

public class CartRecyclerViewAdapter extends RealmRecyclerViewAdapter<Cart, CartRecyclerViewAdapter.MyViewHolder> {
    private final FoodActivity foodActivity;

    public CartRecyclerViewAdapter(@NonNull FoodActivity activity, @Nullable OrderedRealmCollection<Cart> data) {
        super(activity, data, true);
        this.foodActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.right_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cart cart = getData().get(position);
        holder.data = cart;
        if (cart != null) {
            holder.title.setText(cart.getDishesListBean().getDishesName());
            holder.amount.setText(cart.getAmount() + "");
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button min;
        public Button plus;
        public TextView amount;
        public Cart data;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textItem);
            min = (Button) view.findViewById(R.id.minBt);
            plus = (Button) view.findViewById(R.id.plusBt);
            amount = (TextView) view.findViewById(R.id.amountTv);
        }


    }
}
