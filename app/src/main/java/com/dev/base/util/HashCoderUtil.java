package com.dev.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 加密工具类
 */
public class HashCoderUtil {

    /**
     * MD5散列
     *
     * @param keyBytes
     * @return
     */
    public static String md5Crypt(byte[] keyBytes) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            // 使用指定的字节更新摘要
            mdInst.update(keyBytes);

            // 获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            return byteToHexStr(md);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * MD5散列（盐加密）
     *
     * @param keyBytes
     * @param salt
     * @return
     */
    public static String md5Crypt(byte[] keyBytes, String salt) {
        String strKey = new String(keyBytes);

        if (salt != null && "".equals(salt) == false) {
            strKey = strKey + "{" + salt.toString() + "}";
        }

        return md5Crypt(strKey.getBytes());
    }

    /**
     * SHA1散列
     *
     * @param keyBytes
     * @return
     */
    public static String sha1Crypt(byte[] keyBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(keyBytes);
            return byteToHexStr(digest);
        } catch (NoSuchAlgorithmException e) {
        }

        return "";
    }

    /**
     * SHA1散列（盐加密）
     *
     * @param keyBytes
     * @param salt
     * @return
     */
    public static String sha1Crypt(byte[] keyBytes, String salt) {
        String fullStr = new String(keyBytes) + "{" + salt + "}";

        return sha1Crypt(fullStr.getBytes());
    }

    /**
     * 将byte数组变为16进制对应的字符串
     *
     * @param byteArray byte数组
     * @return 转换结果
     */
    private static String byteToHexStr(byte[] byteArray) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            String shaHex = Integer.toHexString(byteArray[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }


    /**
     * MD5编码
     *
     * @param origin 原始字符串
     *
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}
