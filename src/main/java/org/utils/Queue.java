package org.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Queue{
    private int maxSize ;
    private int first;
    private int last;
    private String[] arr;

    public Queue(int maxSize){
        this.maxSize = maxSize;
        this.arr = new String[maxSize];
        this.first = 0;
        this.last = 0;
    }

    private boolean isEmpty(){
        return last == first;
    }

    private boolean isFull(){
        return (last + 1) % maxSize == first;
    }

    private String pop(){
        if (isEmpty()) {
            return null;
        }
        String value = arr[first];
        first = (first+1) % maxSize;
        return value;
    }

    private int dataNum(){
        return (last+maxSize-first) % maxSize;
    }

    public List<String> getAll(String markLine){
        List<String> list = new LinkedList<>();
        if (isEmpty()) {
            return list;
        }
        int pos = (-1);
        if(!CmmnUtils.isEmpty(markLine)){
            for (int i = first; i < first+dataNum() ; i++) {
                if(arr[i%maxSize].equals(markLine)){
                    pos = i;
                    break;
                }
            }
        }
        pos = ((-1)==pos)?first:pos;
        for (int i = pos; i < first+dataNum() ; i++) {
            list.add(arr[i%maxSize]);
        }
        return list;
    }

    public boolean push(String value) {
        if(isFull()){
            pop();
        }
        arr[last] = value;
        last = (last + 1) % maxSize;
        return true;
    }
}
