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
import com.dijiaapp.eatserviceapp.kaizhuo.EnterActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wjy on 2016/11/7.
 */

public class OrdersItemAdapter extends RecyclerView.Adapter<OrdersItemAdapter.ViewHolder> {
    List<OrderInfo> orderInfos;

    public int getLayout() {
        return R.layout.order_listitem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrderInfo orderInfo = orderInfos.get(position);
        if (orderInfo.getStatusId().equals("03")) {
            holder.mOrderItemDone.setVisibility(View.GONE);
            holder.mOrderItemDeliver.setVisibility(View.GONE);
            holder.mOrderItemJiacan.setVisibility(View.GONE);
        }

        holder.mOrderItemNumber.setText(orderInfo.getOrderHeaderNo());
        holder.mOrderItemEat.setText(orderInfo.getDinnerNum() + "");
        holder.mOrderItemServer.setText(orderInfo.getWaiterName());
        holder.mOrderItemStatus.setText(orderInfo.getStatusId());
//        holder.mOrderItemName.setText(orderInfo.getOrderHeaderNo());

        holder.rootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EnterActivityEvent(OrderDetailActivity.class, null, orderInfo.getOrderId() + ""));
            }
        });
        holder.mOrderItemDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @BindView(R.id.order_item_name)
        TextView mOrderItemName;
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

        ViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
