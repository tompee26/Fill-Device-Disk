package com.tompee.utilities.filldevicespace.legal

import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.extensions.setHtmlText
import com.tompee.utilities.filldevicespace.common.ui.BaseActivity
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.databinding.ActivityLegalBinding
import javax.inject.Inject

/**
 * License activity
 */
internal class LicenseActivity : BaseActivity<ActivityLegalBinding>() {

    @Inject
    lateinit var assetManager: AssetManager

    override val layoutId: Int = R.layout.activity_legal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar, true)
        with(viewBinding) {
            toolbarText.setText(R.string.ids_title_open_source)
            header.setText(R.string.ids_title_open_source)
            content.setHtmlText(assetManager.getText("opensource.html"))
            content.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}