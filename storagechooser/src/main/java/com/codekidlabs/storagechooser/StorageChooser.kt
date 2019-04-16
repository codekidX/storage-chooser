package com.codekidlabs.storagechooser

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment
import java.io.Serializable

/**
 * Main Builder for storage-chooser
 */

class StorageChooser2(private val fragmentManager: FragmentManager, private var config: Config) {

    private lateinit var dialog: Dialog

    fun reset() {
        this.config = Config()
    }

    fun show() {
        if (!config.skipOverview) {
            val overview = ChooserDialogFragment()
            val b = Bundle()
            b.putParcelable("test", config)
            overview.arguments = b
            overview.show(fragmentManager, "OVERVIEW")
            return
        }
        // TODO: write this part of the code
//        val secondaryChooser = SecondaryChooserFragment()
        return
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
