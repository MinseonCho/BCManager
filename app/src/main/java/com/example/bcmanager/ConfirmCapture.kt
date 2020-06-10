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
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_confirm_capture.*
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ConfirmCapture : AppCompatActivity(), View.OnClickListener {

    var image: ImageView? = null
    var bitImage: Bitmap? = null
    private val REQUEST_CODE_GALLERY = 200
    private var myApp: BCMApplication? = null
    lateinit var fileName: String
    var filePath: String? = null

    lateinit var tempSelectFile: File
    lateinit var httpConnection: HttpConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_capture)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        myApp = application as BCMApplication

        if (myApp!!.isLogined) goToGallery()
        else Toast.makeText(applicationContext, "로그인이 필요합니다.", Toast.LENGTH_LONG).show()

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
                filePath = myApp!!.getRealPathFromURI(this@ConfirmCapture, dataUri)
                val file_extn = filePath?.substring(filePath?.lastIndexOf(".")!! + 1)
                var `in`: InputStream? = null
                `in` = contentResolver.openInputStream(dataUri)
                bitImage = BitmapFactory.decodeStream(`in`)
                `in`.close()

                val date = SimpleDateFormat("yy_MM_dd_hh_mm_ss").format(Date());
                fileName = myApp?.userNum + "_" + date + "." + file_extn
                tempSelectFile = File(getFilesDir().getPath(), fileName);

                val out: OutputStream = FileOutputStream(tempSelectFile);
                bitImage?.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

                test_image.setImageBitmap(bitImage)
            } else {
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
                bitImage = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true)
                test_image.setImageBitmap(bitImage)
//                bitImage?.recycle()
                scaledBitmap.recycle()
            }
            R.id.btn_again -> {
                goToGallery()
            }
            R.id.btn_ok -> {

//                val image = BitmapFactory.decodeFile(tempSelectFile.getPath());

                httpConnection = HttpConnection(URL(MainActivity.INSERT_IMAGE_URL))
                myApp?.let {
                    httpConnection.requestInsertImage(tempSelectFile, it, object : OnRequestCompleteListener {
                        override fun onSuccess(data: String?) {
                            if (data!!.isNotEmpty()) {
                                Log.d("이미지 저장 성공", data)
                            }
                        }

                        override fun onError() {
                            Log.d("이미지 저장 실패", "")
                        }

                    })
                }

                val stream = ByteArrayOutputStream()
                bitImage?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                val byteArray = stream.toByteArray()

                val intent = Intent()
                intent.putExtra("image", byteArray)
                intent.putExtra("fileName", fileName)
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
