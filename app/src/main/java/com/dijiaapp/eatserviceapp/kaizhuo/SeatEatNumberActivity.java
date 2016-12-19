package com.dijiaapp.eatserviceapp.kaizhuo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.diancan.FoodActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeatEatNumberActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.hotel_name)
    TextView mHotelName;
    @BindView(R.id.hotel_type)
    TextView mHotelType;
    /*@BindView(R.id.hotel_eat_number_et)
    EditText mHotelEatNumberEt;*/
    @BindView(R.id.hotel_done_bt)
    Button mHotelDoneBt;
    @BindView(R.id.hotel_eat_number_spinner)
    Spinner mhotel_eat_number_spinner;
    private Seat seat;

    String [] user_num;
    int usernum=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_eat_number);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHotelDoneBt.setEnabled(false);
        seat = getIntent().getParcelableExtra("Seat");

        user_num=new String[seat.getContainNum()];
        for(int i=0;i<seat.getContainNum();i++){
            int num=i+1;
            user_num[i]=num+"人";
        }
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,user_num);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mhotel_eat_number_spinner.setPrompt("请选择人数");
        //将adapter 添加到spinner中
        mhotel_eat_number_spinner.setAdapter(adapter);



        //设置默认值
        mhotel_eat_number_spinner.setVisibility(View.VISIBLE);

        mHotelName.setText(seat.getSeatName());
        mHotelType.setText(seat.getSeatType());
        mhotel_eat_number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usernum=position+1;
                mHotelDoneBt.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.hotel_done_bt)
    public void onClick() {
        if(usernum>=1) {
            enterFoodActivity();
        }else{
            Toast.makeText(this,"请选择用户人数",Toast.LENGTH_SHORT).show();
        }
    }

    private void enterFoodActivity() {

        FoodActivity.startFoodActivity(this,usernum+"",seat);
    }
}
