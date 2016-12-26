package com.dijiaapp.eatserviceapp;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wjy on 16/8/9.
 *
 */
public class EatServiceApplication extends Application {
    private static  List<Activity> mList ;
    private static EatServiceApplication instance;
    public synchronized static EatServiceApplication getInstance() {
        if (null == instance) {
            mList = new LinkedList<Activity>();
            instance = new EatServiceApplication();
        }
        return instance;
    }

    public  int getListSize(){
        return mList.size();
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

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
