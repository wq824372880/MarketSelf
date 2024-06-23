package com.zeekrlife.market.task.utils;

import com.zeekrlife.common.ext.CommExtKt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    /**
     * 解压缩ZIP文件到指定目录。
     *
     * @param zipFile 需要解压缩的ZIP文件路径。
     * @param targetDir 解压缩的目标目录。
     * @return 如果解压缩成功返回true，否则返回false。
     */
    public static boolean unZip(String zipFile, String targetDir) {
        int BUFFER = 4096; // 使用4KB作为缓冲区大小
        String strEntry;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream(zipFile); // 创建文件输入流
            zis = new ZipInputStream(new BufferedInputStream(fis)); // 创建Zip输入流
            ZipEntry entry;

            // 遍历ZIP文件中的所有条目
            while ((entry = zis.getNextEntry()) != null) {
                BufferedOutputStream dest = null;
                FileOutputStream fos = null;
                try {
                    int count;
                    byte[] data = new byte[BUFFER]; // 创建缓冲区
                    strEntry = entry.getName(); // 获取当前条目名

                    File entryFile = new File(targetDir + strEntry); // 根据条目名创建文件对象
                    // 如果entryFile没有父目录，则返回false
                    if (entryFile.getParent() == null) {
                        return false;
                    }
                    File entryDir = new File(entryFile.getParent()); // 创建条目所在的目录对象
                    // 如果目录不存在且创建失败，则返回false
                    if (!entryDir.exists()) {
                        if (!entryDir.mkdirs()) {
                            return false;
                        }
                    }

                    fos = new FileOutputStream(entryFile); // 创建文件输出流
                    dest = new BufferedOutputStream(fos, BUFFER); // 创建缓冲区输出流
                    // 读取并写入数据到当前条目
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                } catch (Exception ex) {
                    CommExtKt.logStackTrace(ex); // 捕获并记录异常
                } finally {
                    // 关闭输出流
                    if (dest != null) {
                        dest.flush();
                        dest.close();
                    }
                    // 关闭文件输出流
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }
                }
            }

            return true; // 解压缩成功
        } catch (Exception cwj) {
            CommExtKt.logStackTrace(cwj); // 记录异常
            return false;
        } finally {
            // 关闭输入流
            try {
                if (zis != null) {
                    zis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                CommExtKt.logStackTrace(e); // 记录异常
            }
        }
    }
}
