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


//        goToDetail.setOnClickListener {
//            Toast.makeText(context, "상세보기 클릭", Toast.LENGTH_LONG).show()
//            val intent = Intent(context, DetailInfoActivity::class.java)
//            Log.d("카드넘버", cardInfo.CARD_NUMBER.toString())
//            intent.putExtra("cardNumber", cardInfo.CARD_NUMBER)
//            context.startActivity(intent)
//            dlg.dismiss()
//        }
//        goToContact.setOnClickListener {
//            Toast.makeText(context, "연락처 저장 클릭", Toast.LENGTH_LONG).show()
//            // Creates a new Intent to insert a contact
//            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
//                // Sets the MIME type to match the Contacts Provider
//                type = ContactsContract.RawContacts.CONTENT_TYPE
//            }
//            intent.apply {
//                // Inserts an email address
//                putExtra(ContactsContract.Intents.Insert.EMAIL, cardInfo.CARD_EMAIL)
//                /*
//                 * In this example, sets the email type to be a work email.
//                 * You can set other email types as necessary.
//                 */
//                putExtra(
//                        ContactsContract.Intents.Insert.EMAIL_TYPE,
//                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
//                )
//                // Inserts a phone number
//                putExtra(ContactsContract.Intents.Insert.PHONE, cardInfo.CARD_PHONE)
//                putExtra(ContactsContract.Intents.Insert.NAME, cardInfo.CARD_NAME)
//                putExtra(ContactsContract.Intents.Insert.COMPANY, cardInfo.CARD_COMPANY)
//                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, cardInfo.CARD_POSITION)
//
//                /*
//                 * In this example, sets the phone type to be a work phone.
//                 * You can set other phone types as necessary.
//                 */
//                putExtra(
//                        ContactsContract.Intents.Insert.PHONE_TYPE,
//                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK
//                )
//            }
//
//            /* Sends the Intent
//         */
//            context.startActivity(intent)

            // //////////////////////////////////////////////////////
//            val list: ArrayList<ContentProviderOperation> = ArrayList();
//            try {
//                list.add(
//                        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                                .build()
//                );
//
//                list.add(
//                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//
//                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, cardInfo.CARD_NAME)   //이름
//
//                                .build()
//                );
//
//                list.add(
//                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//
//                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, cardInfo.CARD_PHONE)           //전화번호
//                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)   //번호타입(Type_Mobile : 모바일)
//
//                                .build()
//                );
//
//                list.add(
//                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, cardInfo.CARD_TEL)
//                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                                .build()
//                )
//                list.add(
//                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//
//                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, cardInfo.CARD_EMAIL)  //이메일
//                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)     //이메일타입(Type_Work : 직장)
//
//                                .build()
//                );
//
//                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, list);  //주소록추가
//
//                list.clear();   //리스트 초기화
//
//
//
//
//            } catch (e: RemoteException) {
//                e.printStackTrace();
//            } catch (e: OperationApplicationException) {
//                e.printStackTrace();
//            }
            // //////////////////////////////////////////////////////
//
//            dlg.dismiss()
//        }
//        goToDial.setOnClickListener {
//            //유선, 폰 구분해야함
//            Toast.makeText(context, "전화하기 클릭", Toast.LENGTH_LONG).show()
//            val telStr = "tel:" + cardInfo.CARD_PHONE
//            val intent = Intent("android.intent.action.DIAL")
//            intent.data = Uri.parse(telStr)
//            context.startActivity(intent)
//            dlg.dismiss()
//        }
//        goToText.setOnClickListener {
//            Toast.makeText(context, "문자하기 클릭", Toast.LENGTH_LONG).show()
//            val telStr = "tel:" + cardInfo.CARD_PHONE
//            val intent = Intent(Intent.ACTION_SENDTO)
//            val uri = Uri.parse("sms:" + cardInfo.CARD_PHONE)
//
//            intent.data = uri
////            intent.putExtra("sms_body", "hello")
//
//            context.startActivity(intent)
//            dlg.dismiss()
//        }
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
