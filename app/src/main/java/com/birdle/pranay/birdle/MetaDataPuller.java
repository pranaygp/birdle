package com.birdle.pranay.birdle;

import android.util.ArrayMap;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;

/**
 * Makes the API calls for play.me
 */
public class MetaDataPuller {

    private static final String TAG = "Birdle";
    private static final String ECHONEST_API_KEY = "2CPBG5CSF0058GDDP";
    private static final String PLAYME_API_KEY =   "4c6773305653414887";
    private static final String LAST_FM_API_KEY = "063b843482af18d1208146bd9d5018d3";
    public static final boolean useLastFM = false;
    public static final boolean useSpotify = true;
    public static final boolean usePlayMe = false;

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
            String EchonestJSONString = null;
            try {
                EchonestJSONString = HTTPHelper.GET("http://developer.echonest.com/api/v4/song/search?api_key=" + ECHONEST_API_KEY + "&format=json&results=1&combined=" + URLEncoder.encode(YTN, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, EchonestJSONString);

            try
            {
            JSONObject EchonestJSONObject = new JSONObject(EchonestJSONString);
            EchonestJSONObject = EchonestJSONObject.getJSONObject("response");
            JSONArray EchonestSongsJSONArray= EchonestJSONObject.getJSONArray("songs");
            JSONObject EchonestMeta = EchonestSongsJSONArray.getJSONObject(0);

            artist = EchonestMeta.getString("artist_name");
            title = EchonestMeta.getString("title");


            searchQuery = EchonestMeta.getString("artist_name") + " - " + EchonestMeta.getString("title");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(useLastFM){
            String LastFMJSONString = null;
            try {
                LastFMJSONString = HTTPHelper.GET("http://ws.audioscrobbler.com/2.0/?method=track.getinfo&api_key=" + LAST_FM_API_KEY + "&track=" + URLEncoder.encode(title, "UTF-8") + "&artist=" + URLEncoder.encode(artist, "UTF-8") + "&format=json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d(TAG, LastFMJSONString + "\n"); //Debug Line

            try{
                JSONObject LastFMJSONObject = new JSONObject(LastFMJSONString);
                JSONObject LastFMAlbumInfo = LastFMJSONObject.getJSONObject("track").getJSONObject("album");

                album = LastFMAlbumInfo.getString("title");
                albumArt = LastFMAlbumInfo.getJSONArray("image").getJSONObject(1).getString("#text");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(useSpotify){
            String SpotifyJSONString = HTTPHelper.GET("https://api.spotify.com/v1/search?type=track&q=" + URLEncoder.encode(searchQuery));
            Log.d(TAG, SpotifyJSONString + "\n"); //Debug Line

            try {
                JSONObject SpotifyJSONObject = new JSONObject(SpotifyJSONString);
                SpotifyJSONObject = SpotifyJSONObject.getJSONObject("tracks");
                JSONObject SpotifyFirstResult = SpotifyJSONObject.getJSONArray("items").getJSONObject(0);
                JSONObject SpotifyAlbumObject = SpotifyFirstResult.getJSONObject("album");

                album = SpotifyAlbumObject.getString("name");
                albumArt = SpotifyAlbumObject.getJSONArray("images").getJSONObject(1).getString("url");


            } catch (JSONException e) {
                //Handle incorrect parse/ no song found
                return null;
            }
        }

        if (usePlayMe) {
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

            } catch (JSONException e) {
                //Handle incorrect parse/ no song found
                return null;
            }
        }

        Log.d(TAG, title + "\n" + artist + "\n" + album + "\n" + albumArt); //debug line

        ArrayMap<String, String> parse = new ArrayMap<>();
        parse.put("title", title);
        parse.put("artist", artist);
        parse.put("album", album);
        parse.put("albumArt", albumArt);

        return parse;
    }

}
