package com.dijiaapp.eatserviceapp.View;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by johe on 2016/12/16.
 */
public class MyLayoutManager extends LinearLayoutManager {

    public MyLayoutManager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        Log.i("gqf","heightSpec"+state.getItemCount());
        if(state.getItemCount()>=4){

        }
       /* if(recycler.getViewForPosition(0)!=null) {
            View view = recycler.getViewForPosition(0);
            measureChild(view, widthSpec, heightSpec);
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = view.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        }*/

    }
}