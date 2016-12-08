package com.codekidlabs.storagechooser;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;
import com.codekidlabs.storagechooser.utils.MemoryUtil;


public class StorageChooserBuilder {

    public static Dialog dialog;
    public static String STORAGE_STATIC_PATH;

    private Activity chooserActivity;
    private static boolean showMemoryBar;
    private FragmentManager fragmentManager;
    private static String preDefinedPath;
    private static SharedPreferences userSharedPreference;
    private static String userSharedPreferenceKey;

    private StorageChooserBuilder(Activity activity,
                                  FragmentManager mFragmentManager,
                                  boolean mShowMemoryBar,
                                  SharedPreferences sharedPreferences,
                                  String key,
                                  String mPath) {

        setChooserActivity(activity);
        setShowMemoryBar(mShowMemoryBar);
        setFragmentManager(mFragmentManager);
        setPreDefinedPath(mPath);
        setUserSharedPreference(sharedPreferences);
        setUserSharedPreferenceKey(key);
    }

    private void init() {
        dialog = getStorageChooserDialog(getChooserActivity());
        ChooserDialogFragment chooserDialogFragment = new ChooserDialogFragment();
        chooserDialogFragment.show(getFragmentManager(),"storagechooser_dialog");
    }

    private Activity getChooserActivity() {
        return chooserActivity;
    }

    private void setChooserActivity(Activity chooserActivity) {
        this.chooserActivity = chooserActivity;
    }

    public static boolean isShowMemoryBar() {
        return showMemoryBar;
    }

    private void setShowMemoryBar(boolean showMemoryBar) {
        StorageChooserBuilder.showMemoryBar = showMemoryBar;
    }

    private FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    private void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static String getPreDefinedPath() {
        return preDefinedPath;
    }

    public static void setPreDefinedPath(String preDefinedPath) {
        StorageChooserBuilder.preDefinedPath = preDefinedPath;
    }

    private static Dialog getStorageChooserDialog(Activity activity) {
        return new Dialog(activity);
    }

    public static SharedPreferences getUserSharedPreference() {
        return userSharedPreference;
    }

    public static void setUserSharedPreference(SharedPreferences userSharedPreference) {
        StorageChooserBuilder.userSharedPreference = userSharedPreference;
    }

    public static String getUserSharedPreferenceKey() {
        return userSharedPreferenceKey;
    }

    public static void setUserSharedPreferenceKey(String userSharedPreferenceKey) {
        StorageChooserBuilder.userSharedPreferenceKey = userSharedPreferenceKey;
    }

    public static class Builder {

        private Activity mActivity;
        private FragmentManager mFragmentManager;
        private boolean mShowMemoryBar;
        private String mPath;
        private SharedPreferences mSharedPreferences;
        private String mSharedPreferencesKey;

        public Builder() {
        }

        public Builder withActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder withFragmentManager(FragmentManager fragmentManager) {
            mFragmentManager = fragmentManager;
            return this;
        }

        public Builder withMemoryBar(boolean memoryBarBoolean) {
            mShowMemoryBar = memoryBarBoolean;
            return this;
        }

        public Builder withPredefinedPath(String path) {
            mPath = path;
            return this;
        }

        public Builder actionSave(SharedPreferences sharedPreferences, String spKey) {
            mSharedPreferences = sharedPreferences;
            mSharedPreferencesKey = spKey;
            return this;
        }


        public Builder build() {
            return this;
        }

        public void show() {
            new StorageChooserBuilder(mActivity, mFragmentManager, mShowMemoryBar, mSharedPreferences, mSharedPreferencesKey, mPath).init();
        }
    }
}
