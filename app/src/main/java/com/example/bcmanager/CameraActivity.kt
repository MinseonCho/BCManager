package com.example.bcmanager

import android.Manifest.permission.CAMERA
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import java.util.*


class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    private val TAG = "opencv"
    private var matInput: Mat? = null
    private var matResult: Mat? = null

    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    private external fun ConvertRGBtoGray(matAddrInput: Long, matAddrResult: Long)


    init {
        System.loadLibrary("opencv_java4")
        System.loadLibrary("native-lib")
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {

                    mOpenCvCameraView!!.enableView()
                }
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera)


        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE;
//        mOpenCvCameraView?.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView?.setCvCameraViewListener(this);
        mOpenCvCameraView?.setCameraIndex(0); // front-camera(1),  back-camera(0)
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        matInput = inputFrame!!.rgba()

        if (matResult == null) matResult = Mat(matInput!!.rows(), matInput!!.cols(), matInput!!.type())

        ConvertRGBtoGray(matInput!!.getNativeObjAddr(), matResult!!.getNativeObjAddr())

        return matResult as Mat
    }


    protected fun getCameraViewList(): MutableList<CameraBridgeViewBase?> {
        return Collections.singletonList(mOpenCvCameraView)
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
