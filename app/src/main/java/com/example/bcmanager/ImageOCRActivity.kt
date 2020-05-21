package com.example.bcmanager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequest
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class ImageOCRActivity : AppCompatActivity() {


    /**
     * Google Vision API
     */
//    private val CLOUD_VISION_API_KEY = "AIzaSyBack0yYSdaawly84Kazy6nsttfKulyY-c"
//    val FILE_NAME = "temp.jpg"
//    private val ANDROID_CERT_HEADER = "X-Android-Cert"
//    private val ANDROID_PACKAGE_HEADER = "X-Android-Package"
//
//    private val TAG_ = ImageOCRActivity::class.java.simpleName
//    private val GALLERY_PERMISSIONS_REQUEST = 0
//    private val GALLERY_IMAGE_REQUEST = 1
//    val CAMERA_PERMISSIONS_REQUEST = 2
//    val CAMERA_IMAGE_REQUEST = 3
//    private val MAX_LABEL_RESULTS = 10
//    private val MAX_DIMENSION = 1200

    var cardImage: ImageView? = null
    var info_name: EditText? = null
    var info_positon: EditText? = null
    var info_company: EditText? = null
    var info_phone: EditText? = null
    var info_email: EditText? = null
    var info_number: EditText? = null
    var info_address: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_o_c_r)

        cardImage = findViewById(R.id.card_image)
        info_name = findViewById(R.id.name)
        info_positon = findViewById(R.id.position)
        info_company = findViewById(R.id.company)
        info_phone = findViewById(R.id.phone)
        info_email = findViewById(R.id.email)
        info_number = findViewById(R.id.number)
        info_address = findViewById(R.id.address)


        val intent = intent
        if( intent != null){
            val bytes = intent.getByteArrayExtra("image");
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size);
            cardImage?.setImageBitmap(bitmap)
            Log.d("image사이즈", bitmap.width.toString() + " " +bitmap.height.toString())
        }

    }

////    fun startGalleryChooser() {
////        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
////            val intent = Intent()
////            intent.type = "image/*"
////            intent.action = Intent.ACTION_GET_CONTENT
////            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
////                    GALLERY_PERMISSIONS_REQUEST)
////        }
////    }
//
////    fun startCamera() {
////        if (PermissionUtils.requestPermission(
////                        this,
////                        CAMERA_PERMISSIONS_REQUEST,
////                        Manifest.permission.READ_EXTERNAL_STORAGE,
////                        Manifest.permission.CAMERA)) {
////            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////            val photoUri: Uri = FileProvider.getUriForFile(this, applicationContext.packageName.toString() + ".provider", getCameraFile())
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////            startActivityForResult(intent, CAMERA_IMAGE_REQUEST)
////        }
////    }
//
////    fun getCameraFile(): File? {
////        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
////        return File(dir, FILE_NAME)
////    }
//
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
////            uploadImage(data.data)
////        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
////            val photoUri: Uri = FileProvider.getUriForFile(this, applicationContext.packageName.toString() + ".provider", getCameraFile())
////            uploadImage(photoUri)
////        }
////    }
//
////    fun onRequestPermissionsResult(
////            requestCode: Int, @NonNull permissions: Array<String?>?, @NonNull grantResults: IntArray?) {
////        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
////        when (requestCode) {
////
////            CAMERA_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
////                startCamera()
////            }
////            GALLERY_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
////                startGalleryChooser()
////            }
////        }
////    }
//
////    fun uploadImage(uri: Uri?) {
////        if (uri != null) {
////            try {
////                // scale the image to save on bandwidth
////                val bitmap = scaleBitmapDown(
////                        MediaStore.Images.Media.getBitmap(contentResolver, uri),
////                        MainActivity.MAX_DIMENSION)
////                callCloudVision(bitmap)
////                mMainImage.setImageBitmap(bitmap)
////            } catch (e: IOException) {
////                Log.d(MainActivity.TAG, "Image picking failed because " + e.message)
////                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show()
////            }
////        } else {
////            Log.d(MainActivity.TAG, "Image picker gave us a null image.")
////            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show()
////        }
////    }
//
//    @Throws(IOException::class)
//    private fun prepareAnnotationRequest(bitmap: Bitmap): Vision.Images.Annotate {
//        val httpTransport: HttpTransport = AndroidHttp.newCompatibleTransport()
//        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
//        val requestInitializer: VisionRequestInitializer = object : VisionRequestInitializer(CLOUD_VISION_API_KEY) {
//            /**
//             * We override this so we can inject important identifying fields into the HTTP
//             * headers. This enables use of a restricted cloud platform API key.
//             */
//            @Throws(IOException::class)
//            protected override fun initializeVisionRequest(visionRequest: VisionRequest<*>) {
//                super.initializeVisionRequest(visionRequest)
//                val packageName = packageName
//                visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName)
//                val sig: String = PackageManagerUtils.getSignature(packageManager, packageName)
//                visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig)
//            }
//        }
//        val builder: Vision.Builder = Vision.Builder(httpTransport, jsonFactory, null)
//        builder.setVisionRequestInitializer(requestInitializer)
//        val vision: Vision = builder.build()
//        val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
//        batchAnnotateImagesRequest.setRequests(object : ArrayList<AnnotateImageRequest?>() {
//            init {
//                val annotateImageRequest = AnnotateImageRequest()
//
//                // Add the image
//                val base64EncodedImage = Image()
//                // Convert the bitmap to a JPEG
//                // Just in case it's a format that Android understands but Cloud Vision
//                val byteArrayOutputStream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
//                val imageBytes = byteArrayOutputStream.toByteArray()
//
//                // Base64 encode the JPEG
//                base64EncodedImage.encodeContent(imageBytes)
//                annotateImageRequest.setImage(base64EncodedImage)
//
//                // add the features we want
//                annotateImageRequest.setFeatures(object : ArrayList<Feature?>() {
//                    init {
//                        val labelDetection = Feature()
//                        labelDetection.setType("TEXT_DETECTION")
//                        labelDetection.setMaxResults(MAX_LABEL_RESULTS)
//                        add(labelDetection)
//                    }
//                } as List<Feature>?)
//
//                // Add the list of one thing to the request
//                add(annotateImageRequest)
//            }
//        })
//        val annotateRequest: Vision.Images.Annotate = vision.images().annotate(batchAnnotateImagesRequest)
//        // Due to a bug: requests to Vision API containing large images fail when GZipped.
//        annotateRequest.setDisableGZipContent(true)
//        Log.d(TAG_, "created Cloud Vision request object, sending request")
//        return annotateRequest
//    }
//
//    private class LableDetectionTask internal constructor(activity: MainActivity, annotate: Vision.Images.Annotate) : AsyncTask<Any?, Void?, String>() {
//        private val mActivityWeakReference: WeakReference<MainActivity>
//        private val mRequest: Vision.Images.Annotate
//        protected override fun doInBackground(vararg params: Any?): String? {
//            try {
//                Log.d(TAG_, "created Cloud Vision request object, sending request")
//                val response: BatchAnnotateImagesResponse = mRequest.execute()
//                return convertResponseToString(response)
//            } catch (e: GoogleJsonResponseException) {
//                Log.d(TAG_, "failed to make API request because " + e.getContent())
//            } catch (e: IOException) {
//                Log.d(TAG_, "failed to make API request because of other IOException " +
//                        e.message)
//            }
//            return "Cloud Vision API request failed. Check logs for details."
//        }
//
//        override fun onPostExecute(result: String) {
//            val activity = mActivityWeakReference.get()
//            if (activity != null && !activity.isFinishing) {
//                val imageDetail: TextView = activity.findViewById(R.id.image_details)
//                imageDetail.text = result
//            }
//        }
//
//        init {
//            mActivityWeakReference = WeakReference(activity)
//            mRequest = annotate
//        }
//    }
//
//    private fun callCloudVision(bitmap: Bitmap) {
//        // Switch text to loading
//        mImageDetails.setText(R.string.loading_message)
//
//        // Do the real work in an async task, because we need to use the network anyway
//        try {
//            val labelDetectionTask: LableDetectionTask = LableDetectionTask(this, prepareAnnotationRequest(bitmap))
//            labelDetectionTask.execute()
//        } catch (e: IOException) {
//            Log.d(MainActivity.TAG, "failed to make API request because of other IOException " +
//                    e.message)
//        }
//    }
//
//    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
//        val originalWidth = bitmap.width
//        val originalHeight = bitmap.height
//        var resizedWidth = maxDimension
//        var resizedHeight = maxDimension
//        if (originalHeight > originalWidth) {
//            resizedHeight = maxDimension
//            resizedWidth = (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
//        } else if (originalWidth > originalHeight) {
//            resizedWidth = maxDimension
//            resizedHeight = (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
//        } else if (originalHeight == originalWidth) {
//            resizedHeight = maxDimension
//            resizedWidth = maxDimension
//        }
//        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
//    }
//
//    private fun convertResponseToString(response: BatchAnnotateImagesResponse): String? {
//        val message = StringBuilder("I found these things:\n\n")
//        val labels: List<EntityAnnotation> = response.getResponses().get(0).getLabelAnnotations()
//        if (labels != null) {
//            for (label in labels) {
//                message.append(java.lang.String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()))
//                message.append("\n")
//            }
//        } else {
//            message.append("nothing")
//        }
//        return message.toString()
//    }
}
