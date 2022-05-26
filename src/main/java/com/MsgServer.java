package com;

import com.core.Manager;
import com.web.Gate;
import org.event.*;
import org.utils.Logger;
import org.vo.Rsp;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MsgServer extends Thttpd implements Manager.MessageSender {
    public static final int CONNECTION_TIMEOUT = 50000;
    public static final int PORT = 28000;

    public MsgServer(EventLoop loop) {
        super(loop,CONNECTION_TIMEOUT,PORT);
        Manager manager = Manager.getInstance();
        manager.register(this);
        wsHandler = manager;
    }

    private String toHeader(Map<String,String> headers){
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\r\n");
        for (Map.Entry<String, String> m : headers.entrySet()) {
            String key = m.getKey();
            String value = m.getValue();
            builder.append(key).append(": ").append(value).append("\r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }

    public ByteBuffer toRsp(Rsp rsp){
        Map<String, String> headers;
        ByteBuffer body;
        if(rsp == null) {
            byte[] _error = "error".getBytes();
            body = ByteBuffer.wrap(_error);
            headers = new HashMap<>();
            headers.put("Content-Length","5");
        }else{
            headers = rsp.getHeaders();
            body = rsp.getBody();
        }
        Logger.log("[THttpD] Rsp header===>" + headers);
        byte[] headerBuffer = toHeader(headers).getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(headerBuffer.length
                + body.limit() - body.position());
        byteBuffer.put(headerBuffer);
        byteBuffer.put(body);
        byteBuffer.flip();
        return byteBuffer;
    }

    public void onReqCycled(HttpReq req) {
        ByteBuffer byteBuffer = toRsp((Rsp)req.getUsr());
        boolean r = req.getConn().sendBuffer(byteBuffer.array());
        req.setUsr(null);
        req.clear();
        long used = System.currentTimeMillis()-req.time;
        if(r){
            Logger.log("[THttpD] req("+req.iID+") success,used-time="+used);
        }else{
            Logger.log("[THttpD] req("+req.iID+") error,used-time="+used);
        }
    }

    @Override
    protected void handle(HttpReq req) {
        req.time = System.currentTimeMillis();
        if(req.isUpgrade()){
            boolean r = upgradeWsChannel(req);
            req.clear();
            if(!r){
                req.getConn().close(0);
            }
            long used = System.currentTimeMillis()-req.time;
            Logger.log("[THttpD] req ("+req.iID+") "
                    +"upgrade "+(r?"success":"error")+",used-time="+used);
            Logger.log("[THttpD] WebSocket established");
            return;
        }
        loop.actorAdd(new EvActor.TCB() {
            @Override
            public void handle(Object usr, int mask) {
                onReqCycled((HttpReq)usr);
            }
            @Override
            public boolean run(Object usr) {
                Rsp r = Gate.getInstance().handle((HttpReq)usr);
                req.setUsr(r);
                return true;
            }
        },req);
    }

    @Override
    public void onClosing(Connection conn, int code) {
        Object p = conn.getUsr();
        if (p != null && p instanceof SnParser) {
            Manager.getInstance().onClosing(conn);
            ((SnParser)p).clear();
            conn.setUsr(null);
            Logger.log("[THttpD] WebSocket closed");
        }
        super.onClosing(conn,code);
    }

    @Override
    public boolean postMessage(Connection conn, String message) {
        return sendWsMessage(conn, message);
    }

    public static void main(String[] args) {
        EventLoop loop = new EventLoop(8,50000);
        MsgServer web = new MsgServer(loop);
        if(web.start()){
            while (true) {
                if(loop.sig_exit()){
                    Logger.log("SIG_EXIT caught...");
                    break;
                }
                loop.processEvents();
            }
            web.stop();
        }
        System.exit(0);
    }
}
