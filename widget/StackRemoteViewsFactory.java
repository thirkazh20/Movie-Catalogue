package com.blogspot.thirkazh.moviecatalogue.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.CONTENT_URI;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private Cursor cursor;

    StackRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }

        final long identity = Binder.clearCallingIdentity();

        cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);

        Binder.restoreCallingIdentity(identity);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || cursor == null ||
                !cursor.moveToPosition(position)) {
            return null;
        }

        MovieItem movieItem = getItem(position);

        Bitmap bmp = null;
        String poster_url = IMAGE_BASE_URL + "w342" + movieItem.getPosterPath();
        String movie_title = movieItem.getTitle();
        String date = movieItem.getReleaseDate();

        try {

            bmp = Glide.with(context)
                    .asBitmap()
                    .load(poster_url)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

        } catch (InterruptedException | ExecutionException e) {
            Log.d("widget", "widget gagal tampil");
        }

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        rv.setImageViewBitmap(R.id.imageView, bmp);

        Bundle extras = new Bundle();
        extras.putString(FavoriteWidget.EXTRA_ITEM, movie_title + "\n" + date);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        if (cursor.moveToPosition(i)) {
            return cursor.getLong(0);
        } else return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private MovieItem getItem(int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid Position");
        }
        return new MovieItem(cursor);
    }

}
