package com.example.bcmanager

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.bcmanager.CardOCR.fileCacheItem
import java.io.File
import java.net.ProxySelector
import java.net.URL
import java.util.*

class Fileinput{

    var myApp: BCMApplication? = null
    lateinit var httpConnection: HttpConnection
    fun fileupload(file: File) {
        Log.d("fileupload-------", "")

        httpConnection = HttpConnection(URL(MainActivity.INSERT_IMAGE_URL))
        myApp?.let {
            httpConnection.requestInsertImage(file, it, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {
                    if (data!!.isNotEmpty()) {
                        Log.d("이미지 저장 성공--------", data)
                    }
                }

                override fun onError() {
                    Log.d("이미지 저장 실패---------", "")
                }

            })
        }

        //myApp = application as BCMApplication
    }
}
