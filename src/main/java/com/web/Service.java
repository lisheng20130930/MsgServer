package com.web;

import com.core.Manager;

public class Service {
    private static Service service = new Service();

    private Service(){}

    public static Service getInstance(){
        return service;
    }

    public boolean publish(String destId, String content) {
        Manager.getInstance().handlePublish(destId, content);
        return true;
    }
}
