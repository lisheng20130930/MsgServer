package org.vo;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Map;

@Data
public class Rsp {
    Map<String,String> headers;
    ByteBuffer body;

    public Rsp(Map<String, String> headers, byte[] buffer) {
        this.headers = headers;
        this.body = ByteBuffer.wrap(buffer);
    }
}
