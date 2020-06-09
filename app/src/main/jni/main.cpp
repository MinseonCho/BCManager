#include <jni.h>
#include "com_example_bcmanager_MainActivity.h"

#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

static Point2f pts[4];
static Point2f result_pts[4];
Point2f dst_pts[4];
float maxWidth;
float maxHeight;

void filter(Mat img, Mat &dst, Mat mask);

float resize(Mat img_src, Mat &img_resize, int resize_width);

bool compareContourAreas(std::vector<cv::Point> contour1, std::vector<cv::Point> contour2);

void calculate_points(Mat &output, int flag);

void recognition_card_first(Mat &input, Mat &output); //first step for card recognition
void recognition_card_sec(Mat &input, Mat &output); //second step for card recognition
void recognition_card_third(Mat &input, Mat &output);

extern "C" {

JNIEXPORT jint JNICALL Java_com_example_bcmanager_CameraActivity_ConvertRGBtoGray(
        JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {
    int area = 0, cnt = 0;
    // TODO: implement BlurImage()

    Mat &input = *(Mat *) matAddrInput;
    CV_Assert(input.data);
    Mat &output = *(Mat *) matAddrResult;
    vector<int>::iterator it;

    jfloatArray result;
    jfloat fill[8];
    result = (*env).NewFloatArray(8);

    LOGD("%d : input.cols", input.cols);
    LOGD("%d : input.rows", input.rows);
    input.convertTo(output, CV_8UC3);
    Mat tmp;
    Mat grayInput;
    cvtColor(input, grayInput, COLOR_RGB2GRAY);

    resize(grayInput, grayInput, Size(0, 0), 0.5, 0.5, INTER_AREA);

    Canny(grayInput, tmp, 100, 200, 3, false);
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(tmp, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point());

    vector<Point> test;
    sort(contours.begin(), contours.end(), compareContourAreas);
    int size = contours.size();
    test = contours[size - 1];


    vector<Point> approx;

    approxPolyDP(Mat(test), approx, arcLength(Mat(test), true) * 0.02, true);


    int tmp_area = int(((grayInput.rows * grayInput.cols) * 0.1));

    if (fabs(contourArea(Mat(approx))) > tmp_area) {
        int size = approx.size();
        //Contour를 근사화한 직선을 그린다.
        if (size == 4) {
            for (int i = 0; i < 4; i++) {
                approx[i].x *= 2;
                approx[i].y *= 2;
                pts[i] = approx[i];
            }

//            int i = 0;
//            int j = 0;
//            while (i < 8) {
//                if (i % 2 == 0) fill[i] = approx[j].x;
//                if (i % 2 != 0) {
//                    fill[i] = approx[j].y;
//                    j++;
//                }
//                i++;
//            }

//            (*env).SetFloatArrayRegion(result, 0, 8, fill);

//            rectangle(output, approx[0]*2, approx[2]*2,Scalar(104, 212, 160),6);
            polylines(output, approx, true, Scalar(104, 212, 160), 3, LINE_AA);
//            line(output, approx[0] * 2, approx[approx.size() - 1] * 2, Scalar(104, 212, 160), 8);
//            for (int k = 0; k < size - 1; k++)
//                line(output, approx[k] * 2, approx[k + 1] * 2, Scalar(104, 212, 160), 8);

            return 100;
        }
    }
//    (*env).SetFloatArrayRegion(result, 0, 4, fill);

    LOGD("%d : output.cols", output.cols);
    LOGD("%d : output.rows", output.rows);

    return 101;


}

JNIEXPORT void JNICALL
Java_com_example_bcmanager_MainActivity_RecognitionCard(JNIEnv *env, jobject thiz,
                                                        jlong input_image,
                                                        jlong output_image) {
    LOGD("1", "1");

    Mat &input = *(Mat *) input_image;
    Mat &output = *(Mat *) output_image;

    recognition_card_first(input, output);

}

JNIEXPORT void JNICALL
Java_com_example_bcmanager_CameraActivity_ImageProcessing(JNIEnv *env, jobject thiz, jlong output) {
    vector<Point2f> tmp;
    Mat &output_ = *(Mat *) output;

    LOGD("값값 = %f, %f", pts[0].x, pts[0].y);

    pts[0].x += 5; pts[0].y +=5;
    pts[1].x += 5; pts[1].y +=5;
    pts[2].x += 5; pts[2].y +=5;
    pts[3].x += 5; pts[3].y +=5;

    calculate_points(output_, 2);
}
}

void recognition_card_first(Mat &input, Mat &output) {

    Mat grayInput;

    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    input.convertTo(output, CV_8UC3);
    LOGD("3 = %d, %d", input.cols, input.rows);
    cvtColor(input, grayInput, COLOR_RGB2GRAY);
    LOGD("4 = %d, %d", output.cols, output.rows);

    double sigmaColor = 10.0;
    double sigmaSpace = 30.0;


    //blur image
    Mat blured_image;
    GaussianBlur(grayInput, blured_image, Size(5, 5), 0);

    Mat bilateraledImage;
    bilateralFilter(blured_image, bilateraledImage, -1, sigmaColor, sigmaSpace);
    LOGD("4.1 = %d, %d", bilateraledImage.cols, bilateraledImage.rows);

    Canny(bilateraledImage, tmp, 50, 200, 3, false); // tmp = canny 결과
    LOGD("5 = %d, %d", tmp.cols, tmp.rows);

    Mat closed_img;
    Matx<uchar, 3, 3> mask;
    mask << 0, 1, 0,
            1, 1, 1,
            0, 1, 0;

    morphologyEx(tmp, closed_img, MORPH_CLOSE, mask, Point(-1, -1), 1);


    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(closed_img, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point());
    LOGD("6", "1");


    vector<vector<Point>> bigvalues;
    sort(contours.begin(), contours.end(), compareContourAreas);

    int i = 0;
    int j = contours.size() - 1;
    while (i < 5) {
        bigvalues.push_back(contours[j]);
        if (i == contours.size() - 1) break;
        j--;
        i++;
    }


    vector<Point> approx, result_approx;

    for (int j = 0; j < i; j++) {
        approxPolyDP(Mat(bigvalues[j]), approx, arcLength(Mat(bigvalues[j]), true) * 0.04, true);
        if (approx.size() == 4) {
            if (fabs(contourArea(Mat(approx))) > int(((input.rows * input.cols) * 0.1))) {
                result_approx = approx;
                break;
            }
        }
    }
    if (result_approx.size() == 4) {

        for (int i = 0; i < 4; i++) {
            pts[i] = result_approx[i];
        }
        calculate_points(output, 1);

    } else {
        recognition_card_sec(input, output);
    }
}

void recognition_card_sec(Mat &input, Mat &output) {

    LOGD("recognition_card_sec");
    Mat grayInput;

    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    input.convertTo(output, CV_8UC3);
    cvtColor(input, grayInput, COLOR_RGB2GRAY);

    Canny(grayInput, tmp, 75, 200, 3, false); // tmp = canny 결과
    Mat closed_img;
    Matx<uchar, 3, 3> mask;
    mask << 0, 1, 0,
            1, 1, 1,
            0, 1, 0;

    morphologyEx(tmp, closed_img, MORPH_CLOSE, mask, Point(-1, -1), 3);

    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(closed_img, contours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point());

    vector<vector<Point>> bigvalues;
    sort(contours.begin(), contours.end(), compareContourAreas);
    int i = 0;
    int j = contours.size() - 1;
    while (i < 5) {
        bigvalues.push_back(contours[j]);
        if (i == contours.size() - 1) break;
        j--;
        i++;
    }

    vector<Point> approx, result_approx;

    for (int z = 0; z < i; z++) {
        approxPolyDP(Mat(bigvalues[z]), approx, arcLength(Mat(bigvalues[z]), true) * 0.04, true);
        if (approx.size() == 4) {
            if (fabs(contourArea(Mat(approx))) > int(((input.rows * input.cols) * 0.1))) {
                result_approx = approx;
                break;
            }
        }
    }

    if (result_approx.size() == 4) {
        for (int k = 0; k < 4; k++) {
            pts[k] = result_approx[k];
        }
        calculate_points(output, 1);

    } else {
        LOGD(" fail");
        Mat nullOutput;
        output = nullOutput;
//        recognition_card_third(input, output);
    }

}

void recognition_card_third(Mat &input, Mat &output) {

    LOGD("recognition_card_third");
    Mat grayInput;

    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    input.convertTo(output, CV_8UC3);
    cvtColor(input, grayInput, COLOR_RGB2GRAY);

    double sigmaColor = 20.0;
    double sigmaSpace = 40.0;

    Mat bilateraledImage;
    bilateralFilter(grayInput, bilateraledImage, -1, sigmaColor, sigmaSpace);
    Canny(bilateraledImage, tmp, 75, 200, 3, false); // tmp = canny 결과
    Mat closed_img;
    Matx<uchar, 3, 3> mask;
    mask << 0, 1, 0,
            1, 1, 1,
            0, 1, 0;

    morphologyEx(tmp, closed_img, MORPH_CLOSE, mask, Point(-1, -1), 3);

    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(closed_img, contours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point());

    vector<vector<Point>> bigvalues;
    sort(contours.begin(), contours.end(), compareContourAreas);
    int i = 0;
    int j = contours.size() - 1;
    while (i < 5) {
        bigvalues.push_back(contours[j]);
        if (i == contours.size() - 1) break;
        j--;
        i++;
    }

    vector<Point> approx, result_approx;

    for (int z = 0; z < i; z++) {
        approxPolyDP(Mat(bigvalues[z]), approx, arcLength(Mat(bigvalues[z]), true) * 0.04, true);
        if (approx.size() == 4) {
            if (fabs(contourArea(Mat(approx))) > int(((input.rows * input.cols) * 0.1))) {
                result_approx = approx;
                break;
            }
        }
    }

    if (result_approx.size() == 4) {
        for (int k = 0; k < 4; k++) {
            pts[k] = result_approx[k];
        }
        calculate_points(output, 1);

    } else {
        LOGD(" fail");
        Mat nullOutput;
        output = nullOutput;
    }
}

void filter(Mat img, Mat &dst, Mat mask) {
    dst = Mat(img.size(), CV_32F, Scalar(0));
    Point h_m = mask.size() / 2;

    for (int i = h_m.y; i < img.rows - h_m.y; i++) {
        for (int j = h_m.x; j < img.cols - h_m.y; j++) {

            float sum = 0;
            for (int u = 0; u < mask.rows; u++) {
                for (int v = 0; v < mask.cols; v++) {
                    int y = i + u - h_m.y;
                    int x = j + v - h_m.x;
                    sum += mask.at<float>(u, v) * img.at<uchar>(y, x); //회선수식
                }
            }

            dst.at<float>(i, j) = sum;
        }
    }

    dst.convertTo(dst, CV_8U);
}

float resize(Mat img_src, Mat &img_resize, int resize_width) {
    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}


// comparison function object
bool compareContourAreas(std::vector<cv::Point> contour1, std::vector<cv::Point> contour2) {
    double i = fabs(contourArea(cv::Mat(contour1)));
    double j = fabs(contourArea(cv::Mat(contour2)));
    //cout << "i = "<< i << "   j = " << j << endl;
    return (i < j);
}

void calculate_points(Mat &output, int flag) {

    int sum_min = 0, sum_max = 0, diff_min = 0, diff_max = 0, result;
    float sum[4], diff[4];

    for (int i = 0; i < 4; i++) {
        sum[i] = pts[i].x + pts[i].y;
        diff[i] = (pts[i].y - pts[i].x);
        /*float tmp = pts[i].x + pts[i].y;
        if (sum_min > tmp) {
            sum_min = i;
        }*/
    }
    for (int i = 1; i < 4; i++) {
        if (sum[sum_max] < sum[i]) sum_max = i;
        if (sum[sum_min] > sum[i]) sum_min = i;
        if (diff[diff_max] < diff[i]) diff_max = i;
        if (diff[diff_min] > diff[i]) diff_min = i;
    }


    result_pts[0] = pts[sum_min]; //top-left
    result_pts[2] = pts[sum_max]; //bottom-right
    result_pts[1] = pts[diff_min]; //top-right
    result_pts[3] = pts[diff_max]; //bottom-left

    if(flag == 2){
        int plusNum = 3;
        result_pts[0].x += plusNum; result_pts[0].y += plusNum;
        result_pts[1].x -= plusNum; result_pts[1].y += plusNum;
        result_pts[2].x -= plusNum; result_pts[2].y -= plusNum;
        result_pts[3].x += plusNum; result_pts[3].y -= plusNum;
    }

    Point2f topLeft = result_pts[0];
    Point2f topRight = result_pts[1];
    Point2f bottomRight = result_pts[2];
    Point2f bottomLeft = result_pts[3];

    float w1 = fabs(bottomRight.x - bottomLeft.x);
    float w2 = fabs(topRight.x - topLeft.x);
    float h1 = fabs(topRight.y - bottomRight.y);
    float h2 = fabs(topLeft.y - bottomLeft.y);
    maxWidth = max(w1, w2);
    maxHeight = max(h1, h2);
    LOGD("단계 칼큘레이트 %f, %f", maxWidth, maxHeight);

    Point2f dst_pts[4] = {
            Point2f(0, 0), Point2f(maxWidth - 1, 0), Point2f(maxWidth - 1, maxHeight - 1),
            Point2f(0, maxHeight - 1)
    };
    Mat perspect_mat = getPerspectiveTransform(result_pts, dst_pts);
    warpPerspective(output, output, perspect_mat, Size((int(maxWidth)), (int(maxHeight))),
                    INTER_CUBIC);
    LOGD("단계 칼큘레이트 %d, %d", output.cols, output.rows);

}
