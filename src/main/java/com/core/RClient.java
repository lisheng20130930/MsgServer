package com.core;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.utils.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class RClient extends WebSocketClient {
    private static final String _url = "ws://127.0.0.1:28000";
    private static final String _id = "100000000";
    private static final int ERR = 3;
    private volatile Thread keep_alive = null;
    private static RClient instance = null;
    private Delegate _delegate = null;
    private int counter = 0;
    private boolean connected = false;

    private RClient() throws URISyntaxException {
        super(new URI(_url));
    }

    private static synchronized RClient getInstance(){
        if(instance==null){
            try {
                instance = new RClient();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void startKeepAliveTimer(){
        connected = true;
        keep_alive = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                while (keep_alive == thisThread) {
                    try {
                        thisThread.sleep(15000);
                    } catch (InterruptedException ex) {
                    }
                    if (!connected) {
                        return;
                    }
                    Logger.log("============ping=========");
                    JSONObject object = new JSONObject();
                    object.put("message", "success");
                    object.put("cmd","500");
                    object.put("id", _id);
                    sendMessage(object.toJSONString());
                }
            }
        }, "KeepAlive");
        keep_alive.start();
    }

    private void stopKeepAliveTimer(){
        keep_alive = null;
        connected = false;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        JSONObject object = new JSONObject();
        object.put("message", "success");
        object.put("cmd","100");
        object.put("id", _id);
        sendMessage(object.toJSONString());
    }

    @Override
    public void onMessage(String message) {
        if(message.contains("300")){
            Logger.log("[Login Success]===>"+message);
            if(_delegate!=null){
                _delegate.onLoginSuccess();
            }
            startKeepAliveTimer();
            counter=0;
            return;
        }
        if(message.contains("501")){
            Logger.log("============Pong==========");
            return;
        }
        if(_delegate!=null){
            _delegate.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        stopKeepAliveTimer();
        if(counter>=ERR){
            if(_delegate!=null) {
                _delegate.onLeaving();
            }
        }else{
            counter++;
            new Thread(()->reconnect()).start();
        }
    }

    @Override
    public void onError(Exception e) {
        System.out.println("error==>"+e.getMessage());
    }

    public void setupHarbor(Delegate delegate){
        _delegate = delegate;
        try {
            connectBlocking();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clear(){
        try {
            closeBlocking();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        send(message);
        Logger.log("message "+message+" sent...........");
    }

    public interface Delegate{
        void onLoginSuccess();
        void onMessage(String message);
        void onLeaving();
    }

    public static void main(String[] args) {
        RClient.getInstance().setupHarbor(new Delegate() {
            @Override
            public void onLoginSuccess() {
                JSONObject object = new JSONObject();
                object.put("message", "success");
                object.put("cmd","103");
                object.put("id", _id);
                RClient.getInstance().sendMessage(object.toJSONString());
            }

            @Override
            public void onMessage(String message) {
                Logger.log("============message=========="+message);
            }

            @Override
            public void onLeaving() {
                Logger.log("============CLOSED==========");
            }
        });
    }
}
