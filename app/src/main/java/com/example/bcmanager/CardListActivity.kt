package com.example.bcmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_card_list.*
import java.util.*
import kotlin.collections.ArrayList

class CardListActivity : AppCompatActivity() {

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

        Log.d("CardListActivity", myApp.unregisterdCards.size.toString())
        cardListAdapter = CardListAdapter(this@CardListActivity, myApp.unregisterdCards)
        val layoutManager = LinearLayoutManager(this@CardListActivity)
        rcged_card_recyclerview.setLayoutManager(layoutManager)
        rcged_card_recyclerview.adapter = cardListAdapter

    }
}
