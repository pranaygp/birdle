package com.birdle.pranay.birdle;

import android.util.ArrayMap;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URLEncoder;

/**
 * Makes the API calls for play.me
 */
public class MetaDataPuller {

    private static final String TAG = "Birdle";
    private static final String ECHONEST_API_KEY = "2CPBG5CSF0058GDDP";
    private static final String PLAYME_API_KEY =   "4c6773305653414887";

    public MetaDataPuller(){


    }

    //
    // Takes a YTName, searchs PlayMe API for the song name, artist, album, and album art
    // then returns those 4 variables as a JSONObject for the  class. If the song isn't found
    // or an unexpected error occurs, return null
    //
    public static ArrayMap<String, String> pull(String YTN, Boolean useEchonest){
        //Variables to pull
        String title = "";
        String artist = "";
        String album = "";
        String albumArt = "";
        String searchQuery = YTN;

        if (useEchonest){
            String EchonestJSONString = HTTPHelper.GET("http://developer.echonest.com/api/v4/song/search?api_key=" + ECHONEST_API_KEY + "&format=json&results=1&combined=" + URLEncoder.encode(YTN));
            Log.i(TAG, EchonestJSONString);

            try
            {
            JSONObject EchonestJSONObject = new JSONObject(EchonestJSONString);
            EchonestJSONObject = EchonestJSONObject.getJSONObject("response");
            JSONArray EchonestSongsJSONArray= EchonestJSONObject.getJSONArray("songs");
            JSONObject EchonestMeta = EchonestSongsJSONArray.getJSONObject(0);

            searchQuery = EchonestMeta.getString("artist") + " - " + EchonestMeta.getString("title");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String PlayMeJSONString = HTTPHelper.GET("http://api.playme.com/track.search?step=1&format=json&apikey=" + PLAYME_API_KEY + "&query=" + URLEncoder.encode(searchQuery));
        Log.d(TAG, PlayMeJSONString + "\n"); //Debug Line

        try {
            JSONObject PlayMeJSONObject = new JSONObject(PlayMeJSONString);
            PlayMeJSONObject = PlayMeJSONObject.getJSONObject("response");
            JSONArray PlayMeSongsJSONArray = PlayMeJSONObject.getJSONArray("tracks");
            JSONObject PlayMe = PlayMeSongsJSONArray.getJSONObject(0);

            JSONObject PlayMeArtist = PlayMe.getJSONObject("artist");
            JSONObject PlayMeAlbum = PlayMe.getJSONObject("album");
            JSONObject PlayMeImages = PlayMe.getJSONObject("images");

            title = PlayMe.getString("name");
            artist = PlayMeArtist.getString("name");
            album = PlayMeAlbum.getString("name");
            albumArt = PlayMeImages.getString("img_256"); //Change here if you want different res art.

            Log.d(TAG, title + "\n" + artist + "\n" + album + "\n" + albumArt); //debug line

            ArrayMap<String, String>  parce = new ArrayMap<>();
            parce.put("title", title);
            parce.put("artist", artist);
            parce.put("album", album);
            parce.put("albumArt", albumArt);

            return parce;

        } catch (JSONException e) {
            //Handle incorrect parse/ no song found
            return null;
        }
    }

}
