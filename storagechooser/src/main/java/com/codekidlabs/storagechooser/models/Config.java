package com.codekidlabs.storagechooser.models;

import android.content.SharedPreferences;
import android.widget.ListView;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * Model to save configs passed to the Builder without passing too many things to the constructor
 * of its super class.
 */
public class Config {

    private android.app.FragmentManager fragmentManager;
    private String predefinedPath;
    private boolean showMemoryBar;
    private boolean hideFreeSpaceLabel;
    private float memorybarHeight;
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
    private boolean applyThreshold;
    private String primaryPath;
    private boolean isGridView;
    private boolean resumeSession;
    //typeface config
    private String headingFont;
    private String listFont;
    private boolean headingFromAssets;
    private boolean listFromAssets;

    //multi
    private boolean multiSelect = true;

    private String secondaryAction;

    private Content content;
    private int[] scheme;
    private StorageChooser.FileType singleFilter;
    private List<String> customEnum;
    private boolean customFilter;


    public android.app.FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(android.app.FragmentManager fragmentManager) {
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

    public boolean isHideFreeSpaceLabel() {
        return hideFreeSpaceLabel;
    }

    public void setHideFreeSpaceLabel(boolean hideFreeSpaceLabel) {
        this.hideFreeSpaceLabel = hideFreeSpaceLabel;
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

    public boolean isApplyThreshold() {
        return applyThreshold;
    }

    public void setApplyThreshold(boolean applyThreshold) {
        this.applyThreshold = applyThreshold;
    }


    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }


    public StorageChooser.FileType getSingleFilter() {
        return singleFilter;
    }

    public void setSingleFilter(StorageChooser.FileType singleFilter) {
        this.singleFilter = singleFilter;
    }

    public List<String> getCustomEnum() {
        return customEnum;
    }

    public void setCustomEnum(List<String> customEnum) {
        this.customEnum = customEnum;
    }

    public boolean isCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(boolean customFilter) {
        this.customFilter = customFilter;
    }

    public boolean isGridView() {
        return isGridView;
    }

    public void setGridView(boolean gridView) {
        isGridView = gridView;
    }

    public boolean isResumeSession() {
        return resumeSession;
    }

    public void setResumeSession(boolean resumeSession) {
        this.resumeSession = resumeSession;
    }

    public int[] getScheme() {
        return scheme;
    }

    public void setScheme(int[] scheme) {
        this.scheme = scheme;
    }

    public float getMemorybarHeight() {
        return memorybarHeight;
    }

    public void setMemorybarHeight(float memorybarHeight) {
        this.memorybarHeight = memorybarHeight;
    }

    public String getHeadingFont() {
        return headingFont;
    }

    public void setHeadingFont(String headingFont) {
        this.headingFont = headingFont;
    }

    public String getListFont() {
        return listFont;
    }

    public void setListFont(String listFont) {
        this.listFont = listFont;
    }

    public boolean isHeadingFromAssets() {
        return headingFromAssets;
    }

    public void setHeadingFromAssets(boolean headingFromAssets) {
        this.headingFromAssets = headingFromAssets;
    }

    public boolean isListFromAssets() {
        return listFromAssets;
    }

    public void setListFromAssets(boolean listFromAssets) {
        this.listFromAssets = listFromAssets;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
}
