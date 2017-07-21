package com.masato25.hellopapp;

/**
 * Created by masato on 2017/6/26.
 */

public class DeviceItem {

    private String deviceName;
    private String address;
    private int bondstatus;
    private boolean connected;

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return connected;
    }

    public String getAddress() {
        return address;
    }

    public int getBondStatus() {
        return bondstatus;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceItem(String name, String address, int bondstatus, String connected){
        this.deviceName = name;
        this.address = address;
        this.bondstatus = bondstatus;
        if (connected == "true") {
            this.connected = true;
        }
        else {
            this.connected = false;
        }
    }
}