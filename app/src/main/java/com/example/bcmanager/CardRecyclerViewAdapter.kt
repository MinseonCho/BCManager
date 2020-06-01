package com.example.bcmanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList

class CardRecyclerViewAdapter(val context: Context, val cardList: ArrayList<CardInfoItem.cardInfo>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("onCreateViewHolder","")
        val view = LayoutInflater.from(context).inflate(R.layout.card_recyclerview_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
       return cardList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("onBindViewHolder","")
        val instance = cardList[position]
        val cardNumber = cardList[position].CARD_NUMBER
        if( holder is Holder){
            val url =  MainActivity.IMAGE_URL + cardList[position].CARD_IMAGE

            Glide.with(context).load(url)
                    .apply(RequestOptions.fitCenterTransform())
                    .override(MainActivity.device_width-50, 200)
                    .into(holder.card!!)


//            holder.card?.setImageResource(cardList[position].card!!)

            holder.cv?.setOnClickListener {
                val customDialog = CustomDialogForCard(context, instance )
                customDialog.callDialog()
            }
        }
    }

    class Holder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: ImageView? = null
        var cv: CardView? = null
        init {
            card = itemView.findViewById<ImageView>(R.id.rcy_card_image)
            cv = itemView.findViewById(R.id.rcy_cardview)
        }

    }
}