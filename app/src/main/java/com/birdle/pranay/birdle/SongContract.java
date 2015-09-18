package com.birdle.pranay.birdle;

import android.provider.BaseColumns;

/**
 * Created by pranaygp on 9/2/15.
 */
public final class SongContract {
    public SongContract(){}

    public static abstract class SongSchema implements BaseColumns {
        public static final String TABLE_NAME = "songs";
        public static final String COLUMN_NAME_SONG_YTN = "YTN";
        public static final String COLUMN_NAME_SONG_YTURL = "YTURL";
        public static final String COLUMN_NAME_SONG_TITLE = "title";
        public static final String COLUMN_NAME_SONG_ARTIST = "artist";
        public static final String COLUMN_NAME_SONG_ALBUM = "album";

        public static final String[] COLUMN_NAMES= {
                _ID,
                COLUMN_NAME_SONG_YTN,
                COLUMN_NAME_SONG_YTURL,
                COLUMN_NAME_SONG_TITLE,
                COLUMN_NAME_SONG_ARTIST,
                COLUMN_NAME_SONG_ALBUM,
        };

    }
}
