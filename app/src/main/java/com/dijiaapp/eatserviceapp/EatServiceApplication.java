package com.dijiaapp.eatserviceapp;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wjy on 16/8/9.
 *
 */
public class EatServiceApplication extends Application {

//七星x渊渊弘x玄太阿x尊太阿x尊太阿
    public static String username;
    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).schemaVersion(2).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

    }


}
