package com.dijiaapp.eatserviceapp.kaizhuo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Seat;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

/**
 * Created by wjy on 16/8/15.
 */
public class SeatRecyclerviewAdapter extends RecyclerView.Adapter<SeatRecyclerviewAdapter.ViewHolder> {
    private List<Seat> seatList;
    private Context mContext;
    public int getLayout() {
        return R.layout.seat_listitem;
    }
    public SeatRecyclerviewAdapter(){}
    public SeatRecyclerviewAdapter(Context mContext){
        this.mContext=mContext;
    }
    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
        notifyDataSetChanged();
        Log.i("Daniel","-----------setSeatList");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Seat seat = seatList.get(position);
        holder.mSeatListitemName.setText(seat.getSeatName());
        /*holder.mSeatListitemNumber.setText(String.valueOf(seat.getContainNum()));
        holder.mSeatListitemStatus.setText(seat.getUseStatus());*/

        holder.view.setOnClickListener(new View.OnClickListener() {
            @DebugLog
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EnterActivityEvent(SeatEatNumberActivity.class, seat));
            }
        });
        switch (seat.getUseStatus()){
            case "01":
                holder.mSeatListitemName.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.mSeatListitemNumber.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.mSeatListitemStatus.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.mseat_img_bg.setImageResource(R.drawable.bg004);
                holder.mSeatListitemStatus.setText("空闲");
                holder.mseat_listitem_img.setImageResource(R.drawable.mandark);
                break;
            case "02":
                holder.mSeatListitemName.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mSeatListitemNumber.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mSeatListitemStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mseat_img_bg.setImageResource(R.drawable.bg005);
                holder.mSeatListitemStatus.setText("使用中");
                holder.mseat_listitem_img.setImageResource(R.drawable.manlight);
                break;
            case "03":
                holder.mSeatListitemName.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mSeatListitemNumber.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mSeatListitemStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mseat_img_bg.setImageResource(R.drawable.bg003);
                holder.mSeatListitemStatus.setText("已预定");
                holder.mseat_listitem_img.setImageResource(R.drawable.manlight);
                break;
        }
        holder.mSeatListitemNumber.setText(String.valueOf(seat.getContainNum())+"");


    }

    @Override
    public int getItemCount() {
        return seatList != null ? seatList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        @BindView(R.id.seat_listitem_ral)
        RelativeLayout mSeatListitemRal;
        @BindView(R.id.seat_listitem_name)
        TextView mSeatListitemName;
        @BindView(R.id.seat_listitem_number)
        TextView mSeatListitemNumber;
        @BindView(R.id.seat_listitem_status)
        TextView mSeatListitemStatus;
        @BindView(R.id.seat_img_bg)
        ImageView mseat_img_bg;
        @BindView(R.id.seat_listitem_img)
        ImageView mseat_listitem_img;


        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
