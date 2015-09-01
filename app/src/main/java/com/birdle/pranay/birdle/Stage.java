package com.birdle.pranay.birdle;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Stage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        song[] songs = song.list();  //TODO: this is where we get array of songs
        //s,s,ns,s,s>

        //TODO:Do it with ListView

        //Fill our list View
        TextView[] songTexts = {
                (TextView) findViewById(R.id.song1Text),
                (TextView) findViewById(R.id.song2Text),
                (TextView) findViewById(R.id.song3Text),
                (TextView) findViewById(R.id.song4Text)

        };
        for(int i=0; i<songTexts.length; i++) {
            songTexts[i].setText(songs[i].getTitle() + " by " + songs[i].getArtist());
        }

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
