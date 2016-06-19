package com.kevinye.progressdownloader.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件操作相关的工具类
 * Created by Kevin on 2016/6/18.
 */
public class FileUtil {

    public static void save(InputStream inputStream, File file) {
        FileOutputStream fos = null;
        if (inputStream != null) {
            try {
                fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int slice;
                while ((slice = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, slice);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
