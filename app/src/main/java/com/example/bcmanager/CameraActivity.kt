package com.example.bcmanager

import android.Manifest.permission.CAMERA
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.opencv.android.*
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.collections.ArrayList


class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    private val TAG = "opencv"
    private var matInput: Mat? = null
    private var matResult: Mat? = null
    var approxList: ArrayList<Point>? = null
    var resultt: FloatArray? = null

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    var mRgba: Mat? = null
    var mRgbaF: Mat? = null
    var mRgbaT: Mat? = null

    var llBottom: LinearLayout? = null

    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    var rotatedBitmap: Bitmap? = null;


    private var image: ImageView? = null
    private var button: FloatingActionButton? = null
    private var btnReject: FloatingActionButton? = null
    private var btnAccept: FloatingActionButton? = null
    private var bitmapImage: Bitmap? = null
    private external fun ConvertRGBtoGray(matAddrInput: Long, matAddrResult: Long): FloatArray
    private external fun ImageProcessing(output: Long)

    private var showPreviews = false

    private val writeLock: Semaphore = Semaphore(1);

    public fun getWriteLock() {
        writeLock.acquire();
    }

    public fun releaseWriteLock() {
        writeLock.release();
    }

    init {
        System.loadLibrary("opencv_java4")
        System.loadLibrary("native-lib")
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    button!!.setOnClickListener {
                        showPreviews = !showPreviews;
                        val tmpImage = matResult;
//                        bitmapImage = Bitmap.createBitmap(tmpImage!!.cols(), tmpImage.rows(), Bitmap.Config.RGB_565)
                        Log.d("단계", "1")
//                        val my: Vector<PointF> = Vector()
//                        my.add(PointF(resultt!!.get(0), resultt!!.get(1)))
//                        my.add(PointF(resultt!!.get(2), resultt!!.get(3)))
//                        my.add(PointF(resultt!!.get(4), resultt!!.get(5)))
//                        my.add(PointF(resultt!!.get(6), resultt!!.get(7)))


//                        val mat_img = Mat()
//                        Utils.bitmapToMat(bitmapImage, mat_img)
//
//
                        ImageProcessing(tmpImage!!.nativeObjAddr)
                        Log.d("단계", "1.1")
                        val resultBitmap =
                                Bitmap.createBitmap(tmpImage.cols(), tmpImage.rows(), Bitmap.Config.RGB_565)

                        if (tmpImage != null) {
                            Log.d("단계", "2")
                            Utils.matToBitmap(tmpImage, resultBitmap)

                            val matrix: Matrix = Matrix()
                            matrix.postRotate(90F)

                            rotatedBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap!!.getWidth(), resultBitmap!!.height, matrix, true)


                            image?.setImageBitmap(resultBitmap)

                            llBottom!!.visibility = View.VISIBLE
                            button!!.hide()
                            mOpenCvCameraView!!.disableView()
                        }


                    }
                    btnAccept!!.setOnClickListener {

                        val stream = ByteArrayOutputStream()
                        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                        Log.d("흐음3", rotatedBitmap!!.width.toString() + rotatedBitmap!!.height)
                        val byteArray = stream.toByteArray()
                        Log.d("13", "1");

//                        val lnth: Int = rotatedBitmap!!.getByteCount()
//                        val dst: ByteBuffer = ByteBuffer.allocate(lnth)
//                        rotatedBitmap!!.copyPixelsToBuffer(dst)
//                        val barray: ByteArray = dst.array()

                        val intent = Intent(applicationContext, ConfirmCapture::class.java)
                        intent.putExtra("image", byteArray)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    btnReject!!.setOnClickListener {
                        showAcceptedRejectedButton(false)
                    }
//
//                    mOpenCvCameraView!!.enableView();
//                    mOpenCvCameraView!!.setOnTouchListener(this@CameraActivity);
//                    mOpenCvCameraView!!.setCvCameraViewListener(this@CameraActivity);
                    mOpenCvCameraView!!.enableView()
                }
//
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null)
            mOpenCvCameraView!!.disableView();
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView?.disableView()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "카메라"

        approxList = ArrayList()
        resultt = FloatArray(8)

        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE;
        mOpenCvCameraView?.setCvCameraViewListener(this);
        mOpenCvCameraView?.setCameraIndex(0); // front-camera(1),  back-camera(0)

        image = findViewById(R.id.ivBitmap)
        button = findViewById(R.id.btnCapture)
        llBottom = findViewById(R.id.llBottom)
        btnReject = findViewById(R.id.btnReject)
        btnAccept = findViewById(R.id.btnAccept)

//        button!!.setOnClickListener {
//            val tmpImage = matResult;
//            bitmapImage = Bitmap.createBitmap(tmpImage!!.cols(), tmpImage.rows(), Bitmap.Config.RGB_565)
//            Utils.matToBitmap(tmpImage, bitmapImage)
//            image?.setImageBitmap(bitmapImage)
//            llBottom!!.visibility = View.VISIBLE
//            button!!.hide()
//            mOpenCvCameraView!!.disableView()
//        }

//        btnAccept!!.setOnClickListener {
//            mOpenCvCameraView!!.disableView()
//            //바로 디비에 넣어버려
////            val intent = Intent(applicationContext, ConfirmCapture::class.java)
////            intent.putExtra("image", bitmapImage)
////            setResult(Activity.RESULT_OK, intent)
////            finish()
//        }
//        btnReject!!.setOnClickListener {
//            showAcceptedRejectedButton(false)
//
//        }


//        val button1: Button = findViewById(R.id.button);
//        button1.setOnClickListener {
////
////            val tmpImage = matResult;
////            val bitmapOutput = Bitmap.createBitmap(tmpImage!!.cols(), tmpImage.rows(), Bitmap.Config.RGB_565)
////            image!!.setImageBitmap(bitmapOutput)
////            Utils.matToBitmap(tmpImage, bitmapOutput)
////            Log.d("테스트","테스트");
////            val intent = Intent()
////            intent.putExtra("image", bitmapOutput)
////            setResult(Activity.RESULT_OK, intent);
////            mOpenCvCameraView!!.disableView()
////            Log.d("테스트","테스트2");
////            finish()
////
////
//            try {
//                getWriteLock();
//                val path: File = File(this.getExternalFilesDir(null)?.absolutePath + "/Images/");
//                path.mkdirs();
//                val file: File = File(path, "image.png");
//                val filename = file.toString();
//                Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGB, 4);
//
//                val bitmapOutput = Bitmap.createBitmap(matResult!!.cols(), matResult!!.rows(), Bitmap.Config.RGB_565)
//                Utils.matToBitmap(matResult, bitmapOutput)
//
//                val intent = Intent(applicationContext, ConfirmCapture::class.java)
//                intent.putExtra("image", bitmapOutput)
//                setResult(Activity.RESULT_OK, intent);
//                mOpenCvCameraView!!.disableView()
//
//                val ret = Imgcodecs.imwrite(filename, matResult);
//                if (ret) Log.d(TAG, "SUCESS");
//                else Log.d(TAG, "FAIL");
//                val mediaScanIntent: Intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaScanIntent.setData(Uri.fromFile(file));
//                sendBroadcast(mediaScanIntent);
//
//                finish();
//            } catch (e: InterruptedException) {
//                e.printStackTrace();
//            }
//            releaseWriteLock();
//        }


    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
        mRgbaF = Mat(height, width, CvType.CV_8UC4)
        mRgbaT = Mat(width, width, CvType.CV_8UC4)
        Log.d("카메라", "onCameraViewStarted")
    }

    override fun onCameraViewStopped() {
        Log.d("카메라", "onCameraViewStopped")

    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        if (!showPreviews) {
            matInput = inputFrame!!.rgba()
            if (matResult == null) matResult = Mat(matInput!!.rows(), matInput!!.cols(), matInput!!.type())
            resultt = ConvertRGBtoGray(matInput!!.getNativeObjAddr(), matResult!!.getNativeObjAddr())

        }

        return matResult as Mat

    }


    protected fun getCameraViewList(): MutableList<CameraBridgeViewBase?> {
        return Collections.singletonList(mOpenCvCameraView)
    }

    private fun showAcceptedRejectedButton(value: Boolean) {
        if (value) {
            finish()
        } else {
            showPreviews = !showPreviews
            mOpenCvCameraView!!.enableView()
            button!!.show()
            image!!.visibility = View.GONE
            llBottom!!.visibility = View.GONE
            mOpenCvCameraView!!.setVisibility(SurfaceView.VISIBLE)
            mOpenCvCameraView?.setCameraIndex(0);
        }
    }

    //여기서부턴 퍼미션 관련 메소드
    private val CAMERA_PERMISSION_REQUEST_CODE = 200


    protected fun onCameraPermissionGranted() {
        val cameraViews = getCameraViewList() ?: return
        for (cameraBridgeViewBase in cameraViews) {
            cameraBridgeViewBase?.setCameraPermissionGranted()
        }
    }

    override fun onStart() {
        super.onStart()
        var havePermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                havePermission = false
            }
        }
        if (havePermission) {
            onCameraPermissionGranted()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(requestCode: Int?, permissions: Array<String?>?, grantResults: IntArray?) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults!!.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted()
        } else {
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
        }
        super.onRequestPermissionsResult(requestCode!!, permissions!!, grantResults!!)
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@CameraActivity)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("예", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, id: Int) {
                requestPermissions(arrayOf(CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        })
        builder.setNegativeButton("아니오", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface?, arg1: Int) {
                finish()
            }
        })
        builder.create().show()
    }


}
