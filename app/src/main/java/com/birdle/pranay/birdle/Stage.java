package com.birdle.pranay.birdle;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v4.view.MotionEventCompat;


public class Stage extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        Song[] songs = Song.list();

        songView[] songElements = new songView[songs.length];



        //dynamically add the elements
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);

        for (int i = 0; i < songs.length; i++) {
            songElements[i] = new songView(this, (Song) songs[i]);

            root.addView(songElements[i]);
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
