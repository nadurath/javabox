package com.snam.jukebox;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class homeClient extends ActionBarActivity implements WifiP2pManager.ConnectionInfoListener{//} implements PlayerNotificationCallback, ConnectionStateCallback {

    private static Vinyl v;
    String artist;
    String songTitle;
    String albumTitle;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    Receive mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pInfo  info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        v = new Vinyl(getApplication(), findViewById(R.id.albumArtwork));
        v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new Receive(mManager, mChannel, this);
        ListView listview = (ListView) findViewById(R.id.listView);
        connectServer(null);


    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
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
//        if(token!=null) {//only pass to the search view if we have been logged in
//            Intent i = new Intent(this, queueSearch.class);
//            i.putExtra("TOKEN",token);
//            startActivity(i);
//        }
    }
    public void connectServer(View view)
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("p2p","Discover peers run");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("p2p","Discover peers failed because "+reasonCode );
            }
        });
    }
    public void connectToDevice(WifiP2pDevice device)
    {
        config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("p2pconnect","Connected to device");
                Log.d("p2pConnect",config.deviceAddress);
            }

            @Override
            public void onFailure(int reason) {
                Log.e("p2pconnect","Could not connect to device"+reason);
            }
        });
    }

    public void brodcastMessage(View view)//"Lets all make funof sam's spelling!"
    {
        String message = ((TextView)findViewById(R.id.send_song)).getText().toString();
        if(message!=null&&message.length()!=0)
           new PostMessage().execute(message);
    }
    private WifiP2pConfig config;


    public void changeArt(View view) {
        //v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        //System.out.println("add " + maps.get(0).uri + " to queue");

        mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peer) {
                ArrayList<WifiP2pDevice> peers = new ArrayList<>(peer.getDeviceList());//using record as testing button
                Log.d("p2pDevices", peers.toString());
            }
        });
    }

    public void onTrackChange(View view){

        //somehow get the track that is currently playing???????

//        t = new kaaes.spotify.webapi.android.models.Track(); //instead of new track, pull info for the track that begins playing NOW
//        //t.albumArt = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3); //temporary, until i can actually pull a song from somewhere and grab have its albumart
//        songTitle = t.name;
//        artist = t.artists.get(0).name;
//        albumTitle = t.album.name;
//        TextView songView = (TextView) findViewById(R.id.songTitle);
//        songView.setText(songTitle);
//        TextView artistView = (TextView) findViewById(R.id.artist);
//        artistView.setText(artist);
//        TextView albumView = (TextView) findViewById(R.id.albumTitle);
//        albumView.setText(albumTitle);
//        //v.changeArt(t.albumArt);

        //figure out how to seamlessly transition between songs
        //when the track changes, change the ImageView of the rotating record to the track.albumArt bitmap

        //when the track changes, change all of the textview to track.title / track.album / track.artist and so on and so forth

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d("P2PInfo","Info found");
    }

    private class PostMessage extends AsyncTask<String,Void , String> { //this is sending a message to host
        protected String doInBackground(String... s) {
            String ret = null;
            if(mReceiver.pInfo!=null)
            {
                if(config!=null)
                Log.d("p2pconfig",config.deviceAddress);
                //WifiP2pDevice device = mReceiver.peers.get(0);
                int port = 4206;
                int len = 100000;
                Socket socket = new Socket();
                byte buf[]  = new byte[1024];//not really sure what this is for yet
                try {
                    /**
                     * Create a client socket with the host,
                     * port, and timeout information.
                     */
                    socket.bind(null);
                    InetSocketAddress address = new InetSocketAddress(mReceiver.pInfo.groupOwnerAddress, port);
                    socket.connect(address, len);//connects to host (server)

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream stream = new ByteArrayInputStream(s[0].getBytes(StandardCharsets.UTF_8));//this puts our string into a input stream
                    while ((len = stream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);//this writes it to the output stream which should go to the other device
                        Log.d("BITE","WRITEBITE");
                    }
                    ret = s[0];
                    outputStream.close();
                    stream.close();
                } catch (IOException e) {
                    Log.e("p2pdata",e.toString());
                    e.printStackTrace();
                }

                finally {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e("p2pdata",e.toString());

                            }
                        }
                    }
                }
            }
           return ret;
        }
        protected void onPostExecute(String result) {
            if(result==null)
                Log.e("p2pmessage","the send resullt was null");
            else
                Log.e("p2pmessage","the message was sent");
        }
    }
}
