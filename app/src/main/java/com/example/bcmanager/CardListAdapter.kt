package com.example.bcmanager

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class CardListAdapter(val context: Context, val cardList: ArrayList<CardInfoItem.cardInfo>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var myApp: BCMApplication? = null
    init {
        myApp = context.applicationContext as BCMApplication

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rcged_card_recyclerview_item, parent, false)
        return CardRecyclerViewAdapter.Holder(view)
    }

    override fun getItemCount(): Int {
//        Log.d("민선", myApp!!.unregisterdCards.size.toString())
        return myApp!!.unregisterdCards.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CardViewHolder){
//            Log.d("민선", "바인드뷰홀더")
            holder.bind(myApp!!.unregisterdCards[position], context)
        }
    }


    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cv = itemView.findViewById<CardView>(R.id.rcged_rcy_cardview)
        val cardImage = itemView.findViewById<ImageView>(R.id.rcged_image)


        fun bind(data: CardInfoItem.cardInfo, context: Context) {

//            Log.d("민선", "바인드")
            Glide.with(context).load(data.CARD_IMAGE)
                    .apply(RequestOptions.fitCenterTransform())
                    .override(200, 150)
                    .into(cardImage)

            cv.setOnClickListener {
                Toast.makeText(context, "클릭크릭", Toast.LENGTH_LONG).show()
            }
        }

    }
}