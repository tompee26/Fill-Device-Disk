package com.tompee.utilities.filldevicespace.feature.help

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.tompee.utilities.filldevicespace.BuildConfig
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.base.BaseActivity
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_license.*
import kotlinx.android.synthetic.main.toolbar.*

class HelpActivity : BaseActivity() {
    companion object {
        const val TAG_MODE = "mode"
        const val ABOUT = 0
        const val LICENSE = 1
        const val PRIVACY = 2
    }

    lateinit var assetManager: AssetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setToolbar(toolbar, true)
        val mode = intent.getIntExtra(TAG_MODE, ABOUT)
        when (mode) {
            ABOUT -> {
                toolbar_text.setText(R.string.ids_lbl_about)
                version.text = String.format(getString(R.string.ids_message_version), BuildConfig.VERSION_NAME)
            }
            LICENSE -> {
                toolbar_text.setText(R.string.ids_title_open_source)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    content.text = Html.fromHtml(assetManager.getStringFromAsset("opensource.html"),
                            Html.FROM_HTML_MODE_LEGACY)
                } else {
                    content.text = Html.fromHtml(assetManager.getStringFromAsset("opensource.html"))
                }
                content.movementMethod = LinkMovementMethod.getInstance()
            }
            else -> {
                toolbar_text.setText(R.string.ids_title_privacy_policy)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    content.text = Html.fromHtml(assetManager.getStringFromAsset("policy.html"),
                            Html.FROM_HTML_MODE_LEGACY)
                } else {
                    content.text = Html.fromHtml(assetManager.getStringFromAsset("policy.html"))
                }
                content.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    override fun setupComponent() {
        assetManager = (application as FillDeviceDiskApp).component.assetManager()
    }

    override fun layoutId(): Int {
        val mode = intent.getIntExtra(TAG_MODE, ABOUT)
        return if (mode == ABOUT) R.layout.activity_about else R.layout.activity_license
    }
}