package com.example.bcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean isOpenCvLoaded = false;

    //test
    private ImageView inputImage;
    private ImageView outputImage;
    private Button btn_click;
    private Button btn_clickToCamera;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hello Miyoung

        inputImage = findViewById(R.id.inputimage);
        outputImage = findViewById(R.id.outputimage);
        btn_click = findViewById(R.id.click);
        btn_clickToCamera = findViewById(R.id.clickToCamera);
        inputImage.setImageResource(R.drawable.test_11);

        btn_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("테스트","테스트");
                //메소드를 불러서 결과를 outputImage에 저장.
                try {
                    Log.d("테스트","테스트2");
                    InputStream is = getAssets().open("card2.jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    Mat image = new Mat();
                    Utils.bitmapToMat(bitmap, image);
                    Log.d("채널", String.valueOf(image.channels()));
                    Log.d("타입", String.valueOf(image.depth()));

                    Mat output = new Mat();

                    BlurImage(image.getNativeObjAddr(), output.getNativeObjAddr());

                    if(output != null && image != null) {
                        Bitmap bitmapOutput = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(output, bitmapOutput);
                        Bitmap bitmapInput = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(image, bitmapInput);
                        outputImage.setImageBitmap(bitmapOutput);
                        inputImage.setImageBitmap(bitmapInput);
                    }
                    Log.d("output채널", String.valueOf(output.channels()));
                    Log.d("output타입", String.valueOf(output.depth()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        btn_clickToCamera.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });


    }


}
