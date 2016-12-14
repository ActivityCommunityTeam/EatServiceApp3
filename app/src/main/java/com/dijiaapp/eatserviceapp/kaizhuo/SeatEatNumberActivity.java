package com.dijiaapp.eatserviceapp.kaizhuo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.utils.StringUtils;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.diancan.FoodActivity;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
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
    @BindView(R.id.hotel_eat_number_spinner)
    Spinner mhotel_eat_number_spinner;
    private Seat seat;

    String [] user_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_eat_number);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        seat = getIntent().getParcelableExtra("Seat");

        user_num=new String[seat.getContainNum()];
        for(int i=0;i<seat.getContainNum();i++){
            int num=i+1;
            user_num[i]=num+"";

        }

        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,user_num);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        mhotel_eat_number_spinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        mhotel_eat_number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置默认值
        mhotel_eat_number_spinner.setVisibility(View.VISIBLE);

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
