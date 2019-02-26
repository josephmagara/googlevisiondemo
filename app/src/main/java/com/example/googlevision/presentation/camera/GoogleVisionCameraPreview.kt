package com.example.googlevision.presentation.camera

import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by josephmagara on 26/2/19.
 */
class GoogleVisionCameraPreview(cameraPreviewView: View) : SurfaceHolder.Callback, GLSurfaceView.Renderer {


    private var googleVisionCamera: Camera? = null
    private var cameraPreview: SurfaceView? = null

    init {
        cameraPreview = cameraPreviewView as SurfaceView
        cameraPreview?.holder?.addCallback(this)
        cameraPreview?.holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

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

    fun startPreview() = googleVisionCamera?.startPreview()

}