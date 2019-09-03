package com.codekidlabs.storagechooser.utils;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.codekidlabs.storagechooser.ChooserType;
import com.codekidlabs.storagechooser.Config;
import com.codekidlabs.storagechooser.StorageChooser2;
import com.codekidlabs.storagechooser.fragments.SecondaryChooserFragment;

import androidx.fragment.app.FragmentManager;

public class DiskUtil {

    public static final String IN_KB = "KiB";
    public static final String IN_MB = "MiB";
    public static final String IN_GB = "GiB";
    public static final String SC_PREFERENCE_KEY = "storage_chooser_path";
    public static java.lang.String SC_CHOOSER_FLAG = "storage_chooser_type";

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void saveChooserPathPreference(SharedPreferences sharedPreferences, String path) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SC_PREFERENCE_KEY, path);
            editor.apply();
        } catch (NullPointerException e) {
            Log.e("StorageChooser", "No sharedPreference was supplied. Supply sharedPreferencesObject via withPreference() or disable saving with actionSave(false)");
        }
    }

    public static boolean isLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * secondary choosers are dialogs apart from overview (CustomChooserFragment and FilePickerFragment)
     * Configs :-
     * setType()
     * allowCustomPath()
     *
     * @param dirPath root path(starting-point) for the secondary choosers
     * @param config  configuration from developer
     */

    public static void showSecondaryChooser(String dirPath, Config config, FragmentManager fragmentManager) {

        Bundle bundle = new Bundle();
        bundle.putString(DiskUtil.SC_PREFERENCE_KEY, dirPath);
        bundle.putParcelable("config", config);

        switch (config.getType()) {
            case BASIC:
                break;
            case DIRECTORY:
                bundle.putBoolean(DiskUtil.SC_CHOOSER_FLAG, false);
                SecondaryChooserFragment c = new SecondaryChooserFragment();
                c.setArguments(bundle);
                c.show(fragmentManager, "custom_chooser");
                break;
            case FILE:
                bundle.putBoolean(DiskUtil.SC_CHOOSER_FLAG, true);
                SecondaryChooserFragment f = new SecondaryChooserFragment();
                f.setArguments(bundle);
                f.show(fragmentManager, "file_picker");
                break;
        }
    }
}
