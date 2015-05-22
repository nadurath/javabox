package com.snam.jukebox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 5/19/2015.
 */
public class Receive extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener{
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    Activity activity;
    WifiP2pManager.PeerListListener myPeerListListener;
    public ArrayList<WifiP2pDevice> peers = new ArrayList();
    public WifiP2pInfo pInfo;
    public Receive(WifiP2pManager man, WifiP2pManager.Channel chan, Activity act) {
        super();
        manager = man;
        channel = chan;
        activity = act;
        Log.d("receive", "object created at least");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("p2p", "onReceive called");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("p2p","wifi p2p is enabled");
            } else {
                Log.d("p2p","wifi p2p is disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("p2p","Peers changed");
            if (manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        peers = new ArrayList<>(peerList.getDeviceList());
                        Log.d("p2pPeers", peers.size() == 1 ? "found 1 peer" : "found " + peers.size() + " peers");
                        if(peers.size()>0)
                            ((homeClient)activity).connectToDevice(peers.get(0));
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        Log.d("info","Important");
                        pInfo = info;
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("device changed",action);
        }
        else
            Log.d("p2p",action);
    }
    public WifiP2pInfo Info;
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Info = info;
        Log.d("p2pinfo","got connection info");
    }
}

