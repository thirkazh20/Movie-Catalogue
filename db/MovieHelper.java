package com.blogspot.thirkazh.moviecatalogue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;

import java.sql.SQLException;
import java.util.ArrayList;

import static android.provider.MediaStore.Audio.Playlists.Members._ID;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.DATE;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.OVERVIEW;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.POSTER;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.TITLE;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.TABLE_MOVIE;

public class MovieHelper {
    private static final String DATABASE_TABLE = TABLE_MOVIE;
    private static DatabaseHelper dataBaseHelper;
    private static MovieHelper INSTANCE;
    private static SQLiteDatabase database;

    private MovieHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static MovieHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    public ArrayList<MovieItem> getAllMovies() {
        ArrayList<MovieItem> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null);
        cursor.moveToFirst();
        MovieItem note;
        if (cursor.getCount() > 0) {
            do {
                note = new MovieItem();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                note.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW)));
                note.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                note.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(POSTER)));
                arrayList.add(note);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public MovieItem getMovieById(int id) {
        Cursor cursor = database.query(
                DATABASE_TABLE,
                new String[]{_ID, TITLE, OVERVIEW, DATE, POSTER},
                _ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);

        MovieItem movieItem = new MovieItem();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            movieItem.setId(cursor.getColumnIndex(_ID));
            movieItem.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            movieItem.setReleaseDate(cursor.getString(cursor.getColumnIndex(DATE)));
            movieItem.setPosterPath(cursor.getString(cursor.getColumnIndex(POSTER)));

            cursor.close();
            return movieItem;
        }

        return null;
    }

    public long insertMovie(MovieItem movie) {
        ContentValues args = new ContentValues();
        args.put(_ID, movie.getId());
        args.put(TITLE, movie.getTitle());
        args.put(OVERVIEW, movie.getOverview());
        args.put(DATE, movie.getReleaseDate());
        args.put(POSTER, movie.getPosterPath());
        return database.insert(DATABASE_TABLE, null, args);
    }

    public int deleteMovie(int id) {
        return database.delete(DATABASE_TABLE, _ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_TABLE, null
                , _ID + " = ?"
                , new String[]{id}
                , null
                , null
                , null
                , null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_TABLE
                , null
                , null
                , null
                , null
                , null
                , _ID + " ASC");
    }

    public long insertProvider(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int updateProvider(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_TABLE, _ID + "=?", new String[]{id});
    }

}
