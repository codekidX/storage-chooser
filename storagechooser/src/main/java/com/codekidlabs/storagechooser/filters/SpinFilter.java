package com.codekidlabs.storagechooser.filters;

import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.util.ArrayList;

public class SpinFilter {

    String rootFolder;
    String singleFilter;

    ArrayList<StorageChooser.FileType> multipleFilter;

    public SpinFilter(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public SpinFilter(String rootFolder, String singleFilter) {
        this.rootFolder = rootFolder;
        this.singleFilter = singleFilter;
    }

    public SpinFilter(String rootFolder, ArrayList<StorageChooser.FileType> multipleFilter) {
        this.rootFolder = rootFolder;
        this.multipleFilter = multipleFilter;
    }

    public File[] filter() {
        return null;
    }
}
