package com.config;

public interface CMD_MsgServer {
    int C_Login = 100;
    int S_LoginSuccess = 300;
    int C_Publish = 102;
    int S_PublishSuccess = 305;
    int C_Subscribe = 103;
    int S_Receiving = 303;
    int C_Ping = 500;
    int S_Pong = 501;
    int S_MsgNew = 200;
}
