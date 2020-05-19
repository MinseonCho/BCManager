#include <jni.h>
#include "com_example_bcmanager_MainActivity.h"

#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

Point2f pts[4];
Point2f result_pts[4];

void filter(Mat img, Mat &dst, Mat mask);

float resize(Mat img_src, Mat &img_resize, int resize_width);

bool compareContourAreas(std::vector<cv::Point> contour1, std::vector<cv::Point> contour2);

void calculate_points();

extern "C" {

JNIEXPORT void JNICALL Java_com_example_bcmanager_CameraActivity_ConvertRGBtoGray(
        JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {
    int area = 0, cnt = 0;
    // TODO: implement BlurImage()

    Mat &input = *(Mat *) matAddrInput;
    CV_Assert(input.data);
    Mat &output = *(Mat *) matAddrResult;
    vector<int>::iterator it;

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
    test = contours[size-1];


    vector<Point> approx;

    approxPolyDP(Mat(test), approx, arcLength(Mat(test), true) * 0.02, true);


    int tmp_area = int(((grayInput.rows * grayInput.cols) * 0.1));

    if (fabs(contourArea(Mat(approx))) > tmp_area){
        int size = approx.size();
        //Contour를 근사화한 직선을 그린다.
        if (size == 4) {
//            rectangle(output, approx[0], approx[2], Scalar(0, 255, 0),3);
            line(output, approx[0] *2, approx[approx.size() - 1]*2, Scalar(255, 102, 165), 8);
            for (int k = 0; k < size - 1; k++)
                line(output, approx[k]*2, approx[k + 1]*2, Scalar(255, 102, 165), 8);

        }
    }

    LOGD("%d : output.cols", output.cols);
    LOGD("%d : output.rows", output.rows);

}

JNIEXPORT void JNICALL Java_com_example_bcmanager_MainActivity_BlurImage(JNIEnv *env, jobject thiz,
                                                                         jlong input_image,
                                                                         jlong output_image) {
    LOGD("1", "1");
    int area = 0, cnt = 0;

    // TODO: implement BlurImage()

    Mat &input = *(Mat *) input_image;
    Mat &output = *(Mat *) output_image;
    LOGD("2 = %d, %d", input.cols, input.rows);

    Mat grayInput;

    resize(input, grayInput, Size(1500, 843), 0, 0, INTER_LANCZOS4);

    Mat tmp = Mat::zeros(grayInput.rows, grayInput.cols, CV_8UC3);
    grayInput.convertTo(output, CV_8UC3);
    LOGD("3 = %d, %d", grayInput.cols, grayInput.rows);
    cvtColor(grayInput, grayInput, COLOR_RGB2GRAY);
    LOGD("4 = %d, %d", output.cols, output.rows);

    double sigmaColor = 20.0;
    double sigmaSpace = 30.0;

    Mat bilateraledImage;
    bilateralFilter(grayInput, bilateraledImage, -1, sigmaColor, sigmaSpace);
    LOGD("4.1 = %d, %d", bilateraledImage.cols, bilateraledImage.rows);
    Canny(bilateraledImage, tmp, 75, 200, 3, false); // tmp = canny 결과
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

    for (int i = contours.size() - 1; i > contours.size() - 6; i--) {
        bigvalues.push_back(contours[i]);
    }
    LOGD("7", "1");
    vector<Point> approx, result_approx;

    for (int j = 0; j < 5; j++) {
        approxPolyDP(Mat(bigvalues[j]), approx, arcLength(Mat(bigvalues[j]), true) * 0.02, true);
        if (approx.size() == 4) {
            if (fabs(contourArea(Mat(approx))) > int(((grayInput.rows * grayInput.cols) * 0.1))) {
                result_approx = approx;
//                line(output, result_approx[0], result_approx[result_approx.size() - 1], Scalar(0, 255, 0), 3);
//                for (int k = 0; k < result_approx.size() - 1; k++)
//                    line(output, result_approx[k], result_approx[k + 1], Scalar(0, 255, 0), 3);
                break;
            }
        }
    }
    LOGD("8", "1");
    LOGD(" %d", result_approx.size());
    if (result_approx.size() == 4) {


        for (int i = 0; i < 4; i++) {
            pts[i] = result_approx[i];
//        pts[i].x = result_approx[i].x;
//        pts[i].y = result_approx[i].y ;
        }
        LOGD("8.1", "1");
        calculate_points();
        LOGD("8.2", "1");
        Point2f topLeft = result_pts[0];
        Point2f topRight = result_pts[1];
        Point2f bottomRight = result_pts[2];
        Point2f bottomLeft = result_pts[3];
        LOGD("8.3", "1");
        float w1 = fabs(bottomRight.x - bottomLeft.x);
        float w2 = fabs(topRight.x - topLeft.x);
        float h1 = fabs(topRight.y - bottomRight.y);
        float h2 = fabs(topLeft.y - bottomLeft.y);
        float maxWidth = max(w1, w2);
        float maxHeight = max(h1, h2);

        LOGD("9", "1");
        Point2f dst_pts[4] = {
                Point2f(0, 0), Point2f(maxWidth - 1, 0), Point2f(maxWidth - 1, maxHeight - 1),
                Point2f(0, maxHeight - 1)
        };

        LOGD("10", "1");
        Mat perspect_mat = getPerspectiveTransform(result_pts, dst_pts);
        warpPerspective(output, output, perspect_mat, Size((int(maxWidth)), (int(maxHeight))),
                        INTER_CUBIC);
        LOGD("%d, %d , 끝", output.cols, output.rows);
    }
    else{

    }
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

void calculate_points() {

    int sum_min = 0, sum_max = 0, diff_min = 0, diff_max = 0, result;
    float sum[4], diff[4];

    for (int i = 0; i < 4; i++) {
        sum[i] = pts[i].x + pts[i].y;
        diff[i] = (pts[i].y - pts[i].x);
        /*float tmp = pts[i].x + pts[i].y;
        if (sum_min > tmp) {
            sum_min = i;
        }*/
        cout << "sum[i] = " << sum[i] << " " << "diff[i]" << diff[i] << endl;
    }
    for (int i = 1; i < 4; i++) {
        int min, max;
        if (sum[sum_max] < sum[i]) sum_max = i;
        if (sum[sum_min] > sum[i]) sum_min = i;
        if (diff[diff_max] < diff[i]) diff_max = i;
        if (diff[diff_min] > diff[i]) diff_min = i;
    }

    cout << "sum_max" << sum_max << endl;
    cout << "sum_min" << sum_min << endl;
    cout << "diff_max" << diff_max << endl;
    cout << "diff_min" << diff_min << endl;

    result_pts[0] = pts[sum_min]; //top-left
    result_pts[2] = pts[sum_max]; //bottom-right
    result_pts[1] = pts[diff_min]; //top-right
    result_pts[3] = pts[diff_max]; //bottom-left

}
