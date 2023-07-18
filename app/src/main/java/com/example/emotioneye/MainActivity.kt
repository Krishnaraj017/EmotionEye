@file:Suppress("DEPRECATION")

package com.example.emotioneye

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class MainActivity : AppCompatActivity() {
    private var cameraButton: Button? = null
    private var image: FirebaseVisionImage? = null
    private var detector: FirebaseVisionFaceDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing our firebase in the main activity
        FirebaseApp.initializeApp(this)

        // finding the elements by their IDs allotted.
        cameraButton = findViewById<Button>(R.id.camera_button)

        // setting an onclick listener to the button to request image capture using the camera
        cameraButton?.setOnClickListener {
            // creating a new intent for opening the camera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                // if the image is not captured, display an error toast
                Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap?
            detectFace(bitmap)
        }
    }
    private fun detectFace(bitmap: Bitmap?) {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap!!)
            detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        detector?.detectInImage(image!!)
            ?.addOnSuccessListener { faces ->
                var resultText = ""
                var i = 1
                for (face in faces) {
                    resultText += """
                        FACE NUMBER $i:
                        Smile: ${(face.smilingProbability * 100)}%
                        Left Eye Open: ${(face.leftEyeOpenProbability * 100)}%
                        Right Eye Open: ${(face.rightEyeOpenProbability * 100)}%
                        
                    """.trimIndent()
                    i++
                }

                if (faces.isEmpty()) {
                    Toast.makeText(this@MainActivity, "NO FACE DETECT", Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle()
                    bundle.putString(LCOFaceDetection.RESULT_TEXT, resultText)
                    val resultDialog = ResultDialog()
                    resultDialog.arguments = bundle
                    resultDialog.isCancelable = true
                    resultDialog.show(supportFragmentManager, LCOFaceDetection.RESULT_DIALOG)
                }
            }
            ?.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 124
    }
}
