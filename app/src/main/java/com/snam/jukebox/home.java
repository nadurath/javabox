package com.snam.jukebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;


public class home extends ActionBarActivity implements PlayerNotificationCallback, ConnectionStateCallback {


    private static final String CLIENT_ID = "3e21eec6f57e4f6a9a2446c1beeb2051";
    private static final String REDIRECT_URI = "javabox://callback";
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;
    Vinyl v;
    Track t;
    Bitmap albumArt;
    String artist;
    String songTitle;
    String albumTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"})
        AuthenticationRequest request = builder.build();

        //AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        t = new Track();
        //String albumArt = "";

        t.artist = "Portugal. The Man";
        t.songTitle = "Purple Yellow Red And Blue";
        t.album = "Evil Friends";
        t.albumArt = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3);

        v = new Vinyl(getApplication(), findViewById(R.id.albumArtwork));
        v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));

        TextView songView = (TextView) findViewById(R.id.songTitle);
        songView.setText(t.songTitle);
        TextView artistView = (TextView) findViewById(R.id.artist);
        artistView.setText(t.artist);
        TextView albumView = (TextView) findViewById(R.id.albumTitle);
        albumView.setText(t.album);


        ListView listview = (ListView) findViewById(R.id.listView);
        final ArrayList<Track> maps = new ArrayList<>();


        for (int i = 1; i < 21; i++) {
            Track song3 = new Track();
            song3.songTitle = (int) (Math.random() * 2) == 0 ? "Sandstorm" : "Butts";
            song3.artist = (int) (Math.random() * 2) == 0 ? "Darude" : "Jason Derulo";
            song3.duration = (int) (Math.random() * 2) == 0 ? "4:20" : "6:90";
            song3.album = (int) (Math.random() * 2) == 0 ? "The Green Album" : "Album 2";
            song3.queuePos = i + " ";
            maps.add(song3);
        }


        SongAdapter adapter = new SongAdapter(this, R.layout.song_cell, maps);
        listview.setAdapter(adapter);
        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> temp = maps.get(position);
                String choice = temp.get("title");
                onSelectItem(getApplicationContext(),choice);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public static void onSelectItem(Context context, String songChoice){
        //CharSequence text = songChoice + " has been added to the queue!";
        /*CharSequence text = "Okay so currently what we want is to simply display the upcoming tracks rather than adding it to the queue so I fucked up\n well actually you fucked up Sam";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 200, -380);
        toast.show();*/

        System.out.println("add " + songChoice + " to queue");
    }

    public void queueNew(View view) {
        Intent i = new Intent(this, queueSearch.class);
        startActivity(i);
    }

    public void changeArt(View view) {
        v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
    }

    public void onTrackChange(View view){

        //somehow get the track that is currently playing???????
        t = new Track(); //instead of new track, pull info for the track that begins playing NOW
        t.albumArt = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3); //temporary, until i can actually pull a song from somewhere and grab have its albumart
        songTitle = t.songTitle;
        artist = t.artist;
        albumTitle = t.album;
        TextView songView = (TextView) findViewById(R.id.songTitle);
        songView.setText(songTitle);
        TextView artistView = (TextView) findViewById(R.id.artist);
        artistView.setText(artist);
        TextView albumView = (TextView) findViewById(R.id.albumTitle);
        albumView.setText(albumTitle);
        v.changeArt(t.albumArt);

        //figure out how to seamlessly transition between songs
        //when the track changes, change the ImageView of the rotating record to the track.albumArt bitmap

        //when the track changes, change all of the textview to track.title / track.album / track.artist and so on and so forth

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(home.this);
                        mPlayer.addPlayerNotificationCallback(home.this);
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("home", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("home class", "User logged in");
    }

    @Override
    public void onLoggedOut(){
        Log.d("home class", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error){
        Log.d("home class", "Login failed");
    }

    @Override
    public void onTemporaryError(){
        Log.d("home class", "Temporary error occured");
    }

    @Override
    public void onConnectionMessage(String message){
        Log.d("home class", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState){
        Log.d("home class", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails){
        Log.d("home class", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
