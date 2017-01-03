package com.dijiaapp.eatserviceapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wjy on 16/8/9.
 *
 */
public class EatServiceApplication extends Application {
    private static  List<Activity> mList ;
   /* public static class Instance{
        public static EatServiceApplication instance=new EatServiceApplication();
    }

    public static EatServiceApplication getInstance() {
        return Instance.instance;
    }*/

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
        mList = new ArrayList<>();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).schemaVersion(2).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

    }
    /**
     * 版本对比(是否需要更新版本)
     *
     * @param newVersion
     *            服务器上获取的版本
     * @param indexVersion
     *            当前使用的版本
     * @return true：服务器上是最新版本，需要更新； false：不需要更新
     */
    public static boolean isUpdateForVersion(String newVersion, String indexVersion) {
        // boolean resultFlag = false;

        if ("".equals(newVersion) || null == newVersion
                || "null".equals(newVersion)) {
            return false;
        } else {

            String[] newNums = newVersion.split("\\.");
            String[] indexNums = indexVersion.split("\\.");

            if (newNums.length > indexNums.length) {// 服务器版本长度 比当前版本要长
                for (int i = 0; i < newNums.length; i++) {
                    //位数不够,给当前版本补零
                    int currentValue = 0;
                    if(i < indexNums.length){
                        currentValue = Integer.parseInt(indexNums[i]);
                    }


                    // 服务器上同位版本数如果有一个数大于 当前的，就是最新版，要更新；否则不更新
                    if (Integer.parseInt(newNums[i]) > currentValue) {
                        return true;
                    } else if (Integer.parseInt(newNums[i]) <= currentValue) {
                        //                        return false;
                    }
                }
                return false;
            } else if (newNums.length <= indexNums.length) {
                for (int i = 0; i < indexNums.length; i++) {
                    //位数不够,给服务器版本补零
                    int newValue = 0;
                    if(i < newNums.length){
                        newValue = Integer.parseInt(newNums[i]);
                    }

                    // 服务器上同位版本数如果有一个数大于 当前的，就是最新版，要更新；否则不更新
                    if (newValue > Integer.parseInt(indexNums[i])) {
                        return true;
                    } else if (newValue <= Integer.parseInt(indexNums[i])) {
                        //                        return false;
                    }
                }
                return false;
            }

        }
        return false;

    }

    // 获取当前版本的版本号
    public static String getCurrentVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.
                    getPackageName(), 0);
            Log.d("TAG", "getCurrentVersion: 版本值--》"+packageInfo.versionCode);
            Log.d("TAG", "getCurrentVersion: 版本号--》"+packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

}
