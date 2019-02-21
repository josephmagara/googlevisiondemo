package com.example.googlevision.domain.models

import kotlin.reflect.KClass

/**
 * Created by josephmagara on 21/2/19.
 */
data class GvBarcodeInformation(val description: String, val value: Any?, val primitiveType: KClass<*>)