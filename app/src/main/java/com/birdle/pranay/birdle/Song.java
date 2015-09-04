package com.birdle.pranay.birdle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.ArrayMap;

import org.cmc.music.metadata.ImageData;

import java.util.ArrayList;

/**
 * Created by pranaygp on 9/1/15.
 */
public class Song {

    // DATA

    private long ID;

    private static SongDBHelper mDBHelper;

    private String YTN;
    private String YTURL;

    private String file;
    private String title;
    private String artist;
    private String album;


    // CONSTRUCTORS

    public Song(Context context, String YTURL){
        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);

        this.YTURL = YTURL;
        fetchYTN();
    }

    public Song(Context context, long id){
        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);

        // Initialize song with data from DB
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String testColumns = SongContract.SongSchema._ID + " = ?";
        String[] testColumnsArgs = {
                String.valueOf(id)
        };

        Cursor c = db.query(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema.COLUMN_NAMES, testColumns, testColumnsArgs, null, null, null);
        assignFieldsFromCursor(c);
        c.close();
    }

    // PUBLIC FUNCTIONS

    public void download(){
        // fetchYTN
        // Download to staging area
        // Update file field
    }

    public void pullMeta(){
        // Call getMetaFromPuller here
        // Assign its values to the class fields
        // Call saveMetaToDB
    }

    public static Song[] list(){
        // Returns array of Songs from DB
        Cursor songsCursor = getListOfItems();
        return null;
    }

    public static ArrayList<Song> listAsArrayList(){
        // Returns an ArrayList containing a list of all the songs from the database
        Cursor songsCursor = getListOfItems();
        return null;
    }

    public static Cursor listAsCursor(){
        // Returns a Cursor containing a list of all the songs from the database
        Cursor songsCursor = getListOfItems();
        return songsCursor;
    }

    public void delete(){
        // Delete meta from db
    }

    public void save(){
        //helper to call saveMetaToDB and then save data on file
        saveMetaToDB();
    }

    public void saveMetaToDB(){
        // Save meta to SQL Database using current fields
    }

    // INTERNAL HELPER FUNCTIONS

    private void assignFieldsFromCursor(Cursor c){
        c.moveToFirst();
        this.ID = c.getInt(c.getColumnIndexOrThrow(SongContract.SongSchema._ID));;
        this.YTN = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTN));
        this.YTURL = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTURL));
        this.file = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_FILE));
        this.title = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_TITLE));
        this.artist = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ARTIST));
        this.album = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ALBUM));
    }

    private static Cursor getListOfItems(){
        // Get List of songs as cursor
        return null;
    }

    private ArrayMap<String, String> getMetaFromPuller(){
        // Uses the metadataPuller class to return an ArrayMap
        // Example: return metadataPuller.pull(YTN);
        return null;
    }

    private void saveMetaToDB(ArrayMap<String, String> metaArrayMap){
        // Parse and save meta to SQL Database
    }


    private void fetchYTN(){
        // Call youtubeinmp3 advanced API to get YTN using YTURL
    }

    private void saveMetaToFile(){
        // Save meta from the object to an mp3 file and deletes the temporary birdle file
    }

    // GETTERS AND SETTERS


    public long getID() {
        return ID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public ImageData getAlbumArt() {
        // Get File and return it as ImageData
        return null;
    }

    public void setAlbumArt(String albumArt) {
        // Parse String as URL and save the downloaded image to File
    }

}
