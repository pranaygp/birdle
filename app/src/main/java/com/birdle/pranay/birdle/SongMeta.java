package com.birdle.pranay.birdle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 9/8/2015.
 *
 * Holds Title, Artist, Album, Album Art url
 *
 *
 */

public class SongMeta {

    private String title;
    private String artist;
    private String album;
    private String albumArt;
    //TODO add image type deal

    public SongMeta(JSONObject jobj) {

        try {
            title = jobj.getString("title");
            artist = jobj.getString("artist");
            album = jobj.getString("album");
            albumArt = jobj.getString("albumArt");
        }
        catch (JSONException e){
            //TODO catch error
        }
        //TODO convert image url to image right

    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum(){
        return album;
    }

}
