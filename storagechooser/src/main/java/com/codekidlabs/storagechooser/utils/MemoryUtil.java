package com.codekidlabs.storagechooser.utils;


import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryUtil {

    public static final String SELF_DIR_NAME = "self";
    public static final String EMULATED_DIR_NAME = "emulated";
    public static final String EMULATED_DIR_KNOX = "knox-emulated";
    public static final String SDCARD0_DIR_NAME = "sdcard0";
    public static final String CONTAINER = "container";
    private static final String ERROR = "error";

    public boolean isExternalStoragePresent() {
        return getStorageListSize() != 1;
    }

    /**
     * Returns an the number of the files inside '/storage' directory
     *
     * @return int - number of storages present except the redundant once
     */
    public int getStorageListSize() {
        File storageDir = new File("/storage");
        List<File> volumeList = new ArrayList<>();
        Collections.addAll(volumeList, storageDir.listFiles());
        // segregate the list
        for (int i = 0; i < volumeList.size(); i++) {
            String storageName = volumeList.get(i).getName();
            if (storageName.equals(SELF_DIR_NAME) ||
                    storageName.equals(EMULATED_DIR_KNOX) ||
                    storageName.equals(EMULATED_DIR_KNOX) ||
                    storageName.equals(SDCARD0_DIR_NAME) ||
                    storageName.equals(CONTAINER)) {
                volumeList.remove(i);
            }
        }

        return volumeList.size();
    }


    /**
     * calculate available/free size of any directory
     *
     * @param path path of the storage
     * @return size in bytes
     */
    public long getAvailableMemorySize(String path) {
        File file = new File(path);
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * calculate total size of any directory
     *
     * @param path path of the storage
     * @return size in bytes
     */
    public long getTotalMemorySize(String path) {
        File file = new File(path);
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * mainly to format the available bytes into user readable string
     *
     * @param size long - value gained from the getTotalMemorySize() and getAvailableMemorySize()
     *             using StatFs
     * @return a formatted string with KiB, MiB, GiB suffix
     */
    public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GiB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public long suffixedSize(long size, String suffix) {

        switch (suffix) {
            case "KiB":
                return size / 1024;
            case "MiB":
                return (long) (size / Math.pow(1024, 2));
            case "GiB":
                return (long) (size / Math.pow(1024, 3));
            default:
                return 0;
        }
    }
}
