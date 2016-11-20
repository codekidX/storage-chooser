package com.codekidlabs.storagechooser.utils;


import android.content.SharedPreferences;
import android.os.Build;

import com.codekidlabs.storagechooser.StorageChooserBuilder;

public class DiskUtil {

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void saveChooserPathPreference(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, StorageChooserBuilder.STORAGE_STATIC_PATH);
        editor.apply();
    }
}
