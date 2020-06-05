package com.example.bcmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.api.client.util.Data
import java.util.*

class CardRecyclerViewAdapter(val context: Context, val cardList: ArrayList<CardInfoItem.cardInfo>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //recyclerview 확장/축소
    // Item의 클릭 상태를 저장할 array 객체e var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    // 직전에 클릭됐던 Item의 position
    private var prePosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("onCreateViewHolder", "")
        val view = LayoutInflater.from(context).inflate(R.layout.card_recyclerview_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "")
        val instance = cardList[position]
        val cardNumber = cardList[position].CARD_NUMBER
        if (holder is Holder) {
            val url = MainActivity.IMAGE_URL + cardList[position].CARD_IMAGE

            Glide.with(context).load(url)
                    .apply(RequestOptions.fitCenterTransform())
                    .override(MainActivity.device_width - 50, 200)
                    .into(holder.card!!)


//            holder.card?.setImageResource(cardList[position].card!!)

            holder.changeVisibility(selectedItems.get(position));

            holder.cv?.setOnClickListener {

                if (selectedItems.get(position)) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(position);
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition);
                    // 클릭한 Item의 position을 저장
                    selectedItems.put(position, true);
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(position);
                // 클릭된 position 저장
                prePosition = position;

            }

            holder.linear_info?.setOnClickListener {
                val intent = Intent(context, DetailInfoActivity::class.java)
                    Log.d("카드넘버", cardNumber.toString())
                    intent.putExtra("cardNumber", cardNumber)
                    context.startActivity(intent)
            }
            holder.linear_contact?.setOnClickListener {
                // Creates a new Intent to insert a contact
                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    // Sets the MIME type to match the Contacts Provider
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                }
                intent.apply {
                    // Inserts an email address
                    putExtra(ContactsContract.Intents.Insert.EMAIL, instance.CARD_EMAIL)
                    /*
                     * In this example, sets the email type to be a work email.
                     * You can set other email types as necessary.
                     */
                    putExtra(
                            ContactsContract.Intents.Insert.EMAIL_TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    )
                    // Inserts a phone number
                    putExtra(ContactsContract.Intents.Insert.PHONE, instance.CARD_PHONE)
                    putExtra(ContactsContract.Intents.Insert.NAME, instance.CARD_NAME)
                    putExtra(ContactsContract.Intents.Insert.COMPANY, instance.CARD_COMPANY)
                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, instance.CARD_POSITION)

                    /*
                     * In this example, sets the phone type to be a work phone.
                     * You can set other phone types as necessary.
                     */
                    putExtra(
                            ContactsContract.Intents.Insert.PHONE_TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                    )
                }

                /* Sends the Intent
             */
                context.startActivity(intent)

            }
            holder.linear_dial?.setOnClickListener {
                val telStr = "tel:" + instance.CARD_PHONE
                val intent = Intent("android.intent.action.DIAL")
                intent.data = Uri.parse(telStr)
                context.startActivity(intent)
            }
            holder.linear_message?.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                val uri = Uri.parse("sms:" + instance.CARD_PHONE)

                intent.data = uri

                context.startActivity(intent)
            }
            holder.linear_share?.setOnClickListener {
              val customDialogForCard:CustomDialogForCard = CustomDialogForCard(context, instance)
                customDialogForCard.callDialog()
            }

        }



    }



    class Holder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: ImageView? = null
        var linear_info: LinearLayout? = null
        var linear_contact: LinearLayout? = null
        var linear_dial: LinearLayout? = null
        var linear_message: LinearLayout? = null
        var linear_share: LinearLayout? = null
        var cv: CardView? = null
        var expendView: LinearLayout? = null

        init {
            card = itemView.findViewById<ImageView>(R.id.rcy_card_image)
            cv = itemView.findViewById(R.id.rcy_cardview)
            expendView = itemView.findViewById(R.id.expend_view)
            linear_info = itemView.findViewById(R.id.linear_detail_info)
            linear_dial = itemView.findViewById(R.id.linear_dial)
            linear_contact = itemView.findViewById(R.id.linear_contact)
            linear_message = itemView.findViewById(R.id.linear_message)
            linear_share = itemView.findViewById(R.id.linear_share)
        }


        fun changeVisibility(isExpanded: Boolean) {
            expendView!!.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
//            val va = ValueAnimator()
//            // Animation이 실행되는 시간, n/1000초
//            // Animation이 실행되는 시간, n/1000초
//            va.setDuration(600)
//            va.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
//                override fun onAnimationUpdate(animation: ValueAnimator) {
//
//                     expendView!!.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
//                }
//            })
//            // Animation start
//            va.start()
        }

//        override fun onClick(v: View?) {
//            when(v?.getId()) {
//                R.id.linear_detail_info -> {
//                    val intent = Intent(context, DetailInfoActivity::class.java)
//                    Log.d("카드넘버", cardInfo.CARD_NUMBER.toString())
//                    intent.putExtra("cardNumber", cardInfo.CARD_NUMBER)
//                    context.startActivity(intent)
//                }
//                    R.id.linear_contact ->
//
//
//            }
//        }

    }
}