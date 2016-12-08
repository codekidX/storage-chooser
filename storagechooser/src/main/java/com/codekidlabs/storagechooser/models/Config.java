package com.codekidlabs.storagechooser.models;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v4.app.FragmentManager;

/**
 * Model to save configs passed to the Builder without passing too many things to the constructor
 * of its super class.
 */
public class Config {

    private FragmentManager fragmentManager;
    private String predefinedPath;
    private boolean showMemoryBar;
    private boolean actionSave;
    private SharedPreferences preference;
    private int memoryThreshold;
    private String thresholdSuffix;

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

    public boolean isShowMemoryBar() {
        return showMemoryBar;
    }

    public void setShowMemoryBar(boolean showMemoryBar) {
        this.showMemoryBar = showMemoryBar;
    }

    public boolean isActionSave() {
        return actionSave;
    }

    public void setActionSave(boolean actionSave) {
        this.actionSave = actionSave;
    }

    public SharedPreferences getPreference() {
        return preference;
    }

    public void setPreference(SharedPreferences preference) {
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
