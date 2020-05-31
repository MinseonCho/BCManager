package com.example.bcmanager

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import java.util.*

public class CustomDialogForCard(private var context: Context) {

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
            context.startActivity(Intent(context, DetailInfoActivity::class.java))
            dlg.dismiss()
        }
        goToContact.setOnClickListener {
            Toast.makeText(context,"연락처 저장 클릭", Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
        goToDial.setOnClickListener {
            Toast.makeText(context,"전화하기 클릭", Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
        goToText.setOnClickListener {
            Toast.makeText(context,"문자하기 클릭", Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
    }

}
