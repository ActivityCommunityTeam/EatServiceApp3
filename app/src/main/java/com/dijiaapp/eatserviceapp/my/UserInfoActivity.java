package com.dijiaapp.eatserviceapp.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dijiaapp.eatserviceapp.EatServiceApplication;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.user.LoginActivity;
import com.dijiaapp.eatserviceapp.util.SettingsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.logout)
    Button logout;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EatServiceApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setToolBar();
        realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        userName.setText(userInfo.getWaiterName());

    }

    private void setToolBar() {
        toolbar.setTitle("服务员app");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.logout)
    public void onClick() {
        realm.beginTransaction();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        if (userInfo != null) {
            userInfo.deleteFromRealm();
        }
        realm.commitTransaction();
        SettingsUtils.setPrefAutoLogin(getApplicationContext(),false);
        SettingsUtils.setPrefRememberPassword(getApplicationContext(),false);
        startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));

//        onBackPressed();
    }
}
