package edu.utep.cs4330.battleship;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/18/2017.
 *
 * Wrapper class used to replace toString method of WifiP2pDevice safely.
 */

public class CustomDevice{
    public WifiP2pDevice device;

    public CustomDevice(WifiP2pDevice d){
        device = d;
    }

    public String toString(){
        if(device == null)
            return "No devices found.";
        return device.deviceName;
    }
}
