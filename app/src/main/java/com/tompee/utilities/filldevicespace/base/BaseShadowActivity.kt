package com.tompee.utilities.filldevicespace.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection

internal abstract class BaseShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        provideFragmentFactory()?.let { supportFragmentManager.fragmentFactory = it }
        super.onCreate(savedInstanceState)
    }

    protected open fun provideFragmentFactory(): FragmentFactory? = null
}