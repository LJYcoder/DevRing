package com.ljy.devring.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ljy.devring.other.RingLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            RingLog.e(TAG, "isSDCardAvailable : SD卡不可用!", e);
            return false;
        }
    }

    /**
     * 创建一个文件夹, 存在则返回, 不存在则新建
     *
     * @param parentDirectory 父目录路径
     * @param directory       目录名
     * @return 文件，null代表失败
     */
    public static File getDirectory(String parentDirectory, String directory) {
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
     * @param directory       目录名
     * @return 文件，null代表失败
     */
    public static File getDirectory(File parentDirectory, String directory) {
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
    public static File getFile(String catalogPath, String name) {
        if (TextUtils.isEmpty(catalogPath) || TextUtils.isEmpty(name)) {
            Log.e(TAG, "getFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(catalogPath, name);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "getFile : 创建" + catalogPath + "目录下的文件" + name + "文件失败!", e);
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
    public static File getFile(File catalog, String name) {
        if (catalog == null || TextUtils.isEmpty(name)) {
            Log.e(TAG, "getFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(catalog, name);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "getFile : 创建" + catalog + "目录下的文件" + name + "文件失败!", e);
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
    public static File getFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "getFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "getFile : 创建" + file.getName() + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 计算文件/文件夹的大小
     *
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
     * 删除文件夹中的所有文件
     *
     * @param file 指定的文件夹
     * @param isDeleteSelf 是否删除文件夹本身
     * @return true代表成功删除
     */
    public static boolean deleteFile(File file, boolean isDeleteSelf) {
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
                        if (!deleteFile(subFile,true)) {
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

        if (isDeleteSelf) {
            if (!file.delete()) {
                result = false;
            }
        }

        return result;
    }

    public static boolean copyFile(File source, File target) {
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] bytes = new byte[1024];
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean saveFile(InputStream inputStream, OutputStream outputStream) {
        if (inputStream == null || outputStream == null) {
            return false;
        }

        try {
            try {
                byte[] buffer = new byte[1024 * 4];

                while (true) {
                    int read = inputStream.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //返回"/data"目录
    public static String getDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    //返回"/storage/emulated/0"目录（需要外部存储权限）
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
     * （需要外部存储权限）
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return 返回"/storage/emulated/0/xxx"目录
     * 例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Alarms"
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
    public static String getCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    //返回"/data/user/0/com.xxx.xxx/files"目录
    public static String getFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    //返回"/storage/emulated/0/Android/data/com.xxx.xxx/cache"目录
    public static String getExternalCacheDir(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return 返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"目录
     * 例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"
     */
    public static String getExternalFilesDir(Context context, String type) {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_ALARMS);
        //返回的目录有可能不存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    /** android 7.0 File适配相关 开始 **/

    /**
     * 根据file获取uri，适配7.0系统
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFileAndroid7(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFileAndroid7(Context context, File file) {
        Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(context, context.getPackageName() + ".android7.fileprovider", file);
        return fileUri;
    }

    /**
     * 授予文件的读写权限
     * @param context 上下文
     * @param packageName 应用包名
     * @param fileUri 文件uri
     */
    public static void grantUriPermission(Context context, String packageName, Uri fileUri) {
        context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public static void setIntentDataAndType(Context context, Intent intent, File file, String type, boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
    /** android 7.0 File适配相关 结束 **/

}
