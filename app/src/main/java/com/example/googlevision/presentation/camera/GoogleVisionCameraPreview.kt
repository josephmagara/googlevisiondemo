package com.example.googlevision.presentation.camera

import android.Manifest
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.opengl.GLSurfaceView
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlevision.util.CAMERA_PERMISSION_REQUEST_CODE
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Created by josephmagara on 26/2/19.
 */
class GoogleVisionCameraPreview(cameraPreviewView: SurfaceView, private val activity: Activity) :
    SurfaceHolder.Callback, GLSurfaceView.Renderer {


    private var googleVisionCamera: Camera? = null
    private var cameraPreview: SurfaceView = cameraPreviewView
    private var cameraManager: CameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager

    init {
        cameraPreview.holder?.addCallback(this)
        cameraPreview.holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        cameraPreview.holder?.setKeepScreenOn(true)
        setupCamera()
        googleVisionCamera = Camera.open()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        googleVisionCamera?.let {
            val params = it.parameters
            val sizes = params.supportedPreviewSizes
            val selected = sizes[0]
            params.setPreviewSize(selected.width, selected.height)
            it.parameters = params

            it.setDisplayOrientation(90)
            it.startPreview()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            googleVisionCamera?.setPreviewDisplay(cameraPreview?.holder)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Timber.v("Preview surface Destroyed")
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
    }

    fun stopPreview() = googleVisionCamera?.stopPreview()

    fun release() = googleVisionCamera?.release()

    private fun setupCamera() {
        val cameraList = cameraManager.cameraIdList

        val cameraStateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Toast.makeText(activity, "onOpened", Toast.LENGTH_SHORT).show()
                //requesting permission
                val permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

                    } else {
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                        Toast.makeText(activity, "request permission", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "PERMISSION_ALREADY_GRANTED", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onDisconnected(camera: CameraDevice) {
                Toast.makeText(activity, "onDisconnected", Toast.LENGTH_SHORT).show()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Toast.makeText(activity, "onError", Toast.LENGTH_SHORT).show()
            }
        }

        val cameraAvailableCB = object : CameraManager.AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                super.onCameraAvailable(cameraId)

                Toast.makeText(activity, "onCameraAvailable", Toast.LENGTH_SHORT).show()
            }

            override fun onCameraUnavailable(cameraId: String) {
                super.onCameraUnavailable(cameraId)

                Toast.makeText(activity, "onCameraUnavailable", Toast.LENGTH_SHORT).show()
            }
        }

        //opening the camera
        try {
            cameraManager.openCamera(cameraList[1], cameraStateCallback, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        cameraManager.registerAvailabilityCallback(cameraAvailableCB, Handler())
    }
}