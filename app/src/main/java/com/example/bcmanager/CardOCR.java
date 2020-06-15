package com.example.bcmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
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



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;

import static com.google.common.collect.ComparisonChain.start;

public class CardOCR extends Activity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyB3_sf4bXDPThjn5SYMGRpsfBgTaStKBcI";
    public static String CARD_INPUT = "http://104.197.171.112/precard_input2.php";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    public static AsyncResponse delegate = null;

    private static BCMApplication myApp;

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
    private static String fp;
    public static File fileCacheItem;
    public static Handler mHandler = null;
    private static final String TAG = "ddd";
    public Bitmap bitmap_;

    //    public static class CallKotlin{
//        public static void main(String[] args){
//            Fileinput fileinput = new Fileinput(); //thread2
//            fileinput.fileupload(fileCacheItem);
//        }
//
//    }
    public static String dd(AsyncResponse delegate) {
        Log.d("DD함수", "실행 ");
        Log.d(TAG, "ad확인db" + ad);
        Log.d(TAG, "nm확인db" + nm);
        Log.d(TAG, "fx확인db" + fx);
        Log.d(TAG, "cp확인db" + cp);
//        putData();
//        done = "1";
        String result = "0";


        InsertData task = new InsertData(delegate); //thread1
        result = task.execute(CARD_INPUT, nm, ph, ad, em, nb, fx, po, memo, cp, ocruserid, done, fn).toString();


//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                //MessageQueue messageQueue = Looper.myQueue();
//                Log.d(TAG, "threadsuccess");
//                Fileinput fileinput = new Fileinput(); //thread2
//                fileinput.fileupload(fileCacheItem);
//                Looper.loop();
//            }
//        });
//        thread.start();

        Log.d(TAG, "result확인 try안 = " + result);


//        if (result.isEmpty() || result.equals("") || result == null || result.contains("Error")) {
        if (result.equals("true")) {
            result = "1";
        } else {
            result = "0";
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
        this.bitmap_ = bitmap;
        fn = filename;
//        fp = filePath;
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
        fp = context.getFilesDir().getPath();
        Log.d(TAG, "FP 확인 할꺼 = " + fp);
//        File file = new File(fp);
//
//        // If no folders
//        if (!file.exists()) {
//            file.mkdirs();
//            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//        }
        fileCacheItem = new File(fp, fn);
        Log.d(TAG, "getFilesDir().getPath()확인할꺼 = " + fileCacheItem.toString());
        Drawable drawable = context.getResources().getDrawable(R.drawable.check, null);
        Bitmap tbitmap = ((BitmapDrawable)drawable).getBitmap();

        try {
//            fileCacheItem.createNewFile();
            OutputStream out = new FileOutputStream(fileCacheItem);
            tbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            Log.d("찾아보자", fileCacheItem.getName());
            Log.d("찾아보자", fileCacheItem.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "예외냐");
        } finally {

            Log.d(TAG, "trysuccess");
        }

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
        AsyncResponse delegate = null;

        LableDetectionTask(Context activity, Vision.Images.Annotate annotate, AsyncResponse delegate) {
            this.delegate = delegate;
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response, delegate);

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
                Log.d("테스트", result);
//                delegate.processFinish("true");

            }

        }


    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response, AsyncResponse delegate) {
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

        city_number.addAll(Arrays.asList("051", "053", "032", "062", "042", "052", "044", "031", "033", "043", "041", "063", "061", "054", "055", "064", "02"));
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

            int phindex;
            if (textlist.get(i).contains("010")) {
                phindex = textlist.get(i).indexOf("010");
                for (int j = phindex; j < textlist.get(i).length(); j++) {
                    ph += textlist.get(i).charAt(j);
                }
                if (ph.contains("."))
                    ph = ph.replace(".", "-");
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
                    else if (textlist.get(i).contains("E-mail"))
                        em = em.replace("E-mail", "");
                    else if (textlist.get(i).contains("이메일:"))
                        em = em.replace("이메일:", "");
                }
            } else if (textlist.get(i).contains(".com")) {
                if (em.length() < 2) {
                    em = textlist.get(i);
                }
            }
            Log.d(TAG, "em = " + em);


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
                Log.d(TAG, "cp위한 nm확인 = " + nm);
                Log.d(TAG, "po가뭣이냐" + po);
                if (textlist.get(i).contains(nm)) {
                } else {
                    if (cp.length() < 2) {
                        cp = textlist.get(i);
                        Log.d(TAG, "cp가뭣이냐" + cp);
//                        if(cp == po){ //po랑 cp랑 같을때가 문제임 -> 아직 cameraocr에 적용 안했음
//                            cp = "";
//                            Log.d(TAG, "cp위한 po확인3 = " + cp);
//                        }
                    }
                }
            }
        }
        Log.d(TAG, "cp확인 " + cp);
        Log.d(TAG, "nb값" + nb);
        Log.d(TAG, "fx = " + fx);
        done = "1";
        Log.d(TAG, "done값" + done);
        Log.d("convert어쩌구", "텍스트 추출 완료");
        String tmp = dd(delegate);
        if (tmp.equals("1")) {
            Log.d("convert어쩌구", "텍스트 저장 완료");
            return message.toString();
        } else {
            return message.toString();
        }
    }

    private static String Fxdetection(String findstring, String detailstring) { //휴대폰 & 팩스번호 추출
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

    static class InsertData extends AsyncTask<String, Void, String> {
        //       ProgressDialog progressDialog;
        public AsyncResponse delegate = null;

        int bytesAvailable, bufferSize, bytesRead;
        byte[] buffer;
        int maxBufferSize = 10 * 1024 *1024;
        String boundary = "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("InsertData", "onPreExecute");
//            progressDialog = ProgressDialog.show(CardOCR.this,
//                    "Please Wait", null, true, true);
        }

        public InsertData(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("InsertData", "onPostExecute");
//            Log.d("InsertData result  ", result.toString());

//            delegate.processFinish(result);

            delegate.processFinish(result);
            //  progressDialog.dismiss();
            //  mTextViewResult.setText("입력되었습니다.");
//            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {
            Log.d("InsertData", "doInBackground");
//            Log.d(TAG, "DB확인");
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

                FileInputStream fileInputStream = new FileInputStream(fileCacheItem);
                Log.d("파일", String.valueOf(fileCacheItem));
                Log.d("파일", fileCacheItem.getName());
                Log.d("파일", fileCacheItem.getPath());

//
//                String filePath = fileCacheItem.getPath();
//                Bitmap bitmapss = BitmapFactory.decodeFile(filePath);
//                Log.d("파일", String.valueOf(bitmapss.getByteCount()));
//                Log.d("파일", String.valueOf(bitmapss.getWidth()));
//                Log.d("파일", String.valueOf(bitmapss.getHeight()));

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

//                String boundary = "*****";
////                String lineEnd = "\r\n";
////                String twoHuphens = "--";
//                String boundary = UUID.randomUUID().toString();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
//                httpURLConnection.setRequestProperty("Content-Type: application/octet-stream");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                httpURLConnection.setRequestProperty("uploaded_file", fn);
                Log.d("파일fnfnfnfnfnfn", fn);
                httpURLConnection.connect();


                DataOutputStream request = new DataOutputStream(httpURLConnection.getOutputStream());
                request.writeBytes(twoHyphens + boundary + lineEnd);
//                request.write(postParameters.getBytes("UTF-8"));
                request.writeBytes(twoHyphens + boundary + lineEnd);
                request.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fn + "\""+ lineEnd);
//                request.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fn + "\"\r\n\r\n");
//                request.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fn + "\"" + "\r\n");
                request.writeBytes(lineEnd);
                request.writeBytes(fn);
                request.writeBytes(lineEnd);





//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();

                // create a buffer of  maximum size

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) { //buffer에 담기

                    request.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                request.writeBytes(lineEnd);
                request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                request.flush();
                request.close();


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
//                    Log.d("에러어어엉2", " 와일 몇번? : ");
                    sb.append(line);
//                    Log.d("에러어어엉2", sb.toString());
                }

//                Log.d("에러어어엉21", sb.toString());
                bufferedReader.close();
                Log.d("에러어어엉22", sb.toString());

                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}