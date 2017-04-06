package edu.utep.cs4330.battleship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class ConnectionActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener{

    private final IntentFilter intentFilter = new IntentFilter();

    /**The Wifi Manager that will be used to handle operations*/
    private WifiP2pManager.Channel mChannel;

    /**Manages wifi*/
    private WifiP2pManager mManager;

    /**Spinner that displays all devices that are within wifi-direct range*/
    private Spinner deviceSpinner;

    /**Contains spinner information*/
    private ArrayAdapter<String> spinnerAdapter;

    private BroadcastReceiver receiver;

    private final String NO_DEVICES_FOUND = "No devices found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        deviceSpinner = (Spinner) findViewById(R.id.DeviceSpinner);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        List<String> items = new LinkedList<>();
        items.add(NO_DEVICES_FOUND);
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner, R.id.list, items);

        deviceSpinner.setAdapter(spinnerAdapter);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                Log.d("Connection", "Successful discovery initiation");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Log.d("Connection", "Failed discovery initiation");
            }
        });

        final ConnectionActivity activity = this;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){

                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                    // Determine if Wifi P2P mode is enabled or not, alert
                    // the Activity.
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        activity.setIsWifiP2pEnabled(true);
                    } else {
                        activity.setIsWifiP2pEnabled(false);
                    }
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                    // The peer list has changed!  We should probably do something about
                    // that.
                    if (mManager != null) {
                        mManager.requestPeers(mChannel, activity);
                    }
                    Log.d("wifiMe", "P2P peers changed");

                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                    // Connection state changed!  We should probably do something about
                    // that.

                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
                }
            }
        };

    }

    /**Called when p2p wifi is enabled/disabled*/
    public void setIsWifiP2pEnabled(boolean enabled){

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        //receiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        Log.d("wifiMe", "Found a change in the device list");

        //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged(); //TODO

        if (refreshedPeers.isEmpty()) {
            spinnerAdapter.clear();
            spinnerAdapter.add(NO_DEVICES_FOUND);
        } else {
            spinnerAdapter.clear();
            for (WifiP2pDevice device : peerList.getDeviceList()) {
                spinnerAdapter.add(device.deviceName);
            }
        }
        Log.d("wifiMe", "Devices updates");
        spinnerAdapter.notifyDataSetChanged();
        //deviceSpinner.setAdapter(spinnerAdapter);

    }
}
