package com.example.bcmanager

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_confirm_capture.view.*
import kotlinx.android.synthetic.main.activity_image_o_c_r.*
import java.net.URL
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    var intent_: Intent? = null
    lateinit var tCardNumber: String
    lateinit var myApp: BCMApplication
    lateinit var cardInfo: CardInfoItem.cardInfo
    var flag: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_o_c_r)

        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        myApp = application as BCMApplication

        intent_ = intent
        if (intent_ != null) {
            flag = intent_!!.getIntExtra("flag", 0)
            Log.d("플래그", flag.toString());
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
                if (flag != null && flag == 1) {
                    Log.d("플래그", flag.toString());
                    Log.d("ResiterActivity", "CardInfo 수정 요청")
                    updateInfos()
                } else {
                    Log.d("ResiterActivity", "CARD_TB에 저장 / TCARD_TB꺼 삭제 / 해당 리스트에서 삭제")
                    //CARD_TB에 저장 / TCARD_TB꺼 삭제 / 해당 리스트에서 삭제
                    val httpConnection = HttpConnection(URL(MainActivity.REGISTER_CARD))
                    httpConnection.requestRegister(tCardNumber, myApp.userNum, object : OnRequestCompleteListener {
                        override fun onSuccess(data: String?) {
                            if (data != null) {
                                runOnUiThread(Runnable {
                                    if (data.equals("1")) Toast.makeText(applicationContext, "등록되었습니다", Toast.LENGTH_LONG).show()
                                    else Toast.makeText(applicationContext, "등록이 실패되었습니다", Toast.LENGTH_LONG).show()

                                    myApp.count--;

                                    val intent_ = Intent()
                                    intent_.setFlags(Activity.RESULT_OK)
                                    finish()
                                })

                            }
                        }

                        override fun onError() {
                            Log.d("RegisterActivity", "등록실패")
                        }

                    })
                }
            }
        }
    }

    fun updateInfos() {
        val httpConnection = HttpConnection(URL(MainActivity.UPDATE_CARD_INFOS))
        httpConnection.requestUpdateInfos(tCardNumber, name.text.toString(),
                company.text.toString(), position.text.toString(), email.text.toString(),
                phone.text.toString(), number.text.toString(), address.text.toString(),
                fax.text.toString(), object : OnRequestCompleteListener {
            override fun onSuccess(data: String?) {
                if (data != null && data.isNotEmpty()) {
                    if (data.equals("1")) {
                        Log.d("ResiterActivity", "CardInfo 수정 성공")
                        intent = Intent();
                        setResult(Activity.RESULT_OK, intent);
                        outMessage(1)
                    }
                }
            }

            override fun onError() {
                outMessage(0)
            }

        })
    }


    fun outMessage(value: Int) {

        when (value) {
            1 -> {
                runOnUiThread(Runnable {
                    Toast.makeText(applicationContext, "수정되었습니다.", Toast.LENGTH_SHORT).show(); finish()
                })
            }
            0 -> {
                runOnUiThread(Runnable {
                    Toast.makeText(applicationContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}
