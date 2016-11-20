package com.codekidlabs.storagechooser;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;

import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;


public class StorageChooserBuilder {

    public static Dialog dialog;

    private Activity chooserActivity;
    private static boolean showMemoryBar;
    private FragmentManager fragmentManager;
    private static String preDefinedPath;

    private StorageChooserBuilder(Activity activity, FragmentManager mFragmentManager, boolean mShowMemoryBar, int mMemoryTextColor, String mPath) {
        setChooserActivity(activity);
        setShowMemoryBar(mShowMemoryBar);
        setFragmentManager(mFragmentManager);
        setPreDefinedPath(mPath);
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


    public static class Builder {

        private Activity mActivity;
        private FragmentManager mFragmentManager;
        private boolean mShowMemoryBar;
        private int mMemoryTextColor = 0;
        private String mPath;

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


        public Builder build() {
            return this;
        }

        public void show() {
            new StorageChooserBuilder(mActivity, mFragmentManager, mShowMemoryBar, mMemoryTextColor, mPath).init();
        }
    }


}
