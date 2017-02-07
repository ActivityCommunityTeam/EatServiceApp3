package com.dijiaapp.eatserviceapp.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.dijiaapp.eatserviceapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Daniel on 2016/12/20.
 */

public class OrderSubmitSuccessDialog extends DialogFragment {

    @BindView(R.id.imageView3)
    ImageView imageView3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.activity_order_submit_success, container);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.imageView3)
    public void onClick() {
        dismiss();
    }
}
