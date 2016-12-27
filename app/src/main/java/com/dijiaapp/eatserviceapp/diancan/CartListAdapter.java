package com.dijiaapp.eatserviceapp.diancan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Cart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/10.
 */

public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Cart> mDatas;
    private final LayoutInflater mLayoutInflater;
    private MyItemClickListener mItemClickListener;

    public CartListAdapter(Context mContext, List<Cart> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.food_listitem, parent, false);
        RecyclerView.ViewHolder viewHolder = new MyViewHoder(v);

        return viewHolder;
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i("Daniel","---position---"+position);
        MyViewHoder myViewHoder = (MyViewHoder) holder;
        Cart _Cart = mDatas.get(position);
        myViewHoder.foodName.setText(_Cart.getDishesListBean().getDishesName());
        myViewHoder.number.setText(""+_Cart.getAmount());
        myViewHoder.moneyTv.setText(""+_Cart.getMoney());


    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public interface MyItemClickListener {
        public void onItemClick(View view, int postion);
    }

    static class MyViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.foodName)
        TextView foodName;
        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.moneyTv)
        TextView moneyTv;
        private MyItemClickListener mListener;

        public MyViewHoder(View view) {
            super(view);
            ButterKnife.bind(this,view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getPosition());
            }

        }
    }
}
