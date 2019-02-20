package com.example.googlevision.util.extensions

import android.content.Context
import android.os.Environment
import com.example.googlevision.util.DATE_FORMAT
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by josephmagara on 20/2/19.
 */
fun Context.createFile(fileName: String): File {
    val timeStamp: String = SimpleDateFormat(DATE_FORMAT).format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${fileName}_$timeStamp", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}