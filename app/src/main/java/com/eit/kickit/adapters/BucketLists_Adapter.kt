package com.eit.kickit.adapters

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.activities.ChallengesListActivity
import com.eit.kickit.R
import com.eit.kickit.models.BucketList

/**
 * This adapter is for the bucketlists to bind to the layout in bucket list tab
 */
class BucketLists_Adapter(private val bucketlists: ArrayList<BucketList>): RecyclerView.Adapter<BucketLists_Adapter.ViewHolder>() {

    public val Bucketlists: ArrayList<BucketList> = bucketlists

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view, bucketlists)
    }

    override fun getItemCount(): Int {
     return bucketlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = bucketlists.get(position).blName
        holder.blReqPnts.text = bucketlists.get(position).blReqPoints.toString()
        holder.blId = bucketlists.get(position).blID
    }

    class ViewHolder(itemView : View, bucketLists: ArrayList<BucketList>) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
        val blReqPnts : TextView = itemView.findViewById(R.id.normalText)
        var blId : Int = 0
        init{
            itemView.setOnClickListener {
                var data:MutableList<BucketList> = ArrayList<BucketList>()
                data = bucketLists
                val intent = Intent(itemView.context, ChallengesListActivity::class.java)
                intent.putParcelableArrayListExtra("BucketLists", data as java.util.ArrayList<out Parcelable>)
                intent.putExtra("ID", blId)
                itemView.context.startActivity(intent)



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