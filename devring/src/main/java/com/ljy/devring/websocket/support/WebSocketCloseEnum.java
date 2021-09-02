package com.ljy.devring.websocket.support;
/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: WebSocket关闭代码枚举
 */
public enum WebSocketCloseEnum {
    NORMAL_EXIT(1000,"正常关闭"),
    USER_EXIT(1001,"终端离开"),
    ERROR_PROTOCOL(1002,"协议错误关闭连接"),
    ERROR_DATA(1003,"接收到不能接受的数据关闭连接"),
    RETAIN_1004(1004,"保留"),
    RETAIN_1005(1005,"保留"),
    RETAIN_1006(1006,"保留"),
    ERROR_TYPE(1007,"接收到的数据没有消息类型关闭连接"),
    ERROR_POLICY(1008,"接收到的消息背离它的政策关闭连接"),
    DATA_TOO_BIG(1009,"接收到的消息太大以至于不能处理关闭连接"),
    ERROR_RESPONSE(1010,"服务器不在响应消息关闭连接"),
    ERROR_UNEXPECTED(1011,"服务器因为遇到非预期的情况导致它不能完成请求而关闭连接"),
    RETAIN_1015(1015,"保留")
    ;
    private int code;
    private String reason;


    WebSocketCloseEnum(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
