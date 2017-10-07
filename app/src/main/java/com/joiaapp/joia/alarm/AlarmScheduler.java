package com.joiaapp.joia.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.joiaapp.joia.service.ServiceFactory;

import java.util.Calendar;

/**
 * Created by arnell on 10/3/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class AlarmScheduler {
    public static void setAlarm(Context context) {
        ServiceFactory.init(context);
        if (ServiceFactory.getUserService().getCurrentUser() == null) {
            return;
        }
        Intent notifyIntent = new Intent(context, NotificationTapBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(System.currentTimeMillis());
        startTime.set(Calendar.HOUR_OF_DAY, 20);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(startTime)) {
            startTime.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), 1000 * 3600 * 24, pendingIntent);
    }
}
