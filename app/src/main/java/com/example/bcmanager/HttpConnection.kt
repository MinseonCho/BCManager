package com.example.bcmanager

import android.app.Application
import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.Normalizer
import kotlin.jvm.internal.Ref

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

    override fun onFailure(call: Call, e: IOException) {
        onRequestCompleteListener?.onError()
        println("error on httpConnection")
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            val body = response.body()?.string()
//            Log.d("TAG",body);
            parse(body)

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