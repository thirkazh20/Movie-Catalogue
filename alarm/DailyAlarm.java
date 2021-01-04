package com.blogspot.thirkazh.moviecatalogue.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.blogspot.thirkazh.moviecatalogue.BuildConfig;
import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.activity.MainActivity;

import java.util.Calendar;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DailyAlarm extends BroadcastReceiver {
    static final String CHANNEL_ID = "channel1";
    private static final CharSequence CHANNEL_NAME = "movie channel";
    private final int ID_REPEATING = 101;

    private int idNotif = 0;

    public void setDailyAlarm(Context context) {
        cancelDailyAlarm(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DailyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getPedingIntent(context));
        }

        Toast.makeText(context, R.string.daily_set, Toast.LENGTH_SHORT).show();
    }

    public void cancelDailyAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(getPedingIntent(context));
            Log.d(TAG, "cancelDailyAlarm: exiting alarm canceled !");
        }
    }

    private PendingIntent getPedingIntent(Context context) {
        Intent intent = new Intent(context, DailyAlarm.class);
        return PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        shoNotificaton(context);
        Log.d(TAG, "onReceiveDailyAlarm: masuk");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void shoNotificaton(Context context) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ID_REPEATING, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String notifTitle = context.getString(R.string.app_name);
        String notifText = context.getString(R.string.daily_reminder_text);

        Notification.Builder builder = new Notification.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_notif_app))
                .setSmallIcon(R.drawable.ic_notif_app)
                .setContentTitle(notifTitle)
                .setContentText(notifText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId(CHANNEL_ID);
            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(idNotif, builder.build());
        }
    }
}



