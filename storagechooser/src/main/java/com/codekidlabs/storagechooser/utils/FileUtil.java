package com.codekidlabs.storagechooser.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * manages each and everything related to file api
 */

public class FileUtil {

    public File[] listFilesAsDir(String dirPath) {
        return new File(dirPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
    }

    public File[] listFilesInDir(String dirPath) {
        return new File(dirPath).listFiles();
    }

    public String[] arrangeAscending(String[] dirNames) {
        Arrays.sort(dirNames);
        return dirNames;
    }

    public String[] fileListToStringArray(List<String> dirNames) {
        String[] dirList = new String[dirNames.size()];
        for(int i=0; i< dirNames.size(); i++) {
            dirList[i] = dirNames.get(i);
        }
        return dirList;
    }

    public static boolean createDirectory(String name, String path) {
        File dir = new File(path + "/" + name);
        return dir.mkdirs();
    }

    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }
}
