package com.example.googlevision.presentation.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlevision.util.TAKE_PICTURE_REQUEST_CODE
import com.example.googlevision.util.containsPermission
import com.example.googlevision.util.hasAllNeededPermissions
import com.example.googlevision.util.requestPermissions
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeViewModel : HomeViewModel

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

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
            }
        }
    }

}
