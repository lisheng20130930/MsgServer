package com.core;

import com.config.CMD_MsgServer;
import org.event.Connection;
import org.event.Thttpd;
import org.utils.CmmnUtils;
import org.utils.Queue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Manager implements Thttpd.WsHandler{
    public static final int MAX_SIZE = 256;
    private static Manager sInstance = new Manager();
    private MessageSender sender = null;
    private List<CHarbor> clientList;
    private Parser parser;

    private Manager(){
        clientList = new LinkedList<>();
        parser = new Parser();
    }

    public static Manager getInstance() {
        return sInstance;
    }

    @Override
    public boolean onWsMessage(Connection conn, String message) {
        Map<String,String> msg = parser.parseMessage(message);
        if(msg==null){
            return false;
        }
        boolean r = false;
        Integer cmd = CmmnUtils.asInteger(msg.get("cmd"));
        if(cmd==null){
            return false;
        }
        switch (cmd){
            case CMD_MsgServer.C_Subscribe:
                r = handleSubscribe(msg);
                break;
            case CMD_MsgServer.C_Publish:
                r = handlePublish(msg);
                break;
            case CMD_MsgServer.C_Login:
                r = handleLogin(conn,msg);
                break;
            case CMD_MsgServer.C_Ping:
                r = handlePing(msg);
                break;
            default:
                break;
        }
        return r;
    }

    private boolean sendMessage(Connection conn, String message){
        if(sender!=null){
            return sender.postMessage(conn,message);
        }
        return false;
    }

    public void onClosing(Connection conn){
        CHarbor client = getClientByConn(conn);
        if(client==null){
            return;
        }
        client.setStatus(CHarbor.EcStatus.OFFLINE);
        client.setConn(null);
    }

    public void register(MessageSender sender){
        this.sender = sender;
    }

    public interface MessageSender{
        boolean postMessage(Connection conn, String message);
    }

    /////////////////////////////////////////////////////////////////////
    /// 具体逻辑
    private boolean handleLogin(Connection conn, Map<String, String> params) {
        String id = params.get("id");
        if(CmmnUtils.isEmpty(id)){
            return false;
        }
        CHarbor client = getClientById(id);
        if(client!=null){
            if(client.getStatus()== CHarbor.EcStatus.OFFLINE){
                client.setStatus(CHarbor.EcStatus.NONE);
                client.setConn(conn);
            }else{
                client.getConn().close(0);
                client = null;
            }
        }
        if(client == null){
            client = new CHarbor();
            client.setId(id);
            client.setConn(conn);
            client.setStatus(CHarbor.EcStatus.NONE);
            client.setQueue(new Queue(MAX_SIZE));
            clientList.add(client);
        }
        client.setLoginTime(new Date());
        String msg = buildMsg(CMD_MsgServer.S_LoginSuccess,"success");
        sendMessage(client.getConn(), msg);
        return true;
    }

    public void handlePublish(String destId, String content){
        CHarbor c = getClientById(destId);
        if(c==null){
            c = loadClientOffline(destId);
        }
        c.getQueue().push(content);
        if(!c.getStatus().equals(CHarbor.EcStatus.RECEIVING)){
            return;
        }
        String msg = buildMsg(CMD_MsgServer.S_MsgNew,content);
        sendMessage(c.getConn(),msg);
    }

    private boolean handlePublish(Map<String, String> params) {
        String id = params.get("id");
        String destId = params.get("destId");
        String content = params.get("content");
        if(CmmnUtils.isEmpty(id)
                || CmmnUtils.isEmpty(destId)
                || CmmnUtils.isEmpty(content)){
            return false;
        }
        CHarbor client = getClientById(id);
        if(client==null){
            return false;
        }
        if(client.getStatus().equals(CHarbor.EcStatus.OFFLINE)){
            return false;
        }
        String msg = buildMsg(CMD_MsgServer.S_PublishSuccess,"success");
        sendMessage(client.getConn(), msg);
        handlePublish(destId, content);
        return true;
    }

    private boolean handleSubscribe(Map<String, String> params) {
        String markLine = params.get("markLine");
        String id = params.get("id");
        if(CmmnUtils.isEmpty(id)){
            return false;
        }
        CHarbor client = getClientById(id);
        if(client==null){
            return false;
        }
        if(client.getStatus().equals(CHarbor.EcStatus.OFFLINE)){
            return false;
        }
        String msg = buildMsg(CMD_MsgServer.S_Receiving,"success");
        sendMessage(client.getConn(), msg);
        if(client.getStatus().equals(CHarbor.EcStatus.RECEIVING)){
            return true;
        }
        client.setStatus(CHarbor.EcStatus.RECEIVING);
        List<String> dataList = client.getQueue().getAll(markLine);
        if(dataList.size()>0){
            for(String s: dataList){
                msg = buildMsg(CMD_MsgServer.S_MsgNew,s);
                sendMessage(client.getConn(), msg);
            }
        }
        return true;
    }

    private boolean handlePing(Map<String, String> params) {
        String id = params.get("id");
        if(CmmnUtils.isEmpty(id)){
            return false;
        }
        CHarbor client = getClientById(id);
        if(client==null){
            return false;
        }
        String msg = buildMsg(CMD_MsgServer.S_Pong,"Pong");
        return sendMessage(client.getConn(),msg);
    }

    private CHarbor loadClientOffline(String id) {
        CHarbor client = new CHarbor();
        client.setId(id);
        client.setConn(null);
        client.setStatus(CHarbor.EcStatus.OFFLINE);
        client.setQueue(new Queue(MAX_SIZE));
        client.setLoginTime(new Date());
        return client;
    }

    private CHarbor getClientByConn(Connection conn) {
        for(CHarbor c : clientList){
            if(c.getConn()==conn){
                return c;
            }
        }
        return null;
    }

    private CHarbor getClientById(String id) {
        for(CHarbor c : clientList){
            if(c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }

    private String buildMsg(int cmd, String content) {
        return String.format("{\"cmd\":%d,\"content\":\"%s\"}",
                cmd,
                content
        );
    }
}
