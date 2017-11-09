package com.dev.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.dev.base.app.constant.BaseConstants;
import com.dev.base.util.log.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 异常处理类(app出现异常会保留文件到本地)
 *
 * @author zhup-Administrator
 * @ClassName: CrashLogUtil
 * @date 2015年6月24日 上午9:55:29
 */
public class CrashLogUtil implements UncaughtExceptionHandler {
    public static final String TAG = CrashLogUtil.class.getSimpleName();

    private static CrashLogUtil INSTANCE = new CrashLogUtil();

    private Context mContext;

    private UncaughtExceptionHandler mDefaultHandler;

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    //用来存储设备信息和异常信息
    private Map<String, String> mInformation = new HashMap<String, String>();

    private File mFileOutput;

    private CrashLogUtil() {
    }

    public static CrashLogUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化方法
     *
     * @param ctx 上下文
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    //设置异常信息输出的文件
    public void setFileOutput(File file) {
        mFileOutput = file;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "error : ", e);
            }
            // 退出程序
            System.exit(0);
        }

    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//		DialogHelper helper = new DialogHelper(Context);
        //--
        //收集设备参数信息   
        collectDeviceInfo(mContext);
        //保存日志文件   
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mInformation.put("versionName", versionName);
                mInformation.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            LogUtil.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInformation.put(field.getName(), field.get(null).toString());
                LogUtil.e(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtil.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : mInformation.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        LogUtil.e(TAG, sb.toString());
        try {
            String time = mFormatter.format(new Date());
            String fileName = time + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                File dir = FileUtil.generateCacheTemporaryFile(mContext, fileName);

                if (mFileOutput == null) {
                    File dirTemp = FileUtil.generateDirectory(FileUtil.getExternalCacheDir(), BaseConstants.APP_TMP);
                    mFileOutput = FileUtil.generateFile(dirTemp, fileName);
                }

                if (mFileOutput == null) {
                    LogUtil.e(TAG, "文件创建失败!");
                    return null;
                }
                FileOutputStream fos = new FileOutputStream(mFileOutput);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            LogUtil.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

}
