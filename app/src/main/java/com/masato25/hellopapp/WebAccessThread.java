package com.masato25.hellopapp;

/**
 * Created by masato on 2017/6/27.
 */

public class WebAccessThread implements Runnable {
    private String url;
    private NetWorkingTmp httpcli;
    private Avatar avatar;
    private String avatarAddress;

    public WebAccessThread(String surl, NetWorkingTmp cli) {
        this.url = surl;
        this.httpcli = cli;
    }

    public void setAvatar(Avatar avatar){
        this.avatar = avatar;
    }
    public void setAvatarAddress(String address){
        this.avatarAddress = address;
    }

    public void setUrl(String surl){
        this.url = surl;
    }

    public void run() {
        // 運行網路連線的程式

        if(this.avatar != null) {
            this.httpcli.requestAvatar(this.url, this.avatar);
        }else if(this.avatarAddress != null){
            this.httpcli.requestLeaveAvatar(this.url, this.avatarAddress);
        }else{
            this.httpcli.request(this.url);
        }
    }
}
