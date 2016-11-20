package com.codekidlabs.storagechooser;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.codekidlabs.storagechooser.fragments.PathFinderDialogFragment;

public class ExternalStoragePathFinder {

    public static Dialog dialog;

    public static String STORAGE_EXTERNAL_PATH;
    public static String EXTERNAL_STORAGE_PATH_KEY = "external_storage_dir";

    private Activity chooserActivity;
    private FragmentManager fragmentManager;
    private static SharedPreferences userSharedPreference;

    private ExternalStoragePathFinder(Activity activity,
                                  FragmentManager mFragmentManager,
                                  SharedPreferences sharedPreferences) {

        setChooserActivity(activity);
        setFragmentManager(mFragmentManager);
        setUserSharedPreference(sharedPreferences);

    }

    private static Dialog getStorageChooserDialog(Activity activity) {
        return new Dialog(activity);
    }

    private Activity getChooserActivity() {
        return chooserActivity;
    }

    private void setChooserActivity(Activity chooserActivity) {
        this.chooserActivity = chooserActivity;
    }

    private FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    private void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static SharedPreferences getUserSharedPreference() {
        return userSharedPreference;
    }

    public static void setUserSharedPreference(SharedPreferences userSharedPreference) {
        ExternalStoragePathFinder.userSharedPreference = userSharedPreference;
    }

    private void init() {
        dialog = getStorageChooserDialog(getChooserActivity());
        PathFinderDialogFragment pathFinderDialogFragment = new PathFinderDialogFragment();
        pathFinderDialogFragment.show(getFragmentManager(),"storagechooser_dialog");
    }

    public static class Builder {
        private Activity mActivity;
        private FragmentManager mFragmentManager;
        private SharedPreferences mSharedPreferences;

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

        public Builder actionSave(SharedPreferences sharedPreferences) {
            mSharedPreferences = sharedPreferences;
            return this;
        }


        public Builder build() {
            return this;
        }

        public void show() {
            new ExternalStoragePathFinder(mActivity, mFragmentManager, mSharedPreferences).init();
        }
    }
}
