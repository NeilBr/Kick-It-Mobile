package com.eit.kickit.models

import android.os.Parcel
import android.os.Parcelable

/**
 *  Data class for individual bucket list
 */
data class BucketList (
    var blID : Int = 0,
    var blName : String,
    var blDescription : String,
    var blReqPoints : Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    public fun setID(id: Int){
        blID = id
    }

    public fun getID(): Int{
        return blID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(blID)
        parcel.writeString(blName)
        parcel.writeString(blDescription)
        parcel.writeInt(blReqPoints)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BucketList> {
        override fun createFromParcel(parcel: Parcel): BucketList {
            return BucketList(parcel)
        }

        override fun newArray(size: Int): Array<BucketList?> {
            return arrayOfNulls(size)
        }
    }


}