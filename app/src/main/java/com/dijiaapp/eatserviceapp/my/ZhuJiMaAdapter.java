package com.dijiaapp.eatserviceapp.my;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.MemoryCode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wjy on 2016/11/7.
 */

public class ZhuJiMaAdapter extends RecyclerView.Adapter<ZhuJiMaAdapter.ViewHolder> {
    List<MemoryCode> datas;
    MyItemClickListener mItemClickListener;



    public int getLayout() {
        return R.layout.memorycode_listitem;
    }

    public ZhuJiMaAdapter(List<MemoryCode> datas) {
        this.datas = datas;
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemoryCode memoryCode = datas.get(position);
        holder.dishesName.setText(memoryCode.getDishesName());
        holder.dishesUnit.setText(memoryCode.getDishesUnit());
        holder.memoryCode.setText(memoryCode.getMemoryCode());

    }

    @Override
    public int getItemCount() {
//        Log.i("Daniel","---datas.size()---"+datas.size());
        return datas != null ? datas.size() : 0;
    }

    public void setdatas(List<MemoryCode> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        View rootView;
        @BindView(R.id.memoryCode)
        TextView memoryCode;
        @BindView(R.id.dishesName)
        TextView dishesName;
        @BindView(R.id.dishesUnit)
        TextView dishesUnit;

//        @BindView(R.id.seat_listitem_status)
//        TextView seatListitemStatus;

        ViewHolder(View view) {
            super(view);
//            rootView = view;
            ButterKnife.bind(this, view);
//            this.mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            if (mListener != null) {
//                mListener.onItemClick(view, getPosition());
//            }

        }
    }
}
