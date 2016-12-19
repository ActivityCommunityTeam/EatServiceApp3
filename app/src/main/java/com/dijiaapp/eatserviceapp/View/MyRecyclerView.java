package com.dijiaapp.eatserviceapp.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by johe on 2016/12/16.
 */

public class MyRecyclerView extends RecyclerView{
    public MyRecyclerView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        Log.i("gqf","heightSpec"+heightSpec);

        super.onMeasure(widthSpec, heightSpec);
    }
}
