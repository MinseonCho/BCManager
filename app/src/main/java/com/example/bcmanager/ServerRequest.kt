package com.example.bcmanager


import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.URL

class ServerRequest(url: URL) : Callback {

    private var url: String? = null
    private var data: String? = null
    private var onRequestCompleteListener: OnRequestCompleteListener? = null

    init {
        this.url = url.toString()
    }

    fun getImage(number: String, callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback

        val formBody = FormBody.Builder()
                .add("TEST_NUMBER", number)
                .build()
        val request = Request.Builder().url(url!!).post(formBody).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(this@ServerRequest)

    }

    override fun onFailure(call: Call, e: IOException) {
        onRequestCompleteListener?.onError()
        println("error")
    }

    override fun onResponse(call: Call, response: Response) {
        CoroutineScope(Dispatchers.Default).launch {
            if (response.isSuccessful) {
                val body = response.body()?.string()

                parse(body)
            }

            onRequestCompleteListener?.onSuccess(data)
        }
    }

    private fun parse(response: String?) {
        this.data = response  //when I debug this, it contains data I need.
    }

}

interface OnRequestCompleteListener {
    fun onSuccess(data: String?)
    fun onError()
}