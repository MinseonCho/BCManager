package com.example.bcmanager

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import java.util.*

public class CustomDialogForCard(private var context: Context,  var cardInfo: CardInfoItem.cardInfo) {

    fun callDialog(){
        val dlg = Dialog(context)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.activity_custom_dialog_for_card)
        Objects.requireNonNull(dlg.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)))

        dlg.show()

        val goToDetail = dlg.findViewById<ImageView>(R.id.dialog_goToDetail)
        val goToContact = dlg.findViewById<ImageView>(R.id.dialog_goToContact)
        val goToDial = dlg.findViewById<ImageView>(R.id.dialog_goToDial)
        val goToText = dlg.findViewById<ImageView>(R.id.dialog_goToText)

        goToDetail.setOnClickListener {
            Toast.makeText(context,"상세보기 클릭", Toast.LENGTH_LONG).show()
            val intent = Intent(context, DetailInfoActivity::class.java)
            Log.d("카드넘버", cardInfo.CARD_NUMBER.toString())
            intent.putExtra("cardNumber", cardInfo.CARD_NUMBER)
            context.startActivity(intent)
            dlg.dismiss()
        }
        goToContact.setOnClickListener {
            Toast.makeText(context,"연락처 저장 클릭", Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
        goToDial.setOnClickListener {
            //유선, 폰 구분해야함
            Toast.makeText(context,"전화하기 클릭", Toast.LENGTH_LONG).show()
            val telStr = "tel:" + cardInfo.CARD_PHONE
            val intent = Intent("android.intent.action.DIAL")
            intent.data = Uri.parse(telStr)
            context.startActivity(intent)
            dlg.dismiss()
        }
        goToText.setOnClickListener {
            Toast.makeText(context,"문자하기 클릭", Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
    }

}
