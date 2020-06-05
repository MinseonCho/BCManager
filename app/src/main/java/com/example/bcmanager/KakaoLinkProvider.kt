package com.example.bcmanager

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.kakao.util.helper.Utility.getPackageInfo
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object KakaoLinkProvider {
    private const val KAKAO_BASE_LINK = "https://developers.kakao.com"

    // 공유하기 눌렀을 때 처리
    fun sendKakaoLink(context: Context, cardNumber: Int, cardImage: String) {
        val params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("BCManager",
                        MainActivity.IMAGE_URL + cardImage,
                        LinkObject.newBuilder()
                                .build())
                        .setDescrption("이 명함을 확인해보세요!")
                        .build())
                .addButton(ButtonObject("앱에서 바로 확인", LinkObject.newBuilder()
                        .setAndroidExecutionParams("CARD_NUMBER=${cardNumber}")
                        .build()))
                .build()

        val serverCallbackArgs: MutableMap<String, String> = HashMap()
        serverCallbackArgs["CARD_NUMBER"] = "\${cardNumber}"


        // 상세페이지 접근 리스너
        KakaoLinkService.getInstance().sendDefault(context, params, serverCallbackArgs, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Toast.makeText(context, "존재하지 않는 게시판 입니다.", Toast.LENGTH_SHORT).show()
//                Log.d("공유-","존재하지 않는 게시판")

            }

            override fun onSuccess(result: KakaoLinkResponse) {
//                Log.d("공유-","serverCallbackArgs[course_number]")
            }
        })
    }


    fun getKeyHash(context: Context?): String? {
        val packageInfo: PackageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES)
                ?: return null
        for (signature in packageInfo.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                Log.w(TAG, "Unable to get MessageDigest. signature=$signature", e)
            }
        }
        return null
    }


}