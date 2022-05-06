package com.core;

import com.alibaba.fastjson.JSON;

import java.util.Map;

public class Parser {
    public Map<String,String> parseMessage(String msg) {
        try {
            return JSON.parseObject(msg, Map.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
