package com.birdle.pranay.birdle;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.MotionEventCompat;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;


public class Stage extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        ListView songListView = (ListView) findViewById(R.id.songListView);

//        Debug

        Song test1 = new Song(this, "https://youtube.com/watch?v=Wevqe12A2");
        //test1.saveMetaToDB();
//        test.saveMetaToDB();

        Song[] songs = {test1};

        final ArrayList<Song> songList = new ArrayList<Song>();

        for(int i=0; i< songs.length; i++) {
            songs[i].setArtist("Artist");  //TODO remove these lines
            songs[i].setTitle("Title");//TODO remove these lines
            songList.add(songs[i]);
        }

        final ArrayAdapter songAdapter = new ArrayAdapter(this, R.layout.song_element_layout, R.id.artist, Song.listAsArrayList());
        songListView.setAdapter(songAdapter);

        //set up the list view
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Add functionality
                songAdapter.notifyDataSetChanged();
            }
        });






        /* this does it dynmaically, trying to do it with list view
        songView[] songElements = new songView[songs.length];



        //dynamically add the elements
        ViewGroup root = (ViewGroup) findViewById(R.id.content);

        for (int i = 0; i < songs.length; i++) {
            songElements[i] = new songView(this, songs[i]);
            root.addView(songElements[i]);
            Log.d("Birdle Stage", "onCreate - item "+(i+1));
        }


        for (int i = 0; i < songElements.length; i++) {

            //set up gesture recognition
            songElements[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = MotionEventCompat.getActionMasked(event);

                    if(action == MotionEvent.ACTION_DOWN) {
                        //songs[i].save();
                        //songs[i].delete();
                    }
                    return false;
                }
            });
        }*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
