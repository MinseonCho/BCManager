package com.example.bcmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class CardListAdapter(val context: Context, val cardList: ArrayList<CardInfoItem.cardInfo>, listener: OnItemClick) : RecyclerView.Adapter<CardListAdapter.CardHolder>() {

    private var myApp: BCMApplication? = null
    private var mCallback: OnItemClick? = null

    init {
        myApp = context.applicationContext as BCMApplication
        mCallback = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rcged_card_recyclerview_item, parent, false)
        return CardListAdapter.CardHolder(view)
    }

    override fun getItemCount(): Int {
//        Log.d("민선", myApp!!.unregisterdCards.size.toString())
        return cardList.size
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val item = cardList[position]
        val url = MainActivity.IMAGE_URL + item.CARD_IMAGE

        Glide.with(context).load(url)
                .apply(RequestOptions.fitCenterTransform())
                .override(200, 150)
                .into(holder.cardImage)
        holder.cv.setOnClickListener {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra("name", item.CARD_NAME)
            intent.putExtra("position", item.CARD_POSITION)
            intent.putExtra("company", item.CARD_COMPANY)
            intent.putExtra("phone", item.CARD_PHONE)
            intent.putExtra("number", item.CARD_TEL)
            intent.putExtra("email", item.CARD_EMAIL)
            intent.putExtra("address", item.CARD_ADDRESS)
            intent.putExtra("fax", item.CARD_FAX)
            intent.putExtra("cardNum", item.CARD_NUMBER)
            intent.putExtra("image", item.CARD_IMAGE)
            intent.putExtra("memo", item.CARD_MEMO)
            context.startActivity(intent)
        }
        holder.delete.setOnClickListener {
            cardList.removeAt(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cardList.size);

            mCallback?.onClick(item.CARD_NUMBER.toString())
//            val httpConnection = HttpConnection(URL(MainActivity.DELETE_ITEM))
//            httpConnection.requestDeleteItem(item.CARD_NUMBER.toString(), object : OnRequestCompleteListener{
//                override fun onSuccess(data: String?) {
//                    if (data != null) {
//                        if(data.isNotEmpty()){
//                            if(data.equals("1"))  Log.d("삭제 결과", data )
//                            else  Log.d("삭제 결과", data )
//
//                        }
//                    }
//                }
//
//                override fun onError() {
//                    Log.d("삭제 결과", "실패" )
//                }
//
//            })
        }

    }


    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cv = itemView.findViewById<CardView>(R.id.rcged_rcy_cardview)
        val cardImage = itemView.findViewById<ImageView>(R.id.rcged_image)
        val delete = itemView.findViewById<ImageView>(R.id.delete)

    }
}