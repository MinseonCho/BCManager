#include <jni.h>
#include "com_example_bcmanager_MainActivity.h"

#include <opencv2/opencv.hpp>
#include <android/log.h>

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

    LOGD("%d : input.cols", input.cols);
    LOGD("%d : input.rows", input.rows);
    input.convertTo(output, CV_8UC3);
    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    Mat grayInput;
    cvtColor(input, grayInput, COLOR_RGB2GRAY);

//    resize(grayInput, grayInput, Size(0, 0), 0.5,0.5,INTER_AREA);


    Canny(grayInput, tmp, 100, 200, 3, false);
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
//            rectangle(output, approx[0], approx[2], Scalar(0, 255, 0),3);
            line(output, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);
            for (int k = 0; k < size - 1; k++)
                line(output, approx[k], approx[k + 1], Scalar(0, 255, 0), 4);

        }
    }

    LOGD("%d : output.cols", output.cols);
    LOGD("%d : output.rows", output.rows);

}

JNIEXPORT void JNICALL Java_com_example_bcmanager_MainActivity_BlurImage(JNIEnv *env, jobject thiz,
                                                                         jlong input_image,
                                                                         jlong output_image) {

    int area = 0, cnt = 0;
    Point2f pts[4];
    Point2f dst_pts[4];
    float minWidth;
    float minHeight;

    // TODO: implement BlurImage()

    Mat &input = *(Mat *) input_image;
    Mat &output = *(Mat *) output_image;

    input.convertTo(output, CV_8UC3);
    Mat tmp = Mat::zeros(input.rows, input.cols, CV_8UC3);
    Mat grayInput;

    cvtColor(input, grayInput, COLOR_RGB2GRAY);

    double sigmaColor = 20.0;
    double sigmaSpace = 20.0;
    Mat bilateraledImage;
    bilateralFilter(grayInput, bilateraledImage, -1, sigmaColor, sigmaSpace);

    Canny(bilateraledImage, tmp, 100, 200, 3, false);
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

            for (int i = 0; i < 4; i++) {
                pts[i] = approx[i];
            }
            Point2f topLeft = pts[0];
            Point2f topRight = pts[3];
            Point2f bottomLeft = pts[1];
            Point2f bottomRight = pts[2];

            float w1 = abs(bottomRight.x - bottomLeft.x);
            float w2 = abs(topRight.x - topLeft.x);
            float h1 = abs(topRight.y - bottomRight.y);
            float h2 = abs(topLeft.y - bottomLeft.y);
            minWidth = min(w1, w2);
            minHeight = min(h1, h2);

            dst_pts[0] = Point2f(0,0);  dst_pts[1] = Point2f(0, minHeight-1);
            dst_pts[2] = Point2f(minWidth-1, minHeight-1);  dst_pts[3] = Point2f(minWidth-1,0);


            Mat perspect_mat = getPerspectiveTransform(pts, dst_pts);
            warpPerspective(output, output, perspect_mat, Size((int(minWidth)), (int(minHeight))), INTER_CUBIC);

//            line(output, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);
//            for (int k = 0; k < size - 1; k++)
//                line(output, approx[k], approx[k + 1], Scalar(0, 255, 0), 3);

        }
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

