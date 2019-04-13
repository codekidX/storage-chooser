package com.codekidlabs.storagechooser

import android.app.Activity
import android.app.Dialog
import java.io.Serializable

/**
 * Main Builder for storage-chooser
 */

class StorageChooser2(private val activity: Activity, private var config: Config) {

    private lateinit var dialog: Dialog

    fun reset() {
        this.config = Config()
    }

    fun show() {
        dialog = Dialog(activity, R.style.DialogTheme)
    }

//    fun show() {
//        when (this.config.type) {
//            else -> {
//
//            }
//        }
//        val intent = Intent(activity, ChooserActivity::class.java)
//        val bundle = Bundle()
//        bundle.putParcelable("test", config)
//        intent.putExtra("configBundle", bundle)
//        activity.startActivity(intent)
//    }

    interface Selection: Serializable {
        fun onSingleSelection(path: String)
        fun onMultipleSelection(paths: List<String>)
    }

    interface Cancellation: Serializable {
        fun onCancel(lastOpenedPath: String)
    }
}
