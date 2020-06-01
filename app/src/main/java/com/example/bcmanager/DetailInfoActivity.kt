package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_detail_info.*
import java.net.MalformedURLException
import java.net.URL

//메인 -> 카드클릭 -> 다이어로그 -> 상세보기
class DetailInfoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var httpConnection: HttpConnection
    private lateinit var cardNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)

        val detailIntent = intent
        cardNumber = detailIntent.getIntExtra("cardNumber", 0).toString()

        Log.d("cardNumber", cardNumber)
        getCardInfo()

        detail_btn_edit.setOnClickListener(this)
        detail_btn_ok.setOnClickListener(this)
    }

    fun getCardInfo() {
        var result: CardInfoItem.cardInfo? = null
        try {
            httpConnection = HttpConnection(URL(MainActivity.GET_CARD_INFO))
            httpConnection.requestGetCard(cardNumber, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {
                    val gson = GsonBuilder().create()
                    val jsonParser = JsonParser()
                    val jsonObject = jsonParser.parse(data) as JsonObject
                    val jsonArray = jsonObject["cardInfo"] as JsonArray

                    val j = jsonArray[0].asJsonObject
                    result = gson.fromJson(j, CardInfoItem.cardInfo::class.java)

                    runOnUiThread {
                        detail_name.text = result?.CARD_NAME.toString()
                        detail_address.text = result?.CARD_ADDRESS.toString()
                        detail_company.text = result?.CARD_COMPANY.toString()
                        detail_position.text = result?.CARD_POSITION.toString()
                        detail_fax.text = result?.CARD_FAX.toString()
                        detail_email.text = result?.CARD_EMAIL.toString()
                        detail_tel.text = result?.CARD_TEL.toString()
                        detail_phone.text = result?.CARD_PHONE.toString()

                        Glide.with(this@DetailInfoActivity)
                                .load(MainActivity.IMAGE_URL+result?.CARD_IMAGE)
                                .override(MainActivity.device_width, 200)
                                .into(detail_card)
                    }
                }

                override fun onError() {}
            })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.detail_btn_edit -> finish()
            R.id.detail_btn_ok -> finish()

        }
    }
}
