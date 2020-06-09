package com.example.bcmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_image_o_c_r.*
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    var intent_: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_o_c_r)

        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        intent_ = intent
        if(intent_ != null){
            Glide.with(this)
                    .load(MainActivity.IMAGE_URL + intent_!!.getStringExtra("image"))
                    .override(MainActivity.device_width, 200)
                    .into(card_image)
            name.setText(intent_!!.getStringExtra("name"))
            position.setText(intent_!!.getStringExtra("position"))
            company.setText(intent_!!.getStringExtra("company"))
            phone.setText(intent_!!.getStringExtra("phone"))
            number.setText(intent_!!.getStringExtra("number"))
            email.setText(intent_!!.getStringExtra("email"))
            address.setText(intent_!!.getStringExtra("address"))
            fax.setText(intent_!!.getStringExtra("fax"))
            memo.setText(intent_!!.getStringExtra("memo"))

        }

        ocrbtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {

        }
    }
}
