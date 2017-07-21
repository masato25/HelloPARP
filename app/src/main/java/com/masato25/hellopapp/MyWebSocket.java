package com.masato25.hellopapp;

/**
 * Created by masato on 2017/7/12.
 */

import org.phoenixframework.channels.*;
import android.util.Log;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class MyWebSocket  implements Runnable {
    private Socket socket;
    private Channel channel;

    public MyWebSocket() {
        // TODO Auto-generated method stub
        try{
            socket = new Socket("ws://192.168.31.223:4000/socket/websocket");
            socket.connect();
            channel = socket.chan("rooms:lobby", null);
            channel.join()
                    .receive("ignore", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            System.out.println("IGNORE");
                        }
                    })
                    .receive("ok", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            System.out.println("JOINED with " + envelope.toString());
                        }
                    });
        }catch(Exception e){
            //當斷線時會跳到catch,可以在這裡寫上斷開連線後的處理
            e.printStackTrace();
        }
    }

    public void run() {

        // TODO Auto-generated method stub
        try{
            channel.on("new:msg", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    System.out.println("NEW MESSAGE: " + envelope.toString());
                }
            });

            channel.on(ChannelEvent.CLOSE.getPhxEvent(), new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    System.out.println("CLOSED: " + envelope.toString());
                }
            });

            channel.on(ChannelEvent.ERROR.getPhxEvent(), new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    System.out.println("ERROR: " + envelope.toString());
                }
            });
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                    .put("user", "my_username")
                    .put("body", "aaaa");
            channel.push("new:msg", node);
        }catch(Exception e){
            //當斷線時會跳到catch,可以在這裡寫上斷開連線後的處理
            e.printStackTrace();
        }
    }
}
