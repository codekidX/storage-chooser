package com.codekidlabs.storagechooser;

/**
 * StorageChooserView class handles changes to views
 */

public class StorageChooserView {

    public static String LABEL_SELECT;
    public static String CHOOSER_HEADING;
    public static String INTERNAL_STORAGE_TEXT;

    public static void setLabelSelect(String labelSelect) {
        LABEL_SELECT = labelSelect;
    }

    public static void setChooserHeading(String chooserHeading) {
        CHOOSER_HEADING = chooserHeading;
    }

    public static void setInternalStorageText(String internalStorageText) {
        INTERNAL_STORAGE_TEXT = internalStorageText;
    }
}
