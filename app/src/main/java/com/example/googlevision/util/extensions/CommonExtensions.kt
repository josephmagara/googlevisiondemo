package com.example.googlevision.util.extensions

import kotlin.reflect.KClass

/**
 * Created by josephmagara on 21/2/19.
 */

fun <T: Any> cast(any: Any, clazz: KClass<out T>): T = clazz.javaObjectType.cast(any)!!