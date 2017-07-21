package com.masato25.parpcamara;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by masato on 2017/7/6.
 */

public class WebAccessThread implements Runnable {
    private String url;
    private NetWorkingTmp httpcli;
    private String method;
    private JSONObject jsobj;
    private Toast toast;
    private int duration;
    private Context context;

    public WebAccessThread(String surl, NetWorkingTmp cli, String method, JSONObject jsobj) {
        this.toast = toast;
        this.url = surl;
        this.httpcli = cli;
        this.method = method;
        this.jsobj = jsobj;
    }

    public void setToast(Toast toast){
        this.toast = toast;
        this.duration = Toast.LENGTH_SHORT;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void run() {
        // 運行網路連線的程式
        String result = this.httpcli.request(this.url, this.method, this.jsobj);
        if (this.context != null && this.toast != null) {
            toast = Toast.makeText(this.context, "show: " + result, this.duration);
            toast.show();
        }
    }
}