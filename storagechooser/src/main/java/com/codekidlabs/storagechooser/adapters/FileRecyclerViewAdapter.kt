package com.codekidlabs.storagechooser.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codekidlabs.storagechooser.ChooserActivity
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.inflate
import java.io.File

/**
 * Copyright 2017 codekid for storage-chooser. Do not use any
 * of the code befor asking my permission.
 */
internal class FileRecyclerViewAdapter(private var fileList: MutableList<File>, private var chooserActivity: ChooserActivity) : RecyclerView.Adapter<FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = parent.inflate(R.layout.row_storage, false)
        return FileViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.nameTV.text = fileList[position].name
        holder.itemView.setOnClickListener {
            chooserActivity.updateFileManagerWithPath(fileList[position])
        }
    }
}

internal class FileViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val nameTV: TextView = view.findViewById(R.id.storage_name)
}