package com.tompee.utilities.filldevicespace.fill

import android.content.Context
import dagger.Binds
import dagger.Module

@Module
internal abstract class FillModule {

    @Binds
    abstract fun bindContext(fillActivity: FillActivity): Context
}
