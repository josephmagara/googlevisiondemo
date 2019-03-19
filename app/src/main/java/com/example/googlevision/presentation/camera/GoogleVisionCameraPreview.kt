package com.example.googlevision.presentation.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.*
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Message
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Created by josephmagara on 26/2/19.
 */
class GoogleVisionCameraPreview(
        cameraPreviewView: SurfaceView,
        private val activity: Activity) : SurfaceHolder.Callback, GLSurfaceView.Renderer, Handler.Callback {

    companion object {
        private const val CAMERA_OPENED = 1
        private const val SURFACE_READY = 2
    }

    private var captureSession: CameraCaptureSession? = null
    private var cameraPreview: SurfaceView = cameraPreviewView
    private val messageHandler = Handler(this)
    private var cameraSurface: Surface = cameraPreviewView.holder.surface
    private var cameraManager: CameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private var surfaceCreated = true
    private var cameraIsConfigured = false

    init {
        cameraPreview.holder?.addCallback(this)
        cameraPreview.holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        cameraPreview.holder?.setKeepScreenOn(true)
        openCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        cameraSurface = holder.surface
        surfaceCreated = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Timber.v("Preview surface Destroyed")
        surfaceCreated = false
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            CAMERA_OPENED, SURFACE_READY ->
                // if both surface is created and camera device is opened
                // - ready to set up preview and other things
                if (surfaceCreated && cameraDevice != null && !cameraIsConfigured) {
                    configureCamera()
                }
        }

        return true
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val cameraStateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Toast.makeText(activity, "onOpened", Toast.LENGTH_SHORT).show()
                cameraDevice = camera

                messageHandler.sendEmptyMessage(CAMERA_OPENED)
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

        try {
            cameraManager.openCamera(cameraManager.cameraIdList[0], cameraStateCallback, Handler())
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        cameraManager.registerAvailabilityCallback(cameraAvailableCB, Handler())
    }


    private fun configureCamera() {
        // prepare list of surfaces to be used in capture requests
        val sfl = ArrayList<Surface>()

        sfl.add(cameraSurface)

        // configure camera with all the surfaces to be ever used
        try {
            cameraDevice?.createCaptureSession(sfl, CaptureSessionListener(), null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    inner class CaptureSessionListener : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Timber.d("CaptureSessionConfigure failed")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            Timber.d("CaptureSessionConfigure onConfigured");
            captureSession = session

            try {
                val previewRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                previewRequestBuilder?.let {
                    it.addTarget(cameraSurface)
                    captureSession?.setRepeatingRequest(it.build(), null, null)
                }
            } catch (e: CameraAccessException) {
                Timber.d("setting up preview failed")
                e.printStackTrace()
            }
        }
    }
}