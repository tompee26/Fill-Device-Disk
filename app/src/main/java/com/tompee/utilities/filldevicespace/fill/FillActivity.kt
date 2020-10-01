package com.tompee.utilities.filldevicespace.fill

import android.Manifest
import android.os.Bundle
import com.tompee.utilities.filldevicespace.R
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