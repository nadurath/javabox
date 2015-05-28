package com.snam.jukebox;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class homeClient extends Activity {

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

    public void brodcastMessage(View view)//"Lets all make funof sam's spelling!"
    {

        String message = ((TextView)findViewById(R.id.songText)).getText().toString();

        if(message!=null&&message.length()!=0) {
            if(mService==null) {
                animateMessage("I haven't found the server yet. Make sure you two are on the same network",5000);
            }
            else
                new PostMessage().execute(message);
        }
        else
            Log.e("error","message is not full or is nothing :"+message);
    }
    public void sentMessage()
    {
        ((TextView)findViewById(R.id.songText)).setText("");
        animateMessage(sent(),2000);
    }
    public void messageNotSent()
    {
        animateMessage("Something went wrong... Try again in a moment",2000);
    }

    private void animateMessage(final String s, final long messageTime)
    {
        TextView text = (TextView)findViewById(R.id.nowPlay);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        mLoadAnimation.setDuration(500);
        mLoadAnimation.setFillBefore(true);
        mLoadAnimation.setInterpolator(new LinearInterpolator());
        mLoadAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                TextView text = (TextView) findViewById(R.id.nowPlay);
                text.setText(s);
                Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                mLoadAnimation.setDuration(500);
                mLoadAnimation.setFillAfter(true);
                mLoadAnimation.setInterpolator(new LinearInterpolator());
                mLoadAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TextView text = (TextView) findViewById(R.id.nowPlay);
                                Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                                mLoadAnimation.setDuration(500);
                                mLoadAnimation.setFillAfter(true);
                                mLoadAnimation.setInterpolator(new LinearInterpolator());
                                mLoadAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        TextView text = (TextView) findViewById(R.id.nowPlay);
                                        text.setText("What would you like to hear next?");
                                        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                                        mLoadAnimation.setDuration(500);
                                        mLoadAnimation.setFillAfter(true);
                                        mLoadAnimation.setInterpolator(new LinearInterpolator());
                                        text.startAnimation(mLoadAnimation);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                text.startAnimation(mLoadAnimation);
                            }
                        }, messageTime);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                text.startAnimation(mLoadAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        text.startAnimation(mLoadAnimation);
    }
    private String sent()
    {
        ArrayList<String> responces = new ArrayList<>();
        responces.add("Interesting choice. We'll see how they like it");
        responces.add("I'll make it happen");
        responces.add("I'll see if I can find that");
        responces.add("Coming right up");
        responces.add("Let's hope everyone is up for that");
        responces.add("That's one of my favorites actually");
        responces.add("On its way to the server now");
        responces.add("Here we go");
        responces.add("Let's play that funky music");
        responces.add("If that's REALLY what you want to hear");
        responces.add("Coming up soon");

        return responces.get((int)(Math.random()*responces.size()));
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
            if(result==null) {
                Log.e("p2pmessage", "the send resullt was null");
                messageNotSent();
            }
            else {
                sentMessage();
                Log.e("p2pmessage", "the message was sent");
            }
        }
    }
}
