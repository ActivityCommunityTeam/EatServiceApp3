package com.dijiaapp.eatserviceapp.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.kaizhuo.SeatFragment;
import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by wjy on 2016/11/7.
 */

public class OrdersItemAdapter extends RecyclerView.Adapter<OrdersItemAdapter.ViewHolder> {
    List<OrderInfo> orderInfos;
    MyItemClickListener mItemClickListener;


    public int getLayout() {
        return R.layout.order_listitem;
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    public interface MyItemClickListener {
        public void onItemClick(View view, int postion);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);

        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrderInfo orderInfo = orderInfos.get(position);
        if (orderInfo.getStatusId().equals("03")) {
            holder.mOrderItemDone.setVisibility(View.GONE);
            holder.mOrderItemDeliver.setVisibility(View.GONE);
            holder.mOrderItemJiacan.setVisibility(View.GONE);
        } else {
            holder.mOrderItemStatus.setText("已点餐");
        }
        holder.mOrderItemNumber.setText("订单编号：" + orderInfo.getOrderHeaderNo());
        holder.mOrderItemEat.setText("就餐人数：" + orderInfo.getDinnerNum() + "");
        holder.mOrderItemServer.setText("服务人员：" + orderInfo.getWaiterName());
        int _seatId = Integer.parseInt(orderInfo.getSeatName());
        Seat _seat=SeatFragment.getSeat(_seatId);
        holder.seatListitemName.setText(_seat.getSeatName());
        holder.seatListitemNumber.setText(_seat.getContainNum()+"");
//        if (_seat.getUseStatus().equals("02")){
//            holder.seatListitemStatus.setText("使用中");
//        }



        RxView.clicks(holder.mOrderItemDone)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        Log.i("Daniel","--------翻桌翻翻翻");
                        EventBus.getDefault().post(new OrderOverEvent(orderInfo));

                    }
                });


        holder.mOrderItemJiacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new OrderAddFoodEvent(orderInfo));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderInfos != null ? orderInfos.size() : 0;
    }

    public void setOrderInfos(List<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View rootView;
        @BindView(R.id.order_item_name)
        LinearLayout mOrderItemName;
        @BindView(R.id.order_item_number)
        TextView mOrderItemNumber;
        @BindView(R.id.order_item_eat)
        TextView mOrderItemEat;
        @BindView(R.id.order_item_server)
        TextView mOrderItemServer;
        @BindView(R.id.order_item_layout)
        LinearLayout mOrderItemLayout;
        @BindView(R.id.order_item_status)
        TextView mOrderItemStatus;
        @BindView(R.id.order_item_deliver)
        View mOrderItemDeliver;
        @BindView(R.id.order_item_done)
        Button mOrderItemDone;
        @BindView(R.id.order_item_jiacan)
        Button mOrderItemJiacan;
        MyItemClickListener mListener;
        @BindView(R.id.seat_listitem_name)
        TextView seatListitemName;
        @BindView(R.id.seat_listitem_number)
        TextView seatListitemNumber;
//        @BindView(R.id.seat_listitem_status)
//        TextView seatListitemStatus;

        ViewHolder(View view, MyItemClickListener listener) {
            super(view);
            rootView = view;
            ButterKnife.bind(this, view);
            this.mListener = listener;
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
