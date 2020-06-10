package com.example.bcmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_image_o_c_r.*
import java.net.URL
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    var intent_: Intent? = null
    lateinit var tCardNumber: String

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
            tCardNumber = intent_!!.getIntExtra("cardNum", 0).toString()
        }

        ocrbtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.ocrbtn -> {
                Toast.makeText(applicationContext, "확인클릭", Toast.LENGTH_LONG).show()
                //CARD_TB에 저장 / TCARD_TB꺼 삭제 / 해당 리스트에서 삭제
                val httpConnection = HttpConnection(URL(MainActivity.REGISTER_CARD))
                httpConnection.requestRegister(tCardNumber, object : OnRequestCompleteListener{
                    override fun onSuccess(data: String?) {
                        if(data != null){
                            if(data.equals("1")) Toast.makeText(applicationContext, "등록되었습니다", Toast.LENGTH_LONG).show()
                            else Toast.makeText(applicationContext, "등록이 실패되었습니다", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError() {
                        Toast.makeText(applicationContext, "등록이 실패되었습니다.", Toast.LENGTH_LONG).show()
                    }

                })
            }
        }
    }
}
