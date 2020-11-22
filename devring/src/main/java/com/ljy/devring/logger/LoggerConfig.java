package com.ljy.devring.logger;

/**
 * author:  XieYos
 * date:    2020-11-22
 * description: 日志模块
 */
public class LoggerConfig {
    private boolean mIsShowRingLog = true;
    private String mRingLogFolder;
    private boolean mIsRingLogFolder = false;
    private String mTag = "DevRing";
    private int mMethodCount = 2;
    private int mMethodOffset = 0;
    private boolean mIsShowThreadInfo = false;
    private LogStrategy mLogStrategy;

    public void init() {

    }
    public boolean isShowRingLog() {
        return mIsShowRingLog;
    }

    //设置是否显示Ringlog打印的内容，默认true
    public LoggerConfig setIsShowRingLog(boolean isShowRingLog) {
        mIsShowRingLog = isShowRingLog;
        return this;
    }

    public boolean isRingLogFolder() {
        return mIsRingLogFolder;
    }

    //设置是否记录Ringlog打印的内容到文件，默认true
    public LoggerConfig setIsRingLogFolder(boolean isRingLogFolder) {
        mIsRingLogFolder = isRingLogFolder;
        return this;
    }

    public String getRingLogFolder() {
        return mRingLogFolder;
    }

    //设置日志目录
    public LoggerConfig setRingLogFolder(String mRingLogFolder) {
        this.mRingLogFolder = mRingLogFolder;
        return this;
    }

    public String getTag(){ return mTag;}

    //设置日志Tag
    public LoggerConfig setTag(String tag) {
        this.mTag = tag;
        return this;
    }

    public int getMethodCount() {return mMethodCount;}

    public LoggerConfig setMethodCount(int methodCount) {
        this.mMethodCount = methodCount;
        return this;
    }

    public int getMethodOffset(){return mMethodOffset;}

    public LoggerConfig setMethodOffset(int methodOffset){
        this.mMethodOffset = methodOffset;
        return this;
    }

    public LogStrategy getLogStrategy(){return mLogStrategy;}

    public LoggerConfig setLogStrategy(LogStrategy logStrategy){
        this.mLogStrategy = logStrategy;
        return this;
    }

    public boolean isShowThreadInfo(){return mIsShowThreadInfo;}

    public LoggerConfig setShowThreadInfo(boolean isShowThreadInfo){
        this.mIsShowThreadInfo = isShowThreadInfo;
        return this;
    }
}
