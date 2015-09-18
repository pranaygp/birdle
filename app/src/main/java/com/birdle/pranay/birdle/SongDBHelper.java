package com.birdle.pranay.birdle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pranaygp on 9/2/15.
 */

public class SongDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "birdle.db";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " +
            SongContract.SongSchema.TABLE_NAME + " (" +
                SongContract.SongSchema._ID + " INTEGER PRIMARY KEY, " +
                SongContract.SongSchema.COLUMN_NAME_SONG_YTN + " TEXT, " +
                SongContract.SongSchema.COLUMN_NAME_SONG_YTURL + " TEXT, " +
                SongContract.SongSchema.COLUMN_NAME_SONG_TITLE + " TEXT, " +
                SongContract.SongSchema.COLUMN_NAME_SONG_ARTIST + " TEXT, " +
                SongContract.SongSchema.COLUMN_NAME_SONG_ALBUM + " TEXT " +
            ");";

    private static final String DELETE_TABLE_SQL = "DROP TABLE IF EXISTS " + SongContract.SongSchema.TABLE_NAME;

    public SongDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_SQL);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
