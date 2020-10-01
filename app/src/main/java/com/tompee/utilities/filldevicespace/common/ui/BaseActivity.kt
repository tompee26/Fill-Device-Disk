package com.tompee.utilities.filldevicespace.common.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Base activity with a layout that supports data binding
 */
internal abstract class BaseActivity<T : ViewDataBinding> : BaseShadowActivity() {

    /**
     * Target layout ID
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /**
     * View binding instance
     */
    protected lateinit var viewBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.lifecycleOwner = this
    }

    protected fun setToolbar(toolbar: Toolbar, homeButtonEnable: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(homeButtonEnable)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}