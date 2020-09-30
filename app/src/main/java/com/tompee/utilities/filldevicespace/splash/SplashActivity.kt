package com.tompee.utilities.filldevicespace.splash

import android.content.Intent
import android.os.Bundle
import com.tompee.utilities.filldevicespace.common.ui.BaseShadowActivity
import com.tompee.utilities.filldevicespace.fill.FillActivity

/**
 * Splash activity
 * Directly transitions to [FillActivity]
 */
internal class SplashActivity : BaseShadowActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, FillActivity::class.java)
        startActivity(intent)
        finish()
    }
}