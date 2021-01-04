package com.blogspot.thirkazh.moviecatalogue.alarm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.blogspot.thirkazh.moviecatalogue.BuildConfig;
import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.activity.MainActivity;
import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;
import com.blogspot.thirkazh.moviecatalogue.model.movie.ResponseMovie;
import com.blogspot.thirkazh.moviecatalogue.network.ApiClient;
import com.blogspot.thirkazh.moviecatalogue.network.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ReleaseAlarm extends BroadcastReceiver {
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
     static final String CHANNEL_ID = "channel1";
    private static final CharSequence CHANNEL_NAME = "movie channel";
    private final int ID_REPEATING = 101;

    private List<MovieItem> listMovies = new ArrayList<>();

    private int idNotif = 0;

    public void setReapeatAlarm(Context context) {
        cancelReleaseAlarm(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReleaseAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getPedingIntent(context));
        }

        Toast.makeText(context, R.string.release_set, Toast.LENGTH_SHORT).show();
    }

    public void cancelReleaseAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(getPedingIntent(context));
            Log.d(TAG, "cancelReleaseAlarm: exiting alarm canceled !");
        }
    }

    private PendingIntent getPedingIntent(Context context) {
        Intent intent = new Intent(context, ReleaseAlarm.class);
        return PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String currentDate = sdf.format(date);

        Log.d("onReceiveReleaseAlarm ", String.valueOf(idNotif));

        getDataRelease(currentDate, context);
    }

    private void getDataRelease(String currentDate, final Context context) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseMovie> movieCall = apiInterface.getReleaseMovie(API_KEY, currentDate, currentDate);
        movieCall.enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMovie> call, @NonNull Response<ResponseMovie> response) {
                if (response.body() != null) {
                    Log.d("onResponseReleaseMovie ", response.body().toString());
                    listMovies = response.body().getResults();
                    for (int i = 0; i < listMovies.size(); i++) {
//                        showNotification(context, listMovies.get(i), idNotif);
                        new generatePictureStyleNotification(context, listMovies.get(i), idNotif).execute();
                        idNotif++;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMovie> call, @NonNull Throwable t) {
                Log.d("onFailureReleaseMovie ", t.getMessage());
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap>{
        private Context context;
        private MovieItem movieItem;
        private int notifId;

        generatePictureStyleNotification(Context context, MovieItem movieItem, int idNotif) {
            super();
            this.context = context;
            this.movieItem = movieItem;
            notifId = idNotif;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap;
            String poster_url = IMAGE_BASE_URL + "w342" + movieItem.getPosterPath();

            try {
                bitmap = Glide.with(context)
                        .asBitmap()
                        .load(poster_url)
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                return bitmap;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d(TAG, "showReleaseNotification: " + notifId);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, ID_REPEATING, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String notifTitle = context.getResources().getString(R.string.release_reminder_title);

            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_notif_app)
                    .setLargeIcon(bitmap)
                    .setContentTitle(notifTitle)
                    .setContentText(movieItem.getTitle())
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bitmap))
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
                notificationManagerCompat.notify(notifId, builder.build());
            }
        }
    }
}



