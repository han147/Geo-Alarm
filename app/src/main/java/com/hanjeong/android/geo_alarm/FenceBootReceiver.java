package com.hanjeong.android.geo_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hanjeong on 2017. 2. 10..
 */

public class FenceBootReceiver extends BroadcastReceiver {

    public static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(BOOT_ACTION)) {

            context.startService(new Intent(context, LocationFenceService.class));

        }
    }
}
