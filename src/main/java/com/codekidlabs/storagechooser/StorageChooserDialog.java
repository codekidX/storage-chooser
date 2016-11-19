package com.codekidlabs.storagechooser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;

import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;


public class StorageChooserDialog {

    public static Dialog dialog;

    Activity chooserActivity;
    static boolean showMemoryBar;
    FragmentManager fragmentManager;

    public StorageChooserDialog(Activity activity, FragmentManager mFragmentManager, boolean mShowMemoryBar, int mMemoryTextColor, String mPath) {
        setChooserActivity(activity);
        setShowMemoryBar(mShowMemoryBar);
        setFragmentManager(mFragmentManager);
    }

    private void init() {
        dialog = getStorageChooserDialog(getChooserActivity(), isShowMemoryBar());
        ChooserDialogFragment chooserDialogFragment = new ChooserDialogFragment();
        chooserDialogFragment.show(getFragmentManager(),"storagechooser_dialog");
    }

    public Activity getChooserActivity() {
        return chooserActivity;
    }

    public void setChooserActivity(Activity chooserActivity) {
        this.chooserActivity = chooserActivity;
    }

    public static boolean isShowMemoryBar() {
        return showMemoryBar;
    }

    public void setShowMemoryBar(boolean showMemoryBar) {
        this.showMemoryBar = showMemoryBar;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private static Dialog getStorageChooserDialog(Activity activity, boolean shouldShowMemoryBar) {
        Context context = activity.getApplicationContext();
        Dialog dialog = new Dialog(activity);
        return dialog;
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

        public Builder withPrimaryColor(int color) {
            if(color == 0) {
                mMemoryTextColor = Color.BLACK;
            } else {
                mMemoryTextColor = color;
            }
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
            new StorageChooserDialog(mActivity, mFragmentManager, mShowMemoryBar, mMemoryTextColor, mPath).init();
        }
    }


}
