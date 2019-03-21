package com.example.googlevision.presentation.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Message
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.example.googlevision.presentation.ImageRetrievalPipeline
import com.example.googlevision.presentation.MotionDetector
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Created by josephmagara on 26/2/19.
 */
class GoogleVisionCameraPreview(
    cameraPreviewView: SurfaceView,
    private val activity: Activity,
    private val motionDetector: MotionDetector?,
    private val imageRetrievalPipeline: ImageRetrievalPipeline
) :
    SurfaceHolder.Callback, GLSurfaceView.Renderer, Handler.Callback {

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
    private var currentCameraId: String = ""
    private var processingForLastPhotoCompleted: Boolean = true

    private val canTakePhoto: Boolean
        get() {
            return motionDetector?.deviceIsStill == true && processingForLastPhotoCompleted
        }

    init {
        cameraPreview.holder?.addCallback(this)
        cameraPreview.holder?.setKeepScreenOn(true)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

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
        Timber.d("Opening camera")
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

        val cameraAvailableCallback = object : CameraManager.AvailabilityCallback() {
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
            currentCameraId = cameraManager.cameraIdList[0]
            cameraManager.openCamera(currentCameraId, cameraStateCallback, Handler())
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        cameraManager.registerAvailabilityCallback(cameraAvailableCallback, Handler())
    }


    private fun configureCamera() {
        // prepare list of surfaces to be used in capture requests
        val imageReader =
            ImageReader.newInstance(cameraPreview.width, cameraPreview.height, ImageFormat.YUV_420_888, 50)
        imageReader.setOnImageAvailableListener({
            processingForLastPhotoCompleted = false
            val image = it.acquireLatestImage()
            if (image != null) {
                Toast.makeText(activity, "Photo taken", Toast.LENGTH_SHORT).show()
                imageRetrievalPipeline.onImageReceived(image, currentCameraId)
            } else {
                processingForLastPhotoCompleted = true
            }
        }, null)

        val surfaceList = listOf(cameraSurface, imageReader.surface)

        // configure camera with all the surfaces to be ever used
        try {
            cameraDevice?.createCaptureSession(surfaceList, CaptureSessionListener(imageReader), null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun stopPreview() {
        try {
            if (captureSession != null) {
                captureSession?.stopRepeating()
                captureSession?.close()
                captureSession = null
            }

            currentCameraId = ""
            cameraIsConfigured = false
        } catch (e: CameraAccessException) {
            // Doesn't matter, closing device anyway
            e.printStackTrace()
        } finally {
            if (cameraDevice != null) {
                cameraDevice?.close()
                cameraDevice = null
                captureSession = null
            }
        }
    }

    fun startPreview() = openCamera()

    fun finishedProcessingLastImage() {
        processingForLastPhotoCompleted = true
    }

    inner class CaptureSessionListener(private val imageReader: ImageReader) : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Timber.d("CaptureSessionConfigure failed")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            Timber.d("CaptureSessionConfigure onConfigured")
            captureSession = session

            try {
                val previewRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                previewRequestBuilder?.let {

                    // Configure the preview
                    it.addTarget(cameraSurface)

                    val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                        private fun process(result: CaptureResult, currentSession: CameraCaptureSession) {
                            val autoFocusState = result.get(CaptureResult.CONTROL_AF_STATE)
                            if (CaptureResult.CONTROL_AF_TRIGGER_START == autoFocusState) {
                                if (canTakePhoto) {
                                    //Run specific task here
                                    val singleRequest = currentSession.device
                                        .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                                    singleRequest.addTarget(cameraSurface)
                                    singleRequest.addTarget(imageReader.surface)
                                    session.capture(singleRequest.build(), null, null)
                                }
                            }
                        }

                        override fun onCaptureProgressed(
                            session: CameraCaptureSession, request: CaptureRequest,
                            partialResult: CaptureResult
                        ) = process(partialResult, session)


                        override fun onCaptureCompleted(
                            session: CameraCaptureSession, request: CaptureRequest,
                            result: TotalCaptureResult
                        ) = process(result, session)
                    }

                    captureSession?.setRepeatingRequest(it.build(), captureCallback, null)
                }
            } catch (e: CameraAccessException) {
                Timber.d("setting up preview failed")
                e.printStackTrace()
            }
        }
    }
}