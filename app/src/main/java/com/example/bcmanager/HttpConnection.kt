package com.example.bcmanager

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL

class HttpConnection(url: URL) : Callback {

    private var url: String? = null
    private var data: String? = null
    private var onRequestCompleteListener: OnRequestCompleteListener? = null

    private lateinit var httpConnection: HttpConnection

    init {
        this.url = url.toString()
    }

    /**
     * 웹 서버로 요청을 한다.
     */
    fun requestInsertImage(file: File, myApp: BCMApplication, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.name, RequestBody.create(MultipartBody.FORM, file))
                .addFormDataPart("USER_ID", myApp.userID)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestSignUp(user_name: String, user_id: String, user_email: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("USER_ID", user_id)
                .add("USER_EMAIL", user_email)
                .add("USER_NAME", user_name)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestGetCards(uID: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback
        Log.d("겟카운트", "requestGetCards실행")
        val body = FormBody.Builder()
                .add("USER_ID", uID)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestRcgedGetCards(uID: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("USER_ID", uID)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }


    fun signUp(user_id: String, user_name: String, user_email: String) {
        httpConnection = HttpConnection(URL(url))
        httpConnection.requestSignUp(user_name, user_id, user_email, object : OnRequestCompleteListener {
            override fun onSuccess(data: String?) {

            }

            override fun onError() {
                Log.d("Sign Up Failed", "")
            }

        })
    }

    fun requestGetCard(cardNumber: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("CARD_NUMBER", cardNumber)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestRegister(tCardNumber: String, userNum: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("TCARD_NUMBER", tCardNumber)
                .add("USER_NUMBER", userNum)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestDeleteItem(cardNumber: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("CARD_NUMBER", cardNumber)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestGetUserNumber(uID: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("USER_ID", uID)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestUpdateInfos(cardNumber: String, name: String, company: String, positon: String,
                           email: String, phone: String, tel: String, address: String, fax: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("CARD_NUMBER", cardNumber)
                .add("CARD_NAME", name)
                .add("CARD_COMPANY", company)
                .add("CARD_POSITION", positon)
                .add("CARD_PHONE", phone)
                .add("CARD_TEL", tel)
                .add("CARD_FAX", fax)
                .add("CARD_ADDRESS", address)
                .add("CARD_EMAIL", email)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestInsertCardInfo(cardNumber: String, userNum: String,callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("CARD_NUMBER", cardNumber)
                .add("USER_NUMBER", userNum)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    fun requestUpdateTcardResult(tCardImage: String,callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = FormBody.Builder()
                .add("TCARD_IMAGE", tCardImage)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }
//    CardOCR.ph = ""
//    CardOCR.nm = ""
//    CardOCR.ad = ""
//    CardOCR.em = ""
//    CardOCR.nb = ""
//    CardOCR.fx = ""
//    CardOCR.po = ""
//    CardOCR.cp = ""
//    CardOCR.memo = null


    //갤러리 사진 등록/ 카메라 사진 등록 모두 쓰임
    fun requestInsertInfos(file: File, uID: String, detectedCardInfo: CardInfoItem.detectedCardInfo, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.name, RequestBody.create(MultipartBody.FORM, file))
                .addFormDataPart("USER_ID", uID)
                .addFormDataPart("CARD_IMAGE", detectedCardInfo.CARD_IMAGE)
                .addFormDataPart("CARD_NAME", detectedCardInfo.CARD_NAME)
                .addFormDataPart("CARD_POSITION", detectedCardInfo.CARD_POSITION)
                .addFormDataPart("CARD_PHONE", detectedCardInfo.CARD_PHONE)
                .addFormDataPart("CARD_TEL", detectedCardInfo.CARD_TEL)
                .addFormDataPart("CARD_ADDRESS", detectedCardInfo.CARD_ADDRESS)
                .addFormDataPart("CARD_FAX", detectedCardInfo.CARD_FAX)
                .addFormDataPart("CARD_COMPANY", detectedCardInfo.CARD_COMPANY)
                .addFormDataPart("CARD_EMAIL", detectedCardInfo.CARD_EMAIL)
                .addFormDataPart("CARD_MEMO", detectedCardInfo.CARD_MEMO)
                .build()

        val request = Request.Builder()
                .url(url!!)
                .post(body)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }
    override fun onFailure(call: Call, e: IOException) {
        onRequestCompleteListener?.onError()
        println("error on httpConnection")
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {

            try {
                val body = response.body()?.string()
                parse(body)
            } catch (e: IOException) {
                // Signal to the user failure here, re-throw the exception, or
                // whatever else you want to do on failure
            }

        }
        onRequestCompleteListener?.onSuccess(data)
    }

    private fun parse(response: String?) {
        this.data = response  //when I debug this, it contains data I need.
    }

}


interface OnRequestCompleteListener {
    fun onSuccess(data: String?)
    fun onError()
}