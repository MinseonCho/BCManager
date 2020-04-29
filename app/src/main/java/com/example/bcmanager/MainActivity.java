package com.example.bcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.features2d.BOWImgDescriptorExtractor;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean isOpenCvLoaded = false;

    //test
    private ImageView inputImage;
    private ImageView outputImage;
    private Button btn_click;

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    public native void BlurImage(long inputImage, long outputImage);


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }
    static {
        System.loadLibrary("native-lib");

        if (!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is not loaded");
        }else{
            Log.d(TAG, "OpenCV is loaded successfully!");
        }
    }


//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    mOpenCvCameraView.enableView();
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hello Miyoung

        inputImage = findViewById(R.id.inputimage);
        outputImage = findViewById(R.id.outputimage);
        btn_click = findViewById(R.id.click);

        inputImage.setImageResource(R.drawable.test_11);

        btn_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("테스트","테스트");
                //메소드를 불러서 결과를 outputImage에 저장.
                try {
                    Log.d("테스트","테스트2");
                    InputStream is = getAssets().open("test_11.jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    Mat image = new Mat();
                    Utils.bitmapToMat(bitmap, image);
                    Mat output = new Mat();

                    BlurImage(image.getNativeObjAddr(), output.getNativeObjAddr());

                    Bitmap bitmapOutput = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(output, bitmapOutput);
                    Bitmap bitmapInput = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(image, bitmapInput);
                    outputImage.setImageBitmap(bitmapOutput);
                    inputImage.setImageBitmap(bitmapInput);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        setContentView(R.layout.activity_main);
//
//        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
//        } else {
//            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
    }


    public void onDestroy() {
        super.onDestroy();

//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        if (matResult == null)

            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

        return matResult;
    }

//    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
//        return Collections.singletonList(mOpenCvCameraView);
//    }


    //여기서부턴 퍼미션 관련 메소드
//    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
//
//
//    protected void onCameraPermissionGranted() {
//        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
//        if (cameraViews == null) {
//            return;
//        }
//        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
//            if (cameraBridgeViewBase != null) {
//                cameraBridgeViewBase.setCameraPermissionGranted();
//            }
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        boolean havePermission = true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//                havePermission = false;
//            }
//        }
//        if (havePermission) {
//            onCameraPermissionGranted();
//        }
//    }
//
//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            onCameraPermissionGranted();
//        }else{
//            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private void showDialogForPermission(String msg) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
//        builder.setTitle("알림");
//        builder.setMessage(msg);
//        builder.setCancelable(false);
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id){
//                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//            }
//        });
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                finish();
//            }
//        });
//        builder.create().show();
//    }
}
