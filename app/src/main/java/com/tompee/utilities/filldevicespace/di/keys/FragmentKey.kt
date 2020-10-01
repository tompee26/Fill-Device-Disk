package com.tompee.utilities.filldevicespace.di.keys

import androidx.fragment.app.Fragment
import dagger.MapKey
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention
@MapKey
internal annotation class FragmentKey(val value: KClass<out Fragment>)