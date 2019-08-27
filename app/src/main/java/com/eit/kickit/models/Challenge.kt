package com.eit.kickit.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class for individual Challenge
 */
data class Challenge(
    var cID : Int,
    var cName : String,
    var cDescription : String,
    var cPoints : Int,
    var cPrice : Double,
    var cStatus : Boolean,
    var blID : Int
)

    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    public fun setID(id: Int){
        cID = id
    }

    public fun getID(): Int{
        return cID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cID)
        parcel.writeString(cName)
        parcel.writeString(cDescription)
        parcel.writeInt(cPoints)
        parcel.writeDouble(cPrice)
        parcel.writeByte(if (cStatus) 1 else 0)
        parcel.writeInt(blID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Challenge> {
        override fun createFromParcel(parcel: Parcel): Challenge {
            return Challenge(parcel)
        }

        override fun newArray(size: Int): Array<Challenge?> {
            return arrayOfNulls(size)
        }
    }
}