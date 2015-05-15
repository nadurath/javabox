package com.snam.jukebox;

import android.app.SearchManager;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class queueSearch extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_search);

        ListView listview = (ListView) findViewById(R.id.queueListView);

        final ArrayList<Track> maps = new ArrayList<>();


//        for(int i = 0;i<15;i++) {
//            Map<String,String> song = new TreeMap<>();
//            song.put("title", (int)(Math.random()*2)==0?"Follow Through":"Nine is God");
//            song.put("artist", (int)(Math.random()*2)==0?"Freelance Whales":"Wavves");
//            song.put("length", (int)(Math.random()*2)==0?"4:20":"6:90");
//            song.put("album", (int)(Math.random()*2)==0?"Diluvia":"GTA V Soundtrack");
//            maps.add(song);
//        }

        SongAdapter adapter = new SongAdapter(this, R.layout.queue_song_cell, maps);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track temp = maps.get(position);
                String choice = temp.songTitle;
                onSelectItem(choice);
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

    public void onSelectItem(String songChoice) {
        CharSequence text = songChoice + " has been added to the queue!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        home.onSelectItem(getApplicationContext(), songChoice);
    }

    public void search(View view){
        SearchView searchView = (SearchView) findViewById(R.id.searchBarD);
        System.out.println(searchView.getQuery());

    }

    //private void doMySearch(String query) {
      //  System.out.println(query);
    //}
}
