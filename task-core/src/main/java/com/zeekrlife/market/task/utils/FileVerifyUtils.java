package com.zeekrlife.market.task.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.EncryptUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FileVerifyUtils {

    /**
     * 验证文件的MD5值是否与给定的MD5字符串匹配。
     *
     * @param filePath 要验证的文件路径。
     * @param hash 给定的MD5字符串。
     * @return 如果文件存在且其MD5值与给定的MD5字符串匹配，则返回true；否则返回false。
     */
    public static boolean verify(String filePath, String hash) {
        // 检查文件路径是否为空
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file;
        try {
            file = new File(filePath);
        } catch (NullPointerException e) {
            // 在创建File对象时发生异常，返回false
            return false;
        }
        // 检查文件是否存在
        if (!file.exists()) {
            return false;
        }
        // 检查给定的MD5字符串是否为空
        if (TextUtils.isEmpty(hash)) {
            return false;
        }
        // 计算文件的MD5值
        String fileMd5 = EncryptUtils.encryptMD5File2String(file);
        // 将给定的MD5字符串转换为大写，以做比较
        String hashMd5 = hash.toUpperCase();
        // 比较文件的MD5值和给定的MD5字符串是否匹配
        return fileMd5.equalsIgnoreCase(hashMd5);
    }

    /**
     * 使用公钥进行解密操作。
     *
     * @param data 需要解密的数据，使用Base64编码。
     * @param publicKey 公钥，使用Base64编码。
     * @return 解密后的字符串。如果在解密过程中遇到任何异常，则返回null。
     */
    private static String decryptByPublicKey(String data, String publicKey) {
        try {
            // 对传入的数据和公钥进行Base64解码
            byte[] dataByte = Base64.decode(data, Base64.NO_WRAP | Base64.NO_PADDING);
            byte[] publicKeyByte = Base64.decode(publicKey, Base64.NO_WRAP | Base64.NO_PADDING);

            // 从公钥字节数据创建X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByte);

            // 实例化RSA公钥工厂
            KeyFactory kf = KeyFactory.getInstance("RSA");

            // 通过公钥规格生成公钥对象
            PublicKey keyPublic = kf.generatePublic(keySpec);

            // 实例化加密器，并设置为解密模式
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPublic);

            // 执行解密操作
            byte[] result = cipher.doFinal(dataByte);

            // 将解密后的字节数据转换为字符串返回
            return new String(result, Charset.defaultCharset());
        } catch (NoSuchAlgorithmException e) {
            // 捕获算法找不到异常
            CommExtKt.logStackTrace(e);
        } catch (InvalidKeyException e) {
            // 捕获无效密钥异常
            CommExtKt.logStackTrace(e);
        } catch (NoSuchPaddingException e) {
            // 捕获找不到填充方式异常
            CommExtKt.logStackTrace(e);
        } catch (BadPaddingException e) {
            // 捕获数据填充异常
            CommExtKt.logStackTrace(e);
        } catch (InvalidKeySpecException e) {
            // 捕获密钥规格异常
            CommExtKt.logStackTrace(e);
        } catch (IllegalBlockSizeException e) {
            // 捕获非法块大小异常
            CommExtKt.logStackTrace(e);
        } catch (Throwable e) {
            // 捕获其他所有异常
            CommExtKt.logStackTrace(e);
        }
        // 如果发生异常，则返回null
        return null;
    }

}
