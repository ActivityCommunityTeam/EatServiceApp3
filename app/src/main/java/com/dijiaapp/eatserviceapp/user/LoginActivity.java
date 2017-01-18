package com.dijiaapp.eatserviceapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.blankj.utilcode.utils.ScreenUtils;
import com.dijiaapp.eatserviceapp.EatServiceApplication;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.FoodType;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.kaizhuo.MainActivity;
import com.dijiaapp.eatserviceapp.network.Network;
import com.dijiaapp.eatserviceapp.util.SettingsUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_name_et)
    EditText mLoginNameEt;
    @BindView(R.id.login_password_et)
    EditText mLoginPasswordEt;
    @BindView(R.id.login_rememberPassword)
    CheckBox mLoginRememberPassword;
    @BindView(R.id.login_auto_login)
    CheckBox mLoginAutoLogin;
    @BindView(R.id.login_bt)
    Button mLoginBt;
    @BindView(R.id.login_form)
    ScrollView mLoginForm;
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;
    CompositeSubscription compositeSubscription;
    String name;
    String password;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EatServiceApplication.getInstance().addActivity(this);
        ScreenUtils.hideStatusBar(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginBt.setEnabled(false);
        //如果activity集合size不为0则遍历退出activity
        if (((EatServiceApplication) getApplication()).getListSize() != 0) {
            ((EatServiceApplication) getApplication()).exit();
        }

        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
        initLoginSetting();
        initLogin();
        initLoginBt();


    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            if(realm.where(FoodType.class).findFirst()!=null) {
                realm.beginTransaction();
                realm.delete(FoodType.class);
                realm.commitTransaction();
            }
//            if(SettingsUtils.isAutoLogin(getApplicationContext())){
//                realm.beginTransaction();
//                UserInfo userInfo = realm.where(UserInfo.class).findFirst();
//                if (userInfo != null) {
//                    userInfo.deleteFromRealm();
//                }
//                realm.commitTransaction();
//            }
            Toast.makeText(LoginActivity.this, "再按一次退出服务员app", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {

//            MyConfig.clearSharePre(this, "users");
            finish();
            System.exit(0);
        }
    }

    /**
     * 登录设置
     */
    private void initLoginSetting() {
        mLoginAutoLogin.setChecked(SettingsUtils.isAutoLogin(getApplicationContext()));
        mLoginRememberPassword.setChecked(SettingsUtils.isRememberPassword(getApplicationContext()));
        RxCompoundButton.checkedChanges(mLoginRememberPassword)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        SettingsUtils.setPrefRememberPassword(getApplicationContext(), aBoolean);
                        if(aBoolean==false){
                            mLoginAutoLogin.setChecked(false);
                            SettingsUtils.setPrefAutoLogin(getApplicationContext(), aBoolean);
                        }
                    }
                });

        RxCompoundButton.checkedChanges(mLoginAutoLogin)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {

                        SettingsUtils.setPrefAutoLogin(getApplicationContext(), aBoolean);
                        if(aBoolean==true){
                            SettingsUtils.setPrefRememberPassword(getApplicationContext(), aBoolean);
                            mLoginRememberPassword.setChecked(true);
                        }

                    }
                });

        if (SettingsUtils.isRememberPassword(getApplicationContext())) {
            UserInfo userInfo = realm.where(UserInfo.class).findFirst();
            if(userInfo!=null) {
                name = userInfo.getUsername();
                EatServiceApplication.username = name;
                password = userInfo.getPassword();
                mLoginNameEt.setText(name);
                mLoginPasswordEt.setText(password);
                mLoginBt.setEnabled(true);
                if (SettingsUtils.isAutoLogin(getApplicationContext()))
                    doLogin();
            }
        }

    }

    @DebugLog
    private void initLoginBt() {

        Subscription loginBt = RxView.clicks(mLoginBt).throttleFirst(400, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {

                    }

                    @DebugLog
                    @Override
                    public void onNext(Void aVoid) {

                        doLogin();
                    }
                });
        compositeSubscription.add(loginBt);
    }

    @DebugLog
    private void doLogin() {
        Subscription logSc = Network.getUserService().login(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        deletUser();
                    }




                    @DebugLog
                    @Override
                    public void onNext(UserInfo userInfo) {
                        if (userInfo.getHotelId() == 0) {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            deletUser();
                        } else {
                            EatServiceApplication.username = name;
                            UserInfo user = realm.where(UserInfo.class).findFirst();
                            if(user.getHotelId()!=userInfo.getHotelId()){
                                if(realm.where(FoodType.class).findFirst()!=null) {
                                    realm.beginTransaction();
                                    realm.delete(FoodType.class);
                                    realm.commitTransaction();
                                }
                            }
                            if(user!=null) {
                                realm.beginTransaction();
                                user.deleteFromRealm();
                                realm.commitTransaction();
                            }
                            realm.beginTransaction();
                            userInfo.setPassword(password);
                            userInfo.setUsername(name);
                            realm.copyToRealmOrUpdate(userInfo);
                            realm.commitTransaction();
                            Log.i("gqf",userInfo.toString());
                            Log.i("gqf",realm.where(UserInfo.class).findFirst().toString());
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        }
                    }
                });

        compositeSubscription.add(logSc);
    }

    private void deletUser() {
        realm.beginTransaction();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        if (userInfo != null) {
            userInfo.deleteFromRealm();
        }
        realm.commitTransaction();
        SettingsUtils.setPrefAutoLogin(getApplicationContext(), false);
        SettingsUtils.setPrefRememberPassword(getApplicationContext(), false);
    }

    /**
     * 对输入框是否为null进行控制
     */
    private void initLogin() {

        Observable<CharSequence> usernameOs = RxTextView.textChanges(mLoginNameEt).skip(1);
        final Observable<CharSequence> passwordOs = RxTextView.textChanges(mLoginPasswordEt).skip(1);

        Subscription etSc = Observable.combineLatest(usernameOs, passwordOs, new Func2<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence, CharSequence charSequence2) {
                boolean usernameBl = !TextUtils.isEmpty(charSequence);
                boolean passwordBl = !TextUtils.isEmpty(charSequence2);
                if (!usernameBl) {
                    mLoginNameEt.setError("请输入用户名");
                } else {
                    mLoginNameEt.setError(null);
                }

                if (!passwordBl)
                    mLoginPasswordEt.setError("请输入密码");
                else
                    mLoginPasswordEt.setError(null);

                return usernameBl && passwordBl;
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

                name = mLoginNameEt.getText().toString();
                password = mLoginPasswordEt.getText().toString();
                mLoginBt.setEnabled(aBoolean);
            }
        });

        compositeSubscription.add(etSc);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
        compositeSubscription.unsubscribe();

    }

}
