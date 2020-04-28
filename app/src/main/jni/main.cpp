#include <jni.h>
#include "com_example_bcmanager_MainActivity.h"

#include <opencv2/opencv.hpp>


void filter(cv::Mat image, cv::Mat mat, cv::Mat mat1);

using namespace cv;


extern "C" {

JNIEXPORT void JNICALL Java_com_example_bcmanager_MainActivity_ConvertRGBtoGray(
        JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {


    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

}

}
extern "C"
JNIEXPORT void JNICALL Java_com_example_bcmanager_MainActivity_BlurImage(JNIEnv *env, jobject thiz,
                                                                         jlong input_image,
                                                                         jlong output_image) {
    // TODO: implement BlurImage()


    float data_1[] = {
            1 / 9.f, 1 / 9.f, 1 / 9.f,
            1 / 9.f, 1 / 9.f, 1 / 9.f,
            1 / 9.f, 1 / 9.f, 1 / 9.f
    };

    float data2[] = {
            0,-1,0,
            -1,5,-1,
            0,-1,0
    };

    Mat &input = *(Mat*) input_image;
    CV_Assert(input.data);
    Mat &output = *(Mat*) output_image;

//    cvtColor( input, output, COLOR_RGB2GRAY);

//    Sobel(input, output, CV_32F, 1,0,3);
//    convertScaleAbs(output, output);

    Mat mask(3, 3, CV_32F, data_1);
    Mat mask_2(3, 3, CV_32F, data2);

    Mat blur_1;
//    filter(input, output, mask);

    output = Mat(input.size(), CV_32F, Scalar(0));
    Point h_m = mask_2.size() / 2;

    for (int i = h_m.y; i < input.rows - h_m.y; i++) {
        for (int j = h_m.x; j < input.cols - h_m.y; j++) {

            float sum = 0;
            for (int u = 0; u < mask_2.rows; u++) {
                for (int v = 0; v < mask_2.cols; v++) {
                    int y = i + u - h_m.y;
                    int x = j + v - h_m.x;
                    sum += mask_2.at<float>(u, v) * input.at<uchar>(y, x); //회선수식
                }
            }

            output.at<float>(i, j) = sum;
        }
    }

    output.convertTo(output, CV_8U);
}

//void filter(Mat img, Mat& dst, Mat mask) {
//    dst = Mat(img.size(), CV_32F, Scalar(0));
//    Point h_m = mask.size() / 2;
//
//    for (int i = h_m.y; i < img.rows - h_m.y; i++) {
//        for (int j = h_m.x; j < img.cols - h_m.y; j++) {
//
//            float sum = 0;
//            for (int u = 0; u < mask.rows; u++) {
//                for (int v = 0; v < mask.cols; v++) {
//                    int y = i + u - h_m.y;
//                    int x = j + v - h_m.x;
//                    sum += mask.at<float>(u, v) * img.at<uchar>(y, x); //회선수식
//                }
//            }
//
//            dst.at<float>(i, j) = sum;
//        }
//    }
//}
