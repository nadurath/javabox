package com.snam.jukebox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 5/19/2015.
 */
public class Receive extends BroadcastReceiver {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    homeClient activity;
    WifiP2pManager.PeerListListener myPeerListListener;
    private ArrayList<WifiP2pDevice> peers = new ArrayList();

    public Receive(WifiP2pManager man, WifiP2pManager.Channel chan, homeClient act) {
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
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("p2p","!!!!!");
            if (manager != null) {
                manager.requestPeers(channel, myPeerListListener);
            }
             WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peerList) {
                    return;
                }
            };
                    Log.d("p2p","found possible peers?");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d("connection changed",action);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("device changed",action);
        }
        else
            Log.d("p2p",action);
    }
    public void requestPeers()
    {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("p2p", "service works");
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        Log.d("p2p","found devices");
                        peers = new ArrayList<>(peerList.getDeviceList());
                        Log.d("p2p",peers.size()+"");
                        for(WifiP2pDevice d:peers)
                            Log.d("p2p",d.deviceName);
                        return;
                    }
                });
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("p2p","service doesn't works");
            }
        });
    }
}

