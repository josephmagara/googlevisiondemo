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
class CreateFileUsecase @Inject constructor(private val context: Context){

    fun createFile(): File {
        val timeStamp: String = SimpleDateFormat(DATE_FORMAT).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }
}