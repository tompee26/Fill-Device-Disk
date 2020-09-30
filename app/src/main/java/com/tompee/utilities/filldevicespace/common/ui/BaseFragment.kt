package com.tompee.utilities.filldevicespace.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base Fragment.
 * Supports layout and data binding. Provides convenience functions for fragment instantiation,
 * live data observation and view model scoping
 */
internal abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    /**
     * View model scope
     */
    protected enum class Scope {

        /**
         * Hosting activity scope
         */
        ACTIVITY,

        /**
         * Parent fragment scope
         */
        PARENT,

        /**
         * This fragment scope
         */
        THIS,
    }

    /**
     * Transient view model owner
     */
    protected enum class Owner {

        /**
         * Hosting activity
         */
        ACTIVITY,

        /**
         * Parent fragment
         */
        PARENT,

        /**
         * This fragment
         */
        THIS,
    }

    /**
     * A lazy property that gets cleaned up when the fragment's view is destroyed.
     * Hugely based on AutoClearValue but is more specialized in fragment binding
     *
     * Accessing this variable while the fragment's view is destroyed will throw NPE.
     */
    class FragmentBinding<T : ViewDataBinding>(private val fragment: BaseFragment<T>) :
        ReadWriteProperty<Fragment, T> {

        private var value: T? = null

        init {
            fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                        viewLifecycleOwner?.lifecycle?.addObserver(object :
                            DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                value = null
                                Timber.d("Destroyed ${fragment::class.java.name} binding")
                            }
                        })
                    }
                }
            })
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("Does not exist")
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            Timber.d("Initialized ${fragment::class.java.name} binding")
            this.value = value
        }
    }

    /**
     * Creates an [FragmentBinding] associated with this fragment.
     */
    private fun fragmentBinding() = FragmentBinding(this)

    /**
     * Set of created transient view models. They will be tracked here since transient view models need to be
     * cleared explicitly
     */
    private val transientViewModelSet = mutableSetOf<BaseViewModel>()

    /**
     * View binding
     */
    protected var viewBinding by fragmentBinding()

    @get:LayoutRes
    abstract val layoutId: Int

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate<T>(inflater, layoutId, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    /**
     * Returns a scoped ViewModel. A scoped view model survives its scope's configuration changes.
     *
     * @param scope the scoping entity
     * @return a scoped ViewModel
     */
    protected inline fun <reified VM : BaseViewModel> ViewModelFactory.get(scope: Scope): VM {
        val owner: ViewModelStoreOwner = when (scope) {
            Scope.ACTIVITY -> requireActivity()
            Scope.PARENT -> requireParentFragment()
            Scope.THIS -> this@BaseFragment
        }
        return ViewModelProvider(owner, this).get(VM::class.java)
    }

    /**
     * Returns a transient view model. A transient view model only lives together with its owner.
     * If the owner is destroyed, it will also be a candidate for garbage collection as long as there is no
     * lingering reference to it. Any request within the same lifecycle period will return the same instance.
     *
     * @return a transient view model
     */
    protected inline fun <reified VM : BaseViewModel> ViewModelFactory.getTransient(owner: Owner = Owner.THIS): VM {
        return when (owner) {
            Owner.ACTIVITY -> (activity as BaseActivity<*>).getTransient(this)
            Owner.PARENT -> (parentFragment as BaseFragment<*>).getTransient(this)
            Owner.THIS -> getTransient(this)
        }
    }

    /**
     * Returns a transient view model. A transient view model only lives together with this fragment.
     * If the fragment is destroyed, it will also be a candidate for garbage collection as long as there is no
     * lingering reference to it. Any request within the same lifecycle period will return the same instance.
     *
     * @param factory view model factory
     * @return a transient view model
     */
    inline fun <reified VM : BaseViewModel> getTransient(factory: ViewModelFactory): VM {
        synchronized(this) {
            return transientViewModelSet.filterIsInstance<VM>().firstOrNull()
                ?: factory.create(VM::class.java).apply { transientViewModelSet.add(this) }
        }
    }

    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     */
    protected inline fun <T> LiveData<T>.observeBy(crossinline handler: (T) -> Unit) {
        observe(viewLifecycleOwner, { handler(it) })
    }

    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     *
     * @param observer live data observer
     */
    protected fun <T> LiveData<T>.observeBy(observer: Observer<T>) {
        observe(viewLifecycleOwner, observer)
    }

    /**
     * An extension method use to abstract lifecycle owner in live data observation. It also provides a streamlined
     * syntax as it only accepts a handler lambda instead of a SAM constructor
     *
     * @param handler live data value handler. Will only be invoked when event exists
     */
    protected inline fun <T> BaseViewModel.EventLiveData<T>.observeBy(crossinline handler: (T) -> Unit) {
        observe(viewLifecycleOwner, { event ->
            event.get()?.let { handler(it) }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        transientViewModelSet.forEach { it.onCleared() }
        transientViewModelSet.clear()
    }
}