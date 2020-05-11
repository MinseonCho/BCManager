package com.example.bcmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView

class ImageOCRActivity : AppCompatActivity() {

    var cardImage: ImageView? = null
    var info_name: EditText? = null
    var info_positon: EditText? = null
    var info_company: EditText? = null
    var info_phone: EditText? = null
    var info_email: EditText? = null
    var info_number: EditText? = null
    var info_address: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_o_c_r)

        cardImage = findViewById(R.id.card_image)
        info_name = findViewById(R.id.name)
        info_positon = findViewById(R.id.position)
        info_company = findViewById(R.id.company)
        info_phone = findViewById(R.id.phone)
        info_email = findViewById(R.id.email)
        info_number = findViewById(R.id.number)
        info_address = findViewById(R.id.address)


    }
}
