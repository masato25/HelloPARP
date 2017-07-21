package com.masato25.parpcamara;

/**
 * Created by masato on 2017/7/12.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class MyWebSocket {
    private Thread thread;                //執行緒
    private Socket clientSocket;        //客戶端的socket
    private BufferedWriter bw;            //取得網路輸出串流
    private BufferedReader br;            //取得網路輸入串流
    private String tmp;                    //做為接收時的緩存
    private JSONObject json_write,json_read;        //從java伺服器傳遞與接收資料的json
    private String address;
    private int port;

    public void MyWebSocket(String address, int port) {
        this.address = address;
        this.port = port;
    }
    //連結socket伺服器做傳送與接收
    public Runnable Connection=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                // IP為Server端
                InetAddress serverIp = InetAddress.getByName(address);
                int serverPort = 5050;
                clientSocket = new Socket(serverIp, serverPort);

                //取得網路輸出串流
                bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
                // 取得網路輸入串流
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();    //宣告一個緩衝,從br串流讀取值
                    // 如果不是空訊息
                    if(tmp!=null){
                        //將取到的String抓取{}範圍資料
                        tmp=tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
                        json_read=new JSONObject(tmp);
                        //從java伺服器取得值後做拆解,可使用switch做不同動作的處理
                    }
                }
            }catch(Exception e){
                //當斷線時會跳到catch,可以在這裡寫上斷開連線後的處理
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
                try {
                    clientSocket.close();
                    bw.close();
                    br.close();
                }catch(Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    };
}
