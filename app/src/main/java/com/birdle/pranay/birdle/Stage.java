package com.birdle.pranay.birdle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;


public class Stage extends ActionBarActivity {

    private class songAdapter extends ArrayAdapter {

        private Context context;
        private ArrayList<Song> songs;

        public songAdapter(Context context, int resourceID) {
            super(context,resourceID);
            init(context,null);

        }

        public songAdapter(Context context, int resourceID, ArrayList<Song> list) {
            super(context,resourceID, list);
            init(context, list);

        }

        private void init(Context context, ArrayList<Song> songs) {
            this.context = context;
            this.songs = songs;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.song_element_layout, parent, false);
            TextView titleTextView = (TextView) rowView.findViewById(R.id.title);
            TextView artistTextView = (TextView) rowView.findViewById(R.id.artist);
            ImageView albumArtView = (ImageView) rowView.findViewById(R.id.artwork);

            titleTextView.setText(songs.get(position).getTitle());
            artistTextView.setText(songs.get(position).getArtist());
//            albumArtView.setImageURI(songs.get(position).getAlbumArt().toURI());

//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(songs.get(position).getAlbumArt().getAbsolutePath(), bmOptions);
//            bitmap = Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);
////            bitmap = Bitmap.createScaledBitmap(bitmap);
//            albumArtView.setImageBitmap(bitmap);

            Drawable d = Drawable.createFromPath(songs.get(position).getAlbumArt().getAbsolutePath());
            albumArtView.setImageDrawable(d);

            return rowView;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        ListView songListView = (ListView) findViewById(R.id.songListView);

//        Debug

        try {
            Song test1 = new Song(this, "www.youtube.com/watch?v=8aJw4chksqM");
            test1.saveMetaToDB();

        } catch (JSONException e) {
            System.out.print(e.getStackTrace());
        }

//        test.saveMetaToDB();

        Song init = new Song(this);

        final ArrayList<Song> songList = Song.listAsArrayList();

            final songAdapter songAdapter = new songAdapter(this, R.layout.song_element_layout, songList);
            songListView.setAdapter(songAdapter);
            Log.d("Birdle", "onCreate - " + Song.listAsArrayList().toString());



            //set up the list view
            songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Birdle", "onItemClick - position: " + position);
                    (songList.get(position)).save();
                    songList.remove(position);
                    songAdapter.notifyDataSetChanged();
                }
            });


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
