package com.eit.kickit.adapters

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.ChallengesListActivity
import com.eit.kickit.R
import com.eit.kickit.models.BucketList

/**
 * This adapter is for the bucketlists to bind to the layout in bucket list tab
 */
class BucketLists_Adapter(private val bucketlists: ArrayList<BucketList>, private val advID : Int, private val point: Double): RecyclerView.Adapter<BucketLists_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
     return bucketlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lblName.text = bucketlists.get(position).blName
        holder.lblReqPnts.text = bucketlists.get(position).blReqPoints.toString()
        holder.blId = bucketlists.get(position).blID
        holder.curName = bucketlists.get(position).blName
        holder.curDesc = bucketlists.get(position).blDescription
        holder.reqPoints = bucketlists.get(position).blReqPoints
        holder.advID = advID
        holder.points = point
    }



    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val lblName : TextView = itemView.findViewById(R.id.titleText)
        val lblReqPnts : TextView = itemView.findViewById(R.id.normalText)
        var blId : Int = 0
        var reqPoints : Int = - 1
        var curName : String = ""
        var curDesc : String = ""
        var advID : Int = -1
        var points : Double = 0.0
        init{
            itemView.setOnClickListener {
               // var data:MutableList<BucketList> = ArrayList<BucketList>()
               // data = bucketLists
                if (points >= reqPoints)
                {
                    val intent = Intent(itemView.context, ChallengesListActivity::class.java)
                    intent.putExtra("ID", blId)
                    intent.putExtra("Name", curName)
                    intent.putExtra("Desc", curDesc)
                    intent.putExtra("reqPoints", reqPoints)
                    intent.putExtra("advID", advID)
                    itemView.context.startActivity(intent)
                }
                else
                {
                    Toast.makeText(itemView.context, "Not enough points to unlock!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BucketList>
        {
            override fun createFromParcel(parcel: Parcel): BucketList {
                return BucketList(parcel)
            }

            override fun newArray(size: Int): Array<BucketList?> {
                return arrayOfNulls(size)
            }
        }
    }


}