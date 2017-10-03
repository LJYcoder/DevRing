package com.dev.base.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dev.base.app.MyApplication;
import com.dev.base.util.log.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * author：   ljy
 * date：     2017/10/1
 * description 文件/文件夹工具类
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * SD卡是否能用
     *
     * @return true 可用,false不可用
     */
    public static boolean isSDCardAvailable() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            LogUtil.e(TAG, "isSDCardAvailable : SD卡不可用!", e);
            return false;
        }
    }

    /**
     * 创建一个文件夹, 存在则返回, 不存在则新建
     *
     * @param parentDirectory 父目录路径
     * @param directory  目录名
     * @return 文件，null代表失败
     */
    public static File generateDirectory(String parentDirectory, String directory) {
        if (TextUtils.isEmpty(parentDirectory) || TextUtils.isEmpty(directory)) {
            return null;
        }
        File file = new File(parentDirectory, directory);
        boolean flag;
        if (!file.exists()) {
            flag = file.mkdir();
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 创建一个文件夹, 存在则返回, 不存在则新建
     *
     * @param parentDirectory 父目录
     * @param directory  目录名
     * @return 文件，null代表失败
     */
    public static File generateDirectory(File parentDirectory, String directory) {
        if (parentDirectory == null || TextUtils.isEmpty(directory)) {
            return null;
        }
        File file = new File(parentDirectory, directory);
        boolean flag;
        if (!file.exists()) {
            flag = file.mkdir();
        } else {
            flag = true;
        }
        return flag ? file : null;
    }


    /**
     * 创建一个文件, 存在则返回, 不存在则新建
     *
     * @param catalogPath 父目录路径
     * @param name        文件名
     * @return 文件，null代表失败
     */
    public static File generateFile(String catalogPath, String name) {
        if (TextUtils.isEmpty(catalogPath) || TextUtils.isEmpty(name)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(catalogPath, name);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "generateFile : 创建" + catalogPath + "目录下的文件" + name + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 创建一个文件, 存在则返回, 不存在则新建
     *
     * @param catalog 父目录
     * @param name    文件名
     * @return 文件，null代表失败
     */
    public static File generateFile(File catalog, String name) {
        if (catalog == null || TextUtils.isEmpty(name)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(catalog, name);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "generateFile : 创建" + catalog + "目录下的文件" + name + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 根据全路径创建一个文件
     *
     * @param filePath 文件全路径
     * @return 文件，null代表失败
     */
    public static File generateFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "generateFile : 创建" + file.getName() + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 计算文件/文件夹的大小
     * @param file 文件或文件夹
     * @return 文件大小
     */
    public static long calculateFileSize(File file) {
        if (file == null) {
            return 0;
        }

        if (!file.exists()) {
            return 0;
        }

        long result = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        result += calculateFileSize(subFile);
                    } else {
                        result += subFile.length();
                    }
                }
            }
        }
        result += file.length();
        return result;
    }

    /**
     * 删除文件/文件夹
     * 如果是文件夹，则会删除其下的文件以及它本身
     * @param file file
     * @return true代表成功删除
     */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return true;
        }
        if (!file.exists()) {
            return true;
        }
        boolean result = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        if (!deleteFile(subFile)) {
                            result = false;
                        }
                    } else {
                        if (!subFile.delete()) {
                            result = false;
                        }
                    }
                }
            }
        }
        if (!file.delete()) {
            result = false;
        }

        return result;
    }

    //返回"/data"目录
    public static String getDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    //返回"/storage/emulated/0"目录
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //返回"/system"目录
    public static String getRootDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    //返回"/cache"目录
    public static String getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    /**
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return 返回"/storage/emulated/0/xxx"目录
     *         例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Alarms"
     */
    public static String getExternalStoragePublicDirectory(String type) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        //返回的目录有可能不存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    //返回"/data/user/0/com.xxx.xxx/cache"目录
    public static String getCacheDir() {
        return MyApplication.getInstance().getCacheDir().getAbsolutePath();
    }

    //返回"/data/user/0/com.xxx.xxx/files"目录
    public static String getFilesDir() {
        return MyApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    //返回"/storage/emulated/0/Android/data/com.xxx.xxx/cache"目录
    public static String getExternalCacheDir() {
        return MyApplication.getInstance().getExternalCacheDir().getAbsolutePath();
    }

    /**
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return  返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"目录
     *          例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"
     */
    public static String getExternalFilesDir(String type) {
        File file = MyApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_ALARMS);
        //返回的目录有可能不存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


}
