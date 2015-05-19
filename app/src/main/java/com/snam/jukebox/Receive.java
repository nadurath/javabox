package com.snam.jukebox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sam on 5/19/2015.
 */
public class Receive extends BroadcastReceiver {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    homeClient activity;
    WifiP2pManager.PeerListListener myPeerListListener;

    public Receive(WifiP2pManager man, WifiP2pManager.Channel chan, homeClient act) {
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
                Log.d("p2p","wifi p2p is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                Log.d("p2p", "Manager?");
                manager.requestPeers(channel,
                        new WifiP2pManager.PeerListListener() {
                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                ArrayList<WifiP2pDevice> devices = new ArrayList<>(peers.getDeviceList());
                                Log.d("p2p devices", devices.size() + "");
                                for (WifiP2pDevice d : devices) {
                                    Log.d("name", d.deviceName);
                                }
                            }
                        });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }
}

