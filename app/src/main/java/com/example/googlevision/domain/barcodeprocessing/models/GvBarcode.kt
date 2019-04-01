package com.example.googlevision.domain.barcodeprocessing.models

/**
 * Created by josephmagara on 21/2/19.
 */
data class GvBarcode(val rawValue: String?, val type: String, val information: List<GvBarcodeInformation>)