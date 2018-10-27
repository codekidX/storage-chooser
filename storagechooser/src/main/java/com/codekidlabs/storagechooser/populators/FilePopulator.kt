package com.codekidlabs.storagechooser.populators

import com.codekidlabs.storagechooser.models.File
import com.codekidlabs.storagechooser.utils.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

/**
 * Copyright 2017 codekid for storage-chooser. Do not use any
 * of the code befor asking my permission.
 */
internal class FilePopulator : CoroutineScope {

    var job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    suspend fun loadFiles(path: String, onlyDirectories: Boolean = false): List<File> {
        val list = mutableListOf<File>()
        async(context = coroutineContext) {
            if (onlyDirectories) {
                val sysFileList: Array<java.io.File> = FileUtil().listFilesAsDir(path)
                sysFileList.map {
                    val file = File(it.name, it.absolutePath, it.isDirectory, it.length())
                    list.add(file)
                }
            }
        }
        return list
    }

}