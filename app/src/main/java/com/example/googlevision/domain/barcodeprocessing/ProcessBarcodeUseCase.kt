package com.example.googlevision.domain.barcodeprocessing

import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessObserver
import com.example.googlevision.domain.barcodeprocessing.models.GvBarcode
import com.example.googlevision.domain.barcodeprocessing.models.GvBarcodeInformation
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

/**
 * Created by josephmagara on 21/2/19.
 */
class ProcessBarcodeUseCase @Inject constructor(barcodeProcessObserver: BarcodeProcessObserver) {

    private var resultObserver = Disposables.disposed()
    private val gvBarcodePublishProcessor = PublishProcessor.create<List<GvBarcode>>()

    init {
        resultObserver = barcodeProcessObserver.barcodeProcessResultObserver()
            .subscribe { barcodes ->
                val results = mutableListOf<GvBarcode>()

                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue

                    val valueType = barcode.valueType
                    // See API reference for complete list of supported types
                    when (valueType) {
                        FirebaseVisionBarcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            results.add(
                                GvBarcode(
                                    rawValue, "WIFI",
                                    listOf(
                                        GvBarcodeInformation("SSID", ssid, String::class),
                                        GvBarcodeInformation("Password", password, String::class),
                                        GvBarcodeInformation("Encryption Type", type, Int::class)
                                    )
                                )
                            )
                        }
                        FirebaseVisionBarcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url

                            results.add(
                                GvBarcode(
                                    rawValue, "URL",
                                    listOf(
                                        GvBarcodeInformation("Title", title, String::class),
                                        GvBarcodeInformation("Url", url, String::class)
                                    )
                                )
                            )
                        }
                    }
                }

                gvBarcodePublishProcessor.onNext(results)
            }
    }

    fun results(): Observable<List<GvBarcode>> = gvBarcodePublishProcessor.toObservable()
}