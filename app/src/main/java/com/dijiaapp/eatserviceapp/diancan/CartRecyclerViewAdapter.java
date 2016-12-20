package com.dijiaapp.eatserviceapp.diancan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.Impl.ListItemSizeChangeLinsener;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;

import org.greenrobot.eventbus.EventBus;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

import static com.dijiaapp.eatserviceapp.R.id.amountTv;

/**
 * Created by wjy on 16/10/8.
 */

public class CartRecyclerViewAdapter extends RealmRecyclerViewAdapter<Cart, CartRecyclerViewAdapter.MyViewHolder> {
    private final FoodActivity foodActivity;

    int itemHeight=-1;

    private ListItemSizeChangeLinsener listItemSizeChangeLinsener;

    public void setListItemSizeChangeLinsener(ListItemSizeChangeLinsener listItemSizeChangeLinsener){
        this.listItemSizeChangeLinsener=listItemSizeChangeLinsener;
    }

    private void listItemSizeChange(int size){
        if(listItemSizeChangeLinsener!=null){
            listItemSizeChangeLinsener.getListItemSize(size);
        }
    }

    public CartRecyclerViewAdapter(@NonNull FoodActivity activity, @Nullable OrderedRealmCollection<Cart> data) {
        super(activity, data, true);
        this.foodActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.right_list_item, parent, false);
        final View v=itemView;
        if(itemHeight==-1){

        }

        Log.i("gqf","getMeasuredHeight"+itemHeight);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        final Cart cart = getData().get(position);
        holder.data = cart;
        if (cart != null) {
            holder.title.setText(cart.getDishesListBean().getDishesName());
            holder.priceItem.setText("￥"+cart.getMoney()+"");
            holder.amount.setText(cart.getAmount() + "");
        }

        holder.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = getData().get(position).getAmount();
                if (amount != 0) {
                    amount--;
                    //getData().get(position).setAmount(amount);
                    holder.amount.setText(amount + "");
                    CartEvent c=new CartEvent(0,getData().get(position).getDishesListBean().getId());
                    EventBus.getDefault().post(c);
                    listItemSizeChange(getData().size()-1 );
                }else {
                    listItemSizeChange(getData().size() );
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount =  getData().get(position).getAmount();
                amount++;
                //getData().get(position).setAmount(amount);
                CartEvent c=new CartEvent(1,getData().get(position).getDishesListBean().getId());
                holder.amount.setText(amount + "");
                EventBus.getDefault().post(c);
                listItemSizeChange(getData().size());
            }
        });


    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button min;
        public Button plus;
        public TextView amount;
        public Cart data;
        public TextView priceItem;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textItem);
            min = (Button) view.findViewById(R.id.minBt);
            plus = (Button) view.findViewById(R.id.plusBt);
            amount = (TextView) view.findViewById(amountTv);
            priceItem=(TextView)view.findViewById(R.id.priceItem);
        }


    }
}
