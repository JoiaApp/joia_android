package com.joiaapp.joia.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.joiaapp.joia.MainActivity;
import com.joiaapp.joia.R;

/**
 * Created by arnell on 9/29/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class AlarmIntentService extends IntentService {

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Joia Reminder");
        builder.setContentText("Time to record your 3 positive moments!");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        //builder.setVibrate(new long[] {0, 100, 200, 500});
        builder.setDefaults(-1);// default sound and vibrate

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(this);
        managerCompat.notify(3, notification);
    }
}
