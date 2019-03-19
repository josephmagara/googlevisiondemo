package com.example.googlevision.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlevision.BuildConfig
import com.example.googlevision.R
import com.example.googlevision.presentation.GoogleVisionCameraPreviewInterface
import com.example.googlevision.presentation.camera.GoogleVisionCameraPreview
import com.example.googlevision.util.TAKE_PICTURE_REQUEST_CODE
import com.example.googlevision.util.extensions.containsPermission
import com.example.googlevision.util.extensions.createFile
import com.example.googlevision.util.extensions.getRotationCompensation
import com.example.googlevision.util.extensions.hasAllNeededPermissions
import com.example.googlevision.util.extensions.requestPermissions
import com.example.googlevision.util.extensions.setScaledPic
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject


class HomeActivity : DaggerAppCompatActivity(), GoogleVisionCameraPreviewInterface {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeViewModel: HomeViewModel

    private var filepath: String? = null
    private var googleVisionCameraPreview: GoogleVisionCameraPreview? = null
    // region LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.googlevision.R.layout.activity_home)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)
                .get(HomeViewModel::class.java)

        homeViewModel.addImageAction().observe(this, Observer {
            takePhoto()
        })

        homeViewModel.captureImage().observe(this, Observer {
            val pictureCallback = Camera.PictureCallback { data, _ ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                getCameraId()?.let { cameraId ->
                    val rotation = getRotationCompensation(cameraId)
                    homeViewModel.extractInformationFromBarcode(bitmap, rotation)
                }
            }
        })

        homeViewModel.processedText().observe(this, Observer { text ->
            Timber.v("Incoming text: $text")
            extracted_text.text = text
        })

        add_button.setOnClickListener {
            if (!this.hasAllNeededPermissions()) {
                this.requestPermissions()
            } else {
                homeViewModel.triggerAddImageAction()
            }
        }

        if (this.hasAllNeededPermissions()) {
            setupCamera()
        } else {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.v("Starting preview")
        googleVisionCameraPreview?.startPreview()
    }

    override fun onPause() {
        super.onPause()
        Timber.v("Stopping preview")
        googleVisionCameraPreview?.stopPreview()
    }
    // endregion

    // region Public functions
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
                    filepath?.let {
                        taken_photo.setScaledPic(it)?.let { bitmap ->
                            getCameraId()?.let { cameraId ->
                                val rotation = getRotationCompensation(cameraId)
                                homeViewModel.extractInformationFromBarcode(bitmap, rotation)
                            }
                        }
                    }
                } else if (requestCode.containsPermission()) {
                    if (hasAllNeededPermissions()) {
                        setupCamera()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                when (permission) {
                    Manifest.permission.CAMERA -> setupCamera()
                }
            }
        }
    }

    override fun onCameraSteadied() = homeViewModel.stopImageCaptureTimer()


    override fun onCameraMoved() = homeViewModel.startImageCaptureTimer()

    // endregion

    // region Private functions
    private fun setupCamera() {
        googleVisionCameraPreview = GoogleVisionCameraPreview(camera_preview, this)
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    this.createFile(getString(R.string.file_name))
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    filepath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra("filepath", it.absolutePath)
                    startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
                }
            }
        }
    }

    private fun getCameraId(): String? {
        var cameraId: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                for (id in cameraManager.cameraIdList) {
                    val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == LENS_FACING_FRONT) {
                        cameraId = id
                        break
                    }
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        return cameraId
    }

    // endregion

}
