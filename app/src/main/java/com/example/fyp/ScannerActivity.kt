package com.example.fyp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.fyp.databinding.ActivityScannerBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityScannerBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview : Preview
    private lateinit var imageAnalysis : ImageAnalysis
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
            processCameraProvider = cameraProviderFuture.get()
                bindCameraPreview()
                bindInputAnalyser()
        }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindCameraPreview(){
        cameraPreview = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation).build()
        cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
        processCameraProvider.bindToLifecycle(this, cameraSelector, cameraPreview)
    }

    private fun bindInputAnalyser() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        )
        imageAnalysis = ImageAnalysis.Builder().setTargetRotation(binding.previewView.display.rotation).build()

        val cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            Log.d("ScannerActivity", "Image analyzed")
            processImageProxy(barcodeScanner, imageProxy)
        }

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            Log.d("ScannerActivity", "Processing image")

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    Log.d("ScannerActivity", "Barcodes processed: ${barcodes.size}")
                    barcodes.forEach { barcode ->
                        Log.d("ScannerActivity", "Barcode Value Type: ${barcode.valueType}")
                        Log.d("ScannerActivity", "Barcode Display Value: ${barcode.displayValue}")
                        Log.d("ScannerActivity", "Barcode Raw Value: ${barcode.rawValue}")
                    }
                    if (barcodes.isNotEmpty()) {
                        onScan?.invoke(barcodes)
                        onScan = null
                        finish()
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            Log.d("ScannerActivity", "Media image is null")
            imageProxy.close()
        }
    }

    companion object {
        private var onScan: ((barcodes: List<Barcode>) -> Unit)? = null

        fun startScanner(context: Context, onScan: (barcodes: List<Barcode>) -> Unit) {
            this.onScan = onScan
            Intent(context, ScannerActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }

}