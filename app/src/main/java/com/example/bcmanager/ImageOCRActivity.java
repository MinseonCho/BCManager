package com.example.bcmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Data;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;


import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class ImageOCRActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyAorR7rj2NM5euIUYoTZvguZz1KPxBNXNI";
    public static String CARD_INPUT = "http://pmy0420.cafe24.com/card_input.php";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    private BCMApplication myApp;

    private static final String TAG = ImageOCRActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private ImageView cardImage;
    private TextView info_name;
    private TextView info_positon;
    private TextView info_company;
    private TextView info_phone;
    private TextView info_email;
    private TextView info_number;
    private TextView info_address;
    private TextView info_fax;
    private TextView info_memo;
    private static ArrayList<String> textlist = new ArrayList<String>();
    private static ArrayList<String> city_address = new ArrayList<String>();
    private static ArrayList<String> city_number = new ArrayList<String>();
    private static ArrayList<String> job_position = new ArrayList<String>();
    private static String ph;
    private static String nm;
    private static String ad;
    private static String em;
    private static String nb;
    private static String fx;
    private static String po;
    private static String cp;
    private static String memo;
    private static String temp;
    private static String ocrusernum;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_o_c_r);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.actionbar_title_nobtn); // 커스텀 사용할 파일 위치
        getSupportActionBar().setTitle("BCManager");
        findViewById(R.id.ocrbtn).setOnClickListener((mClickListener));

        myApp = (BCMApplication) getApplication(); //user정보 가져오기

        cardImage = findViewById(R.id.card_image);
        info_name = findViewById(R.id.name);
        info_positon = findViewById(R.id.position);
        info_company = findViewById(R.id.company);
        info_phone = findViewById(R.id.phone);
        info_email = findViewById(R.id.email);
        info_number = findViewById(R.id.number);
        info_address = findViewById(R.id.address);
        info_fax = findViewById(R.id.fax);
        info_memo = findViewById(R.id.memo);

        Intent intent = getIntent();
        ocrusernum = myApp.userNum;
        Log.d("TAG", "MYAPPUSERID확인" + ocrusernum);


        textlist.clear();
        ph = "";
        nm = "";
        ad = "";
        em = "";
        nb = "";
        fx = "";
        po = "";
        cp = "";
        temp = "";


        if (intent != null) {
            byte[] bytes = intent.getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            cardImage.setImageBitmap(bitmap);
            callCloudVision(bitmap);
//            uploadImage(bitmap);
            //Log.d("image사이즈", bitmap.width.toString() + " " +bitmap.height.toString())
        }

    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

           String date = new SimpleDateFormat("yy_MM_dd_hh_mm_ss").format(Calendar.getInstance().getTime());


            String fileName = myApp.userNum + "_" + date + "." + "jpg";
            File tempSelectFile = new File(getFilesDir().getPath(), fileName);
            Log.d("getFilesDir확인", getFilesDir().getPath());

            OutputStream out = null;
            try {
                out = new FileOutputStream(tempSelectFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            nm = info_name.getText().toString();
            ph = info_phone.getText().toString();
            ad = info_address.getText().toString();
            em = info_email.getText().toString();
            nb = info_number.getText().toString();
            fx = info_fax.getText().toString();
            po = info_positon.getText().toString();
            memo = info_memo.getText().toString();
            cp = info_company.getText().toString();

//            InsertData task = new InsertData();
//            task.execute(CARD_INPUT,nm,ph,ad,em,nb,fx,po,memo,cp,ocrusernum);

            CardInfoItem.detectedCardInfo tmpResult = new CardInfoItem.detectedCardInfo(tempSelectFile.getName(), nm, po, ph, nb, fx, ad, cp, em, memo);
            HttpConnection httpConnection = null;
            Log.d("httpConnection _", tmpResult.getCARD_IMAGE());
            Log.d("httpConnection __", tempSelectFile.getName());
            Log.d("httpConnection __", tmpResult.getCARD_NAME());
            try {
                httpConnection = new HttpConnection(new URL(MainActivity.INSERT_CARD_INFOS_FROM_CAMERA));
                httpConnection.requestInsertInfos(tempSelectFile, myApp.userNum, tmpResult, new OnRequestCompleteListener() {
                    @Override
                    public void onSuccess(@Nullable String data) {
                        if (data != null && !data.isEmpty()) {
                            Log.d("httpConnectiononSuccess", data);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });

                        }
                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
//            sendFTP("TEST");

        }
    };

//    public void uploadImage(Bitmap bitmap) {
//        if (bitmap != null) {
//            // scale the image to save on bandwidth
////                Bitmap bitmap =
////                        scaleBitmapDown(
////                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
////                                MAX_DIMENSION);
//
//            callCloudVision(bitmap);
////                cardImage.setImageBitmap(bitmap);
//
//        } else {
//            Log.d(TAG, "Image picker gave us a null image.");
//            //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
//        }
//    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("TEXT_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<ImageOCRActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(ImageOCRActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            ImageOCRActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.memo);
                TextView phone_number = activity.findViewById(R.id.phone);
                TextView nameDetail = activity.findViewById(R.id.name);
                TextView addressDetail = activity.findViewById(R.id.address);
                TextView emailDetail = activity.findViewById(R.id.email);
                TextView numberDetail = activity.findViewById(R.id.number);
                TextView faxDetail = activity.findViewById(R.id.fax);
                TextView positionDetail = activity.findViewById(R.id.position);
                TextView companyDetail = activity.findViewById(R.id.company);
                imageDetail.setText(result);
                phone_number.setText(ph);
                nameDetail.setText(nm);
                addressDetail.setText(ad);
                emailDetail.setText(em);
                numberDetail.setText(nb);
                faxDetail.setText(fx);
                positionDetail.setText(po);
                companyDetail.setText(cp);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        info_memo.setText("loading");

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

//    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
//
//        int originalWidth = bitmap.getWidth();
//        int originalHeight = bitmap.getHeight();
//        int resizedWidth = maxDimension;
//        int resizedHeight = maxDimension;
//
//        if (originalHeight > originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
//        } else if (originalWidth > originalHeight) {
//            resizedWidth = maxDimension;
//            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
//        } else if (originalHeight == originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = maxDimension;
//        }
//        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
//    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();
            //          for (EntityAnnotation label : labels) {
            //              message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
            //              message.append("\n");
            //          }
        } else {
            message = "nothing";
        }

        String text = "";
        city_address.addAll(Arrays.asList("서울특별시", "인천광역시", "대구광역시", "울산광역시", "부산광역시", "광주광역시", "대전광역시",
                "부천시", "시흥시", "고양시", "성남시", "파주시", "화성시", "수원시", "안성시", "평택시", "과천시", "안양시",
                "광주시", "여주시", "이천시", "용인시", "오산시", "의왕시", "광명시", "군포시", "김포시", "구리시", "하남시",
                "남양주시", "의정부시", "동두천시", "포천시", "안산시", "양주시", "강릉시", "동해시", "삼척시", "속초시", "원주시",
                "춘천시", "태백시", "제천시", "청주시", "충주시", "계릉시", "공주시", "논산시", "당진시", "보령시", "서산시", "아산시", "천안시",
                "경산시", "경주시", "구미시", "김천시", "문경시", "삼주시", "안동시", "영주시", "영천시", "포항시", "거제시", "김해시", "밀양시",
                "사천시", "양산시", "진주시", "창원시", "통영시", "군산시", "김제시", "남원시", "익산시", "전주시", "정읍시", "목포시",
                "광양시", "나주시", "순천시", "여수시", "서귀포시", "제주시"));

        Log.d(TAG, String.valueOf(city_address));

        city_number.addAll(Arrays.asList("051", "053", "032", "062", "042", "052", "044", "031", "033", "043", "041", "063", "061", "054", "055", "064", "02"));
        job_position.addAll(Arrays.asList("학회장","회장", "부회장", "사장", "부사장", "전무", "상무", "부장", "차장", "대리", "과장", "사원", "팀장",
                "교수", "대표", "대표이사", "점장", "지점장","개발자","프로그래머","CEO","ceo","이사"));

        int plus = 0;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) != '\n') {
                for (int j = i; ; j++) {
                    if (message.charAt(j) != 32 && message.charAt(j) != '\n')
                        text += message.charAt(j);
                    if (message.charAt(j) == '\n') {
                        i = j;
                        break;
                    }
                }
            }
            textlist.add(text);
            Log.d(TAG, textlist.get(plus++));
            text = "";
        }

        for (int i = 0; i < textlist.size(); i++) {

            int phindex;

            if (textlist.get(i).contains("010")) {
                phindex = textlist.get(i).indexOf("010");
                for (int j = phindex; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        if (ph.length() < 11)
                            ph += textlist.get(i).charAt(j);
                        else
                            break;

                    }
                }
            }
//                if (ph.contains("."))
//                    ph = ph.replace(".", "-");



            if (textlist.get(i).contains("@")) {
                if (em.length() < 2) {
                    em = textlist.get(i);
                    int emlength = em.length();

                    if (textlist.get(i).contains("email."))
                        em = em.substring(em.lastIndexOf("email.")+1,emlength);
                    else if (textlist.get(i).contains("Email."))
                        em = em.substring(em.lastIndexOf("Email.")+1,emlength);
                    else if (textlist.get(i).contains("E-Mail."))
                        em = em.substring(em.lastIndexOf("E-Mail.")+1,emlength);
                    else if (textlist.get(i).contains("E-mail."))
                        em = em.substring(em.lastIndexOf("E-mail.")+1,emlength);
                    else if (textlist.get(i).contains("E-mail"))
//                        em =em.substring(em.lastIndexOf("E-mail")+1,emlength);
                        em = em.replace("E-mail", "");
                    else if (textlist.get(i).contains("이메일:"))
                        em =em.substring(em.lastIndexOf("이메일:")+1,emlength);
                    else if (textlist.get(i).contains("E."))
                        em = em.substring(em.lastIndexOf("E.")+1,emlength);
//                    else if (textlist.get(i).contains("E"))
//                        em = em.substring(em.lastIndexOf("E")+1,emlength);

                }
            } else if (textlist.get(i).contains(".com")) {
                if (em.length() < 2) {
                    em = textlist.get(i);
                }
            }
        }

        Log.d(TAG, ph);
        Log.d(TAG, nm);
        Log.d(TAG, em);

        fx = Fxdetection("F.", fx);
        fx = Fxdetection("FAX", fx);
        fx = Fxdetection("Fax", fx);

        loop:
        for (int i = 0; i < textlist.size(); i++) {

            if (textlist.get(i).length() == 3)
                nm = textlist.get(i);

            else if (textlist.get(i).length() == 5) {
                if (nm.length() < 2) {
                    for (int j = 0; j < job_position.size(); j++) {
                        if (textlist.get(i).contains(job_position.get(j))) {
                            temp = textlist.get(i);
                            temp = temp.replace(job_position.get(j), "");
                            nm = temp;
                            break loop;
                        }
                    }
                }
            } else if (textlist.get(i).length() == 7) {
                if (nm.length() < 2) {
                    for (int j = 0; j < job_position.size(); j++) {
                        if (textlist.get(i).contains(job_position.get(j))) {
                            temp = textlist.get(i);
                            temp = temp.replace(job_position.get(j), "");
                            nm = temp;
                            break loop;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < textlist.size(); i++) { //address

            for (int j = 0; j < city_address.size(); j++) {

                if (textlist.get(i).contains(city_address.get(j)))
                    ad = textlist.get(i);
            }

            for (int j = 0; j < job_position.size(); j++) { //position
                if (po.length() < 2) {
                    if (textlist.get(i).contains(job_position.get(j)))
                        po = job_position.get(j);
                }
            }
        }
        Log.d(TAG, ad);

        int telindex;
        loop:
        for (int i = 0; i < textlist.size(); i++) {
            for (int j = 0; j < city_number.size(); j++) {
                if (textlist.get(i).contains(city_number.get(j))) {
                    telindex = textlist.get(i).indexOf(city_number.get(j));
                    for (int k = telindex; k < textlist.get(i).length(); k++) {
                        if (nb.length() < 12)
                            nb += textlist.get(i).charAt(k);
                    }
                    if (nb.contains(")"))
                        nb = nb.replace(")", "-");
                    else if (nb.contains("."))
                        nb = nb.replace(".", "-");
                    break loop;
                }
            }
        }
        for (int i = 0; i < textlist.size(); i++) {
            if (textlist.get(i).length() <= 10) {
                if (textlist.get(i).contains(nm)) {}
                else {
                    if (cp.length() < 2) {
                        cp = textlist.get(i);
                    }
                }
            }
        }
        Log.d(TAG, "cp확인 " + cp);

        Log.d(TAG, nb);
        Log.d(TAG, fx);

        return message.toString();
    }

    private static String Fxdetection(String findstring, String detailstring) { //팩스번호 추출
        Log.d("TAG", "Fxdetection진입성공 ");
        int faxindex = 0;
        for (int i = 0; i < textlist.size(); i++) {
            if (textlist.get(i).contains(findstring)) {
                Log.d("TAG", "findstring = " + findstring);
                faxindex = textlist.get(i).lastIndexOf(findstring);
                Log.d("TAG", "faxindex = " + faxindex);
                for (int j = faxindex; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        detailstring += textlist.get(i).charAt(j);
                        Log.d("TAG", "faxindexdetail = " + detailstring);
                    }
                }
            }
        }
        return detailstring;
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ImageOCRActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //  mTextViewResult.setText("입력되었습니다.");
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String nm = (String) params[1];
            String ph = (String) params[2];
            String ad = (String) params[3];
            String em = (String) params[4];
            String nb = (String) params[5];
            String fx = (String) params[6];
            String po = (String) params[7];
            String memo = (String) params[8];
            String cp = (String) params[9];
            String ocrusernum = (String) params[10];

            String serverURL = (String) params[0];
            String postParameters = "&nm=" + nm + "&ph=" + ph + "&ad=" + ad + "&em=" + em + "&nb=" + nb + "&fx=" + fx + "&po=" + po + "&memo=" + memo + "&cp=" + cp + "&ocrusernum=" + ocrusernum;

            Log.d(TAG, "ddongmmong" + serverURL);
            Log.d(TAG, "ddongmmong" + postParameters);


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}