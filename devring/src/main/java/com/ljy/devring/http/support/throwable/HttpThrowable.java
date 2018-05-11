package com.ljy.devring.http.support.throwable;

/**
 * author:  ljy
 * date:    2018/5/11
 * description:
 */

public class HttpThrowable extends Exception {
    public int errorType;
    public String message;
    public Throwable throwable;

    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1002;
    /**
     * DNS解析失败（无网络）
     */
    public static final int NO_NET_ERROR = 1003;
    /**
     * 连接超时错误
     */
    public static final int TIME_OUT_ERROR = 1004;
    /**
     * 网络（协议）错误
     */
    public static final int HTTP_ERROR = 1005;
    /**
     * 证书错误
     */
    public static final int SSL_ERROR = 1006;

    public HttpThrowable(int errorType, String message, Throwable throwable) {
        super(throwable);
        this.errorType = errorType;
        this.message = message;
        this.throwable = throwable;
    }


}
