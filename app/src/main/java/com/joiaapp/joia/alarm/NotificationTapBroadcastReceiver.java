package com.joiaapp.joia.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by arnell on 9/29/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class NotificationTapBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, AlarmIntentService.class);
        context.startService(alarmIntent);
    }
}
