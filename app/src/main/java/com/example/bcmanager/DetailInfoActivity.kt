package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.actionbar_title.*
import kotlinx.android.synthetic.main.activity_detail_info.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*

//메인 -> 카드클릭 -> 다이어로그 -> 상세보기
//카카오 공유
class DetailInfoActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var httpConnection: HttpConnection
    private lateinit var cardNumber: String
    private var flagForBtn = 0
    private lateinit var myApp: BCMApplication
    private lateinit var memo: String
    private val UPDATE_CODE = 500
    private val LOGIN_CODE = 505
    var result: CardInfoItem.cardInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)

        //actionbar title 가운데
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "상세보기"

        val detailIntent = intent
        cardNumber = detailIntent.getIntExtra("cardNumber", 0).toString()

        //메인 리스트에서 선택했을 시 flag == 0
        //kako link에서 왔을 시 flag == 503
        flagForBtn = detailIntent.getIntExtra("flag", 0);
        if(flagForBtn == 503) MainActivity.kakaoLinkNum = 0; //메인에 갔을 때 다시 띄우지 않게



        myApp = application as BCMApplication
        Log.d("cardNumber", cardNumber)

        if (myApp.isLogined) {
            if (!cardNumber.equals("0")) getCardInfo()
        } else {
            Toast.makeText(applicationContext, "로그인이 필요합니다. ", Toast.LENGTH_LONG);
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.putExtra("code", "detailActivity")
            startActivityForResult(intent, LOGIN_CODE)
        }


        detail_btn_edit.setOnClickListener(this)
        detail_btn_ok.setOnClickListener(this)
        actionbar_btn.setOnClickListener(this)
        detail_delete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.detail_btn_edit -> {
                //수정하기

                //수정
                val intent = Intent(applicationContext, RegisterActivity::class.java)
                intent.putExtra("name", result?.CARD_NAME)
                intent.putExtra("position", result?.CARD_POSITION)
                intent.putExtra("company", result?.CARD_COMPANY)
                intent.putExtra("phone", result?.CARD_PHONE)
                intent.putExtra("number", result?.CARD_TEL)
                intent.putExtra("email", result?.CARD_EMAIL)
                intent.putExtra("address", result?.CARD_ADDRESS)
                intent.putExtra("fax", result?.CARD_FAX)
                intent.putExtra("cardNum", result?.CARD_NUMBER)
                intent.putExtra("image", result?.CARD_IMAGE)
                intent.putExtra("memo", memo)


                if (flagForBtn == 0) {
                    //메인카드 상세정보 클릭 -> 수정하기 -> 완료 시 DB 내용 업데이트 후 메인화면으로 이동
                    intent.putExtra("flag", 1)
                } else {
                    //kakao link 수정
                    //수정하기 -> 완료 -> DB에 INSERT
                    intent.putExtra("flag", 2)
                }

                startActivityForResult(intent, UPDATE_CODE)


            }
            R.id.detail_btn_ok -> {
                if (flagForBtn == 0) finish();  //detail page
                else {
                    //공유 후 등록 page
                    Log.d("DetailInfoActivity", "공유 후 등록 페이지")

                    registerCard()
                }
            }
            R.id.actionbar_btn -> {
                showPopup(v)
            }
            R.id.detail_delete -> {
                val httpConnection = HttpConnection(URL(MainActivity.DELETE_CARD))
                httpConnection.requestDeleteItem(cardNumber, object : OnRequestCompleteListener {
                    override fun onSuccess(data: String?) {
                        if (data != null && data.isNotEmpty()) {
                            Log.d("deleteItem data ", data)
                            runOnUiThread(Runnable {
                                finish()
                            })
                        }
                    }

                    override fun onError() {
                        Log.d("삭제 결과", "실패")
                        runOnUiThread(Runnable {
                            Toast.makeText(applicationContext, "삭제에 실패하였습니다.", Toast.LENGTH_SHORT);
                        })
                    }

                })
            }

        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.menu_login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()
                startActivity(intent)
                myApp.isLogined = false
                true
            }
            R.id.menu_userinfo -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showPopup(v: View?) {
        val popup = PopupMenu(this, v!!)
        popup.setOnMenuItemClickListener(this)
        val inflater = popup.menuInflater
        if (myApp.isLogined) inflater.inflate(R.menu.menu_login, popup.menu) else inflater.inflate(R.menu.menu, popup.menu)
        popup.show()
    }

    fun getCardInfo() {

        try {
            httpConnection = HttpConnection(URL(MainActivity.GET_CARD_INFO))
            httpConnection.requestGetCard(cardNumber, object : OnRequestCompleteListener {
                override fun onSuccess(data: String?) {

                    if (data != null) {
                        val gson = GsonBuilder().create()
                        val jsonParser = JsonParser()
                        val jsonObject = jsonParser.parse(data) as JsonObject
                        val jsonArray = jsonObject["cardInfo"] as JsonArray

                        val j = jsonArray[0].asJsonObject
                        result = gson.fromJson(j, CardInfoItem.cardInfo::class.java)
                        memo = j.get("CARD_MEMO").asString


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
                                    .load(MainActivity.IMAGE_URL + result?.CARD_IMAGE)
                                    .override(MainActivity.device_width, 400)
                                    .into(detail_card)
                        }
                    }
                }

                override fun onError() {}
            })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE && resultCode == RESULT_OK) {
            finish()
        }
    }

    fun registerCard() {
        val httpConnection = HttpConnection(URL(MainActivity.INSERT_CARD_INFOS))
        httpConnection.requestInsertCardInfo(cardNumber, myApp.userNum, object : OnRequestCompleteListener {
            override fun onSuccess(data: String?) {
                if (data != null && data.isNotEmpty()) {
                    if (data.equals("1")) {
                        runOnUiThread(Runnable {
                            Toast.makeText(applicationContext, "등록되었습니다.", Toast.LENGTH_SHORT);
                            MainActivity.kakaoLinkNum = 0;
                            finish()
                        })
                    }
                }
            }

            override fun onError() {
                runOnUiThread(Runnable {
                    Toast.makeText(applicationContext, "다시 등록해 주십시오..", Toast.LENGTH_SHORT);
                    finish()
                })
            }

        })
    }
}
