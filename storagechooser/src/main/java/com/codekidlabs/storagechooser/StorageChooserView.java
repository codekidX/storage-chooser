package com.codekidlabs.storagechooser;

/**
 * StorageChooserView class handles changes to views
 */

public class StorageChooserView {

    public static String LABEL_SELECT = "Select";
    public static String LABEL_CREATE = "Create";
    public static String LABEL_NEW_FOLDER = "New Folder";
    public static String LABEL_CANCEL = "Cancel";
    public static String CHOOSER_HEADING;
    public static String INTERNAL_STORAGE_TEXT;
    public static String TOAST_FOLDER_CREATED = "Folder Created";
    public static String TOAST_FOLDER_ERROR = "Error occured while creating folder. Try again.";
    public static String TEXTFIELD_HINT = "Folder Name";
    public static String TEXTFIELD_ERROR = "Empty Folder Name";

    // LAYOUTS
    public static int VIEW_SC = R.layout.custom_storage_list;

    // layout choices
    public static int SC_LAYOUT_SLEEK = R.layout.custom_storage_list;

    // COLORS
    public static int SC_SECONDARY_ACTION_COLOR = android.R.color.black;
    public static int SC_TEXTFIELD_HINT_COLOR = R.color.chevronBgColor;

    // NIGHT COLORS
    public static int[] nightColors;

    public static int[] getNightColors() {
        return nightColors;
    }

    public static void setNightColors(int[] nightColors) {
        StorageChooserView.nightColors = nightColors;
    }

    public static void setLabelSelect(String labelSelect) {
        LABEL_SELECT = labelSelect;
    }

    public static void setLabelCreate(String labelCreate) {
        LABEL_CREATE = labelCreate;
    }

    public static void setLabelNewFolder(String labelNewFolder) {
        LABEL_NEW_FOLDER = labelNewFolder;
    }

    public static void setLabelCancel(String labelCancel) {
        LABEL_CANCEL = labelCancel;
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

    public static void setScTextfieldHintColor(int scTextfieldHintColor) {
        SC_TEXTFIELD_HINT_COLOR = scTextfieldHintColor;
    }

    public static void setTextfieldError(String textfieldError) {
        TEXTFIELD_ERROR = textfieldError;
    }

    public static void setScSecondaryActionColor(int scSecondaryActionColor) {
        SC_SECONDARY_ACTION_COLOR = scSecondaryActionColor;
    }
}
