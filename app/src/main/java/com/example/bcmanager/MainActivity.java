package com.example.bcmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.FileNotFoundException;
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
    private TextView cnt_text;
    private LinearLayout linearLayout;
    private ArrayList<CardRecyclerViewItem> cardList;

    private static final String TAG = "opencv";
    private static final int REQUEST_CODE_GALLERY = 200;

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean isOpenCvLoaded = false;

    private HttpConnection httpConn;

    Handler mHandler = null;
    static int cnt = 0;

    public static Context mContext;


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

        btn_click = findViewById(R.id.clickToGallery);
        btn_clickToCamera = findViewById(R.id.clickToCamera);
        recyclerView = findViewById(R.id.recyclerview);
        linearLayout = findViewById(R.id.layout_for_thread);
        cnt_text = findViewById(R.id.cnt_card);

        mContext = this;

        //handler
        mHandler = new Handler();

        //recyclerview
        cardList = new ArrayList<>();

        cardList.add(new CardRecyclerViewItem(R.drawable.green_card));
        cardList.add(new CardRecyclerViewItem(R.drawable.gray_card));
        cardList.add(new CardRecyclerViewItem(R.drawable.green_card));
        cardList.add(new CardRecyclerViewItem(R.drawable.gray_card));

        adapter = new CardRecyclerViewAdapter(getApplicationContext(), cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        //btn event
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


        if(cnt > 0){
            linearLayout.setVisibility(View.VISIBLE);
            cnt_text.setText( cnt + " 개의 명함을 인식 중 입니다!");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("test", "onActivityResult");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Intent datas = data;

                    final Thread thread_ = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Looper.prepare();
                            MessageQueue messageQueue = Looper.myQueue();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //ui 작업 수행o
                                    cnt++;
                                    if(cnt > 0){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText( cnt + " 개의 명함을 인식 중 입니다!");
                                    }
                                    else{
                                        linearLayout.setVisibility(View.GONE);
                                    }
                                }
                            });

                            byte[] bytes = datas.getByteArrayExtra("image");
                            Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Log.d("result", String.valueOf(result.getWidth()) + String.valueOf(result.getHeight()));
//                    inputImage.setImageBitmap((Bitmap) data.getParcelableExtra("image"));

                            Mat mat_img = new Mat();
                            Utils.bitmapToMat(result, mat_img);
                            Log.d("result_mat_img", String.valueOf(mat_img.rows()) + String.valueOf(mat_img.cols()));
                            Mat output = new Mat();

                            BlurImage(mat_img.getNativeObjAddr(), output.getNativeObjAddr());
                            cnt--;
                            if (output != null && mat_img != null) {

                                Bitmap bitmapOutput = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
                                Utils.matToBitmap(output, bitmapOutput);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmapOutput.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
                                intent.putExtra("image", byteArray);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //ui 작업 수행o

                                    if(cnt > 0){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText( cnt + " 개의 명함을 인식 중 입니다!");
                                    }
                                    else{
                                        linearLayout.setVisibility(View.GONE);
                                    }

                                }
                            });
                            Looper.loop();
                        }
                    });
                    thread_.start();
                }
            }
        }
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                final Uri dataUri = data.getData();

                final Thread thread = new Thread((new Runnable() {
                    @Override
                    public void run() {
                        //ui 작업 수행x
                        Looper.prepare();
                        MessageQueue messageQueue = Looper.myQueue();
                        try {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //ui 작업 수행o
                                    cnt++;
                                    if(cnt > 0){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText( cnt + " 개의 명함을 인식 중 입니다!");
                                    }
                                    else{
                                        linearLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                            String filePath = getRealPathFromURI(dataUri);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                            InputStream in = null;
                            if (dataUri != null)
                                in = getContentResolver().openInputStream(dataUri);

                            Bitmap img = BitmapFactory.decodeStream(in);
                            in.close();

                            Log.d("사이즈", String.valueOf(img.getWidth()) + " " + String.valueOf(img.getHeight()));

                            String date = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
                            File tempSelectFile = new File(getFilesDir().getPath(), date + "." + file_extn);
                            OutputStream out = new FileOutputStream(tempSelectFile);
                            img.compress(Bitmap.CompressFormat.JPEG, 40, out);

                            Bitmap image = BitmapFactory.decodeFile(tempSelectFile.getPath());
                            out.close();

                            Log.d("image사이즈", String.valueOf(image.getWidth()) + " " + String.valueOf(image.getHeight()));
//                    Bitmap test = getResizedBitmap(image, 1500,843);
//                    Log.d("test사이즈", String.valueOf(test.getWidth()) + " "+ String.valueOf(test.getHeight()));

                            Mat mat_img = new Mat();
                            Utils.bitmapToMat(image, mat_img);

                            Mat output = new Mat();

                            BlurImage(mat_img.getNativeObjAddr(), output.getNativeObjAddr());
                            if (output != null && mat_img != null) {

                                Bitmap bitmapOutput = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
                                Utils.matToBitmap(output, bitmapOutput);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmapOutput.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                Intent intent = new Intent(getApplicationContext(), ImageOCRActivity.class);
                                intent.putExtra("image", byteArray);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //ui 작업 수행o
                                    cnt--;
                                    if(cnt > 0){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText( cnt + " 개의 명함을 인식 중 입니다!");
                                    }
                                    else{
                                        linearLayout.setVisibility(View.GONE);
                                    }

                                }
                            });
                            Looper.loop();
                        }
                        //image resize
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }));
                thread.start();

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    test.compress(Bitmap.CompressFormat.JPEG, 60, stream);
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

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

