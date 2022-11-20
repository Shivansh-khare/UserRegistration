package com.example.android.roomwordssample.Camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android.roomwordssample.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXFragment() : Fragment() {


    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var CameraPreview : PreviewView
    private var roatate:Boolean = true
    private lateinit var rotateButton:ImageView
    private lateinit var imView:ImageView
    private lateinit var select:ImageView
    private lateinit var remove:ImageView
    private lateinit var prevLay:View
    private lateinit var capture:ImageView
    private lateinit var savedUri:Uri
    private lateinit var cameraProvider: ProcessCameraProvider
    private var isPreview:Boolean = false
    private var isStarted:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera(roatate)
        } else {
            activity?.let { ActivityCompat.requestPermissions(it.parent, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS) }
        }

        outputDirectory = getOutputDirectory()
//        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        isStarted = true;
        var view = inflater.inflate(R.layout.fragment_camera_x, container, false)
        CameraPreview = view.findViewById(R.id.CameraPreview)
        rotateButton = view.findViewById(R.id.rot)
        capture = view.findViewById(R.id.capture)
        prevLay = view.findViewById(R.id.prevLay)
        imView = view.findViewById(R.id.iv_Picture)
        remove = view.findViewById(R.id.imageView3)
        remove.setOnClickListener {
            prevLay.visibility = View.GONE
            isPreview=false
        }
        rotateButton.setOnClickListener {
            rotate()
        }

        capture.setOnClickListener {
            takePhoto()
        }

        if (savedInstanceState != null) {
            isPreview = savedInstanceState.getBoolean("state")
        }
        if(isPreview) {
            if (savedInstanceState != null) {
                savedUri = Uri.parse(savedInstanceState.getString("img"))
                prevLay.visibility = View.VISIBLE
                imView.setImageURI(savedUri)
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("state",isPreview)
        if(isPreview)
            outState.putString("img",savedUri.toString())
    }

    private fun startCamera(Rotate:Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(CameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            var cameraSelector:CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            if(Rotate){
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 -> ContextCompat.checkSelfPermission(it1, it) } == PackageManager.PERMISSION_GRANTED
    }

    // creates a folder inside internal storage
    fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera(roatate)
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXGFG"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return
        isPreview=true;

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener,
        // which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
//
                    // set the saved uri to the image view
                    prevLay.visibility = View.VISIBLE
                    imView.setImageURI(savedUri)

//
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            })
    }

    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(isVisibleToUser && isStarted){
            Log.d("MLog","CAM VISIBLE")
            startCamera(roatate)
        }else if(isStarted){
            Log.d("MLog","CAM NOT VISIBLE")
            cameraProvider.unbindAll()
        }
    }

    fun rotate() {
        roatate = !roatate
        startCamera(roatate)
    }
}