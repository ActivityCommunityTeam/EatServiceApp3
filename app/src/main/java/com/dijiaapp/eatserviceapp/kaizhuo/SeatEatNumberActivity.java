package com.dijiaapp.eatserviceapp.kaizhuo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.utils.StringUtils;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.diancan.FoodActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

public class SeatEatNumberActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.hotel_name)
    TextView mHotelName;
    @BindView(R.id.hotel_type)
    TextView mHotelType;
    @BindView(R.id.hotel_eat_number_et)
    EditText mHotelEatNumberEt;
    @BindView(R.id.hotel_done_bt)
    Button mHotelDoneBt;
    private Seat seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_eat_number);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        seat = getIntent().getParcelableExtra("Seat");
        mHotelName.setText(seat.getSeatName());
        mHotelType.setText(seat.getSeatType());
        RxTextView.textChanges(mHotelEatNumberEt).skip(1)
                .map(new Func1<CharSequence, Boolean>() {
                    @DebugLog
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return !StringUtils.isEmpty(charSequence);
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mHotelDoneBt.setEnabled(aBoolean);
                    }
                });
    }

    @OnClick(R.id.hotel_done_bt)
    public void onClick() {
        enterFoodActivity();
    }

    private void enterFoodActivity() {

        FoodActivity.startFoodActivity(this,mHotelEatNumberEt.getText().toString(),seat);
    }
}
