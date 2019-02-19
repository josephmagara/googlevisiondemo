package com.example.googlevision.presentation.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.googlevision.util.TAKE_PICTURE_REQUEST_CODE
import com.example.googlevision.util.containsPermission
import com.example.googlevision.util.hasAllNeededPermissions
import com.example.googlevision.util.requestPermissions
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File


class HomeActivity : AppCompatActivity() {


    private var imageUri: Uri? = null
    private var homeViewModel = HomeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.googlevision.R.layout.activity_home)


        homeViewModel.addImageAction().observe(this, Observer {
            takePhoto()
        })

        add_button.setOnClickListener{
            if(!this.hasAllNeededPermissions()){
                this.requestPermissions()
            }else{
                homeViewModel.triggerAddImageAction()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode){
            Activity.RESULT_OK -> {
                if (requestCode == TAKE_PICTURE_REQUEST_CODE){
                    retrievePhoto(imageUri)
                } else if (requestCode.containsPermission()){
                    if(hasAllNeededPermissions()){
                        homeViewModel.triggerAddImageAction()
                    }
                }
            }
        }

    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg")
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo)
        )
        imageUri = Uri.fromFile(photo)
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
    }

    private fun retrievePhoto(photoUri: Uri?){
        photoUri?.let {
            contentResolver.notifyChange(photoUri, null)
            val cr = contentResolver
            val bitmap: Bitmap
            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, photoUri)

                taken_photo.setImageBitmap(bitmap)

                Toast.makeText(this, photoUri.toString(),
                        Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show()
                Log.e("Camera", e.toString())
            }
        }
    }
}
