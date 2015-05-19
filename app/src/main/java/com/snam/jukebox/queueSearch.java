package com.snam.jukebox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;


public class queueSearch extends ActionBarActivity {
    private String token;
    private ArrayList<Track> maps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = getIntent().getStringExtra("TOKEN");//gets spotify token from intent this is important so that we can use the spotify search stuff
        setContentView(R.layout.activity_queue_search);

        ListView listview = (ListView) findViewById(R.id.queueListView);

        maps = new ArrayList<>();

        SongAdapter adapter = new SongAdapter(this, R.layout.queue_song_cell, maps);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track temp = maps.get(position);
                //String choice = temp.songTitle;
                onSelectItem(temp);
            }
        });
    }

    private void refreshList()//refills the list view with the arraylist
    {
        ListView listview = (ListView) findViewById(R.id.queueListView);
        SongAdapter adapter = new SongAdapter(this, R.layout.queue_song_cell, maps);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track temp = maps.get(position);
                //String choice = temp.songTitle;
                onSelectItem(temp);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_queue_search, menu);
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

    public void onSelectItem(Track d) {
        CharSequence text = d.name + " has been added to the queue!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        home.onSelectItem(getApplicationContext(), d.name);
        //addTrack(d);
        Singleton.getInstance().setSong(d);//singleton can pass objects between classes. I'm gonna depreciate DTrack and we should work with their track method now (SAM)

        Intent i = new Intent(this, home.class);
        startActivity(i);
    }

    public void search(View view){
        SearchView searchView = (SearchView) findViewById(R.id.searchBarD);
        new Search().execute(searchView.getQuery().toString());                 //this does the search and passes the string to it
        //System.out.println(searchView.getQuery());
        //addTrack(searchView.getQuery().toString());
    }

    public void addTrack(String track, String singer, String album, String time, String url, String uri){
        String t = track;
        String ar = singer;
        String al = album;
        String dur = time;
        String imgUrl = url;
        String tURI = uri;
        Track w = new Track();
        //w.songTitle = x;
        Intent i = new Intent(this, home.class);
        i.putExtra("NEW_TRACK", track);
        i.putExtra("NEW_ARTIST", ar);
        i.putExtra("NEW_ALBUM", album);
        i.putExtra("NEW_DURATION", dur);
        i.putExtra("NEW_IMG_URL", imgUrl);
        i.putExtra("NEW_URI",tURI);

    }
    private class Search extends AsyncTask<String,Void , String> { //this is the searching function
        protected String doInBackground(String... string) {
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(token);
            Log.d("search",token);
            String ret = "\n";
            SpotifyService spotify = api.getService();
            try {
                maps.clear();
                TracksPager tracks = spotify.searchTracks(string[0]);//this is the actual call to the search
                for(Track a:tracks.tracks.items) {
                    ret += " \n" + a.name;;
                    maps.add(a);
                }
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e("api",error.toString());
            }

            return ret;
        }
        protected void onPostExecute(String result) {
            Log.d("search", result);
            refreshList();
        }
    }
}
