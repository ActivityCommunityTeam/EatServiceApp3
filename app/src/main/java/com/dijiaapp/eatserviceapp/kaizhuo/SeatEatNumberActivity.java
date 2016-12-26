package com.dijiaapp.eatserviceapp.kaizhuo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dijiaapp.eatserviceapp.EatServiceApplication;
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
    @BindView(R.id.hotel_done_auto)
    AutoCompleteTextView autoText;
    private Seat seat;

    String [] user_num;
    int usernum=-1;
    /**
     * 设置toolbar
     */
    private void setToolbar(String seatname) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("开桌");
        TextView toolbartxt=(TextView)toolbar.findViewById(R.id.toolbartxt);
        toolbartxt.setText("操作员："+seatname);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_eat_number);
        ButterKnife.bind(this);

        mHotelDoneBt.setEnabled(false);
        seat = getIntent().getParcelableExtra("Seat");

        user_num=new String[seat.getContainNum()];
        for(int i=0;i<seat.getContainNum();i++){
            int num=i+1;
            user_num[i]=num+"人";
        }

        setToolbar(EatServiceApplication.username);

        mHotelName.setText(seat.getSeatName());
        if(seat.getSeatType().equals("01")){
            mHotelType.setText("大厅");
        }else{
            mHotelType.setText("包间");
        }

        ArrayAdapter<String> autoadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_dropdown_item, user_num);

        autoText.setAdapter(autoadapter);
        autoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                autoText.showDropDown();

            }
        });
        autoText.setInputType(InputType.TYPE_NULL);
        autoText.setKeyListener(null);

        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usernum=position+1;
                mHotelDoneBt.setEnabled(true);
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
