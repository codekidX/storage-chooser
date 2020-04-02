package com.codekidlabs.storagechooser

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.codekidlabs.storagechooser.fragments.OverviewDialogFragment
import com.codekidlabs.storagechooser.fragments.SecondaryChooserFragment
import java.io.Serializable

/**
 * Main Builder for storage-chooser
 */

class StorageChooser2(private val fragmentManager: FragmentManager, private var config: Config) {

    companion object {
        const val SC_PROVIDER_AUTHORITY: String = "com.codekidlabs.storagechooser.fileprovider"
        const val SC_SAVED_PATH: String = "storage_chooser_path"
        const val SC_SESSION_PATH: String = "storage_chooser_session"
    }

    private lateinit var dialog: Dialog

    fun reset() {
        this.config = Config()
    }

    fun set(config: Config) {
        this.config = config
    }

    fun show() {
        val b = Bundle()
        b.putParcelable("config", config)
        if (!config.skipOverview) {
            val overview = OverviewDialogFragment()
            overview.arguments = b
            overview.show(fragmentManager, ChooserType.BASIC.toString())
            return
        }
        // TODO: write this part of the code
        val secondaryChooser = SecondaryChooserFragment()
        secondaryChooser.arguments = b
        secondaryChooser.show(fragmentManager, ChooserType.DIRECTORY.toString())
    }

    interface Selection: Serializable {
        fun onSingleSelection(path: String)
        fun onMultipleSelection(paths: List<String>)
    }

    interface Cancellation: Serializable {
        fun onOverviewCancel()
        fun onCancel()
    }
}
