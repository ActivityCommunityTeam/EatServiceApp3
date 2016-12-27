package com.dijiaapp.eatserviceapp.my;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * “我的”页面
 */
public class MyFragment extends Fragment {


    @BindView(R.id.userInfo)
    LinearLayout userInfo;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.memoryCode)
    LinearLayout memoryCode;
    private Realm realm;

    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        userName.setText(userInfo.getWaiterName());
        return view;
    }


    @OnClick(R.id.userInfo)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userInfo:
                //跳转到用户信息编辑页面
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }
    }

    @OnClick(R.id.memoryCode)
    public void onClick() {
        startActivity(new Intent(getActivity(),ZhuJiMaActivity.class));
    }
}
