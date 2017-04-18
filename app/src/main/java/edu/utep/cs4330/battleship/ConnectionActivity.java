package edu.utep.cs4330.battleship;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConnectionActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {

    private final IntentFilter intentFilter = new IntentFilter();

    /**
     * The Wifi Manager that will be used to handle operations
     */
    private WifiP2pManager.Channel mChannel;

    /**
     * Manages wifi
     */
    private WifiP2pManager mManager;

    /**
     * Spinner that displays all devices that are within wifi-direct range
     */
    private Spinner deviceSpinner;

    /**
     * Contains spinner information
     */
    private ArrayAdapter<WifiP2pDevice> deviceAdapter;

    /**
     * Receives broadcasts
     */
    private BroadcastReceiver receiver;

    private Button turnon;

    final private int port = 7070;

    private final WifiP2pDevice NO_DEVICES_FOUND = new WifiP2pDevice() {
        @Override
        public String toString() {
            return "No devices found";
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        deviceSpinner = (Spinner) findViewById(R.id.DeviceSpinner);
        turnon = (Button) findViewById(R.id.Turnon);
        //createServer();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        List<WifiP2pDevice> items = new LinkedList<>();
        items.add(NO_DEVICES_FOUND);
        deviceAdapter = new ArrayAdapter<>(this, R.layout.spinner, R.id.list, items);

        deviceSpinner.setAdapter(deviceAdapter);

        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        final ConnectionActivity activity = this;

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




        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                    // Determine if Wifi P2P mode is enabled or not, alert
                    // the Activity.
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        activity.setIsWifiP2pEnabled(true);
                        turnon.setEnabled(false);
                    } else {
                        activity.setIsWifiP2pEnabled(false);
                        shootAlert("Go to settings to enable wifi-direct?");
                        turnon.setEnabled(true);
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

                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    if (networkInfo.isConnected()) {

                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                            @Override
                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                                if (!info.groupFormed) {
                                    return;
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (info.isGroupOwner) {
                                            createServer();
                                        } else {
                                            createClient(info.groupOwnerAddress);
                                        }

                                        Intent i = new Intent(activity, PlaceShipsActivity.class);
                                        startActivity(i);
                                    }
                                }).start();
                            }
                        });
                    }
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
                    if (mManager != null) {
                        mManager.requestPeers(mChannel, activity);
                    }
                }
            }
        };

    }


    /**
     * Called when p2p wifi is enabled/disabled
     */
    public void setIsWifiP2pEnabled(boolean enabled) {

    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        //receiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    public void connect(View view) {

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {


                WifiP2pDevice deviceSelected = (WifiP2pDevice) deviceSpinner.getSelectedItem();

                if (deviceSelected == NO_DEVICES_FOUND) {
                    toast("No device selected");

                    return;
                }
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceSelected.deviceAddress;
                config.wps.setup = WpsInfo.PBC;


                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        toast("Wi-fi direct connection established.");
                    }


                    @Override
                    public void onFailure(int reason) {
                        toast("Connection Failed.");
                    }
                });

            }
        });
        connect.start();
    }

    /**
     * Blocks calling thread and creates server, unblocked when there is a connection
     */
    private boolean createServer() {

        try {
            ServerSocket server = new ServerSocket(port);
            Socket client = server.accept();
            NetworkAdapter.setSocket(client);

        } catch (IOException e) {
            Log.d("wifiMe", "Failed creating server");
            return false;
        }

        toast("Game connection to peer established.");
        return true;
    }

    private boolean createClient(InetAddress address) {

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port), 20000);
        } catch (SocketTimeoutException e) {
            toast("Connection timed out");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        toast("Game connection to peer established.");
        NetworkAdapter.setSocket(socket);
        return true;
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

        if (refreshedPeers.isEmpty()) {
            deviceAdapter.clear();
            deviceAdapter.add(NO_DEVICES_FOUND);
        } else {
            deviceAdapter.clear();
            for (WifiP2pDevice device : peerList.getDeviceList()) {
                deviceAdapter.add(device);
            }
        }
        Log.d("wifiMe", "Devices updates");
        deviceAdapter.notifyDataSetChanged();
    }

    public void refresh(View view) {

        mManager.requestPeers(mChannel, this);
        toast("Refreshed!");
    }

    private void toast(final String s) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //makes and displays an AlertDialog
    protected void shootAlert(String msg) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(msg);
        build.setCancelable(true);

        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        build.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //cancel
                dialog.cancel();
                turnon.setEnabled(true);
            }
        });

        AlertDialog alert = build.create();
        alert.show();
    }
}
