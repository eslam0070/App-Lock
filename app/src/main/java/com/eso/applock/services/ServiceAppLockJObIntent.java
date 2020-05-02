package com.eso.applock.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.eso.applock.broadcast.ReceiverApplock;

public class ServiceAppLockJObIntent extends JobIntentService {

    private static final int JOB_ID = 15462;

    public static void enqueueWork(Context context,Intent work){
        enqueueWork(context,ServiceAppLockJObIntent.class,JOB_ID,work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        runAppLock();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startService();
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    public void runAppLock() {
        long endTime = System.currentTimeMillis()+210;
        while (System.currentTimeMillis() < endTime){
            synchronized (this){
                Intent intent = new Intent(this, ReceiverApplock.class);
                try {
                    wait(endTime-System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startService();
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onDestroy();
    }
}
