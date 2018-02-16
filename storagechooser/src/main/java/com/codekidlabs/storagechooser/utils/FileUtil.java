package com.codekidlabs.storagechooser.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * manages each and everything related to file api
 */

public class FileUtil {

    public static boolean createDirectory(String name, String path) {
        File dir = new File(path + "/" + name);
        return dir.mkdirs();
    }

    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }

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

    /**
     * Removes non-operationaal directories which are either used by only the system or
     * not used by anyone
     */
    public void removeNonOperational(List<File> volumeList) {
        // segregate the list

        for (int i = 0; i < volumeList.size(); i++) {
            String volumeName = volumeList.get(i).getName();
            if (volumeName.equals(MemoryUtil.SELF_DIR_NAME)) {
                volumeList.remove(i);
            }
            if (volumeName.equals(MemoryUtil.EMULATED_DIR_NAME)) {
                volumeList.remove(i);
            }
            if (volumeName.equals(MemoryUtil.EMULATED_DIR_KNOX)) {
                volumeList.remove(i);
            }
            if (volumeName.equals(MemoryUtil.SDCARD0_DIR_NAME)) {
                volumeList.remove(i);
            }
        }
    }

    public String[] fileListToStringArray(List<String> dirNames) {
        String[] dirList = new String[dirNames.size()];
        for (int i = 0; i < dirNames.size(); i++) {
            dirList[i] = dirNames.get(i);
        }
        return dirList;
    }
}
