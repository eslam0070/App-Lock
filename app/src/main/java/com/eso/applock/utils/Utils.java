package com.eso.applock.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.os.Process;
import android.os.UserManager;

import androidx.annotation.RequiresApi;

import java.util.List;

import io.paperdb.Paper;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class Utils {

    private String EXTRA_LAST_APP = "EXTRA_LAST_APP";
    private Context context;

    public Utils(Context context) {
        this.context = context;
        Paper.init(context);
    }

    public boolean isLock(String packageName){
        return Paper.book().read(packageName) != null;
    }

    public void lock(String pk){
        Paper.book().write(pk,pk);
    }

    public void unlock(String pk){
        Paper.book().delete(pk);
    }

    public void setLockApp(String pk){
        Paper.book().write(EXTRA_LAST_APP,pk);
    }

    public String getLastApp(){
        return Paper.book().read(EXTRA_LAST_APP);
    }

    public void clearLastApp(){
        Paper.book().delete(EXTRA_LAST_APP);
    }

    UsageStatsManager usageStatsManager;
    public String getLauncherTopApp(){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
            if (null != taskInfos && ! taskInfos.isEmpty()){
                return taskInfos.get(0).topActivity.getPackageName();
            }
        }else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime -10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime,endTime);
            while (usageEvents.hasNextEvent()){
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND){
                    result = event.getPackageName();
                }
            }
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean checkPermission(Context ctx){
        AppOpsManager appOpsManager = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myPid(),ctx.getPackageName());
        return mode == MODE_ALLOWED;
    }
}
