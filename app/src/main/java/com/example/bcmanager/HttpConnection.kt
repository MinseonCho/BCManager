package com.example.bcmanager

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL

class HttpConnection(url: URL): Callback  {

    private var url: String? = null
    private var data: String? = null
    private var onRequestCompleteListener : OnRequestCompleteListener? = null
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
                .url("http://104.197.171.112/insert_image.php")
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
            Log.d("TAG",body);
            parse(body)

        }
        onRequestCompleteListener?.onSuccess(data)
    }
    private fun parse(response: String?) {
        this.data = response  //when I debug this, it contains data I need.
    }

}