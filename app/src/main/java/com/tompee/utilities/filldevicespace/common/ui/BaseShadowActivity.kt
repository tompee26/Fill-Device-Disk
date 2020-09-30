package com.tompee.utilities.filldevicespace.common.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tompee.utilities.filldevicespace.common.BaseFragment
import com.tompee.utilities.filldevicespace.common.factory.FragmentFactory
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import dagger.android.AndroidInjection
import kotlin.reflect.KClass

/**
 * Base shadow activity that doesn't have a UI.
 * It provides convenience functions for fragment instantiation, view model scoping and live
 * data observation.
 *
 * This class supports field injection.
 */
internal abstract class BaseShadowActivity : AppCompatActivity() {

    /**
     * Set of transient view models.
     * Transient view models are view models that only lives along with the immediate activity lifecycle
     */
    private val transientViewModelSet = mutableSetOf<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        provideFragmentFactory()?.let { supportFragmentManager.fragmentFactory = it }
        super.onCreate(savedInstanceState)
    }

    /**
     * Override this function and return a valid fragment factory to allow [getSupportFragmentManager]
     * to use a custom [FragmentFactory]
     */
    protected open fun provideFragmentFactory(): FragmentFactory? = null

    /**
     * Returns a view model scoped in this activity
     */
    protected inline fun <reified VM : BaseViewModel> ViewModelFactory.get(): VM {
        return ViewModelProvider(this@BaseShadowActivity, this).get(VM::class.java)
    }

    /**
     * Returns a transient view model tied to the lifecycle of this activity
     */
    @JvmName("getTransientExtension")
    protected inline fun <reified VM : BaseViewModel> ViewModelFactory.getTransient(): VM {
        synchronized(this@BaseShadowActivity) {
            return transientViewModelSet.filterIsInstance<VM>().firstOrNull()
                ?: create(VM::class.java).apply { transientViewModelSet.add(this) }
        }
    }

    /**
     * Returns a transient view model tied to the lifecycle of this activity
     */
    inline fun <reified VM : BaseViewModel> getTransient(factory: ViewModelFactory): VM {
        return factory.getTransient()
    }

    /**
     * Instantiate a [Fragment] from a [androidx.fragment.app.FragmentFactory]
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <T : Fragment> androidx.fragment.app.FragmentFactory.instantiateAs(fragmentClass: KClass<out Fragment>): T {
        return instantiate(classLoader!!, fragmentClass.java.name) as T
    }

    /**
     * Instantiates a [BaseFragment] from a [FragmentFactory]
     */
    protected fun FragmentFactory.instantiate(clazz: KClass<out Fragment>): BaseFragment<*> {
        return instantiate(classLoader, clazz.java.name) as BaseFragment<*>
    }

    /**
     * Instantiates a derivation of [BaseFragment] from a [FragmentFactory]
     */
    protected inline fun <reified T : Fragment> FragmentFactory.instantiateAs(clazz: KClass<out Fragment>): T {
        return instantiate(classLoader, clazz.java.name) as T
    }


    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     */
    protected inline fun <T> LiveData<T>.observeBy(crossinline handler: (T) -> Unit) {
        this.observe(this@BaseShadowActivity, { handler(it) })
    }

    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     */
    protected fun <T> LiveData<T>.observeBy(observer: Observer<T>) {
        this.observe(this@BaseShadowActivity, observer)
    }

    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     */
    protected inline fun <T> BaseViewModel.EventLiveData<T>.observeEventBy(crossinline handler: (T) -> Unit) {
        this.observe(this@BaseShadowActivity, { event ->
            event.get()?.let { handler(it) }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        transientViewModelSet.forEach { it.onCleared() }
        transientViewModelSet.clear()
    }
}