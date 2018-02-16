package com.codekidlabs.storagechooser.models;

/**
 * A model that is used as the element to store any data regarding storage volumes/disks
 */
public class Storages {

    String storageTitle;
    String storagePath;
    String memoryTotalSize;
    String memoryAvailableSize;


    public String getStorageTitle() {
        return storageTitle;
    }

    public void setStorageTitle(String storageTitle) {
        this.storageTitle = storageTitle;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getMemoryTotalSize() {
        return memoryTotalSize;
    }

    public void setMemoryTotalSize(String memoryTotalSize) {
        this.memoryTotalSize = memoryTotalSize;
    }

    public String getMemoryAvailableSize() {
        return memoryAvailableSize;
    }

    public void setMemoryAvailableSize(String memoryAvailableSize) {
        this.memoryAvailableSize = memoryAvailableSize;
    }
}
