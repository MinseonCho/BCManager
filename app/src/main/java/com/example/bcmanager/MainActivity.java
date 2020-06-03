package com.example.bcmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private String TAG_ = "MainAcitivity";
    /**
     * url
     */
    public static String IMAGE_URL = "http://104.197.171.112/dbimages/";
    public static String GET_IMAGE_URL = "http://104.197.171.112/get_image.php";
    public static String SIGNUP_URL = "http://104.197.171.112/signup.php";
    public static String GET_CARDS_INFO = "http://104.197.171.112/get_cards.php";
    public static String GET_CARD_INFO = "http://104.197.171.112/get_card.php";
    /**
     * end
     */

    private ImageButton btn_click;
    private ImageButton btn_clickToCamera;
    private RecyclerView recyclerView;
    private CardRecyclerViewAdapter adapter;
    private TextView cnt_text;
    private TextView noCard_text;
    private TextView card_text;
    private TextView welcome;
    private LinearLayout linearLayout;
    private ImageView actionbar_btn;
    private ArrayList<CardInfoItem.cardInfo> cardsList;

    private static final String TAG = "opencv";
    private static final int REQUEST_CODE_GALLERY = 200;

    public static HttpConnection httpConn;
    public static Context mContext;

    Handler mHandler = null;
    static int cnt = 0;

    public static int device_width = 0, device_height = 0;

    //user
    FirebaseUser user;
    public static String uID;
    public static boolean isLogined = false;

    public native void RecognitionCard(long inputImage, long outputImage);


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
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        android.app.ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
//            actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));
//        }

        mContext = this;


        //actionbar title 가운데
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.actionbar_title); // 커스텀 사용할 파일 위치
        getSupportActionBar().setTitle("BCManager");


        btn_click = findViewById(R.id.clickToGallery);
        btn_clickToCamera = findViewById(R.id.clickToCamera);
        recyclerView = findViewById(R.id.recyclerview);
        linearLayout = findViewById(R.id.layout_for_thread);
        cnt_text = findViewById(R.id.cnt_card);
        noCard_text = findViewById(R.id.textview_noCard);
        card_text = findViewById(R.id.textview_registerdCard);
        actionbar_btn = findViewById(R.id.actionbar_btn);
        welcome = findViewById(R.id.welcome_text);


        checkCurrentUser();
        getAppKeyHash();


        //handler
        mHandler = new Handler();

        //recyclerview
        cardsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);

        //Display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        device_width = size.x;
        device_height = size.y;
        Log.d("디바이스 가로 = ", String.valueOf(device_width));
        Log.d("디바이스 세로 = ", String.valueOf(device_height));

        if (isLogined) {
            getCardInfo();
        }

        if (cardsList.isEmpty()) {
            card_text.setVisibility(View.GONE);
            noCard_text.setVisibility(View.VISIBLE);
        }

        //btn event
        btn_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "갤러리로이동", Toast.LENGTH_SHORT).show();


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

        actionbar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        if (cnt > 0) {
            linearLayout.setVisibility(View.VISIBLE);
            cnt_text.setText(cnt + " 개의 명함을 인식 중 입니다!");
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
                                    if (cnt > 0) {
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText(cnt + " 개의 명함을 인식 중 입니다!");
                                    } else {
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

                            RecognitionCard(mat_img.getNativeObjAddr(), output.getNativeObjAddr());
                            cnt--;

                            if (!output.empty()) {

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

                                    if (cnt > 0) {
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText(cnt + " 개의 명함을 인식 중 입니다!");
                                    } else {
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
                                    if (cnt > 0) {
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText(cnt + " 개의 명함을 인식 중 입니다!");
                                    } else {
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

                            double image_height = image.getHeight();
                            double image_width = image.getWidth();
                            double tmps = image_width / device_width;
                            int dst_height = (int) (image_height / (image_width / device_width));
                            Log.d("tmps", String.valueOf(tmps));
                            Log.d("image_height", String.valueOf(image_height));
                            Log.d("image_width", String.valueOf(image_width));
                            Log.d("dst_height", String.valueOf(dst_height));
                            image = Bitmap.createScaledBitmap(image, device_width, dst_height, true);

                            Mat mat_img = new Mat();
                            Utils.bitmapToMat(image, mat_img);

                            Mat output = new Mat();

                            RecognitionCard(mat_img.getNativeObjAddr(), output.getNativeObjAddr());

                            Log.d("눌", output.toString());
                            Log.d("눌", String.valueOf(output.empty()));
                            if (!output.empty()) {

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
                                    if (cnt > 0) {
                                        linearLayout.setVisibility(View.VISIBLE);
                                        cnt_text.setText(cnt + " 개의 명함을 인식 중 입니다!");
                                    } else {
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        if (isLogined) getMenuInflater().inflate(R.menu.menu_login, menu);
//        else getMenuInflater().inflate(R.menu.menu, menu);
//
//        return true;
//    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        if (isLogined) inflater.inflate(R.menu.menu_login, popup.getMenu());
        else inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(getIntent());
                isLogined = false;
                return true;
            case R.id.menu_userinfo:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d("쇼팝업","onOptionsItemSelected");
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.menu_login:
//                startActivity(new Intent(this, LoginActivity.class));
//                return true;
//            case R.id.menu_logout:
//                FirebaseAuth.getInstance().signOut();
//                finish();
//                startActivity(getIntent());
//                isLogined = false;
//                return true;
//            case R.id.menu_userinfo:
//                startActivity(new Intent(this, UserProfileActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    public void checkCurrentUser() {
        // [START check_current_user]
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                // User is signed in
                Log.d("INFOuserName", user.getDisplayName() + "");
                Log.d("INFOemail", user.getEmail() + "");
                Log.d("INFOuserphonenumber", user.getPhoneNumber() + "");
                Log.d("INFOUID", user.getUid() + "");
                Log.d("INFOphotourl", String.valueOf(user.getPhotoUrl()) + "");
                uID = user.getUid();
                isLogined = true;
                welcome.setVisibility(View.VISIBLE);
                welcome.setText(user.getDisplayName() +" 님 \n명함을 등록해보세요!");
            } else {
                welcome.setVisibility(View.GONE);
                // No user is signed in
                Log.d(TAG_, "null");
            }
        }
        // [END check_current_user]
    }

    void getCardInfo() {

        try {
            httpConn = new HttpConnection(new URL(GET_CARDS_INFO));
            httpConn.requestGetCards(uID, new OnRequestCompleteListener() {
                @Override
                public void onSuccess(@org.jetbrains.annotations.Nullable String data) {

                    if (data != null && !data.isEmpty()) {
                        Log.d("성공", data);
                        Gson gson = new GsonBuilder().create();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
                        JsonArray jsonArray = (JsonArray) jsonObject.get("cardInfo");

                        CardInfoItem.cardInfo result;
                        for (int i = 0; i < jsonArray.size(); i++) {

                            final JsonObject j = jsonArray.get(i).getAsJsonObject();
                            result = gson.fromJson(j, CardInfoItem.cardInfo.class);

                            cardsList.add(result);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                card_text.setVisibility(View.VISIBLE);
                                noCard_text.setVisibility(View.GONE);
                                adapter = new CardRecyclerViewAdapter(MainActivity.this, cardsList);

                                recyclerView.setAdapter(adapter);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                card_text.setVisibility(View.GONE);
                                noCard_text.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}

