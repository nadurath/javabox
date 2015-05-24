package com.snam.jukebox;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class homeClient extends ActionBarActivity{//} implements PlayerNotificationCallback, ConnectionStateCallback {

    private static Vinyl v;
    String artist;
    String songTitle;
    String albumTitle;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);
        v = new Vinyl(getApplication(), findViewById(R.id.albumArtwork));
        v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        findServers();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
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

    private WifiP2pInfo info;
    public void connectServer(View view) {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("service", "Connected");
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo sinfo) {
                        Log.d("info", sinfo.toString());
                        info = sinfo;
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Log.d("service", "not Connected" + reason);
            }
        });
    }
    final HashMap<String, String> buddies = new HashMap<String, String>();
    public void findServers()
    {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
                Log.d("Service", "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname").toString());
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {
                Log.d("service name",instanceName);
                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                config = new WifiP2pConfig();
                config.deviceAddress = resourceType.deviceAddress;
                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
                        .get(resourceType.deviceAddress) : resourceType.deviceName;
            }
        };
        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
        WifiP2pServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("service","request started");
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.d("service","request failed");
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("service","discover started");
            }

            @Override
            public void onFailure(int code) {
                Log.d("service","discover failed");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
            });
    }

    public void brodcastMessage(View view)//"Lets all make funof sam's spelling!"
    {
        String message = ((TextView)findViewById(R.id.send_song)).getText().toString();
        ((TextView)findViewById(R.id.send_song)).setText("");
        if(message!=null&&message.length()!=0) {
            new PostMessage().execute(message);
            Toast.makeText(this,message+" requested!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private WifiP2pConfig config;


    public void changeArt(View view) {
        //v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        //System.out.println("add " + maps.get(0).uri + " to queue");
    }



    private class PostMessage extends AsyncTask<String,Void , String> { //this is sending a message to host
        protected String doInBackground(String... s) {
            String ret = null;
                if(config!=null)
                Log.d("p2pconfig",config.deviceAddress);
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
                    InetSocketAddress address = new InetSocketAddress(info.groupOwnerAddress, port);
                    socket.connect(address, len);//connects to host (server)

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream stream = new ByteArrayInputStream(s[0].getBytes());//this puts our string into a input stream
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
