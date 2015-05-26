package com.snam.jukebox;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class homeClient extends ActionBarActivity{

    //private static Vinyl v;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager mNsdManager;
    private final String mServiceName = "Javabox";
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);
        initializeDiscoveryListener();
        initializeResolveListener();
        start();
    }

    private void start()
    {
        mNsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    private void initializeDiscoveryListener() {
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        final String TAG = "discovery";
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals("_http._tcp.")) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                //} else if (service.getServiceName().contains("Javabox")){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode + " " + serviceType);
                //mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };


    }

    public void initializeResolveListener() {
         final String TAG = "RESOLVE";
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

//                if (serviceInfo.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same IP.");
//                    return;
//                }
                mService = serviceInfo;
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
                int port = mService.getPort();
                InetAddress host = mService.getHost();

            }
        };
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


    public void changeArt(View view) {
        //v.changeArt(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record3));
        //System.out.println("add " + maps.get(0).uri + " to queue");
    }



    private class PostMessage extends AsyncTask<String,Void , String> { //this is sending a message to host
        protected String doInBackground(String... s) {
            String ret = null;

            Log.i("service","post called");
                if(mService!=null) {
                    Log.i("service","not null");
                    int port = 4206;
                    int len = 100000;
                    Socket socket = new Socket();
                    byte buf[] = new byte[1024];//not really sure what this is for yet
                    try {
                        /**
                         * Create a client socket with the host,
                         * port, and timeout information.
                         */
                        socket.bind(null);
                        InetSocketAddress address = new InetSocketAddress(mService.getHost(), port);
                        socket.connect(address, len);//connects to host (server)

                        OutputStream outputStream = socket.getOutputStream();
                        InputStream stream = new ByteArrayInputStream(s[0].getBytes());//this puts our string into a input stream
                        while ((len = stream.read(buf)) != -1) {
                            outputStream.write(buf, 0, len);//this writes it to the output stream which should go to the other device
                            Log.d("BITE", "WRITEBITE");
                        }
                        ret = s[0];
                        outputStream.close();
                        stream.close();
                    } catch (IOException e) {
                        Log.e("p2pdata", e.toString());
                        e.printStackTrace();
                    } finally {
                        if (socket != null) {
                            if (socket.isConnected()) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e("p2pdata", e.toString());

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
