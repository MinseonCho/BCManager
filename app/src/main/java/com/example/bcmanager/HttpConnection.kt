package com.example.bcmanager

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.Normalizer

class HttpConnection(url: URL): Callback  {

    private var url: String? = null
    private var data: String? = null
    private var onRequestCompleteListener : OnRequestCompleteListener? = null
    private lateinit var httpConnection: HttpConnection
    init {
        this.url = url.toString()
    }

    /**
     * 웹 서버로 요청을 한다.
     */
    fun requestInsertImage(file: File, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.name, RequestBody.create(MultipartBody.FORM, file))
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

    fun requestGetCards(uID: String, callback: OnRequestCompleteListener){
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
    fun signUp(user_id: String, user_name:String, user_email: String){
        httpConnection = HttpConnection(URL(url))
        httpConnection.requestSignUp(user_name, user_id, user_email, object : OnRequestCompleteListener{
            override fun onSuccess(data: String?) {

            }

            override fun onError() {
                TODO("Not yet implemented")
            }

        } )
    }
    fun requestGetCard(cardNumber: String, callback: OnRequestCompleteListener){
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