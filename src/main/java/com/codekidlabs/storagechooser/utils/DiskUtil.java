package com.codekidlabs.storagechooser.utils;


import android.os.Build;

public class DiskUtil {

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}
