package com.tompee.utilities.filldevicespace.about

import android.os.Bundle
import com.tompee.utilities.filldevicespace.BuildConfig
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.ui.BaseActivity
import com.tompee.utilities.filldevicespace.databinding.ActivityAboutBinding

/**
 * About activity
 */
internal class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override val layoutId: Int = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar, true)
        viewBinding.version.text =
            String.format(getString(R.string.ids_message_version), BuildConfig.VERSION_NAME)
    }
}