package com.tompee.utilities.filldevicespace.fill

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.tompee.utilities.filldevicespace.common.factory.FragmentFactory
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import com.tompee.utilities.filldevicespace.di.keys.FragmentKey
import com.tompee.utilities.filldevicespace.di.keys.ViewModelKey
import com.tompee.utilities.filldevicespace.di.scopes.ActivityScope
import com.tompee.utilities.filldevicespace.fill.easy.EasyFillFragment
import com.tompee.utilities.filldevicespace.fill.easy.EasyFillViewModel
import com.tompee.utilities.filldevicespace.fill.storage.StorageFragment
import com.tompee.utilities.filldevicespace.fill.storage.StorageViewModel
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

        @Provides
        @ActivityScope
        fun provideViewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelFactory {
            return ViewModelFactory(providerMap)
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

    @Binds
    @IntoMap
    @FragmentKey(EasyFillFragment::class)
    abstract fun bindEasyFillFragment(easyFillFragment: EasyFillFragment): Fragment

    @Binds
    @IntoMap
    @ViewModelKey(StorageViewModel::class)
    abstract fun bindStorageViewModel(storageViewModel: StorageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EasyFillViewModel::class)
    abstract fun bindEasyFillViewModel(easyFillViewModel: EasyFillViewModel): ViewModel
}
