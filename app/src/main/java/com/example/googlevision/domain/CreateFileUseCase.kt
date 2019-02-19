package com.example.googlevision.domain

import android.content.Context
import android.os.Environment
import com.example.googlevision.util.DATE_FORMAT
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
class CreateFileUseCase @Inject constructor(private val context: Context){

    fun createFile(fileName: String): File {
        val timeStamp: String = SimpleDateFormat(DATE_FORMAT).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${fileName}_$timeStamp", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }
}