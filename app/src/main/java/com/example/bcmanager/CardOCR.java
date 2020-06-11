package com.example.bcmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CardOCR extends Activity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCXyQ0R-emSegoNwWJnbZhFPPbm5rFUdzk";
    public static String CARD_INPUT = "http://104.197.171.112/precard_input.php";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    //  private BCMApplication myApp;

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
    private static String done;
    private static String temp;
    private static String ocruserid;
    private static String fn;

    private static final String TAG = "ddd";

    public static String dd() {
        Log.d(TAG, "DD함수");
        Log.d(TAG, "ad확인db" + ad);
        Log.d(TAG, "nm확인db" + nm);
        Log.d(TAG, "fx확인db" + fx);
        Log.d(TAG, "cp확인db" + cp);
//        putData();
//        done = "1";
        String result = "0";
        InsertData task = new InsertData();

        result = String.valueOf(task.execute(CARD_INPUT, nm, ph, ad, em, nb, fx, po, memo, cp, ocruserid, done, fn));

        Log.d(TAG, "result확인 try안 = " + result);


        if (result.isEmpty() || result.equals("") || result == null || result.contains("Error")) {
            //실패
            result = "0";
        } else {
            result = "1";
        }

        //        putData();
//        Log.d(TAG,"result확인 if 전 = "+result);
//        if(result != "0"){
////            result = "1";
        Log.d(TAG, "result확인db " + result);
//        }
        return result;
    }

//    public static String putData(){
//        Log.d("putData함수 확인",done);
//        return done;
//    }

    Context context;

    CardOCR(Context context, Bitmap bitmap, String userid, String filename) {
        this.context = context;
//        callCloudVision(bitmap);
        // myApp = (BCMApplication) getApplication();
        ocruserid = userid;
        fn = filename;
        Log.d(TAG, "USER체크" + userid);
        Log.d(TAG, "filename체크" + filename);
        textlist.clear();
        ph = "";
        nm = "";
        ad = "";
        em = "";
        nb = "";
        fx = "";
        po = "";
        cp = "";
        memo = null;
        temp = "";


    }



    //메인에서 인식 함수 실행 -> 결과 받고 -> dd함수 실행? 하면 될까?

    public Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
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

                        String packageName = context.getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);

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
        Log.d(TAG, "created Cloud Vision request object, sending request__prepareAnnotationRequest");

        return annotateRequest;
    }


    public static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<Context> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;
        public AsyncResponse delegate = null;

        LableDetectionTask(Context activity, Vision.Images.Annotate annotate,  AsyncResponse delegate) {
            this.delegate =delegate;
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
            Log.d(TAG, "onPostExecute 실행");
            Context activity = mActivityWeakReference.get();
            if (activity != null) {
                delegate.processFinish("true");
                Log.d("테스트", result);
            }

        }


    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";
        Log.d(TAG, "MESSAGE = " + message);

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();
            //          for (EntityAnnotation label : labels) {
            //              message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
            //              message.append("\n");
            //          }
        } else {
            message = "nothing";
            Log.d(TAG, "MESSAGE = " + message);
        }
        memo = message;

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

        city_number.addAll(Arrays.asList("02", "051", "053", "032", "062", "042", "052", "044", "031", "033", "043", "041", "063", "061", "054", "055", "064"));
        job_position.addAll(Arrays.asList("회장", "부회장", "사장", "부사장", "전무", "상무", "부장", "차장", "대리", "과장", "사원", "팀장", "이사", "교수", "대표", "대표이사", "점장", "지점장"));

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

            if (textlist.get(i).contains("010"))
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        ph += textlist.get(i).charAt(j);
                    }
                }

            if (textlist.get(i).contains("@")) {
                if (em.length() < 2) {
                    em = textlist.get(i);

                    if (textlist.get(i).contains("email."))
                        em = em.replace("email.", "");
                    else if (textlist.get(i).contains("Email."))
                        em = em.replace("Email.", "");
                    else if (textlist.get(i).contains("E-Mail."))
                        em = em.replace("E-Mail.", "");
                    else if (textlist.get(i).contains("E-mail."))
                        em = em.replace("E-mail.", "");
                    else if (textlist.get(i).contains("이메일:"))
                        em = em.replace("이메일:", "");
                }
            } else if (textlist.get(i).contains(".com")) {
                if (em.length() < 2) {
                    em = textlist.get(i);
                }
            }
            Log.d(TAG, "em = " + em);

            if (textlist.get(i).contains("F.")) {
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        fx += textlist.get(i).charAt(j);
                    }
                }
            } else if (textlist.get(i).contains("FAX")) {
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        fx += textlist.get(i).charAt(j);
                    }
                }
            } else if (textlist.get(i).contains("Fax")) {
                for (int j = 0; j < textlist.get(i).length(); j++) {
                    if (textlist.get(i).charAt(j) >= 48 && textlist.get(i).charAt(j) <= 57) {
                        fx += textlist.get(i).charAt(j);
                    }
                }
            }
        }

        Log.d(TAG, ph);
        Log.d(TAG, nm);
        Log.d(TAG, em);

        for (int i = 0; i < textlist.size(); i++) {

            if (textlist.get(i).length() <= 9) {
                if (cp.length() < 2) {
                    cp = textlist.get(i);
                }
            }
        }
        Log.d(TAG, "cp확인 " + cp);

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


        Log.d(TAG, "po확인 = " + po);

        for (int i = 0; i < textlist.size(); i++) {

            for (int j = 0; j < city_address.size(); j++) {

                if (textlist.get(i).contains(city_address.get(j)))
                    ad = textlist.get(i);
            }

            for (int j = 0; j < job_position.size(); j++) {
                if (po.length() < 2) {
                    if (textlist.get(i).contains(job_position.get(j)))
                        po = job_position.get(j);
                }
            }
        }
        Log.d(TAG, "ad확인 = " + ad);

        loop:
        for (int i = 0; i < textlist.size(); i++) {

            for (int j = 0; j < city_number.size(); j++) {

                if (textlist.get(i).contains(city_number.get(j))) {

                    for (int k = 0; k < textlist.get(i).length(); k++) {
                        if (textlist.get(i).charAt(k) >= 48 && textlist.get(i).charAt(k) <= 57) {
                            if (nb.length() < 10)
                                nb += textlist.get(i).charAt(k);
                        }
                    }
                    break loop;
                }

            }
        }

        Log.d(TAG, "nb값" + nb);
        Log.d(TAG, "fx = " + fx);
        done = "1";
        Log.d(TAG, "done값" + done);
        String tmp = dd();
        if(tmp.equals("1")){
            return message.toString();
        }else{
            return message.toString();
        }
    }

    static class InsertData extends AsyncTask<String, Void, String> {
        //       ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("에러어어엉2", "InsertData  onPreExecute");
//            progressDialog = ProgressDialog.show(CardOCR.this,
//                    "Please Wait", null, true, true);
        }


//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//          //  progressDialog.dismiss();
//            //  mTextViewResult.setText("입력되었습니다.");
//            Log.d(TAG, "POST response  - " + result);
//        }


        @Override
        protected String doInBackground(String... params) {
            Log.d("에러어어엉2", "InsertData  doInBackground");
            Log.d(TAG, "DB확인");
            String nm = (String) params[1];
            String ph = (String) params[2];
            String ad = (String) params[3];
            String em = (String) params[4];
            String nb = (String) params[5];
            String fx = (String) params[6];
            String po = (String) params[7];
            String memo = (String) params[8];
            String cp = (String) params[9];
            String ocruserid = (String) params[10];
            String done = (String) params[11];
            String fn = (String) params[12];

            String serverURL = (String) params[0];
            String postParameters = "&nm=" + nm + "&ph=" + ph + "&ad=" + ad + "&em=" + em + "&nb=" + nb + "&fx=" + fx + "&po=" + po + "&memo=" + memo + "&cp=" + cp + "&ocruserid=" + ocruserid + "&done=" + done + "&fn=" + fn;

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
                    Log.d("에러어어엉2", "HttpURLConnection.HTTP_OK");
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    Log.d("에러어어엉2", "HttpURLConnection.HTTP_NO");
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("에러어어엉2", " 와일 몇번? : ");
                    sb.append(line);
                    Log.d("에러어어엉2", sb.toString());
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
