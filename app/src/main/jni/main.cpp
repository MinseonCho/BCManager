#include <jni.h>
#include "com_example_bcmanager_MainActivity.h"

#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;


void filter(Mat img, Mat &dst, Mat mask);

float resize(Mat img_src, Mat &img_resize, int resize_width);


extern "C" {

JNIEXPORT void JNICALL Java_com_example_bcmanager_CameraActivity_ConvertRGBtoGray(
        JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {
    int area = 0, cnt = 0;
    // TODO: implement BlurImage()

    Mat &input = *(Mat *) matAddrInput;
    CV_Assert(input.data);
    Mat &output = *(Mat *) matAddrResult;
    vector<int>::iterator it;

    input.convertTo(output,CV_8UC3);
    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    Mat min;

    cvtColor(input, min, COLOR_RGB2GRAY);

    Canny(min, tmp, 100, 200, 3, false);
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(tmp, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point());

    vector<Point> approx;

    for (int i = 0; i < contours.size(); i++) {
        Rect r = boundingRect(contours[i]);
        int pre_area = r.width * r.height;
        if (pre_area > area) {
            area = pre_area;
            cnt = i;
        }
    }
    approxPolyDP(Mat(contours[cnt]), approx, arcLength(Mat(contours[cnt]), true) * 0.02, true);
    if (fabs(contourArea(Mat(approx))) > 100)  //면적이 일정크기 이상이어야 한다.
    {
        int size = approx.size();
        //Contour를 근사화한 직선을 그린다.
        if (size == 4) {
            rectangle(output, approx[0], approx[2], Scalar(0, 255, 0),3);

        }
    }



}

JNIEXPORT void JNICALL Java_com_example_bcmanager_MainActivity_BlurImage(JNIEnv *env, jobject thiz,
                                                                         jlong input_image,
                                                                         jlong output_image) {

    int area = 0, cnt = 0;
    // TODO: implement BlurImage()

    Mat &input = *(Mat *) input_image;
    Mat &output = *(Mat *) output_image;

    input.convertTo(output,CV_8UC3);
    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    Mat min;

    cvtColor(input, min, COLOR_RGB2GRAY);


    Canny(min, tmp, 100, 200, 3, false);
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(tmp, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point());

    vector<Point> approx;

    for (int i = 0; i < contours.size(); i++) {
        Rect r = boundingRect(contours[i]);
        int pre_area = r.width * r.height;
        if (pre_area > area) {
            area = pre_area;
            cnt = i;
        }
    }
    approxPolyDP(Mat(contours[cnt]), approx, arcLength(Mat(contours[cnt]), true) * 0.02, true);
    if (fabs(contourArea(Mat(approx))) > 100)  //면적이 일정크기 이상이어야 한다.
    {
        int size = approx.size();
        //Contour를 근사화한 직선을 그린다.
        if (size % 2 ==0 ) {
            line(output, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);
            for (int k = 0; k < size - 1; k++)
                line(output, approx[k], approx[k + 1], Scalar(0, 255, 0), 3);

        }
    }






//    vector<vector<Point>> contours;
//    vector<Vec4i> hierarchy;
//
//    findContours(output, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));
//
//    vector<Point> cnt = contours[0];
//    drawContours(input, contours, -1, CV_RGB(0, 255, 0), 3);
//
//    double epsilon = 0.1 * arcLength(cnt, true);
//
//    vector<Point> approx;
//    vector<vector<Point>> result_approx;
//
//    approxPolyDP(cnt, approx, epsilon, true);
//
////    result_approx.insert(result_approx.begin(),approx.begin(), approx.end());
//    result_approx.push_back(approx);
//    drawContours(tmp, result_approx, -1, CV_RGB(0, 255, 0), 5);
//    output = tmp;

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

