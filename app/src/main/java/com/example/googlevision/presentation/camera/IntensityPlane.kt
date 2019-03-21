package com.example.googlevision.presentation.camera

import android.graphics.ImageFormat
import android.media.Image
import androidx.annotation.NonNull


/**
 * Created by josephmagara on 21/3/19.
 */
class IntensityPlane// use IntensityPlane.extract instead
private constructor(val width: Int, val height: Int, val plane: ByteArray, val rowStride: Int) {
    companion object {

        /**
         * Extracts the Y-Plane from the YUV_420_8888 image to creates a IntensityPlane.
         * The actual plane data will be copied into the new IntensityPlane object.
         *
         * @throws IllegalArgumentException if the provided images is not in the YUV_420_888 format
         */
        @NonNull
        fun extract(@NonNull img: Image): IntensityPlane {
            if (img.format != ImageFormat.YUV_420_888) {
                throw IllegalArgumentException("image format must be YUV_420_888")
            }

            val planes = img.planes

            val buffer = planes[0].buffer
            val yPlane = ByteArray(buffer.remaining())
            buffer.get(yPlane)

            val yRowStride = planes[0].rowStride

            return IntensityPlane(img.width, img.height, yPlane, yRowStride)
        }
    }
}