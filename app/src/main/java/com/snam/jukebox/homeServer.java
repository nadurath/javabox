package com.snam.jukebox;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.net.URL;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;

public class homeServer extends ActionBarActivity implements PlayerNotificationCallback, ConnectionStateCallback {


    private static final String CLIENT_ID = "3e21eec6f57e4f6a9a2446c1beeb2051";
    private static final String REDIRECT_URI = "javabox://callback";
    private static final int REQUEST_CODE = 1337;
    private static Player mPlayer;
    private static Vinyl v;
    Track t;
    String artist;
    String songTitle;
    String albumTitle;
    private static ArrayList<Track> maps;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    Receive mReceiver;
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        t = new Track();
        new Server(this).execute("Hello");
        if(maps==null)
            maps = new ArrayList<>();
        if(Singleton.getInstance().getSongs()!=null) {//if we already have a queue just add the song
            maps= (Singleton.getInstance().getSongs());
            t = Singleton.getInstance().getSong();
            maps.add(t);
            Singleton.getInstance().setSong(null);
        }
        else if(Singleton.getInstance().getSong()!=null) {//otherwise we add the new song to a new queue
                maps.add(Singleton.getInstance().getSong());
            t = Singleton.getInstance().getSong();
            Singleton.getInstance().setSong(null);
            Singleton.getInstance().setSongs(maps);
        }

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        v = new Vinyl(getApplication(), findViewById(R.id.albumArtwork));
        v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));

        TextView songView = (TextView) findViewById(R.id.songTitle);
        songView.setText(t==null?"---":t.name);
        TextView artistView = (TextView) findViewById(R.id.artist);
        artistView.setText(t==null?"---":t.artists.get(0).name);
        TextView albumView = (TextView) findViewById(R.id.albumTitle);
        albumView.setText(t==null?"---":t.album.name);


        ListView listview = (ListView) findViewById(R.id.listView);


        if(maps.size()>0) {
            SongAdapter adapter = new SongAdapter(this, R.layout.song_cell, maps);
            listview.setAdapter(adapter);
        }



        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION); //this is all the connecting to other devices work
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new Receive(mManager, mChannel, this);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("p2p","Discover peers run");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("p2p","Discover peers failed");
            }
        });

    }

    public void toastString(String string)
    {
        Toast toast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public void restartServer()
    {
        new Server(this).execute("HELLO");
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
        System.out.println("add " + songChoice + " to queue");
    }

    public void queueNew(View view) {
        if(token!=null) {//only pass to the search view if we have been logged in
            Intent i = new Intent(this, queueSearch.class);
            i.putExtra("TOKEN",token);
            startActivity(i);
        }
    }

    public void changeArt(View view) {
        //changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        //System.out.println("add " + maps.get(0).uri + " to queue");
        //mReceiver.requestPeers();
        mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peer) {
                ArrayList<WifiP2pDevice> peers = new ArrayList<>(peer.getDeviceList());
                Log.d("p2pDevices",peers.toString());
            }
        });
    }

    public void brodcastMessage(View view)
    {

    }

    public void searchAndPlay(String string)
    {
        if(maps==null)
            maps = new ArrayList<>();
        new Search().execute(string);
    }

    public void onTrackChange(View view){

        //somehow get the track that is currently playing???????

        t = new Track(); //instead of new track, pull info for the track that begins playing NOW
        //t.albumArt = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3); //temporary, until i can actually pull a song from somewhere and grab have its albumart
        songTitle = t.name;
        artist = t.artists.get(0).name;
        albumTitle = t.album.name;
        TextView songView = (TextView) findViewById(R.id.songTitle);
        songView.setText(songTitle);
        TextView artistView = (TextView) findViewById(R.id.artist);
        artistView.setText(artist);
        TextView albumView = (TextView) findViewById(R.id.albumTitle);
        albumView.setText(albumTitle);
        //v.changeArt(t.albumArt);

        //figure out how to seamlessly transition between songs
        //when the track changes, change the ImageView of the rotating record to the track.albumArt bitmap

        //when the track changes, change all of the textview to track.title / track.album / track.artist and so on and so forth

    }
    String token;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                token = response.getAccessToken();
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(homeServer.this);
                        mPlayer.addPlayerNotificationCallback(homeServer.this);
                        //mPlayer.play("spotify:track:1W2ox3KxfTEQfO5NdOaK7E");// once i find out how to get the track's URI in the search function, this will read "mPlayer.play(maps.get(0).uri); or something like that -> Dawood
                        if(maps.size()>0) {
                            mPlayer.play(maps.get(0).uri);//play the funky music
                            Log.d("arturl",maps.get(0).album.images.get(0).url);
                            try {
                                URL url = new URL(maps.get(0).album.images.get(0).url);
                                new GetArt().execute(url);
                            }
                            catch(Exception e)
                            {
                                Log.e("error",e.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("home", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public void playTrack(){
        mPlayer.play(maps.get(0).uri);
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


    private class GetArt extends AsyncTask<URL,Void , Bitmap> { //this is the getting art function
        protected Bitmap doInBackground(URL... url) {
            Bitmap image = null;
            try {
                 image = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
            }
            catch (Exception e){
                Log.e("art get error",e.toString());}
            return image;
        }
        protected void onPostExecute(Bitmap result) {
            if(result!=null)
                v.changeArt(result);
            else
                Log.e("art","the art was null");
        }
    }

    private class Search extends AsyncTask<String,Void , String> { //this is the searching function again, I sole it from the other class for testing things
        protected String doInBackground(String... string) {         //this version is not really to be used, and plays immediatly
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(token);
            Log.d("search",token);
            String ret = "\n";
            ArrayList<Track> mapsS = new ArrayList<>();
            SpotifyService spotify = api.getService();
            try {
                maps.clear();
                TracksPager tracks = spotify.searchTracks(string[0]);//this is the actual call to the search
                for(Track a:tracks.tracks.items) {
                    ret += " \n" + a.name;
                    mapsS.add(a);
                }
                maps.add(0,mapsS.get(0));
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e("api",error.toString());
            }

            return ret;
        }
        protected void onPostExecute(String result) {
            Log.d("search", result);
            playTrack();
        }
    }
}