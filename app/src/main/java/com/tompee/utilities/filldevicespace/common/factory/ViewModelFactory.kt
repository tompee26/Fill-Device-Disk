package com.tompee.utilities.filldevicespace.common.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

internal class ViewModelFactory(private val providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = providerMap[modelClass]
            ?: throw IllegalArgumentException("Invalid view model class")
        return provider.get() as T
    }
}
