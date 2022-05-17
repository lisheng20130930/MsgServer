package com.core;

import lombok.Data;
import org.event.Connection;
import org.utils.Queue;
import java.util.Date;

@Data
public class RClient {
    public enum EcStatus {
        OFFLINE,
        NONE,
        RECEIVING
    }
    private Connection conn;
    private String id;  //唯一标识
    private Queue queue;
    private Date loginTime;
    private EcStatus status;
}
