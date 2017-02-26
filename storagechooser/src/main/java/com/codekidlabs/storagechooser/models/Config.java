package com.codekidlabs.storagechooser.models;

import android.content.SharedPreferences;
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
    private String dialogTitle;
    private String internalStorageText;
    private boolean allowCustomPath;
    private boolean allowAddFolder;
    private boolean showHidden;
    private boolean skipOverview;
    private String primaryPath;

    private String secondaryAction;

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

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getInternalStorageText() {
        return internalStorageText;
    }

    public void setInternalStorageText(String internalStorageText) {
        this.internalStorageText = internalStorageText;
    }

    public boolean isAllowCustomPath() {
        return allowCustomPath;
    }

    public void setAllowCustomPath(boolean allowCustomPath) {
        this.allowCustomPath = allowCustomPath;
    }

    public boolean isAllowAddFolder() {
        return allowAddFolder;
    }

    public void setAllowAddFolder(boolean allowAddFolder) {
        this.allowAddFolder = allowAddFolder;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }


    public String getSecondaryAction() {
        return secondaryAction;
    }

    public void setSecondaryAction(String secondaryAction) {
        this.secondaryAction = secondaryAction;
    }


    public String getPrimaryPath() {
        return primaryPath;
    }

    public void setPrimaryPath(String primaryPath) {
        this.primaryPath = primaryPath;
    }

    public boolean isSkipOverview() {
        return skipOverview;
    }

    public void setSkipOverview(boolean skipOverview) {
        this.skipOverview = skipOverview;
    }
}
