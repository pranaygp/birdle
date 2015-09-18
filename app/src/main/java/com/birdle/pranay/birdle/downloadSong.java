package com.birdle.pranay.birdle;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
    public static final Integer BUFFER_SIZE = 524288/2;
//    private static final String ECHONEST_API_KEY = "2CPBG5CSF0058GDDP";

//    private static final String PREFERENCE_FILE = "preference";
//    private static final String ISDOWNLOADED = "isdownloaded";
//    private static String YTURL;
//    private static String YTN;
//    SharedPreferences settings;
//    SharedPreferences.Editor editor;
//    Context context;

//    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Birdle";
    private NotificationManager mNotificationManager;
//    NotificationCompat.Builder builder;


    public downloadSong() {
        super("downloadSong");
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
                String[] data = intent.getStringExtra(Intent.EXTRA_TEXT).split(":");
                Log.i(TAG, " Data received: " + intent.getStringExtra(Intent.EXTRA_TEXT));
                int length = data.length;
                String[] urlO = data[(length-1)].split("/");

                try {
                    Song download = new Song(this, "http://www.youtube.com/watch?v=" + urlO[(urlO.length - 1)]);
                    download.pullMeta();
                    download.saveMetaToDB();
                    mNotificationHelper = new NotificationHelper(this, download.getTitle());
                    mNotificationHelper.createNotification();
                    download.download(mNotificationHelper);
                    mNotificationHelper.completed();
                    sendNotification("Birdle", "Downloaded" + download.getTitle());
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendNotification("JSON Error", "Contact developer");
                } catch (IOException e){
                    e.printStackTrace();
                    sendNotification("Unexpected Error", "ContactDeveloper");
                }
            }
        }
    }

    private void sendNotification(String title, String msg, Boolean... options) {
        Uri messageTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = null;

        if (options.length > 0 && options[0]){
            //TODO: Goto Song edit activity
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, Stage.class), 0);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, Stage.class), 0);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setSound(messageTone)
                        .setLights(Color.GREEN, 3000, 1000)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(2, mBuilder.build());
    }
}
