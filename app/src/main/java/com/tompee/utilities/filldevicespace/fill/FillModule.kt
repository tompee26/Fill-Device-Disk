package com.tompee.utilities.filldevicespace.fill

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tompee.utilities.filldevicespace.common.factory.FragmentFactory
import com.tompee.utilities.filldevicespace.di.keys.FragmentKey
import com.tompee.utilities.filldevicespace.di.scopes.ActivityScope
import com.tompee.utilities.filldevicespace.fill.storage.StorageFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module
internal abstract class FillModule {

    companion object {

        @Provides
        @ActivityScope
        fun provideFragmentFactory(providerMap: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>): FragmentFactory {
            return FragmentFactory(providerMap)
        }
    }

    @Binds
    abstract fun bindContext(fillActivity: FillActivity): Context

    @Binds
    abstract fun bindFragmentActivity(fillActivity: FillActivity): FragmentActivity

    @Binds
    @IntoMap
    @FragmentKey(StorageFragment::class)
    abstract fun bindStorageFragment(storageFragment: StorageFragment): Fragment
}
