package com.masato25.hellopapp;

/**
 * Created by masato on 2017/6/28.
 */

public class Avatar {
    private String name;
    private String addr;
    private int bluetoothStatus;
    private int btype;
    private String uuid;
    private int rssi;
    private double lx;
    private double ly;
    private int missingCount;

    public Avatar(String name, String addr, int bluetoothStatus, String uuid, int btype, int brssi, double lx, double ly){
        if (name == null) {
            this.name = "null-" + addr;
        } else {
            this.name = name;
        }
        this.addr = addr;
        this.bluetoothStatus = bluetoothStatus;
        this.uuid = uuid;
        this.btype = btype;
        this.rssi = brssi;
        this.lx = lx;
        this.ly = ly;
    }

    public String GetName(){
        return this.name;
    }

    public String GetAddr(){
        return this.addr;
    }

    public int GetStatus(){
        return  this.bluetoothStatus;
    }

    public String GetUUid(){
        return  this.uuid;
    }

    public int GetType(){
        return this.btype;
    }

    public void setMissingCount(int num){
        this.missingCount = num;
    }

    public void resetMissingCount() { this.missingCount = 0; }

    public void addMissingCount() { this.missingCount = this.getMissingCount() +  1; }

    public int getMissingCount() { return this.missingCount;}

    public boolean isLost() {
        if(this.missingCount > 2){
            return true;
        }
        return false;
    }

    public String GetCoordinate(){ return this.lx + "," + this.ly; }

}
