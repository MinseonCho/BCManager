package com.example.bcmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.ArrayList;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean isOpenCvLoaded = false;

    private ImageButton btn_click;
    private ImageButton btn_clickToCamera;
    private RecyclerView recyclerView;
    private CardRecyclerViewAdapter adapter;
    private ArrayList<CardRecyclerViewItem> cardList;


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
        Log.d("onCreate","onCreate");
        btn_click = findViewById(R.id.clickToGallery);
        btn_clickToCamera = findViewById(R.id.clickToCamera);
        recyclerView = findViewById(R.id.recyclerview);

        cardList = new ArrayList<>();

        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));

        Log.d("onCreate","cardList" + cardList.size());

        adapter = new CardRecyclerViewAdapter(getApplicationContext(), cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d("onCreate","miyoung");

        btn_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "갤러리로이동", Toast.LENGTH_SHORT).show();
                //갤러리로
                //메소드를 불러서 결과를 outputImage에 저장.
//                try {
//                    InputStream is = getAssets().open("card2.jpg");
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                    Mat image = new Mat();
//                    Utils.bitmapToMat(bitmap, image);
//                    Log.d("채널", String.valueOf(image.channels()));
//                    Log.d("타입", String.valueOf(image.depth()));
//
//                    Mat output = new Mat();
//
//                    BlurImage(image.getNativeObjAddr(), output.getNativeObjAddr());
//
//                    if(output != null && image != null) {
//                        Bitmap bitmapOutput = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
//                        Utils.matToBitmap(output, bitmapOutput);
//                        Bitmap bitmapInput = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
//                        Utils.matToBitmap(image, bitmapInput);
//                        outputImage.setImageBitmap(bitmapOutput);
//                        inputImage.setImageBitmap(bitmapInput);
//                    }
//                    Log.d("output채널", String.valueOf(output.channels()));
//                    Log.d("output타입", String.valueOf(output.depth()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        });

        btn_clickToCamera.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, 100);
            }
        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("test","onActivityResult");
//        if(resultCode == Activity.RESULT_OK){
//            if(requestCode == 100){
//                if (data != null) {
////                    inputImage.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
//                }
//            }
//        }
//    }
}
