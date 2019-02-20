package com.example.googlevision.util.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView

/**
 * Created by josephmagara on 20/2/19.
 */

fun ImageView.setScaledPic(absolutePhotoPath: String){
    // Get the dimensions of the View
    val targetW: Int = width
    val targetH: Int = height

    val bmOptions = BitmapFactory.Options().apply {
        // Get the dimensions of the bitmap
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(absolutePhotoPath, this)
        val photoW: Int = outWidth
        val photoH: Int = outHeight

        // Determine how much to scale down the image
        val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        inJustDecodeBounds = false
        inSampleSize = scaleFactor
        inPurgeable = true
    }
    BitmapFactory.decodeFile(absolutePhotoPath, bmOptions)?.also { bitmap ->
        setImageBitmap(bitmap)
    }
}