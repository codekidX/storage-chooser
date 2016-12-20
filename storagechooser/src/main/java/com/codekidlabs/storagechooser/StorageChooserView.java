package com.codekidlabs.storagechooser;

/**
 * StorageChooserView class handles changes to views
 */

public class StorageChooserView {

    public static String LABEL_SELECT;
    public static String CHOOSER_HEADING;
    public static String INTERNAL_STORAGE_TEXT;
    public static String TOAST_FOLDER_CREATED = "Folder Created";
    public static String TOAST_FOLDER_ERROR = "Error occured while creating folder. Try again.";
    public static String TEXTFIELD_HINT = "Folder Name";
    public static String TEXTFIELD_ERROR = "Empty Folder Name";

    public static void setLabelSelect(String labelSelect) {
        LABEL_SELECT = labelSelect;
    }

    public static void setChooserHeading(String chooserHeading) {
        CHOOSER_HEADING = chooserHeading;
    }

    public static void setInternalStorageText(String internalStorageText) {
        INTERNAL_STORAGE_TEXT = internalStorageText;
    }

    public static void setToastFolderCreated(String toastFolderCreated) {
        TOAST_FOLDER_CREATED = toastFolderCreated;
    }

    public static void setToastFolderError(String toastFolderError) {
        TOAST_FOLDER_ERROR = toastFolderError;
    }

    public static void setTextfieldHint(String textfieldHint) {
        TEXTFIELD_HINT = textfieldHint;
    }

    public static void setTextfieldError(String textfieldError) {
        TEXTFIELD_ERROR = textfieldError;
    }
}
