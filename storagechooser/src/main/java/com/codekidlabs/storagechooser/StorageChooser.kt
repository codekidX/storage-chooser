package com.codekidlabs.storagechooser

import android.app.Activity
import java.io.Serializable

/**
 * Main Builder for storage-chooser
 */

class StorageChooser(private val activity: Activity, private val config: Config) {

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
