package com.dijiaapp.eatserviceapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.blankj.utilcode.utils.SPUtils;
import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.kaizhuo.MainActivity;
import com.dijiaapp.eatserviceapp.network.Network;
import com.dijiaapp.eatserviceapp.util.SettingsUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.R.attr.checked;
import static android.R.attr.settingsActivity;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginBt.setEnabled(false);

        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
        initLoginSetting();
        initLogin();
        initLoginBt();


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
                    }
                });

        RxCompoundButton.checkedChanges(mLoginAutoLogin)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        SettingsUtils.setPrefAutoLogin(getApplicationContext(), aBoolean);
                    }
                });
        if (SettingsUtils.isRememberPassword(getApplicationContext())) {
            UserInfo userInfo = realm.where(UserInfo.class).findFirst();
            name = userInfo.getUsername();
            password = userInfo.getPassword();
            mLoginNameEt.setText(name);
            mLoginPasswordEt.setText(password);
            mLoginBt.setEnabled(true);
            if (SettingsUtils.isAutoLogin(getApplicationContext()))
                doLogin();
        }

    }

    @DebugLog
    private void initLoginBt() {

        Subscription loginBt = RxView.clicks(mLoginBt).throttleFirst(400, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        doLogin();
                    }
                });
        compositeSubscription.add(loginBt);
    }

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
                            realm.beginTransaction();
                            userInfo.setPassword(password);
                            userInfo.setUsername(name);
                            realm.copyToRealmOrUpdate(userInfo);
                            realm.commitTransaction();
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
        SettingsUtils.setPrefAutoLogin(getApplicationContext(),false);
        SettingsUtils.setPrefRememberPassword(getApplicationContext(),false);
    }

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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        compositeSubscription.unsubscribe();
    }

}
