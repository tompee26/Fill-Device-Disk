package com.tompee.utilities.filldevicespace.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import timber.log.Timber
import javax.inject.Provider

internal class FragmentFactory(
    private val providerMap: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentClass = loadFragmentClass(classLoader, className)
        val provider = providerMap[fragmentClass]
            ?: return createFragmentAsFallback(classLoader, className)
        try {
            return provider.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun createFragmentAsFallback(classLoader: ClassLoader, className: String): Fragment {
        Timber.d("No creator found for class: $className. Using default constructor")
        return super.instantiate(classLoader, className)
    }
}