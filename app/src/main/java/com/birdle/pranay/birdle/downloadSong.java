package com.birdle.pranay.birdle;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class downloadSong extends IntentService {

    private NotificationHelper mNotificationHelper;

    private static final String PEFERENCE_FILE = "preference";
    private static final String ISDOWNLOADED = "isdownloaded";
    private static String YTURL;
    private static String YTN;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Birdle";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public downloadSong() {
        super("downloadSong");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
                String[] data = intent.getStringExtra(Intent.EXTRA_TEXT).split(":");
                int length = data.length;
                String[] urlO = data[(length-1)].split("/");
                YTURL = "http://www.youtube.com/watch?v=" + urlO[(urlO.length - 1)];
                YTN = "";
                for (int i = 0;i < (length-2);i++)
                    YTN += data[i];
                mNotificationHelper = new NotificationHelper(this, YTN);
                getFile();
            } else {

                Bundle extras = intent.getExtras();
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
                // The getMessageType() intent parameter must be the intent you received
                // in your BroadcastReceiver.
                String messageType = gcm.getMessageType(intent);

                if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
                    if (GoogleCloudMessaging.
                            MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                        sendNotification("Birdle Error", "Unexpected \"Send error\"");
                        Log.i(TAG, extras.toString());
                    } else if (GoogleCloudMessaging.
                            MESSAGE_TYPE_DELETED.equals(messageType)) {
                        sendNotification("Birdle Error", "Reuse the Chrome extension" +
                                extras.toString());
                        // If it's a regular GCM message, do some work.
                    } else if (GoogleCloudMessaging.
                            MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                        // This loop represents the service doing some work.
//                for (int i=0; i<5; i++) {
//                    Log.i(TAG, "Working... " + (i + 1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
//                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                        YTURL = extras.getString("url");
                        YTN = extras.getString("name");

//                        sendNotification("Birdle Debug", "Received: " + extras.getString("url"));
                        Log.i(TAG, "Received: " + extras.toString());

                        mNotificationHelper = new NotificationHelper(this, YTN);

                        getFile();


                        // Post notification of received message.

                    }
                }
                // Release the wake lock provided by the WakefulBroadcastReceiver.
                GcmBroadcastReceiver.completeWakefulIntent(intent);
            }
        }
    }

    private void getFile() {
        try {
            //set the download URL, a url that points to a file on the internet
            //this is the file to be downloaded
            startForeground(1,mNotificationHelper.createNotification());

            URL url = new URL("http://YouTubeInMP3.com/fetch/?video=" + YTURL);

            //create the new connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //set up some things on the connection
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            InputStream inputStream = urlConnection.getInputStream();
            //and connect!
            urlConnection.connect();

            //set the path where we want to save the file
            //in this case, going to save it on the root directory of the
            //sd card.
//            File BirdleDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File BirdleDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Birdle/");
            BirdleDirectory.mkdirs();
            //create a new file, specifying the path, and the filename
            //which we want to save the file as.
            File file = new File(BirdleDirectory, YTN + "_temp.mp3");

            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            //this will be used in reading the data from the internet

            //this is the total size of the file
            int totalSize = urlConnection.getContentLength();
            //variable to store total downloaded bytes
            int downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[65536];
            int bufferLength = 0; //used to store a temporary size of the buffer

            //now, read through the input buffer and write the contents to the file
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                //this is where you would do something to report the prgress, like this maybe
                //updateProgress(downloadedSize, totalSize);
                onProgressUpdate((int) ((downloadedSize * 100) / totalSize));

            }
            //close the output stream when done
            fileOutput.close();
            File img = resToFile();
            ImageData data = new ImageData(readFile(img), "image/jpeg", "Default Album Art",3);

            MusicMetadataSet src_set = new MyID3().read(file);
            MusicMetadata meta = (MusicMetadata) src_set.getSimplified();
            meta.setAlbum("Birdle");
            meta.setArtist("Pranay Prakash");
            meta.addPicture(data);

            File newFile = new File(BirdleDirectory, YTN + ".mp3");
            new MyID3().write(file, newFile, src_set,meta);



            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{newFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            file.delete();
            sendNotification("Birdle", "Downloaded " + YTN);
//catch some possible errors...
        } catch (FileNotFoundException e){
            e.printStackTrace();
            sendNotification("Birdle Error", "The song wasn't found. Try another video or try again later");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            sendNotification("Birdle Error", "An unexpected error occur. Ensure that you made a request from YouTube");
        } catch (IOException e) {
            e.printStackTrace();
            sendNotification("Birdle Error", "The song wasn't found. Try another video or try again later");
        } catch (ID3WriteException e) {
            e.printStackTrace();
            sendNotification("Birdle Error", "Could not write ID3 tags.");
        }
        stopForeground(true);
    }

    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
        Log.i("Birdle", String.valueOf(progress[0]));
    }

    private void sendNotification(String title, String msg) {
        Uri messageTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, downloader.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setSound(messageTone)
                        .setLights(Color.GREEN, 3000, 1000);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static byte[] readFile (File file) throws IOException {
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
    private File resToFile() {
        File BirdlePicsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/Birdle/");
        BirdlePicsDir.mkdirs();
        File file = new File(BirdlePicsDir, "default_album_art.jpg");
        if(file.exists()) {
            return file;
        }

        InputStream is;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            AssetManager am = getApplicationContext().getResources().getAssets();
            is = am.open("default_album_art.jpg");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
//            fos = openFileOutput(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"default_album_art.jpg");
            fos.write(buffer);
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
