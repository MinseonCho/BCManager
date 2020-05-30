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
import java.util.zip.Inflater

class CardRecyclerViewAdapter(val context: Context, val cardList: ArrayList<CardRecyclerViewItem>)
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
        if( holder is Holder){
            holder.card?.setImageResource(cardList[position].card!!)

            holder.cv?.setOnClickListener {
                val customDialog: CustomDialogForCard = CustomDialogForCard(context)
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