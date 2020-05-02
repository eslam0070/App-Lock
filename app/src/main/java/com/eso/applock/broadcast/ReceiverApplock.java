package com.eso.applock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eso.applock.PatternLockAct;
import com.eso.applock.utils.Utils;

public class ReceiverApplock extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils utils = new Utils(context);
        String appRunning = utils.getLauncherTopApp();
        if (utils.isLock(appRunning)){
            if (!appRunning.equals(utils.getLastApp())){
                utils.clearLastApp();
                utils.setLockApp(appRunning);

                Intent intent1 = new Intent(context, PatternLockAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("broadcast_receiver","broadcast_receiver");
                context.startActivity(intent);
            }
        }
    }
}
