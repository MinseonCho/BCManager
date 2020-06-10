package com.example.bcmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bcmanager.CardInfoItem.cardInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_card_list.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class CardListActivity : AppCompatActivity(), OnItemClick {

    lateinit var cardList: ArrayList<CardInfoItem.cardInfo>
    var cardListAdapter: CardListAdapter? = null
    val REQUESTCODE_ = 500
    lateinit var myApp: BCMApplication
    var httpConn: HttpConnection? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        Log.d("CardListActivity","onCreate")
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        myApp = application as BCMApplication
        cardList = ArrayList()

//        if (myApp.count > 0) getUnregisterdCardsInfo()

        Log.d("CardListActivity", myApp.count.toString())

        val layoutManager = LinearLayoutManager(this@CardListActivity)
        rcged_card_recyclerview.setLayoutManager(layoutManager)


    }

    fun getUnregisterdCardsInfo() {
        Log.d("CardListActivity","getUnregisterdCardsInfo")

        try {
            httpConn = HttpConnection(URL(MainActivity.GET_UNREGISTERD_CARD_INFO))
            httpConn!!.requestRcgedGetCards(myApp.userID, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {
                    if (data != null && !data.isEmpty()) {

                        Log.d("성공_등록안된카드", data)
                        val gson = GsonBuilder()
                                .create()
                        val jsonParser = JsonParser()
                        val jsonObject = jsonParser.parse(data) as JsonObject
                        val jsonArray = jsonObject["cardInfo"] as JsonArray
                        var result: cardInfo?
                        Log.d("CardListActivity","jsonArray.size =" + jsonArray.size())
                        cardList.clear()
                        for (i in 0 until jsonArray.size()) {
                            val j = jsonArray[i].asJsonObject
                            result = gson.fromJson(j, cardInfo::class.java)
                            cardList.add(result)
                            Log.d("테스트으", cardList.size.toString())
                        }
                        runOnUiThread {
                            cardListAdapter = CardListAdapter(this@CardListActivity, cardList, this@CardListActivity)
                            rcged_card_recyclerview.adapter = cardListAdapter
                        }
                    }else{
                        Log.d("CardListActivity","카드없음")
                        finish()
                    }
                }

                override fun onError() {}
            })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    fun deleteItem(value: String) {

        val httpConnection = HttpConnection(URL(MainActivity.DELETE_ITEM))
        httpConnection.requestDeleteItem(value, object : OnRequestCompleteListener {
            override fun onSuccess(data: String?) {
                if (data != null) {
                    if (data.isNotEmpty()) {
                        if (data.equals("1")) Log.d("삭제 결과", data)
                        else Log.d("삭제 결과", data)

                    }
                }
            }

            override fun onError() {
                Log.d("삭제 결과", "실패")
            }

        })
    }

    override fun onClick(value: String?, code: Int, position: Int) {
        when (code) {
            100 -> value?.let { deleteItem(it) }
            101 -> {
                val intent = Intent(applicationContext, RegisterActivity::class.java)
                intent.putExtra("name", cardList.get(position).CARD_NAME)
                intent.putExtra("position", cardList.get(position).CARD_POSITION)
                intent.putExtra("company", cardList.get(position).CARD_COMPANY)
                intent.putExtra("phone", cardList.get(position).CARD_PHONE)
                intent.putExtra("number", cardList.get(position).CARD_TEL)
                intent.putExtra("email", cardList.get(position).CARD_EMAIL)
                intent.putExtra("address", cardList.get(position).CARD_ADDRESS)
                intent.putExtra("fax", cardList.get(position).CARD_FAX)
                intent.putExtra("cardNum", cardList.get(position).CARD_NUMBER)
                intent.putExtra("image", cardList.get(position).CARD_IMAGE)
                intent.putExtra("memo", cardList.get(position).CARD_MEMO)
                startActivity(intent)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        Log.d("CardListActivity","onResume")
        Log.d("CardListActivity","onResume cardList.size = " + cardList.size)

        cardList.clear()
        Log.d("CardListActivity","onResume cardList.size = " + cardList.size)

        getUnregisterdCardsInfo()
//        cardListAdapter?.notifyDataSetChanged()
    }
}

interface OnItemClick {
    fun onClick(value: String?, code: Int, position: Int)
}
