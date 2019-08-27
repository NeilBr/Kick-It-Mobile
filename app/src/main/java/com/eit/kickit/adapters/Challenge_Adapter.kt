package com.eit.kickit.adapters

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.ViewChallengeActivity
import com.eit.kickit.R
import com.eit.kickit.models.Challenge

class Challenge_Adapter(private val challenges: ArrayList<Challenge>): RecyclerView.Adapter<Challenge_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = challenges.get(position).cName
        holder.blDescr.text = challenges.get(position).cDescription
        holder.txtDescr = challenges.get(position).cDescription
        holder.txtPoints = challenges.get(position).cPoints
        holder.txtPrice = challenges.get(position).cPrice
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
        val blDescr : TextView = itemView.findViewById(R.id.normalText)
        var txtDescr : String = ""
        var txtPrice : Double = 0.00
        var txtPoints : Int = 0
        init {
            itemView.setOnClickListener {
               val intent = Intent(itemView.context, ViewChallengeActivity::class.java)
                intent.putExtra("MyChallenge", "View challenge layout")
                intent.putExtra("Description", txtDescr)
                intent.putExtra("Points", txtPoints)
                intent.putExtra("Price", txtPrice)
               itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Challenge>
        {
            override fun createFromParcel(parcel: Parcel): Challenge {
                return Challenge(parcel)
            }

            override fun newArray(size: Int): Array<Challenge?> {
                return arrayOfNulls(size)
            }
        }
    }
}