package com.example.locationnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {

    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(int ID ,String title, String desc, int icon, Class activityName) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, activityName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activityIntent = new Intent(this,NotificationReceiver.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MapsActivity.instance,0,activityIntent,0);
        Intent brodcastIntent = new Intent(MapsActivity.instance,NotificationReceiver.class);
        brodcastIntent.putExtra("ID",ID);
        PendingIntent actionIntent = PendingIntent.getBroadcast(MapsActivity.instance,0,brodcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(getApplicationContext().getResources(), icon, null)).getBitmap())
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(desc)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(desc))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.done_icon,"Completed",actionIntent)
                .build();
        NotificationManagerCompat.from(this).notify(ID, notification);

    }


}