package com.example.bcmanager

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ConfirmCapture : AppCompatActivity() {

    var image: ImageView? = null
    var bitImage: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_capture)

        image = findViewById(R.id.test_image)
        val intent = intent
        if(intent != null){
            bitImage = intent.getParcelableArrayExtra("image") as Bitmap
        }

        image!!.setImageBitmap(bitImage)
    }
}
