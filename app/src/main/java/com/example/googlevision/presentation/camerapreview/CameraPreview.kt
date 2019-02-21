package com.example.googlevision.presentation.camerapreview

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import com.example.googlevision.presentation.GoogleVisionCameraPreviewInterface
import timber.log.Timber
import java.io.IOException

/**
 * Created by josephmagara on 21/2/19.
 */
class CameraPreview(
    context: Context,
    private val view: View,
    private val googleVisionCameraPreviewInterface: GoogleVisionCameraPreviewInterface,
    surfaceView: SurfaceView = SurfaceView(context)) : ViewGroup(context),
    SurfaceHolder.Callback {

    private var currentCamera: Camera? = null
    private var supportedPreviewSizes = listOf<Camera.Size>()

    private var holder: SurfaceHolder = surfaceView.holder.apply {
        addCallback(this@CameraPreview)
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    fun safeCameraOpen(id: Int): Boolean {
        return try {
            releaseCameraAndPreview()
            currentCamera = Camera.open(id)
            true
        } catch (e: Exception) {
            Timber.e("failed to open Camera")
            e.printStackTrace()
            false
        }
    }

    private fun releaseCameraAndPreview() {
        setCamera(null)
        currentCamera?.also { camera ->
            camera.release()
            currentCamera = null
        }
    }

    private fun stopPreviewAndFreeCamera() {
        currentCamera?.apply {
            // Call stopPreview() to stop updating the preview surface.
            stopPreview()

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            release()

            currentCamera = null
        }
    }


    fun setCamera(camera: Camera?) {
        if (currentCamera == camera) {
            return
        }

        stopPreviewAndFreeCamera()

        currentCamera = camera

        currentCamera?.apply {
            supportedPreviewSizes = parameters.supportedPreviewSizes
            requestLayout()

            try {
                setPreviewDisplay(holder)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            startPreview()
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        currentCamera?.apply {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            parameters?.also { params ->
                params.setPreviewSize(view.width, view.height)
                requestLayout()
                parameters = params
            }

            // Important: Call startPreview() to start updating the preview surface.
            // Preview must be started before you can take a picture.
            startPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        currentCamera?.stopPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}