package com.birdle.pranay.birdle;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by prana_000 on 05/01/2015.
 */



public class downloadFile extends AsyncTask<Void, Integer, Void>{
    private NotificationHelper mNotificationHelper;

    private static final String PEFERENCE_FILE = "preference";
    private static final String ISDOWNLOADED = "isdownloaded";
    private static String YTURL;
    private static String YTN;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;
    public downloadFile(Context context, String YT, String YTname){
        this.context = context;
        mNotificationHelper = new NotificationHelper(context,YTname);
        YTURL = YT;
        YTN = YTname;
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            //set the download URL, a url that points to a file on the internet
            //this is the file to be downloaded
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
            File file = new File(BirdleDirectory,YTN+".mp3");

            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            //this will be used in reading the data from the internet

            //this is the total size of the file
            int totalSize = urlConnection.getContentLength();
            //variable to store total downloaded bytes
            int downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer

            //now, read through the input buffer and write the contents to the file
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                //this is where you would do something to report the prgress, like this maybe
                //updateProgress(downloadedSize, totalSize);
                onProgressUpdate((int)((downloadedSize*100)/totalSize));

            }
            //close the output stream when done
            fileOutput.close();

            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

//catch some possible errors...
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
        Log.i("Birdle", String.valueOf(progress[0]));
    }
    protected void onPostExecute(Void result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
    }
}
