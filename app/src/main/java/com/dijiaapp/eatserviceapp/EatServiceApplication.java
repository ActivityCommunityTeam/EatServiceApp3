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
    //获取集合size
    public  int getListSize(){
        return mList.size();
    }

    /**
     * add Activity
     * @param activity
     */
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    /**
     * 遍历退出activity
     */
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

    /**
     * OnLowMemory是Android提供的API，在系统内存不足，
     * 所有后台程序（优先级为background的进程，不是指后台运行的进程）都被杀死时，系统会调用OnLowMemory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //垃圾回收
        System.gc();
    }

//Realm初始化
    public static String username;
    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).schemaVersion(2).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

    }


}
