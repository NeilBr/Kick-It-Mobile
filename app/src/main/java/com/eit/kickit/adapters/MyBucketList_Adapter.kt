package com.eit.kickit.adapters

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R
import com.eit.kickit.ViewChallengeActivity
import com.eit.kickit.models.Challenge

class MyBucketList_Adapter(private val mybucketlist: ArrayList<Challenge>, private val AdvID : Int) : RecyclerView.Adapter<MyBucketList_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mybucketlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = mybucketlist.get(position).cName
        holder.blDescr.text = mybucketlist.get(position).cPoints.toString()
        holder.txtCID = mybucketlist.get(position).cID
        holder.txtName = mybucketlist.get(position).cName
        holder.txtDescr = mybucketlist.get(position).cDescription
        holder.txtPoints = mybucketlist.get(position).cPoints
        holder.txtPrice = mybucketlist.get(position).cPrice
        holder.txtStatus = mybucketlist.get(position).cStatus
        holder.advId = AdvID
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
        val blDescr : TextView = itemView.findViewById(R.id.normalText)
        var txtCID : Int = 0
        var txtName : String = ""
        var txtDescr : String = ""
        var txtPrice : Double = 0.00
        var txtPoints : Double = 0.00
        var txtStatus : Boolean = false
        var advId : Int = -1
        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ViewChallengeActivity::class.java)
                intent.putExtra("MyChallenge", "View my challenge layout")
                intent.putExtra("cID", txtCID)
                intent.putExtra("Name", txtName)
                intent.putExtra("Description", txtDescr)
                intent.putExtra("Points", txtPoints)
                intent.putExtra("Price", txtPrice)
                intent.putExtra("Status", txtStatus)
                intent.putExtra("advID", advId)
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