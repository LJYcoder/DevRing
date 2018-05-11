package com.dev.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 加密工具类
 */
public class EncryptUtil {

    /**
     * MD5散列
     *
     * @param keyBytes
     * @return
     */
    public static String md5encrypt(byte[] keyBytes) {
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
    public static String md5encrypt(byte[] keyBytes, String salt) {
        String strKey = new String(keyBytes);

        if (salt != null && "".equals(salt) == false) {
            strKey = strKey + "{" + salt.toString() + "}";
        }

        return md5encrypt(strKey.getBytes());
    }

    /**
     * SHA1散列
     *
     * @param keyBytes
     * @return
     */
    public static String sha1encrypt(byte[] keyBytes) {
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
    public static String sha1encrypt(byte[] keyBytes, String salt) {
        String fullStr = new String(keyBytes) + "{" + salt + "}";

        return sha1encrypt(fullStr.getBytes());
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

    //--------------------AES---------------------//
    public static boolean AESencrypt(String encryptKey, File encryptFile, File targetFile) {
        FileInputStream fis;
        FileOutputStream fos;
        try {
            byte[] oldByte = new byte[(int) encryptFile.length()];
            fis = new FileInputStream(encryptFile);
            fis.read(oldByte);
            byte[] newByte = AESencrypt(encryptKey, oldByte);
            fos = new FileOutputStream(targetFile);
            fos.write(newByte);
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean AESdecrypt(String encryptKey, File decryptFile, File targetFile) {
        FileInputStream fis;
        FileOutputStream fos;
        try {
            byte[] oldByte = new byte[(int) decryptFile.length()];
            fis = new FileInputStream(decryptFile);
            fis.read(oldByte);
            byte[] newByte = AESdecrypt(encryptKey, oldByte);
            fos = new FileOutputStream(targetFile);
            fos.write(newByte);
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static byte[] AESencrypt(String encryptKey, byte[] clearbyte) throws Exception {
        byte[] rawKey = getRawKey(encryptKey.getBytes());
        byte[] result = AESencrypt(rawKey, clearbyte);
        return result;
    }

    private static byte[] AESdecrypt(String encryptKey, byte[] encrypted) throws Exception {
        byte[] rawKey = getRawKey(encryptKey.getBytes());
        byte[] result = AESdecrypt(rawKey, encrypted);
        return result;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] AESencrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] AESdecrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
    //--------------------AES---------------------//

}
