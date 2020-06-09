package com.example.bcmanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bcmanager.CardInfoItem.cardInfo
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
    lateinit var cardListAdapter: CardListAdapter
    lateinit var myApp: BCMApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        myApp = application as BCMApplication
        cardList = ArrayList()

        if (myApp.count > 0) getUnregisterdCardsInfo()

        Log.d("CardListActivity", myApp.unregisterdCards.size.toString())

        val layoutManager = LinearLayoutManager(this@CardListActivity)
        rcged_card_recyclerview.setLayoutManager(layoutManager)


    }

    fun getUnregisterdCardsInfo() {
        var httpConn: HttpConnection? = null
        try {
            httpConn = HttpConnection(URL(MainActivity.GET_UNREGISTERD_CARD_INFO))
            httpConn.requestRcgedGetCards(myApp.userID, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {
                    if (data != null && !data.isEmpty()) {
                        myApp.unregisterdCards.clear()
                        Log.d("성공_등록안된카드", data)
                        val gson = GsonBuilder()
                                .create()
                        val jsonParser = JsonParser()
                        val jsonObject = jsonParser.parse(data) as JsonObject
                        val jsonArray = jsonObject["cardInfo"] as JsonArray
                        var result: cardInfo?
                        for (i in 0 until jsonArray.size()) {
                            val j = jsonArray[i].asJsonObject
                            result = gson.fromJson(j, cardInfo::class.java)
                            cardList.add(result)
                            Log.d("테스트으", cardList.size.toString())
                            Log.d("테스트으", cardList[i].CARD_IMAGE)
                        }
                        runOnUiThread { //                                getCardInfo();
                            cardListAdapter = CardListAdapter(this@CardListActivity, cardList, this@CardListActivity)
                            rcged_card_recyclerview.adapter = cardListAdapter
                        }
                    }
                }

                override fun onError() {}
            })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    fun deleteItem(cardNum: String){
        val httpConnection = HttpConnection(URL(MainActivity.DELETE_ITEM))
        httpConnection.requestDeleteItem(cardNum, object : OnRequestCompleteListener {
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

    override fun onClick(value: String?) {
        value?.let { deleteItem(it) }
    }
}
interface OnItemClick {
    fun onClick(value: String?)
}
