package com.codekidlabs.storagechooser;

/**
 * Content class handles changes to dialog's view and whatever is in it
 */

public class Content {

    private String selectLabel = "Select";
    private String createLabel = "Create";
    private String newFolderLabel = "New Folder";
    private String cancelLabel = "Cancel";
    private String overviewHeading = "Choose Storage";
    private String internalStorageText = "Internal Storage";
    private String freeSpaceText = "%s free";
    private String folderCreatedToastText = "Folder Created";
    private String folderErrorToastText = "Error occured while creating folder. Try again.";
    private String textfieldHintText = "Folder Name";
    private String textfieldErrorText = "Empty Folder Name";


    public String getSelectLabel() {
        return selectLabel;
    }

    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }

    public String getCreateLabel() {
        return createLabel;
    }

    public void setCreateLabel(String createLabel) {
        this.createLabel = createLabel;
    }

    public String getNewFolderLabel() {
        return newFolderLabel;
    }

    public void setNewFolderLabel(String newFolderLabel) {
        this.newFolderLabel = newFolderLabel;
    }

    public String getCancelLabel() {
        return cancelLabel;
    }

    public void setCancelLabel(String cancelLabel) {
        this.cancelLabel = cancelLabel;
    }

    public String getOverviewHeading() {
        return overviewHeading;
    }

    public void setOverviewHeading(String overviewHeading) {
        this.overviewHeading = overviewHeading;
    }

    public String getInternalStorageText() {
        return internalStorageText;
    }

    public void setInternalStorageText(String internalStorageText) {
        this.internalStorageText = internalStorageText;
    }

    public String getFreeSpaceText() {
        return freeSpaceText;
    }

    public void setFreeSpaceText(String freeSpaceText) {
        this.freeSpaceText = freeSpaceText;
    }

    public String getFolderCreatedToastText() {
        return folderCreatedToastText;
    }

    public void setFolderCreatedToastText(String folderCreatedToastText) {
        this.folderCreatedToastText = folderCreatedToastText;
    }

    public String getFolderErrorToastText() {
        return folderErrorToastText;
    }

    public void setFolderErrorToastText(String folderErrorToastText) {
        this.folderErrorToastText = folderErrorToastText;
    }

    public String getTextfieldHintText() {
        return textfieldHintText;
    }

    public void setTextfieldHintText(String textfieldHintText) {
        this.textfieldHintText = textfieldHintText;
    }

    public String getTextfieldErrorText() {
        return textfieldErrorText;
    }

    public void setTextfieldErrorText(String textfieldErrorText) {
        this.textfieldErrorText = textfieldErrorText;
    }
}
