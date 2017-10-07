package com.joiaapp.joia.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by arnell on 10/3/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmScheduler.setAlarm(context);
    }
}
