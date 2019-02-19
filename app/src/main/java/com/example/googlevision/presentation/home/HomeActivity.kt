package com.example.googlevision.presentation.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlevision.BuildConfig
import com.example.googlevision.R
import com.example.googlevision.util.TAKE_PICTURE_REQUEST_CODE
import com.example.googlevision.util.containsPermission
import com.example.googlevision.util.hasAllNeededPermissions
import com.example.googlevision.util.requestPermissions
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File
import java.io.IOException


class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeViewModel : HomeViewModel

    // region LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.googlevision.R.layout.activity_home)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)
            .get(HomeViewModel::class.java)

        homeViewModel.addImageAction().observe(this, Observer {
            takePhoto()
        })

        add_button.setOnClickListener {
            if (!this.hasAllNeededPermissions()) {
                this.requestPermissions()
            } else {
                homeViewModel.triggerAddImageAction()
            }
        }
    }

    // endregion

    // region Public functions
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    taken_photo.setImageBitmap(imageBitmap)
                } else if (requestCode.containsPermission()) {
                    if (hasAllNeededPermissions()) {
                        homeViewModel.triggerAddImageAction()
                    }
                }
            }
        }
    }
    // endregion

    // region Private functions
    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(getString(R.string.file_name))
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
                }
            }
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
            }
        }
    }

    private fun createImageFile(fileName: String): File = homeViewModel.createFile(fileName)
    // endregion

}
