package com.codekidlabs.storagechooser.filters;

import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.util.ArrayList;

public class SpinFilter {

    String rootFolder;
    StorageChooser.FileType singleFilter;

    ArrayList<StorageChooser.FileType> multipleFilter;

    public SpinFilter(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public SpinFilter(String rootFolder, StorageChooser.FileType singleFilter) {
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


    /**
     *  converts FileType enum to understandable extension string
     * @param fileType
     * @return fileType in String
     */
    private String enumToExtension(StorageChooser.FileType fileType) {
        switch (fileType){
            case MP3:
                return "mp3";
            case MP4:
                return "mp4";
            case TTF:
                return "ttf";
            default:
                return "";

        }
    }
}
