package com.example.bcmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.features2d.BOWImgDescriptorExtractor;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import kotlinx.coroutines.CoroutineScope;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity {

    /**
     * url
     */
    public static String IMAGE_URL = "http://104.197.171.112/dbimages/";
    public static String GET_IMAGE_URL = "http://104.197.171.112/get_image.php";

    /**
     * end
     */

    private ImageButton btn_click;
    private ImageButton btn_clickToCamera;
    private RecyclerView recyclerView;
    private CardRecyclerViewAdapter adapter;
    private ArrayList<CardRecyclerViewItem> cardList;

    private static final String TAG = "opencv";
    private static final int REQUEST_CODE_GALLERY = 200;

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean isOpenCvLoaded = false;

    private HttpConnection httpConn;

    public native void BlurImage(long inputImage, long outputImage);


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    static {
        System.loadLibrary("native-lib");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV is not loaded");
        } else {
            Log.d(TAG, "OpenCV is loaded successfully!");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hello Miyoung
        btn_click = findViewById(R.id.clickToGallery);
        btn_clickToCamera = findViewById(R.id.clickToCamera);
        recyclerView = findViewById(R.id.recyclerview);

        cardList = new ArrayList<>();

        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));
        cardList.add(new CardRecyclerViewItem(R.drawable.test_11));


        adapter = new CardRecyclerViewAdapter(getApplicationContext(), cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        btn_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "갤러리로이동", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, REQUEST_CODE_GALLERY);

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
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

        btn_clickToCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, 100);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test", "onActivityResult");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
//                    inputImage.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
                }
            }
        }
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {

                    assert data != null;
                    Uri dataUri = data.getData();


                    String filePath = getRealPathFromURI(dataUri);
                    String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                    InputStream in = getContentResolver().openInputStream(dataUri);

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    Log.d("사이즈", String.valueOf(img.getWidth()) + " "+ String.valueOf(img.getHeight()));

                    String date = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
                    File tempSelectFile = new File(getFilesDir().getPath(), date + "." + file_extn);
                    OutputStream out = new FileOutputStream(tempSelectFile);
                    img.compress(Bitmap.CompressFormat.JPEG, 40, out);

                    Bitmap image = BitmapFactory.decodeFile(tempSelectFile.getPath());
                    out.close();

                    Mat mat_img = new Mat();
                    Utils.bitmapToMat(image, mat_img);

                    Mat output = new Mat();

                    BlurImage(mat_img.getNativeObjAddr(), output.getNativeObjAddr());

                    if (output != null && mat_img != null) {
                        Bitmap bitmapOutput = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(output, bitmapOutput);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapOutput.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
                        intent.putExtra("image", byteArray);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    image.compress(Bitmap.CompressFormat.JPEG, 60, stream);
//                    byte[] byteArray = stream.toByteArray();
//
//                    Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
//                    intent.putExtra("image", byteArray);
//                    startActivity(intent);

//                    httpConn = new HttpConnection(new URL(IMAGE_URL));
//                    httpConn.requestInsertImage(tempSelectFile, new OnRequestCompleteListener() {
//                        @Override
//                        public void onSuccess(@org.jetbrains.annotations.Nullable String data) {
//                            Log.d("민선", "httpConnection onSuccess");
//                            try {
//                                ServerRequest serverRequest = new ServerRequest(new URL(GET_IMAGE_URL));
//                                serverRequest.getImage(String.valueOf(1), new OnRequestCompleteListener() {
//                                    @Override
//                                    public void onSuccess(@org.jetbrains.annotations.Nullable String data) {
//                                        JsonParser jsonParser = new JsonParser();
//                                        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
//                                        JsonArray jsonArray = (JsonArray) jsonObject.get("seoon53");
//
//                                        final JsonObject j = jsonArray.get(0).getAsJsonObject();
//                                        final String image = j.get("TEST_IMAGE").getAsString();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
//                                                intent.putExtra("image", image);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onError() {
//
//                                    }
//                                });
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onError() {
//                            Log.d("onError", "httpConnection onError");
//                        }
//                    });

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    img.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//
//
//                    Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
//                    intent.putExtra("image", byteArray);
//                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        }
    }


    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }
}

