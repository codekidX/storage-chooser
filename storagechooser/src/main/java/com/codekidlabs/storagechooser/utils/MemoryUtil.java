package com.codekidlabs.storagechooser.utils;


import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryUtil {

    private static final String ERROR = "error";
    public static final String SELF_DIR_NAME = "self";
    public static final String EMULATED_DIR_NAME = "emulated";
    public static final String SDCARD0_DIR_NAME = "sdcard0";

    public static boolean isExternalStoragePresent() {
        return getStorageListSize() != 0;
    }

    /**
     * Returns an the number of the files inside '/storage' directory
     * @return
     */
    public static int getStorageListSize() {
        File storageDir = new File("/storage");
        List<File> volumeList = new ArrayList<File>();
        Collections.addAll(volumeList, storageDir.listFiles());
        // segregate the list
        for(int i=0;i < volumeList.size(); i++) {
            if(volumeList.get(i).getName().equals(SELF_DIR_NAME)) {
                volumeList.remove(i);
            }
            if(volumeList.get(i).getName().equals(EMULATED_DIR_NAME)) {
                volumeList.remove(i);
            }
            if(volumeList.get(i).getName().equals(SDCARD0_DIR_NAME)) {
                volumeList.remove(i);
            }
        }

        return volumeList.size();
    }


    /**
     * calculate available/free size of any directory
     * @param file File to use it with StatFs
     * @return string formatted using formatSize()
     */
    public static String getAvailableMemorySize(File file) {
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
    }

    public static long getAvailableMemorySize(String path) {
        File file = new File(path);
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * calculate total size of any directory
     * @param file File to use it with StatFs
     * @return
     */
    public static String getTotalMemorySize(File file) {
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
    }

    public static long getTotalMemorySize(String path) {
        File file = new File(path);
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * mainly to format the available bytes into user readable string
     * @param size long - value gained from the getTotalMemorySize() and getAvailableMemorySize()
     *             using StatFs
     * @return a formatted string with KiB, MiB, GiB suffix
     */
    public static String formatSize(long size) {
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

    public static long suffixedSize(long size, String suffix) {

        switch (suffix) {
            case "KiB":
                return (long) (size/Math.pow(1024, 3));
            case "MiB":
                return (long) (size/Math.pow(1024, 2));
            case "GiB":
                return  size/104;
            default:
                return 0;
        }
    }
}
