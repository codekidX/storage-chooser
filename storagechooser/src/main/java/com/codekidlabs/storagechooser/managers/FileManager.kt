package com.codekidlabs.storagechooser.managers

import com.codekidlabs.storagechooser.ChooserActivity
import com.codekidlabs.storagechooser.utils.FileUtil
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Copyright 2017 codekid for storage-chooser. Do not use any
 * of the code befor asking my permission.
 */
internal class FileManager(private val chooser: ChooserActivity) : CoroutineScope {

    var job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private var masterPath: String = "/storage/"
    var onlyDirectories = false

    private suspend fun loadFiles(): MutableList<File> {
        return async(context = coroutineContext) {
            if (onlyDirectories) {
                FileUtil().listFilesAsDir(masterPath).toMutableList<File>()
            } else {
                FileUtil().listFilesInDir(masterPath).toMutableList()
            }
        }.await()
    }

    private fun getSlashIndex() : Int {
        return masterPath.lastIndexOf("/")
    }

    fun goBack() {
        val slashIndex = masterPath.lastIndexOf("/")
        var type = DisplayType.NORMAL
        // this means that we have already come to the base directory of file system
        if (slashIndex == 8) {
            type = DisplayType.BASE
        }
        // change master path to previous directory
        masterPath = masterPath.substring(0, slashIndex)
        launch {
            chooser.updateFileRecyclerView(loadFiles(), type)
        }
    }

    fun shouldExit() : Boolean {
        return getSlashIndex() < 8
    }
}

internal enum class DisplayType {
    BASE, NORMAL, ROOT
}