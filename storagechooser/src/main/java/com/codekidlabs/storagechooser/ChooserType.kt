package com.codekidlabs.storagechooser

/**
 * Types of storage-chooser:
 *
 * 1. FILE - chooser that let's user choose single of multiple files
 * 2. DIRECTORY - chooser that let's user choose only directories
 * 3. BASIC - chooser that let's user choose internal or external storage root path
 */
enum class ChooserType {
//    GALLERY,
    FILE,
    DIRECTORY,
    BASIC
}