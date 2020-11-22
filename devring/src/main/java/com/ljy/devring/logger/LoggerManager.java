package com.ljy.devring.logger;

import androidx.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * author:  XieYos
 * date:    2020-11-22
 * description: 日志模块管理器
 */
public final class LoggerManager {

    @Inject
    LoggerConfig loggerConfig;

    public void initAndroidLogAdapter(boolean isShowRingLog,boolean showThreadInfo, int methodCount, int methodOffset, LogStrategy logStrategy,String tag) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(showThreadInfo)
                .methodCount(methodCount)
                .methodOffset(methodOffset)
                .logStrategy(logStrategy)
                .tag(tag)
                .build();
        RingLog.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isShowRingLog;
            }
        });
    }

    public void initDiskLogAdapter(boolean isRingLogFolder,LogStrategy logStrategy,String tag) {
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .logStrategy(logStrategy)
                .tag(tag)
                .build();
        RingLog.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isRingLogFolder;
            }
        });
    }
}
