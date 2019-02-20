package com.example.googlevision.data.barcodeprocessor.interfaces

import android.graphics.Bitmap

/**
 * Created by josephmagara on 21/2/19.
 */
interface BarcodeProcessActioner {

    fun extractInformationFromBarcode(barcodeBitmap: Bitmap, rotation: Int)
}