package com.dijiaapp.eatserviceapp.update;

/**
 * Created by johe on 2016/12/29.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.kaizhuo.MainActivity;
import com.dijiaapp.eatserviceapp.util.StringUtils;

import java.io.File;

import rx.Subscriber;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 不要忘记注册，在mainfest文件中
 */
public class UpdateService extends Service {

    private UpdateMsg updateMsg;



    //文件存储
    private File updateDir = null;

    //通知栏

    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //获取传值
        //titleId = intent.getIntExtra("titleId",0);
        String client_version=intent.getStringExtra("client_version");
        String download_url=intent.getStringExtra("download_url");
        String update_log=intent.getStringExtra("update_log");
        String update_install=intent.getStringExtra("update_install");
        updateMsg=new UpdateMsg(client_version,download_url,update_log,update_install);

        //创建文件
        if(android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())){
            //文件路径
            updateDir = new File(Environment.getExternalStorageDirectory(),UpdateInformation.downloadDir);

        }

        //设置下载过程中，点击通知栏，回到主界面
        updateIntent = new Intent(this, MainActivity.class);
        updatePendingIntent = PendingIntent.getActivity(this,0,updateIntent,0);


        //下载通知
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_bottom_mine)
                .setContentTitle(this.getResources().getString(R.string.app_name)+"正在更新")
                .setContentText(updateMsg.getUpdate_log())
                .setContentIntent(updatePendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());

        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
       // new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程
        Log.i("gqf","开始下载");
        download();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    //通知栏显示下载
    private void sendNotification(DownloadProgress download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                StringUtils.getDataSize(download.getCurrentFileSize()) + "/" +
                        StringUtils.getDataSize(download.getTotalFileSize()));
        notificationBuilder.setContentIntent(updatePendingIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }
    private void sendIntent(DownloadProgress download) {
        //通知下载进度
        Intent intent = new Intent("message_progress");
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    File outputFile;
    int downloadCount = 0;
    //下载
    private void download() {
        //下载进度监听
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                //不频繁发送通知，防止通知栏下拉卡顿
                int progress = (int) ((bytesRead * 100) / contentLength);
                if ((downloadCount == 0) || progress > downloadCount) {
                    DownloadProgress download = new DownloadProgress();
                    download.setTotalFileSize(contentLength);
                    download.setCurrentFileSize(bytesRead);
                    download.setProgress(progress);

                    sendNotification(download);
                }
            }
        };
        //下载文件名，地址

        outputFile = new File(updateDir.getPath(),getResources().getString(R.string.app_name)+".apk");

        if (outputFile.exists()) {
            outputFile.delete();
        }


        String baseUrl = StringUtils.getHostName(updateMsg.getDownload_url());
        //开起下载
        new DownloadAPI(baseUrl, listener).downloadAPK(updateMsg.getDownload_url(), outputFile, new Subscriber() {
            @Override
            public void onCompleted() {
                downloadCompleted();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                downloadCompleted();
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {

            }
        });

    }
    //下载完成
    private void downloadCompleted() {
        DownloadProgress download = new DownloadProgress();
        download.setProgress(100);
        //完成下载
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(this.getResources().getString(R.string.app_name)+"更新完成");

        //完成后直接跳转安装界面，安装apk
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
        startActivity(intent);

        //也可通过点击通知跳转安装界面
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        notificationManager.notify(0, notificationBuilder.build());




    }
}

