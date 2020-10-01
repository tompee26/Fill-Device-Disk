package com.tompee.utilities.filldevicespace.fill

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.about.AboutActivity
import com.tompee.utilities.filldevicespace.common.ui.BaseActivity
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.databinding.ActivityFillBinding
import kotlinx.android.synthetic.main.activity_fill.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Fill activity
 */
internal class FillActivity : BaseActivity<ActivityFillBinding>(),
    EasyPermissions.PermissionCallbacks {

    companion object {
        private const val DISK_PERMISSION = 123
    }

    @Inject
    lateinit var assetManager: AssetManager

    override val layoutId: Int = R.layout.activity_fill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar)
        background.setImageDrawable(assetManager.getDrawable("bg.jpg"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_fill, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.menu_about -> {
                Intent(this, AboutActivity::class.java)
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
            }
//            R.id.menu_privacy_policy -> {
//                intent = Intent(this, HelpActivity::class.java)
//                intent.putExtra(HelpActivity.TAG_MODE, HelpActivity.PRIVACY)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(intent)
//                return true
//            }
            R.id.menu_contact -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("tompee26@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Re: Fill Device Disk")
                    putExtra(Intent.EXTRA_TEXT, "")
                }.let { Intent.createChooser(it, getString(R.string.ids_lbl_contact)) }
            }
//            R.id.menu_os -> {
//                intent = Intent(this, HelpActivity::class.java)
//                intent.putExtra(HelpActivity.TAG_MODE, HelpActivity.LICENSE)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(intent)
//                return true
//            }
//        }
            else -> return super.onOptionsItemSelected(item)
        }
        startActivity(intent)
        return true
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermission()
    }

    @AfterPermissionGranted(DISK_PERMISSION)
    private fun checkAndRequestPermission() {
        val perms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.rationale_storage),
                DISK_PERMISSION, *perms
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        /* Do nothing */
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }
}