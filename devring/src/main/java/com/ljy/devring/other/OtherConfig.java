package com.ljy.devring.other;

import com.ljy.devring.other.toast.IToastStyle;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/15
 * description: 其他模块的配置
 */

public class OtherConfig {

    private File mCrashDiaryFolder;
    private boolean mIsUseCrashDiary;
    private boolean mIsShowRingLog;
    private IToastStyle mIToastStyle;

    public File getCrashDiaryFolder() {
        return mCrashDiaryFolder;
    }

    //设置崩溃日志的地址，传入的file需为文件夹，默认保存在/storage/emulated/0/Android/data/com.xxx.xxx/cache/crash_log下
    public OtherConfig setCrashDiaryFolder(File fileDirectory) {
        this.mCrashDiaryFolder = fileDirectory;
        return this;
    }

    public boolean isUseCrashDiary() {
        return mIsUseCrashDiary;
    }

    //设置是否开启崩溃日志功能，默认不开启
    public OtherConfig setIsUseCrashDiary(boolean isUseCrashDiary) {
        mIsUseCrashDiary = isUseCrashDiary;
        return this;
    }

    public boolean isShowRingLog() {
        return mIsShowRingLog;
    }

    //设置是否显示Ringlog打印的内容，默认true
    public OtherConfig setIsShowRingLog(boolean isShowRingLog) {
        mIsShowRingLog = isShowRingLog;
        return this;
    }

    public IToastStyle getIToastStyle() {
        return mIToastStyle;
    }

    public OtherConfig setToastStyle(IToastStyle iToastStyle) {
        this.mIToastStyle = iToastStyle;
        return this;
    }
}
