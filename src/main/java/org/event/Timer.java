package org.event;

public class Timer{
    public Observer handler;
    public Object usr;
    public long timerID;  /* time event identifier. */
    public long when_ms;  /* milliseconds */

    public Timer(long timerID, Observer handler, Object usr){
        this.handler = handler;
        this.timerID = timerID;
        this.usr = usr;
    }

    public void clear(){
        this.handler = null;
        this.usr = null;
    }

    public void addMillisecondsToNow(long milliseconds){
        this.when_ms = System.currentTimeMillis()+milliseconds;
    }
}