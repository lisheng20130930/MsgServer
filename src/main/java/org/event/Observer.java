package org.event;

public interface Observer {
    void handle(Object usr, int mask);
}
