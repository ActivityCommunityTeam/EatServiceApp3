package com.dijiaapp.eatserviceapp.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dijiaapp.eatserviceapp.R;

/**
 * Created by Daniel on 2016/12/20.
 */

public class OrderSubmitSuccessDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_submit_success, container);
        return view;
    }
}
