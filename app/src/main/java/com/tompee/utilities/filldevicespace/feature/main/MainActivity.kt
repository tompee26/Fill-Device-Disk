package com.tompee.utilities.filldevicespace.feature.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
import com.google.android.gms.ads.AdRequest
import com.tompee.utilities.filldevicespace.BuildConfig
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.base.BaseActivity
import com.tompee.utilities.filldevicespace.controller.Utilities
import com.tompee.utilities.filldevicespace.di.component.DaggerMainComponent
import com.tompee.utilities.filldevicespace.di.component.MainComponent
import com.tompee.utilities.filldevicespace.feature.help.HelpActivity
import kotlinx.android.synthetic.main.activity_top.*
import kotlinx.android.synthetic.main.toolbar.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView, EasyPermissions.PermissionCallbacks, TouchInterceptor {
    companion object {
        private const val DISK_PERMISSION = 123
    }

    private lateinit var component: MainComponent

    @Inject
    lateinit var presenter: MainPresenter

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setToolbar(toolbar)
        toolbar_text.setText(R.string.app_name)
        background.setImageDrawable(Utilities.getDrawableFromAsset(this, "bg.jpg"))

        val builder = AdRequest.Builder()
        if (BuildConfig.DEBUG) {
            builder.addTestDevice("3AD737A018BB67E7108FD1836E34DD1C")
        }
        adView.loadAd(builder.build())
        presenter.attachView(this)
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.menu_about -> {
                intent = Intent(this, HelpActivity::class.java)
                intent.putExtra(HelpActivity.TAG_MODE, HelpActivity.ABOUT)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            R.id.menu_contact -> {
                intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("tompee26@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Re: Fill Device Disk")
                intent.putExtra(Intent.EXTRA_TEXT, "")
                startActivity(Intent.createChooser(intent, getString(R.string.ids_lbl_contact)))
                return true
            }
            R.id.menu_os -> {
                intent = Intent(this, HelpActivity::class.java)
                intent.putExtra(HelpActivity.TAG_MODE, HelpActivity.LICENSE)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewPager.isSwipeAllowed) {
            SuperActivityToast.create(this, Style(), Style.TYPE_STANDARD)
                    .setText(getString(R.string.ids_message_stop_process))
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(ContextCompat.getColor(this, R.color.button))
                    .setAnimations(Style.ANIMATIONS_POP).show()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    //endregion

    //region Permission
    @AfterPermissionGranted(DISK_PERMISSION)
    private fun checkAndRequestPermission() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage),
                    DISK_PERMISSION, *perms)
        } else {
            setupView()
        }
    }

    private fun setupView() {
        val adapter = MainViewPagerAdapter(this, supportFragmentManager, component)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        tabLayoutMain.setupWithViewPager(viewPager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        /* Do nothing */
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }
    //endregion

    //region BaseActivity
    override fun setupComponent() {
        component = DaggerMainComponent.builder()
                .appComponent((application as FillDeviceDiskApp).component)
                .build()
        component.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_top
    //endregion

    // region MainView
    override fun showAppRater() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.ids_title_rate)
        builder.setMessage(R.string.ids_message_rate)
        builder.setNeutralButton(R.string.ids_lbl_remind, null)
        builder.setNegativeButton(R.string.ids_lbl_no_rate, null)
        builder.setPositiveButton(R.string.ids_lbl_yes_rate) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
                    BuildConfig.APPLICATION_ID))
            startActivity(intent)
        }
        builder.create().show()
    }
    //endregion

    //region Interceptor
    override fun interceptTouchEvents(intercept : Boolean) {
        viewPager.isSwipeAllowed = intercept
    }
    //endregion
}