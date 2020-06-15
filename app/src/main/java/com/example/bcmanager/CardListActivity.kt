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
import com.google.gson.*
import com.google.gson.reflect.TypeToken
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
    var countOfCard: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        Log.d("CardListActivity", "onCreate")
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "BCManager"

        myApp = application as BCMApplication
        cardList = ArrayList()

//        if (myApp.count > 0) getUnregisterdCardsInfo()

        Log.d("CardListActivity", myApp.count.toString())

        val layoutManager = LinearLayoutManager(this@CardListActivity)
        layoutManager.reverseLayout = true;
        layoutManager.stackFromEnd = true;
        rcged_card_recyclerview.setLayoutManager(layoutManager)

    }

    fun getUnregisterdCardsInfo() {
        Log.d("CardListActivity", "getUnregisterdCardsInfo")

        try {
            httpConn = HttpConnection(URL(MainActivity.GET_UNREGISTERD_CARD_INFO))
            httpConn!!.requestRcgedGetCards(myApp.userID, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {
                    if (data != null && !data.isEmpty()) {

                        try {
                            Log.d("성공_등록안된카드", data)
                            val gson = GsonBuilder()
                                    .setLenient()
                                    .create()
                            val jsonParser = JsonParser()
                            val jsonObject = jsonParser.parse(data) as JsonObject
                            val cardInfo = jsonObject["cardInfo"] as JsonObject
                            var unrcgedData = cardInfo.get("unrcgedData").asJsonArray
                            var resultData = cardInfo.get("resultData").asJsonArray
                            var result: cardInfo?
                            Log.d("CardListActivity", "unrcgedData.size =" + unrcgedData.size())
                            Log.d("CardListActivity", "resultData.size =" + resultData.size())
                            cardList.clear()
                            countOfCard = 0

                            for (i in 0 until unrcgedData.size()) {
                                val j = unrcgedData.get(i).asJsonObject
                                result = gson.fromJson(j, CardInfoItem.cardInfo::class.java)
                                cardList.add(result)
                            }

                            for (i in 0 until resultData.size()) {
                                val j = resultData.get(i).asJsonObject
                                result = gson.fromJson(j, CardInfoItem.cardInfo::class.java)
                                cardList.add(result)
                            }
                            runOnUiThread {
                                countOfCard = cardList.size;
                                Log.d("인식된 카드 리스트 사이즈 : ", cardList.size.toString())
                                if(countOfCard == 0) finish()
                                cardListAdapter = CardListAdapter(this@CardListActivity, cardList, this@CardListActivity)
                                rcged_card_recyclerview.adapter = cardListAdapter


                            }
                        }catch (e: JsonSyntaxException){

                        }

                    } else {
                        Log.d("CardListActivity", "카드없음")
                        finish()
                    }
                }

                override fun onError() {}
            })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    public fun deleteItem(value: String) {

        val httpConnection = HttpConnection(URL(MainActivity.DELETE_ITEM))
        httpConnection.requestDeleteItem(value, object : OnRequestCompleteListener {
            override fun onSuccess(data: String?) {
                if (data != null && data.isNotEmpty()) {
                    Log.d("deleteItem data ", data)

                    myApp.count--;
                    Log.d("마이너스된 count ", myApp.count.toString())
                    if (myApp.count == 0) {
                        runOnUiThread(Runnable {
                            intent = Intent()
                            setResult(Activity.RESULT_OK)
                            intent.putExtra("count", myApp.count)
                            finish()
                        })
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
        Log.d("CardListActivity", "onResume")
        Log.d("CardListActivity", "onResume cardList.size = " + cardList.size)

        cardList.clear()
        Log.d("CardListActivity", "onResume cardList.size = " + cardList.size)

        getUnregisterdCardsInfo()
//        cardListAdapter?.notifyDataSetChanged()
    }
}

interface OnItemClick {
    fun onClick(value: String?, code: Int, position: Int)
}
