package com.example.bcmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageOCRActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyDjGdug_MhnO4hY4HI2C5Ejq9KvaK_Wlfo";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = ImageOCRActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private ImageView cardImage;
    private TextView  info_name;
    private TextView info_positon;
    private TextView  info_company;
    private TextView  info_phone;
    private TextView info_email;
    private TextView  info_number;
    private TextView   info_address;
    private TextView   info_fax;
    private TextView   info_memo;
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
    private static String temp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_o_c_r);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.actionbar_title_nobtn); // 커스텀 사용할 파일 위치
        getSupportActionBar().setTitle("BCManager");


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

        textlist.clear();
        ph = "";
        nm = "";
        ad = "";
        em = "";
        nb = "";
        fx = "";
        po = "";
        temp = "";

        if( intent != null){
            byte[] bytes = intent.getByteArrayExtra("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            cardImage.setImageBitmap(bitmap);
            uploadImage(bitmap);
            //Log.d("image사이즈", bitmap.width.toString() + " " +bitmap.height.toString())
        }

    }

    public void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            // scale the image to save on bandwidth
//                Bitmap bitmap =
//                        scaleBitmapDown(
//                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
//                                MAX_DIMENSION);

            callCloudVision(bitmap);
//                cardImage.setImageBitmap(bitmap);

        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

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
                imageDetail.setText(result);
                phone_number.setText(ph);
                nameDetail.setText(nm);
                addressDetail.setText(ad);
                emailDetail.setText(em);
                numberDetail.setText(nb);
                faxDetail.setText(fx);
                positionDetail.setText(po);
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

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

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
                "남양주시", "의정부시", "동두천시", "포천시", "안산시", "양주시", "강릉시", "동해시", "삼척시","속초시","원주시",
                "춘천시","태백시","제천시","청주시","충주시","계릉시","공주시","논산시","당진시","보령시","서산시","아산시","천안시",
                "경산시","경주시","구미시","김천시","문경시","삼주시","안동시","영주시","영천시","포항시","거제시","김해시","밀양시",
                "사천시","양산시","진주시","창원시","통영시","군산시","김제시","남원시","익산시","전주시","정읍시","목포시",
                "광양시","나주시","순천시","여수시","서귀포시","제주시"));

        Log.d(TAG, String.valueOf(city_address));

        city_number.addAll(Arrays.asList("02","051","053","032","062","042","052","044","031","033","043","041","063","061","054","055","064"));
        job_position.addAll(Arrays.asList("회장","부회장","사장","부사장","전무","상무","부장","차장","대리","과장","사원","팀장","이사","교수","대표","대표이사","점장","지점장"));

        int plus=0;
        for(int i=0;i<message.length();i++){
            if(message.charAt(i) != '\n') {
                for (int j = i; ; j++) {
                    if(message.charAt(j) != 32 && message.charAt(j) != '\n')
                        text += message.charAt(j);
                    if(message.charAt(j) == '\n') {
                        i=j;
                        break;
                    }
                }
            }
            textlist.add(text);
            Log.d(TAG,textlist.get(plus++));
            text = "";
        }

        for(int i = 0; i<textlist.size();i++) {

            if (textlist.get(i).contains("010"))
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        ph += textlist.get(i).charAt(j);
                    }
                }

             else if (textlist.get(i).contains("@")) {
                if(em.length() < 2) {
                    em = textlist.get(i);

                    if(textlist.get(i).contains("."))
                        em = em.replace(".", "");

                    if(textlist.get(i).contains("email"))
                    em = em.replace("email", "");
                    else if(textlist.get(i).contains("Email"))
                    em = em.replace("Email", "");
                    else if(textlist.get(i).contains("E-Mail"))
                        em = em.replace("E-Mail", "");
                    else if(textlist.get(i).contains("E-mail"))
                        em = em.replace("E-mail", "");
                    else if(textlist.get(i).contains("이메일:"))
                        em = em.replace("이메일:", "");
                }
            }

            else if (textlist.get(i).contains(".com")){
               if(em.length() < 2) {
                   em = textlist.get(i);
//                   textlist = textlist.replaceAll(em.);
               }
            }

            else if (textlist.get(i).contains("F.")) {
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        fx += textlist.get(i).charAt(j);
                    }
                }
            }
             else if (textlist.get(i).contains("FAX")) {
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        fx += textlist.get(i).charAt(j);
                    }
                }
            }
              else if (textlist.get(i).contains("Fax")) {
                        for (int j = 0; j < textlist.get(i).length(); j++) {
                            if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                                fx += textlist.get(i).charAt(j);
                            }
                        }
                    }
        }

        Log.d(TAG,ph);
        Log.d(TAG,nm);
        Log.d(TAG,em);

        loop:
        for(int i = 0; i<textlist.size();i++) {

            if (textlist.get(i).length() == 3)
                nm = textlist.get(i);

            else if (textlist.get(i).length() == 5) {
                if(nm.length() < 2) {
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
            else if (textlist.get(i).length() == 7) {
                if(nm.length() < 2) {
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

        for(int i = 0; i<textlist.size();i++) {

            for (int j = 0; j < city_address.size(); j++) {

                if (textlist.get(i).contains(city_address.get(j)))
                    ad = textlist.get(i);
            }

            for (int j = 0; j < job_position.size(); j++) {
                if(po.length() < 2) {
                    if (textlist.get(i).contains(job_position.get(j)))
                        po = job_position.get(j);
                }
            }
        }
        Log.d(TAG,ad);

        loop:
        for(int i = 0; i<textlist.size();i++){

            for(int j = 0; j<city_number.size();j++){

                if(textlist.get(i).contains(city_number.get(j))) {

                    for (int k = 0; k < textlist.get(i).length(); k++) {
                        if (textlist.get(i).charAt(k) >= 48 && textlist.get(i).charAt(k) <= 57) {
                            nb += textlist.get(i).charAt(k);
                        }
                    }
                    break loop;
                }

            }
        }
        Log.d(TAG,nb);
        Log.d(TAG,fx);

        return message.toString();
    }
}