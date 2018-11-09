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

    private var masterPath: String = "/storage/emulated/0"
    private val initialPath = masterPath.substring(0, masterPath.lastIndexOf("/"))
    var onlyDirectories = false
    var chooserType: DisplayType = DisplayType.BASE
    private val fileUtil = FileUtil()

    suspend fun loadFiles(): MutableList<File> {
        return async(context = coroutineContext) {
            File(masterPath).listFiles().toMutableList()
        }.await()
    }

    fun getSegues(): MutableList<String> {
        return masterPath.substring(1, masterPath.count()).split("/").toMutableList()
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
        if (!shouldExit()) {
            // TODO - start some loading view
            launch {
                chooser.updateFileRecyclerView(loadFiles(), type)
                // TODO - stop some loading view
            }
        }
    }

    fun goForward(toFolder: String) {
        masterPath = "$masterPath/$toFolder"
        launch {
            val files = loadFiles()
            chooser.updateFileRecyclerView(files)
        }
    }

    fun shouldExit() : Boolean {
        return this.masterPath == initialPath
    }
}

internal enum class DisplayType {
    BASE, NORMAL, ROOT
}