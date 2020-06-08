package com.example.bcmanager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_confirm_capture.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ConfirmCapture : AppCompatActivity(), View.OnClickListener {

    var image: ImageView? = null
    var bitImage: Bitmap? = null
    private val REQUEST_CODE_GALLERY = 200
    var myApp: BCMApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_capture)

        myApp = BCMApplication()

        goToGallery()

        btn_rotate.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_again.setOnClickListener(this)
    }

    fun goToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                assert(data != null)
                val dataUri = data!!.data
                val filePath: String = myApp!!.getRealPathFromURI(this@ConfirmCapture, dataUri)
                val file_extn = filePath.substring(filePath.lastIndexOf(".") + 1)
                var `in`: InputStream? = null
                `in` = contentResolver.openInputStream(dataUri)
                bitImage = BitmapFactory.decodeStream(`in`)
                `in`.close()

                test_image.setImageBitmap(bitImage)
            }else{
                finish()
            }
        }
    }


    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.btn_rotate -> {
                val matrix = Matrix()
                matrix.postRotate(90F)
                val scaledBitmap = Bitmap.createScaledBitmap(bitImage, bitImage!!.width, bitImage!!.height, true)
                bitImage = Bitmap.createBitmap (scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true)
                test_image.setImageBitmap(bitImage)
//                bitImage?.recycle()
                scaledBitmap.recycle()
            }
            R.id.btn_again -> {
                goToGallery()
            }
            R.id.btn_ok -> {

                val stream = ByteArrayOutputStream()
                bitImage?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                val byteArray = stream.toByteArray()

                val intent = Intent()
                intent.putExtra("image", byteArray)
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
