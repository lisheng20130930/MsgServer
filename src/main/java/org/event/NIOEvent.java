package org.event;

import java.nio.channels.SelectableChannel;

public class NIOEvent {
    public static final int AE_READ     = 0x01;
    public static final int AE_WRITE    = 0x02;
    public static final int AE_ACCEPT   = 0x04;

    public Observer acceptHandler;
    public Observer readHandler;
    public Observer writeHandler;
    public int mask;
    private Object usr;
    SelectableChannel socket;

    public NIOEvent(SelectableChannel socket, Object usr){
        this.socket = socket;
        this.usr = usr;
    }

    public void clear(){
        this.acceptHandler = null;
        this.readHandler = null;
        this.writeHandler = null;
        this.usr = null;
        this.socket = null;
    }

    public Object getUsr() {
        return usr;
    }
}
