package com.kevinye.progressdownloader.sample.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 安装APK的工具类
 * Created by Kevin on 2016/6/18.
 */
public class ApkInstaller {

    public static void installApk(Context context, File apk) {
        if (apk == null || !apk.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apk.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
