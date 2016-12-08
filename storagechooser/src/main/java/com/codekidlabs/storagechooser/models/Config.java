package com.codekidlabs.storagechooser.models;

import android.app.Activity;
import android.preference.Preference;
import android.support.v4.app.FragmentManager;

/**
 * Model to save configs passed to the Builder without passing too many things to the constructor
 * of its super class.
 */
public class Config {

    private Activity activity;
    private FragmentManager fragmentManager;
    private String predefinedPath;
    private boolean actionSave;
    private Preference preference;
    private int memoryThreshold;
    private String thresholdSuffix;


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public String getPredefinedPath() {
        return predefinedPath;
    }

    public void setPredefinedPath(String predefinedPath) {
        this.predefinedPath = predefinedPath;
    }

    public boolean isActionSave() {
        return actionSave;
    }

    public void setActionSave(boolean actionSave) {
        this.actionSave = actionSave;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public int getMemoryThreshold() {
        return memoryThreshold;
    }

    public void setMemoryThreshold(int memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

    public String getThresholdSuffix() {
        return thresholdSuffix;
    }

    public void setThresholdSuffix(String thresholdSuffix) {
        this.thresholdSuffix = thresholdSuffix;
    }
}
