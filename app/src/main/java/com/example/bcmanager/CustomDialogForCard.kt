package com.example.bcmanager

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import java.util.*
import kotlin.reflect.KParameter


public class CustomDialogForCard(private var context: Context, var cardInfo: CardInfoItem.cardInfo) {

    fun callDialog() {
        val dlg = Dialog(context)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.activity_custom_dialog_for_card)
        Objects.requireNonNull(dlg.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)))

        dlg.show()

        val goToShare = dlg.findViewById<ImageView>(R.id.dialog_goToShare)
        val goToLink = dlg.findViewById<ImageView>(R.id.dialog_goToLink)
        val goToKakao = dlg.findViewById<ImageView>(R.id.dialog_goToKakao)
//        val goToText = dlg.findViewById<ImageView>(R.id.dialog_goToText)
//        val goToShare = dlg.findViewById<ImageView>(R.id.dialog_goToShare)


        goToShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.IMAGE_URL + cardInfo.CARD_IMAGE)
            sendIntent.type = "text/plain"

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
        goToLink.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("simple text", MainActivity.IMAGE_URL + cardInfo.CARD_IMAGE)
            clipboard.primaryClip = clip
            Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
            dlg.dismiss()
        }
        goToKakao.setOnClickListener {
            KakaoLinkProvider.sendKakaoLink(context, cardInfo.CARD_NUMBER, cardInfo.CARD_IMAGE)
            dlg.dismiss()
        }
    }


}
