package com.birdle.pranay.birdle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by pranaygp on 9/1/15.
 */
public class Song {

    // DATA

    private long ID;

    private static SongDBHelper mDBHelper;
    private static Context mContext;

    public static final File BirdleDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Birdle/");

    private String YTN;
    private String YTURL;
    private String title;
    private String artist;
    private String album;

    private static final String TAG = "Birdle";

    // CONSTRUCTORS
    public Song(Context context){
        // Initialize Context
        mContext = context;

        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);
    }

    public Song(Context context, String YTURL) throws JSONException{
        // Initialize Context
        mContext = context;

        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);

        this.ID = 0;
        this.YTURL = YTURL;
        fetchYTN();
    }

    public Song(Context context, long id){
        //Initialize Context
        mContext = context;

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

    public void download(NotificationHelper mNotificationHelper) throws IOException, JSONException{

        Integer BUFFER_SIZE = 262144;

        // fetchYTN
        fetchYTN();
        // Download to staging area

        //set the download URL, a url that points to a file on the internet
        //this is the file to be downloaded

        URL url = new URL("http://youtubeinmp3.com/fetch/?video=" + YTURL);

        //create the new connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //set up some things on the connection
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);

        InputStream inputStream = urlConnection.getInputStream();
        urlConnection.connect();

        BirdleDirectory.mkdirs(); //attempt to make the directory

        //create a new temp STAGING file, specifying the path, and the filename
        //which we want to save the file as.
        File file = new File(BirdleDirectory, YTN + "_temp.mp3");
        

        Log.i("Birdle", "Song name: " + YTN);

        //this will be used to write the downloaded data into the file we created
        FileOutputStream fileOutput = new FileOutputStream(file);

        //this will be used in reading the data from the internet

        //this is the total size of the file
        int totalSize = urlConnection.getContentLength();
        //variable to store total downloaded bytes
        int downloadedSize = 0;

        //create a buffer...
        byte[] buffer = new byte[BUFFER_SIZE];
        int bufferLength = 0; //used to store a temporary size of the buffer

        //now, read through the input buffer and write the contents to the file
        while ((bufferLength = inputStream.read(buffer)) > 0) {
            //add the data in the buffer to the file in the file output stream (the file on the sd card
            fileOutput.write(buffer, 0, bufferLength);
            //add up the size so we know how much is downloaded
            downloadedSize += bufferLength;
            //this is where you would do something to report the prgress, like this maybe
            //updateProgress(downloadedSize, totalSize);
            onProgressUpdate(mNotificationHelper, (int) ((downloadedSize * 100) / totalSize));

        }
        //close the output stream when done
        fileOutput.close();
    }

    public void pullMeta(){
        // Call getMetaFromPuller here
        ArrayMap<String, String> meta = getMetaFromPuller();

        // Assign its values to the class fields

        this.title = meta.get("title");
        this.artist = meta.get("artist");
        this.album = meta.get("album");
        setAlbumArt(meta.get("albumArt"));

        // Call saveMetaToDB
        saveMetaToDB();
    }

    public static Song[] list(){
        // Returns array of Songs from DB
        Cursor songsCursor = getListOfItems();
        Song[] songList = new Song[songsCursor.getCount()];
        if (songsCursor != null){
            songsCursor.moveToFirst();

            for (int i = 0; i < songsCursor.getCount(); i++) {
                songList[i] = new Song(mContext, songsCursor.getLong(songsCursor.getColumnIndexOrThrow(SongContract.SongSchema._ID)));
                songsCursor.moveToNext();
            }
        }

        return songList;
    }

    public static ArrayList<Song> listAsArrayList(){
        // Returns an ArrayList containing a list of all the songs from the database
        ArrayList<Song> arrayList = new ArrayList<>();
        for (Song song:
             list()) {
            arrayList.add(song);
        }

        return arrayList;
    }

    public static Cursor listAsCursor(){
        // Returns a Cursor containing a list of all the songs from the database
        Cursor songsCursor = getListOfItems();
        return songsCursor;
    }

    public void delete(){
        // Delete meta from db
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String[] whereArgs = {String.valueOf(ID)};

        db.delete(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema._ID + "=?", whereArgs);
    }

    public void save(){
        //helper to call saveMetaToDB and then save data on file
        saveMetaToDB();
        saveMetaToFile();
    }

    public void saveMetaToFile() {
        // Save Data on File
        File songFile = getFile();
        MusicMetadataSet src_set = null;
        try {
            src_set = new MyID3().read(songFile);
            MusicMetadata meta = (MusicMetadata) src_set.getSimplified();
            meta.setSongTitle(title);
            meta.setAlbum(album);
            meta.setArtist(artist);
//            meta.addPicture(getAlbumArt());
            ImageData albumArt = new ImageData(readFile(getAlbumArt()), "image/jpeg", "Album Art", 3);
            meta.addPicture(albumArt);

            File newFile = new File(BirdleDirectory, YTN + ".mp3");
            new MyID3().write(songFile, newFile, src_set, meta);

            scanMedia(newFile);
            songFile.delete();

            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            String[] whereArgs = {String.valueOf(ID)};
            db.delete(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema._ID + " = ?", whereArgs);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "save could not read Song File");
        } catch (ID3WriteException e) {
            e.printStackTrace();
            Log.e(TAG, "save could not write meta data");
        }
    }

    public void saveMetaToDB(){
        // Save meta to SQL Database using current fields
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SongContract.SongSchema.COLUMN_NAME_SONG_YTN, YTN);
        contentValues.put(SongContract.SongSchema.COLUMN_NAME_SONG_YTURL, YTURL);
        contentValues.put(SongContract.SongSchema.COLUMN_NAME_SONG_TITLE, title);
        contentValues.put(SongContract.SongSchema.COLUMN_NAME_SONG_ARTIST, artist);
        contentValues.put(SongContract.SongSchema.COLUMN_NAME_SONG_ALBUM, album);

        if (ID != 0){
            String[] whereArgs = { String.valueOf(ID) };

            db.update(SongContract.SongSchema.TABLE_NAME, contentValues, SongContract.SongSchema._ID + " = ?", whereArgs);
        } else {
            ID = db.insert(SongContract.SongSchema.TABLE_NAME, null, contentValues);
        }

    }



    // INTERNAL HELPER FUNCTIONS

    protected void onProgressUpdate(NotificationHelper mNotificationHelper, Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
        Log.i(TAG, String.valueOf(progress[0]));
    }

    private void assignFieldsFromCursor(Cursor c){
        c.moveToFirst();
        this.ID = c.getInt(c.getColumnIndexOrThrow(SongContract.SongSchema._ID));;
        this.YTN = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTN));
        this.YTURL = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTURL));
        this.title = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_TITLE));
        this.artist = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ARTIST));
        this.album = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ALBUM));
    }

    private static Cursor getListOfItems(){
        // Get List of songs as cursor
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        return db.query(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema.COLUMN_NAMES, null, null, null, null, null);
    }

    private ArrayMap<String, String> getMetaFromPuller(){
        // Uses the metadataPuller class to return an ArrayMap
        return MetaDataPuller.pull(YTN, false);
    }

    private void fetchYTN() throws JSONException{
        // Call youtubeinmp3 advanced API to get YTN using YTURL
        String JSONstring = HTTPHelper.GET("http://youtubeinmp3.com/fetch/?format=JSON&video=" + YTURL);
        JSONObject JSON = new JSONObject(JSONstring);
        YTN = JSON.getString("title");
        Log.d(TAG, "fetchYTN - "+YTN);
    }

    private void scanMedia(File file){
        MediaScannerConnection.scanFile(mContext,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
    private static byte[] readFile (File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        byte[] data = null;
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");

            // Read file and return data
            data = new byte[length];
            f.readFully(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            f.close();
        }
        return data;
    }

    // GETTERS AND SETTERS


    public long getID() {
        return ID;
    }

    public File getFile() {
        File file = new File(BirdleDirectory, YTN + "_temp.mp3");
        return file;
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

    public File getAlbumArt() {
        // Return Image File
        return new File(BirdleDirectory, YTN + "_art.jpg");
    }

    public void setAlbumArt(String albumArt) {
        // Parse String as URL and save the downloaded image to File
        String filepath = "";
        try
        {
            URL url = new URL(albumArt);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            String filename= YTN + "_art.jpg";
            Log.i("Local filename:",""+filename);
            File file = new File(BirdleDirectory,filename);
            if(file.createNewFile())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 )
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
            }
            fileOutput.close();
            if(downloadedSize==totalSize){
                filepath=file.getPath();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            filepath=null;
            e.printStackTrace();
        }
        Log.i("filepath:"," "+filepath);
    }

}
